package com.zhitao.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.common.context.LoginMemberContext;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import com.zhitao.train.member.domain.entity.Passenger;
import com.zhitao.train.member.domain.repository.PassengerRepository;
import com.zhitao.train.member.req.PassengerQueryReq;
import com.zhitao.train.member.resp.PassengerQueryResp;
import com.zhitao.train.member.req.PassengerSaveReq;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PassengerService {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    @Resource
    private PassengerRepository passengerRepository;

    public void save(PassengerSaveReq req) {
        LocalDateTime now = LocalDateTime.now();
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
        } else {
            passenger.setUpdateTime(now);
        }
        passengerRepository.save(passenger);
    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req) {

        //LOG.info("查询页码：{}", req.getPage());
        //LOG.info("每页条数：{}", req.getSize());
        Pageable pageable = PageRequest.of(req.getPage(), req.getSize());
        Page<Passenger> passengerPage = passengerRepository.findByMemberId(req.getMemberId(),pageable);
        LOG.info("当前页条数：{}", passengerPage.getNumberOfElements());
        LOG.info("总条数：{}", passengerPage.getTotalElements()); // 这个才是总行数
        LOG.info("总页数：{}", passengerPage.getTotalPages());

        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerPage.getContent(), PassengerQueryResp.class);

        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(passengerPage.getTotalElements()); // 这里必须是总条数
        pageResp.setList(list);
        return pageResp;
    }
    public void delete(Long id) {
        passengerRepository.deleteById(id);
    }

    /**
     * 查询我的所有乘客
     */

    public List<PassengerQueryResp> queryMine() {
        List<Passenger> list = passengerRepository.findByMemberIdOrderByNameAsc(LoginMemberContext.getId());
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }

}
