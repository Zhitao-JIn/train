package com.zhitao.train.member.domain.repository;

import com.zhitao.train.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 根据手机号统计数量（判断手机号是否存在）
    long countByMobile(String mobile);

    // 根据手机号查询用户
    Member findByMobile(String mobile);

}