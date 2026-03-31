package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.TrainStation;
import com.zhitao.train.business.domain.repository.TrainStationRepository;
import com.zhitao.train.business.req.TrainStationQueryReq;
import com.zhitao.train.business.req.TrainStationSaveReq;
import com.zhitao.train.business.resp.TrainStationQueryResp;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Resource
    private TrainStationRepository trainStationRepository;

    public void save(TrainStationSaveReq req) {
        Instant now = Instant.now();
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {

            // 保存之前，先校验唯一键是否存在
            TrainStation trainStationDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(req.getTrainCode(), req.getName());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }

            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationRepository.save(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationRepository.save(trainStation);
        }
    }

    private TrainStation selectByUnique(String trainCode, Integer index) {
        List<TrainStation> list = trainStationRepository.findByTrainCodeAndIndex(trainCode, index);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private TrainStation selectByUnique(String trainCode, String name) {
        List<TrainStation> list = trainStationRepository.findByTrainCodeAndName(trainCode,name);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req) {
        Sort sort = Sort.by(Sort.Direction.ASC, "trainCode", "index");

// 分页：page 页码从 0 开始（JPA 规则），所以要 -1
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), sort);

        Page<TrainStation> trainStationPage;
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            // 有 trainCode 条件
            trainStationPage = trainStationRepository.findByTrainCode(req.getTrainCode(), pageable);
        } else {
            // 无 trainCode 条件，查全部
            trainStationPage = trainStationRepository.findAll(pageable);
        }

        LOG.info("每页条数：{}", req.getSize());
        LOG.info("总行数：{}", trainStationPage.getTotalElements());
        LOG.info("总页数：{}", trainStationPage.getTotalPages());

        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationPage.getContent(), TrainStationQueryResp.class);

        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainStationPage.getTotalElements());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainStationRepository.deleteById(id);
    }


    public List<TrainStation> selectByTrainCode(String trainCode) {
        Sort sort = Sort.by(Sort.Direction.ASC, "index");

        return trainStationRepository.findByTrainCode(trainCode, sort);
    }
}
