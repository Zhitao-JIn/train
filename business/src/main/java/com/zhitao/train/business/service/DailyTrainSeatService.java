package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zhitao.train.business.domain.entity.DailyTrainSeat;
import com.zhitao.train.business.domain.entity.TrainSeat;
import com.zhitao.train.business.domain.entity.TrainStation;
import com.zhitao.train.business.domain.repository.DailyTrainRepository;
import com.zhitao.train.business.domain.repository.DailyTrainSeatRepository;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import com.zhitao.train.business.req.DailyTrainSeatQueryReq;
import com.zhitao.train.business.req.DailyTrainSeatSaveReq;
import com.zhitao.train.business.resp.DailyTrainSeatQueryResp;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Resource
    private DailyTrainSeatRepository dailyTrainSeatRepository;

    @Resource
    private TrainSeatService trainSeatService;

    @Resource
    private TrainStationService trainStationService;


    public void save(DailyTrainSeatSaveReq req) {
        Instant now = Instant.now();
        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(req, DailyTrainSeat.class);
        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatRepository.save(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeatRepository.save(dailyTrainSeat);
        }
    }

    public PageResp<DailyTrainSeatQueryResp> queryList(
            DailyTrainSeatQueryReq req) {

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // 多字段排序
        Sort sort = Sort.by(

                Sort.Order.desc("date"),

                Sort.Order.asc("trainCode"),

                Sort.Order.asc("carriageIndex"),

                Sort.Order.asc("carriageSeatIndex")

        );

        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                sort
        );

        // 动态条件
        Specification<DailyTrainSeat> spec =
                (root, query, cb) -> {

                    List<Predicate> list =
                            new ArrayList<>();

                    // trainCode 条件
                    if (ObjectUtil.isNotEmpty(
                            req.getTrainCode())) {

                        list.add(
                                cb.equal(
                                        root.get("trainCode"),
                                        req.getTrainCode()
                                )
                        );
                    }

                    return cb.and(
                            list.toArray(new Predicate[0])
                    );
                };

        // 查询
        Page<DailyTrainSeat> page =
                dailyTrainSeatRepository.findAll(
                        spec,
                        pageable
                );

        LOG.info("总行数：{}",
                page.getTotalElements());

        LOG.info("总页数：{}",
                page.getTotalPages());

        // DTO转换
        List<DailyTrainSeatQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        DailyTrainSeatQueryResp.class
                );

        PageResp<DailyTrainSeatQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(
                page.getTotalElements()
        );

        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainSeatRepository.deleteById(id);
    }

    @Transactional
    public void  genDaily(LocalDate date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的座位信息开始", date, trainCode);

        dailyTrainSeatRepository.deleteByDateAndTrainCode(date,trainCode);
        dailyTrainSeatRepository.flush();

        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', stationList.size() - 1);

        // 查出某车次的所有的座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("该车次没有座位基础数据，生成该车次的座位信息结束");
            return;
        }

        for (TrainSeat trainSeat : seatList) {
            Instant now = Instant.now();
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setSell(sell);
            dailyTrainSeatRepository.save(dailyTrainSeat);
        }
        LOG.info("生成日期【{}】车次【{}】的座位信息结束", date, trainCode);
    }

    public int countSeat(
            LocalDate date,
            String trainCode,
            String seatType) {

        long count =
                dailyTrainSeatRepository
                        .countByDateAndTrainCodeAndSeatType(
                                date,
                                trainCode,
                                seatType
                        );

        if (count == 0L) {
            return -1;
        }

        return (int) count;
    }

    public List<DailyTrainSeat> selectByCarriage(LocalDate date,String trainCode,Integer carriageIndex ) {
        return dailyTrainSeatRepository.findAllByDateAndTrainCodeAndCarriageIndexOrderByCarriageIndexAsc(date,trainCode,carriageIndex);
    }
}
