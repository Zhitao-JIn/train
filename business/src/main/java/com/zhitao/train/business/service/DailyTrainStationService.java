package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.DailyTrainStation;
import com.zhitao.train.business.domain.entity.TrainStation;
import com.zhitao.train.business.domain.repository.DailyTrainRepository;
import com.zhitao.train.business.domain.repository.DailyTrainStationRepository;
import com.zhitao.train.business.req.DailyTrainStationQueryReq;
import com.zhitao.train.business.req.DailyTrainStationSaveReq;
import com.zhitao.train.business.resp.DailyTrainStationQueryResp;
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

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Resource
    private DailyTrainStationRepository dailyTrainStationRepository;

    @Resource
    private TrainStationService trainStationService;

    public void save(DailyTrainStationSaveReq req) {
        Instant now = Instant.now();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(req, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationRepository.save(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationRepository.save(dailyTrainStation);
        }
    }

    public PageResp<DailyTrainStationQueryResp> queryList(
            DailyTrainStationQueryReq req) {

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // 排序（注意 index 字段）
        Sort sort = Sort.by(

                Sort.Order.desc("date"),

                Sort.Order.asc("trainCode"),

                Sort.Order.asc("index")   // 关键点
        );

        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                sort
        );

        // 动态条件
        Specification<DailyTrainStation> spec =
                (root, query, cb) -> {

                    List<Predicate> list =
                            new ArrayList<>();

                    // date 条件
                    if (ObjUtil.isNotNull(req.getDate())) {

                        list.add(
                                cb.equal(
                                        root.get("date"),
                                        req.getDate()
                                )
                        );
                    }

                    // trainCode 条件
                    if (ObjUtil.isNotEmpty(req.getTrainCode())) {

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
        Page<DailyTrainStation> page =
                dailyTrainStationRepository.findAll(
                        spec,
                        pageable
                );

        LOG.info("总行数：{}",
                page.getTotalElements());

        LOG.info("总页数：{}",
                page.getTotalPages());

        // DTO转换
        List<DailyTrainStationQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        DailyTrainStationQueryResp.class
                );

        PageResp<DailyTrainStationQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(
                page.getTotalElements()
        );

        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationRepository.deleteById(id);
    }

    @Transactional
    public void genDaily(LocalDate date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站信息开始", date, trainCode);

        dailyTrainStationRepository.deleteByDateAndTrainCode(date,trainCode);
        dailyTrainStationRepository.flush();

        // 查出某车次的所有的车站信息
        List<TrainStation> stationList = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(stationList)) {
            LOG.info("该车次没有车站基础数据，生成该车次的车站信息结束");
            return;
        }

        for (TrainStation trainStation : stationList) {
            Instant now = Instant.now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationRepository.save(dailyTrainStation);
        }
        LOG.info("生成日期【{}】车次【{}】的车站信息结束", date, trainCode);
    }
}
