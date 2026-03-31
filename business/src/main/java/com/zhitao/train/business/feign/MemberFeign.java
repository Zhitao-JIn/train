package com.zhitao.train.business.feign;

import com.zhitao.train.business.req.MemberTicketReq;
import com.zhitao.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "member", url ="http://localhost:8001")
public interface MemberFeign {
    @GetMapping("member/feign/ticket/save")
    public CommonResp<Object> save(@Valid MemberTicketReq req);
}
