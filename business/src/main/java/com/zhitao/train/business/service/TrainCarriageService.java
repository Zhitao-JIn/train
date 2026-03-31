package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.TrainCarriage;
import com.zhitao.train.business.domain.repository.TrainCarriageRepository;
import com.zhitao.train.business.enums.SeatColEnum;
import com.zhitao.train.business.req.TrainCarriageQueryReq;
import com.zhitao.train.business.req.TrainCarriageSaveReq;
import com.zhitao.train.business.resp.TrainCarriageQueryResp;
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
public class TrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    @Resource
    private TrainCarriageRepository trainCarriageRepository;

    public void save(TrainCarriageSaveReq req) {
        Instant now = Instant.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {

            // 保存之前，先校验唯一键是否存在
            TrainCarriage trainCarriageDB = selectByUnique(req.getTrainCode(), req.getIndex());
            if (ObjectUtil.isNotEmpty(trainCarriageDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }

            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageRepository.save(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageRepository.save(trainCarriage);
        }
    }

    private TrainCarriage selectByUnique(String trainCode, Integer index) {
        List<TrainCarriage> list = trainCarriageRepository.findByTrainCodeAndIndex(trainCode, index);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req) {
        Sort sort = Sort.by(Sort.Direction.ASC, "trainCode", "index");
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), sort);
        Page<TrainCarriage> trainCarriagePage;
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            trainCarriagePage= trainCarriageRepository.findByTrainCode(req.getTrainCode(),pageable);
        }else{
            trainCarriagePage= trainCarriageRepository.findAll(pageable);
        }
        LOG.info("每页条数：{}", req.getSize());
        LOG.info("总行数：{}", trainCarriagePage.getTotalElements());
        LOG.info("总页数：{}", trainCarriagePage.getTotalPages());

        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriagePage.getContent(), TrainCarriageQueryResp.class);

        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainCarriagePage.getTotalElements());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainCarriageRepository.deleteById(id);
    }

    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        Sort sort = Sort.by(Sort.Direction.ASC,  "index");
        return trainCarriageRepository.findByTrainCode(trainCode,sort);
    }
}

