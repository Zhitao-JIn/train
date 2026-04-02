package com.zhitao.train.member.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ticket", schema = "train_member")
public class Ticket {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Size(max = 20)
    @Column(name = "passenger_name", length = 20)
    private String passengerName;

    @NotNull
    @Column(name = "train_date", nullable = false)
    private LocalDate date;

    @Size(max = 20)
    @NotNull
    @Column(name = "train_code", nullable = false, length = 20)
    private String trainCode;

    @NotNull
    @Column(name = "carriage_index", nullable = false)
    private Integer carriageIndex;

    @Size(max = 2)
    @NotNull
    @Column(name = "`seat_row`", nullable = false, length = 2)
    private String row;

    @NotNull
    @Column(name = "seat_col", nullable = false)
    private String col;

    @Size(max = 20)
    @NotNull
    @Column(name = "`start_station`", nullable = false, length = 20)
    private String start;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Size(max = 20)
    @NotNull
    @Column(name = "`end_station`", nullable = false, length = 20)
    private String end;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @NotNull
    @Column(name = "seat_type", nullable = false)
    private String seatType;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}