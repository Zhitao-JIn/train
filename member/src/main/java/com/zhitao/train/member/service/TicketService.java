package com.zhitao.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import com.zhitao.train.member.domain.entity.Ticket;
import com.zhitao.train.member.domain.repository.TicketRepository;
import com.zhitao.train.member.req.TicketQueryReq;
import com.zhitao.train.member.req.TicketSaveReq;
import com.zhitao.train.member.resp.TicketQueryResp;
import io.seata.core.context.RootContext;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    private TicketRepository ticketRepository;


    public void save(TicketSaveReq req) {
        LOG.info("seata全局事务ID save:{}", RootContext.getXID());
        Instant now = Instant.now();
        Ticket ticket = BeanUtil.copyProperties(req, Ticket.class);
        LOG.info("ticket{} 保存", ticket.getId());
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketRepository.save(ticket);
        } else {
            ticket.setUpdateTime(now);
            ticketRepository.save(ticket);
        }
        //throw new BusinessException(BusinessExceptionEnum.BUSiNESS_MEMBER_FEIGN_TICKET_SAVE_ERROR);
    }

    public PageResp<TicketQueryResp> queryList(
            TicketQueryReq req) {

        // 排序
        Sort sort = Sort.by(
                Sort.Order.desc("date"),
                Sort.Order.asc("trainCode"),
                Sort.Order.asc("carriageIndex")
        );

        // 分页
        PageRequest pageRequest = PageRequest.of(
                req.getPage() - 1,  // JPA 从0开始
                req.getSize(),
                sort
        );

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // JPA 查询
        Page<Ticket> page;
        if (req.getMemberId() != null) {
            page=ticketRepository.findAllByMemberId(req.getMemberId(),
                    pageRequest
            );
        }else {
            page = ticketRepository.findAll(pageRequest);
        }
        LOG.info("总行数：{}", page.getTotalElements());
        LOG.info("总页数：{}", page.getTotalPages());

        // DTO 转换
        List<TicketQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        TicketQueryResp.class
                );

        // 返回分页对象
        PageResp<TicketQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(page.getTotalElements());
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }
}


