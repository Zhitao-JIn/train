package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.business.domain.repository.StationRepository;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import com.zhitao.train.business.domain.entity.Station;
import com.zhitao.train.business.req.StationQueryReq;
import com.zhitao.train.business.req.StationSaveReq;
import com.zhitao.train.business.resp.StationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    private StationRepository stationRepository;

    public void save(StationSaveReq req) {
        Instant now = Instant.now();
        Station station = BeanUtil.copyProperties(req, Station.class);
        if (ObjectUtil.isNull(station.getId())) {

            // 保存之前，先校验唯一键是否存在
            Station stationDB = selectByUnique(req.getName());
            if (ObjectUtil.isNotEmpty(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }

            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationRepository.save(station);
        } else {
            station.setUpdateTime(now);
            stationRepository.save(station);
        }
    }

    private Station selectByUnique(String name) {
        List<Station> list = stationRepository.findByName(name);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq req) {
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        Pageable pageable = PageRequest.of(req.getPage()-1, req.getSize());
        Page<Station> stationPage = stationRepository.findAll(pageable);
        LOG.info("当前页条数：{}", stationPage.getNumberOfElements());
        LOG.info("总条数：{}", stationPage.getTotalElements()); // 这个才是总行数
        LOG.info("总页数：{}", stationPage.getTotalPages());

        List<StationQueryResp> list = BeanUtil.copyToList(stationPage.getContent(), StationQueryResp.class);

        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(stationPage.getTotalElements());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        stationRepository.deleteById(id);
    }

    public List<StationQueryResp> queryAll() {
        List<Station> stationList = stationRepository.findAllByOrderByNamePinyinAsc();
        return BeanUtil.copyToList(stationList, StationQueryResp.class);
    }
}
