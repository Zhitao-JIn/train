package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.DailyTrain;
import com.zhitao.train.business.domain.entity.Train;
import com.zhitao.train.business.domain.repository.DailyTrainRepository;
import com.zhitao.train.business.req.DailyTrainQueryReq;
import com.zhitao.train.business.req.DailyTrainSaveReq;
import com.zhitao.train.business.resp.DailyTrainQueryResp;
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
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    private DailyTrainRepository dailyTrainRepository;

    @Resource
    private TrainService trainService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    public void save(DailyTrainSaveReq req) {
        Instant now = Instant.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(req, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainRepository.save(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainRepository.save(dailyTrain);
        }
    }

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq req) {

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // 排序
        Sort sort = Sort.by(
                Sort.Order.desc("date"),
                Sort.Order.asc("code")
        );

        // 分页（JPA页码从0开始）
        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                sort
        );

        // 动态条件
        Specification<DailyTrain> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // date条件
            if (ObjectUtil.isNotNull(req.getDate())) {
                predicates.add(
                        cb.equal(
                                root.get("date"),
                                req.getDate()
                        )
                );
            }
            // code条件
            if (ObjectUtil.isNotEmpty(req.getCode())) {
                predicates.add(
                        cb.equal(
                                root.get("code"),
                                req.getCode()
                        )
                );
            }
            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };

        // 查询
        Page<DailyTrain> page =
                dailyTrainRepository.findAll(
                        specification,
                        pageable
                );

        LOG.info("总行数：{}", page.getTotalElements());
        LOG.info("总页数：{}", page.getTotalPages());

        // DTO转换
        List<DailyTrainQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        DailyTrainQueryResp.class
                );

        // 封装返回
        PageResp<DailyTrainQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(page.getTotalElements());
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainRepository.deleteById(id);
    }

    /**
     * 生成某日所有车次信息，包括车次、车站、车厢、座位
     * @param date
     */
    @Transactional
    public void genDaily(LocalDate date) {
        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
            genDailyTrain(date, train);
        }
    }

    @Transactional
    public void genDailyTrain(LocalDate date, Train train) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", date, train.getCode());
        //List<DailyTrain> list = dailyTrainRepository.findByDateAndCode(date, train.getCode());
        //System.out.println("待删除记录数量：" + list.size());
        // 删除该车次已有的数据
        dailyTrainRepository.deleteByDateAndCode(date,train.getCode());
        //list = dailyTrainRepository.findByDateAndCode(date, train.getCode());
        //System.out.println("删除后记录数量：" + list.size());
        dailyTrainRepository.flush();

        // 生成该车次的数据
        Instant now = Instant.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainRepository.save(dailyTrain);

        // 生成该车次的车站数据
        dailyTrainStationService.genDaily(date, train.getCode());

        // 生成该车次的车厢数据
        dailyTrainCarriageService.genDaily(date, train.getCode());

        // 生成该车次的座位数据
        dailyTrainSeatService.genDaily(date, train.getCode());

        // 生成该车次的余票数据
        dailyTrainTicketService.genDaily(dailyTrain, date, train.getCode());

        LOG.info("生成日期【{}】车次【{}】的信息结束", date, train.getCode());
    }
}
