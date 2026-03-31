package com.zhitao.train.member.controller.feign;

import com.zhitao.train.common.resp.CommonResp;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.member.req.TicketQueryReq;
import com.zhitao.train.member.req.TicketSaveReq;
import com.zhitao.train.member.resp.TicketQueryResp;
import com.zhitao.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member/feign/ticket")
public class FeignTicketController {

    @Resource
    private TicketService ticketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TicketSaveReq req) {
        ticketService.save(req);
        return new CommonResp<>();
    }

}
