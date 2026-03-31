package com.zhitao.train.member.domain.repository;

import com.zhitao.train.member.domain.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Page<Ticket> findAllByMemberId(Long memberId, PageRequest pageRequest);
}
