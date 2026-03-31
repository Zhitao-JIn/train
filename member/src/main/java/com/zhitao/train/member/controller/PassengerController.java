package com.zhitao.train.member.controller;

import com.zhitao.train.common.context.LoginMemberContext;
import com.zhitao.train.common.resp.CommonResp;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.member.req.PassengerQueryReq;
import com.zhitao.train.member.resp.PassengerQueryResp;
import com.zhitao.train.member.req.PassengerSaveReq;
import com.zhitao.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/passenger")
public class PassengerController {

    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> savePassenger(@Valid @RequestBody PassengerSaveReq passengerSaveReq) {
        passengerService.save(passengerSaveReq);
        return CommonResp.builder().success(true).build();
    }

    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> queryList(@Valid PassengerQueryReq passengerQueryReq) {
        passengerQueryReq.setMemberId(LoginMemberContext.getId());
        PageResp<PassengerQueryResp> pageResp = passengerService.queryList(passengerQueryReq);
        return CommonResp.<PageResp<PassengerQueryResp>>builder().success(true).content(pageResp).build();
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        passengerService.delete(id);
        return CommonResp.builder().success(true).build();
    }

    @GetMapping("/query-mine")
    public CommonResp<List<PassengerQueryResp>> queryMine() {
        List<PassengerQueryResp> list = passengerService.queryMine();
        return new CommonResp<>(list);
    }

}
