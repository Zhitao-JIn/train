package com.zhitao.train.member.controller.admin;

import com.zhitao.train.member.req.TicketQueryReq;
import com.zhitao.train.member.req.TicketSaveReq;
import com.zhitao.train.member.resp.TicketQueryResp;
import com.zhitao.train.member.service.TicketService;
import com.zhitao.train.common.resp.CommonResp;
import com.zhitao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/admin/ticket")
public class TicketAdminController {

    @Resource
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return new CommonResp<>();
    }

    /*
    @GetMapping("/query-all")
    public CommonResp<List<TicketQueryResp>> queryList() {
        List<TicketQueryResp> list = ticketService.queryAll();
        return new CommonResp<>(list);
    }

     */

}
