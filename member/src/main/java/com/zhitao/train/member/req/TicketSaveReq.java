package com.zhitao.train.member.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TicketSaveReq {

    /**
     * 车票ID
     */
    private Long id;

    /**
     * 会员ID
     */
    @NotNull(message = "【会员ID】不能为空")
    private Long memberId;

    /**
     * 乘客ID
     */
    @NotNull(message = "【乘客ID】不能为空")
    private Long passengerId;

    /**
     * 乘客姓名
     */
    private String passengerName;

    /**
     * 日期
     */
    @NotNull(message = "【日期】不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;

    /**
     * 车次编号
     */
    @NotNull(message = "【车次编号】不能为空")
    @jakarta.validation.constraints.Size(max = 20, message = "【车次编号】长度不能超过 20")
    private String trainCode;

    /**
     * 车厢号
     */
    @NotNull(message = "【车厢号】不能为空")
    private Integer carriageIndex;

    /**
     * 排
     */
    @NotBlank(message = "【排】不能为空")
    private String row;

    /**
     * 列
     */
    @NotBlank(message = "【列】不能为空")
    private String col;

    /**
     * 起点站
     */
    @NotNull(message = "【起点站】不能为空")
    @jakarta.validation.constraints.Size(max = 20, message = "【起点站】长度不能超过 20")
    private String start;

    /**
     * 出发时间
     */
    @NotNull(message = "【出发时间】不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime startTime;

    /**
     * 终点站
     */
    @NotNull(message = "【终点站】不能为空")
    @jakarta.validation.constraints.Size(max = 20, message = "【终点站】长度不能超过 20")
    private String end;

    /**
     * 到达时间
     */
    @NotNull(message = "【到达时间】不能为空")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime endTime;

    /**
     * 座位类型
     */
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;
}