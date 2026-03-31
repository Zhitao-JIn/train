package com.zhitao.train.business.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "daily_train_ticket", schema = "train_business")
public class DailyTrainTicket {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 20)
    @NotNull
    @Column(name = "train_code", nullable = false, length = 20)
    private String trainCode;

    @Size(max = 20)
    @NotNull
    @Column(name = "`start`", nullable = false, length = 20)
    private String start;

    @Size(max = 50)
    @NotNull
    @Column(name = "start_pinyin", nullable = false, length = 50)
    private String startPinyin;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "start_index", nullable = false)
    private Integer startIndex;

    @Size(max = 20)
    @NotNull
    @Column(name = "`end`", nullable = false, length = 20)
    private String end;

    @Size(max = 50)
    @NotNull
    @Column(name = "end_pinyin", nullable = false, length = 50)
    private String endPinyin;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull
    @Column(name = "end_index", nullable = false)
    private Integer endIndex;

    @NotNull
    @Column(name = "ydz", nullable = false)
    private Integer ydz;

    @NotNull
    @Column(name = "ydz_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal ydzPrice;

    @NotNull
    @Column(name = "edz", nullable = false)
    private Integer edz;

    @NotNull
    @Column(name = "edz_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal edzPrice;

    @NotNull
    @Column(name = "rw", nullable = false)
    private Integer rw;

    @NotNull
    @Column(name = "rw_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal rwPrice;

    @NotNull
    @Column(name = "yw", nullable = false)
    private Integer yw;

    @NotNull
    @Column(name = "yw_price", nullable = false, precision = 8, scale = 2)
    private BigDecimal ywPrice;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}