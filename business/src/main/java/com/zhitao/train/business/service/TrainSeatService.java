package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zhitao.train.business.domain.entity.TrainCarriage;
import com.zhitao.train.business.domain.entity.TrainSeat;
import com.zhitao.train.business.domain.repository.TrainSeatRepository;
import com.zhitao.train.business.enums.SeatColEnum;
import com.zhitao.train.business.req.TrainSeatQueryReq;
import com.zhitao.train.business.req.TrainSeatSaveReq;
import com.zhitao.train.business.resp.TrainSeatQueryResp;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    private TrainSeatRepository trainSeatRepository;

    @Resource
    private TrainCarriageService trainCarriageService;

    public void save(TrainSeatSaveReq req) {
        Instant now = Instant.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(req, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatRepository.save(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatRepository.save(trainSeat);
        }
    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq req) {
        // 排序：train_code asc, carriage_index asc, carriage_seat_index asc
        Sort sort = Sort.by(Sort.Direction.ASC, "trainCode", "carriageIndex", "carriageSeatIndex");

        // 分页：JPA 页码从 0 开始，所以 -1
        Pageable pageable = PageRequest.of(req.getPage() - 1, req.getSize(), sort);

        Page<TrainSeat> trainSeatPage;
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            // 有 trainCode 条件
            trainSeatPage = trainSeatRepository.findByTrainCode(req.getTrainCode(), pageable);
        } else {
            // 无 trainCode 条件，查全部
            trainSeatPage = trainSeatRepository.findAll(pageable);
        }

        LOG.info("每页条数：{}", req.getSize());
        LOG.info("总行数：{}", trainSeatPage.getTotalElements());
        LOG.info("总页数：{}", trainSeatPage.getTotalPages());

        List<TrainSeatQueryResp> list = BeanUtil.copyToList(trainSeatPage.getContent(), TrainSeatQueryResp.class);

        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(trainSeatPage.getTotalElements());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        trainSeatRepository.deleteById(id);
    }

    @Transactional
    public void genTrainSeat(String trainCode) {
        Instant now = Instant.now();
        // 清空当前车次下的所有座位记录（JPA 替换 deleteByExample）
        trainSeatRepository.deleteByTrainCode(trainCode);

        // 查找当前车次下的所有车厢（不用改，直接复用原来的方法）
        List<TrainCarriage> carriageList = trainCarriageService.selectByTrainCode(trainCode);

        // 循环生成每个车厢的座位（业务逻辑完全不变）
        for (TrainCarriage trainCarriage : carriageList) {
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            int seatIndex = 1;

            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);

            for (int row = 1; row <= rowCount; row++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    // JPA 保存替换 mapper.insert
                    trainSeatRepository.save(trainSeat);
                }
            }
        }
    }


    public List<TrainSeat> selectByTrainCode(String trainCode) {

        return trainSeatRepository.findByTrainCodeOrderByIdAsc(trainCode);
    }


}
