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
import java.time.LocalDate;


@Getter
@Setter
@Entity
@Table(name = "daily_train_seat", schema = "train_business")
public class DailyTrainSeat {
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

    @Size(max = 50)
    @NotNull
    @Column(name = "sell", nullable = false, length = 50)
    private String sell;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;

    @Override
    public String toString() {
        return "DailyTrainSeat{" +
                "id=" + id +
                ", date=" + date +
                ", trainCode='" + trainCode + '\'' +
                ", carriageIndex=" + carriageIndex +
                ", row='" + row + '\'' +
                ", col='" + col + '\'' +
                ", seatType='" + seatType + '\'' +
                ", carriageSeatIndex=" + carriageSeatIndex +
                ", sell='" + sell + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}