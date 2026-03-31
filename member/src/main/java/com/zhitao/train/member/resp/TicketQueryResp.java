package com.zhitao.train.member.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TicketQueryResp {

    /**
     * 车票 ID（防止前端精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 会员 ID（防止前端精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long memberId;

    /**
     * 乘客 ID（防止前端精度丢失）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long passengerId;

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
     * 车厢号
     */
    private Integer carriageIndex;

    /**
     * 排
     */
    private String row;

    /**
     * 列
     */
    private String col;

    /**
     * 起点站
     */
    private String start;

    /**
     * 出发时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    /**
     * 终点站
     */
    private String end;

    /**
     * 到达时间
     */
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    /**
     * 座位类型
     */
    private String seatType;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant updateTime;
}
