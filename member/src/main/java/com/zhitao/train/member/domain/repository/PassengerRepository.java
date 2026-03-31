package com.zhitao.train.member.domain.repository;

import com.zhitao.train.member.domain.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long>
{
    Page<Passenger> findByMemberId(Long memberId, Pageable pageable);
    void deleteById(Long id);

    List<Passenger> findByMemberIdOrderByNameAsc(Long id);
}
