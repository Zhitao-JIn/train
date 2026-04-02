package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.entity.Train;
import com.zhitao.train.business.domain.repository.TrainRepository;
import com.zhitao.train.business.req.TrainQueryReq;
import com.zhitao.train.business.req.TrainSaveReq;
import com.zhitao.train.business.resp.TrainQueryResp;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
public class TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Resource
    private TrainRepository trainRepository;

    public void save(TrainSaveReq req) {
        Instant now = Instant.now();
        Train train = BeanUtil.copyProperties(req, Train.class);
        if (ObjectUtil.isNull(train.getId())) {

            // 保存之前，先校验唯一键是否存在
            Train trainDB = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainRepository.save(train);
        } else {
            train.setUpdateTime(now);
            trainRepository.save(train);
        }
    }

    private Train selectByUnique(String code) {
        List<Train> list = trainRepository.findByCode(code);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }


    public PageResp<TrainQueryResp> queryList(TrainQueryReq req) {
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        Pageable pageable = PageRequest.of(req.getPage()-1, req.getSize());
        Page<Train> trainPage = trainRepository.findAll(pageable);

        LOG.info("当前页条数：{}", trainPage.getNumberOfElements());
        LOG.info("总条数：{}", trainPage.getTotalElements()); // 这个才是总行数
        LOG.info("总页数：{}", trainPage.getTotalPages());

        List<TrainQueryResp> list = BeanUtil.copyToList(trainPage.getContent(), TrainQueryResp.class);

        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainPage.getTotalElements());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainRepository.deleteById(id);
    }

    @Cacheable(value = "TrainService.queryAll")
    public List<TrainQueryResp> queryAll() {
        List<Train> trainList = trainRepository.findAllByOrderByIdAsc();
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    public List<Train> selectAll(){
        return trainRepository.findAllByOrderByIdAsc();
    }
}
