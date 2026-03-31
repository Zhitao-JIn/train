package com.zhitao.train.member.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhitao.train.common.req.PageReq;

import java.time.LocalDate;
import java.time.LocalTime;

public class TicketQueryReq extends PageReq {

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 座位类型
     */
    private String seatType;

    private Long memberId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "TicketQueryReq{" +
                "passengerName='" + passengerName + '\'' +
                ", date=" + date +
                ", trainCode='" + trainCode + '\'' +
                ", seatType='" + seatType + '\'' +
                ", memberId=" + memberId +
                '}';
    }
}
