package com.zhitao.train.business.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "train", schema = "train_business")
public class Train {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @NotNull
    @Column(name = "type", nullable = false)
    private Character type;

    @Size(max = 20)
    @NotNull
    @Column(name = "start", nullable = false, length = 20)
    private String start;

    @Size(max = 50)
    @NotNull
    @Column(name = "start_pinyin", nullable = false, length = 50)
    private String startPinyin;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Size(max = 20)
    @NotNull
    @Column(name = "end", nullable = false, length = 20)
    private String end;

    @Size(max = 50)
    @NotNull
    @Column(name = "end_pinyin", nullable = false, length = 50)
    private String endPinyin;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}