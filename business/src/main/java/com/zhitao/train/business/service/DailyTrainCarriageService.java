package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.DailyTrainCarriage;
import com.zhitao.train.business.domain.entity.TrainCarriage;
import com.zhitao.train.business.domain.repository.DailyTrainCarriageRepository;
import com.zhitao.train.business.enums.SeatColEnum;
import com.zhitao.train.business.req.DailyTrainCarriageQueryReq;
import com.zhitao.train.business.req.DailyTrainCarriageSaveReq;
import com.zhitao.train.business.resp.DailyTrainCarriageQueryResp;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Resource
    private DailyTrainCarriageRepository dailyTrainCarriageRepository;

    @Resource
    private TrainCarriageService trainCarriageService;

    public void save(DailyTrainCarriageSaveReq req) {
        Instant now = Instant.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(req, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageRepository.save(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriageRepository.save(dailyTrainCarriage);
        }
    }

    public PageResp<DailyTrainCarriageQueryResp> queryList(
            DailyTrainCarriageQueryReq req) {

        // 排序
        Sort sort = Sort.by(
                Sort.Order.desc("date"),
                Sort.Order.asc("trainCode"),
                Sort.Order.asc("index")
        );

        // 分页
        PageRequest pageRequest = PageRequest.of(
                req.getPage() - 1,  // JPA 从0开始
                req.getSize(),
                sort
        );

        // 条件构造
        Specification<DailyTrainCarriage> spec =
                (root, query, cb) -> {

                    List<Predicate> list = new ArrayList<>();

                    // trainCode 条件
                    if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
                        list.add(
                                cb.equal(
                                        root.get("trainCode"),
                                        req.getTrainCode()
                                )
                        );
                    }

                    // date 条件
                    if (ObjUtil.isNotNull(req.getDate())) {
                        list.add(
                                cb.equal(
                                        root.get("date"),
                                        req.getDate()
                                )
                        );
                    }

                    return cb.and(
                            list.toArray(new Predicate[0])
                    );
                };

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // JPA 查询
        Page<DailyTrainCarriage> page =
                dailyTrainCarriageRepository.findAll(
                        spec,
                        pageRequest
                );

        LOG.info("总行数：{}", page.getTotalElements());
        LOG.info("总页数：{}", page.getTotalPages());

        // DTO 转换
        List<DailyTrainCarriageQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        DailyTrainCarriageQueryResp.class
                );

        // 返回分页对象
        PageResp<DailyTrainCarriageQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(page.getTotalElements());
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainCarriageRepository.deleteById(id);
    }

    @Transactional
    public void genDaily(LocalDate date, String trainCode) {

        LOG.info("生成日期【{}】车次【{}】的车厢信息开始",
                date,
                trainCode);

        // 1. 删除某日某车次的车厢信息
        dailyTrainCarriageRepository.deleteByDateAndTrainCode(date, trainCode);
        dailyTrainCarriageRepository.flush();
        // 2. 查基础车厢数据
        List<TrainCarriage> carriageList =
                trainCarriageService
                        .selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(carriageList)) {

            LOG.info("该车次没有车厢基础数据，生成该车次的车厢信息结束");
            return;
        }

        Instant now = Instant.now();

        // 3. 构造 Daily 数据
        List<DailyTrainCarriage> saveList =
                new ArrayList<>();

        for (TrainCarriage trainCarriage : carriageList) {

            DailyTrainCarriage dailyTrainCarriage =
                    BeanUtil.copyProperties(
                            trainCarriage,
                            DailyTrainCarriage.class
                    );

            dailyTrainCarriage.setId(
                    SnowUtil.getSnowflakeNextId()
            );

            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            dailyTrainCarriage.setDate(date);

            saveList.add(dailyTrainCarriage);
        }

        // 4. 批量保存（关键优化点）
        dailyTrainCarriageRepository
                .saveAll(saveList);

        LOG.info("生成日期【{}】车次【{}】的车厢信息结束",
                date,
                trainCode);
    }
    public List<DailyTrainCarriage> selectByTrainType(LocalDate date,String trainCode, String seatType) {
        return dailyTrainCarriageRepository.findAllByDateAndTrainCodeAndSeatType(date,trainCode,seatType);
    }
}


