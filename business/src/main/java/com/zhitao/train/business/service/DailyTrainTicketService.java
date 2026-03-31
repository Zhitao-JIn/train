package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.DailyTrain;
import com.zhitao.train.business.domain.entity.DailyTrainTicket;
import com.zhitao.train.business.domain.entity.Station;
import com.zhitao.train.business.domain.entity.TrainStation;
import com.zhitao.train.business.domain.repository.DailyTrainTicketRepository;
import com.zhitao.train.business.enums.SeatTypeEnum;
import com.zhitao.train.business.enums.TrainTypeEnum;
import com.zhitao.train.business.req.DailyTrainTicketQueryReq;
import com.zhitao.train.business.req.DailyTrainTicketSaveReq;
import com.zhitao.train.business.resp.DailyTrainTicketQueryResp;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Resource
    private DailyTrainTicketRepository dailyTrainTicketRepository;

    @Resource
    private TrainStationService trainStationService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(DailyTrainTicketSaveReq req) {
        Instant now = Instant.now();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(req, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketRepository.save(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketRepository.save(dailyTrainTicket);
        }
    }

    public PageResp<DailyTrainTicketQueryResp> queryList(
            DailyTrainTicketQueryReq req) {

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // 排序：id desc
        Sort sort = Sort.by(
                Sort.Order.desc("id")
        );

        // 分页（JPA页码从0开始）
        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                sort
        );

        // 动态条件构建
        Specification<DailyTrainTicket> spec =
                (root, query, cb) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    // date
                    if (ObjUtil.isNotNull(req.getDate())) {

                        predicates.add(
                                cb.equal(
                                        root.get("date"),
                                        req.getDate()
                                )
                        );
                    }

                    // trainCode
                    if (ObjUtil.isNotEmpty(req.getTrainCode())) {

                        predicates.add(
                                cb.equal(
                                        root.get("trainCode"),
                                        req.getTrainCode()
                                )
                        );
                    }

                    // start
                    if (ObjUtil.isNotEmpty(req.getStart())) {

                        predicates.add(
                                cb.equal(
                                        root.get("start"),
                                        req.getStart()
                                )
                        );
                    }

                    // end
                    if (ObjUtil.isNotEmpty(req.getEnd())) {

                        predicates.add(
                                cb.equal(
                                        root.get("end"),
                                        req.getEnd()
                                )
                        );
                    }

                    return cb.and(
                            predicates.toArray(new Predicate[0])
                    );
                };

        // 查询
        Page<DailyTrainTicket> page =
                dailyTrainTicketRepository.findAll(
                        spec,
                        pageable
                );

        LOG.info("总行数：{}", page.getTotalElements());
        LOG.info("总页数：{}", page.getTotalPages());

        // DTO转换
        List<DailyTrainTicketQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        DailyTrainTicketQueryResp.class
                );

        // 封装返回
        PageResp<DailyTrainTicketQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(
                page.getTotalElements()
        );

        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainTicketRepository.deleteById(id);
    }

    @Transactional
    public void genDaily(DailyTrain dailyTrain, LocalDate date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的余票信息开始", date, trainCode);
        // 删除某日某车次余票
        dailyTrainTicketRepository.deleteByDateAndTrainCode(date, trainCode);
        dailyTrainTicketRepository.flush();

        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的余票信息结束");
            return;
        }

        Instant now = Instant.now();
        for (int i = 0; i < stationList.size(); i++) {
            // 得到出发站
            TrainStation trainStationStart = stationList.get(i);
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = (i + 1); j < stationList.size(); j++) {
                TrainStation trainStationEnd = stationList.get(j);
                sumKM = sumKM.add(trainStationEnd.getKm());

                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
                int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
                int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
                int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());
                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                String trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketRepository.save(dailyTrainTicket);
            }
        }
        LOG.info("生成日期【{}】车次【{}】的余票信息结束", date, trainCode);

    }

    public DailyTrainTicket selectByUnique(LocalDate date, String trainCode, String start, String end) {
        List<DailyTrainTicket> list = dailyTrainTicketRepository.findByDateAndTrainCodeAndStartAndEnd(date,trainCode,start,end);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
