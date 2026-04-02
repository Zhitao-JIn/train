package com.zhitao.train.member.controller;

import com.zhitao.train.common.resp.CommonResp;
import com.zhitao.train.member.dto.MemberDto;
import com.zhitao.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RefreshScope
public class MemberController {
    @Resource
    private MemberService memberService;

    @Value("${test.nacos}")
    private String nacos;


    @GetMapping("/hello")
    public CommonResp<Object> hello(){
        return CommonResp.builder().message(nacos).success(true).build();
    }
    @GetMapping("/count")
    public CommonResp<MemberDto> findByMobile(MemberDto memberDto){
        return CommonResp.<MemberDto>builder().success(true).content(memberService.findByMobile(memberDto)).build();
    }
    @GetMapping("/register")
    public CommonResp<Long> register(@Valid MemberDto memberDto){
        return CommonResp.<Long>builder().success(true).content(memberService.register(memberDto)).build();
    }

    @PostMapping("/login")
    public CommonResp<MemberDto> login(
            @Valid
            @RequestBody
            MemberDto memberDto){
        var member = memberService.login(memberDto.getMobile());
        if(member==null){
            return CommonResp.<MemberDto>builder().message("login fail").success(false).build();
        }else {
            return CommonResp.<MemberDto>builder().message("login").success(true).content(member).build();
        }

    }

    @PostMapping("/send-code")
    public CommonResp<MemberDto> sendCode(@RequestBody @Valid MemberDto memberDto){
        return CommonResp.<MemberDto>builder().success(true).build();
    }
}
