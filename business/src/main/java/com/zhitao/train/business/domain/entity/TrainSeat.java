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

@Getter
@Setter
@Entity
@Table(name = "train_seat", schema = "train_business")
public class TrainSeat {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "train_code", nullable = false, length = 20)
    private String trainCode;

    @NotNull
    @Column(name = "carriage_index", nullable = false)
    private Integer carriageIndex;

    @Size(max = 2)
    @NotNull
    @Column(name = "`row`", nullable = false, length = 2)
    private String row;

    @NotNull
    @Column(name = "col", nullable = false)
    private String col;

    @NotNull
    @Column(name = "seat_type", nullable = false)
    private String seatType;

    @NotNull
    @Column(name = "carriage_seat_index", nullable = false)
    private Integer carriageSeatIndex;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}