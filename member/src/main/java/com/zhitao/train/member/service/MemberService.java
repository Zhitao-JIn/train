package com.zhitao.train.member.service;

import cn.hutool.core.util.IdUtil;
import com.zhitao.train.common.exception.TestException;
import com.zhitao.train.common.util.JwtUtil;
import com.zhitao.train.member.domain.entity.Member;
import com.zhitao.train.member.domain.repository.MemberRepository;
import com.zhitao.train.member.dto.MemberDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Resource
    private  MemberRepository memberRepository;

    public long countByMobile(String mobile){
        return memberRepository.countByMobile(mobile);
    }

    public long count(){
        return memberRepository.count();
    }

    public MemberDto findByMobile(MemberDto memberDto){
        var mobile = memberDto.getMobile();
        Member member = memberRepository.findByMobile(mobile);
        memberDto=MemberDto.builder().id(member.getId()).mobile(member.getMobile()).build();
        return  memberDto;
    }

    public long register(MemberDto memberDto){
        Member member = Member.builder().mobile(memberDto.getMobile()).id(IdUtil.getSnowflake(1,1).nextId()).build();
        if(memberRepository.findByMobile(member.getMobile())!=null){
            throw new TestException("手机号已注册");
        }
        memberRepository.save(member);
        return member.getId();
    }

    public MemberDto login(String mobile){
        Member member = memberRepository.findByMobile(mobile);
        if(member==null){
            return  null;
        }else {
            var token = JwtUtil.createToken(member.getId(),member.getMobile());
            var memberDto = MemberDto.builder().mobile(member.getMobile()).id(member.getId()).token(token).build();
            return memberDto;
        }
    }
}
