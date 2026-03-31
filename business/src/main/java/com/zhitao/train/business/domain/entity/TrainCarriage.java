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
@Table(name = "train_carriage", schema = "train_business")
public class TrainCarriage {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "train_code", nullable = false, length = 20)
    private String trainCode;

    @NotNull
    @Column(name = "`index`", nullable = false)
    private Integer index;

    @NotNull
    @Column(name = "seat_type", nullable = false)
    private String seatType;

    @NotNull
    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @NotNull
    @Column(name = "row_count", nullable = false)
    private Integer rowCount;

    @NotNull
    @Column(name = "col_count", nullable = false)
    private Integer colCount;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}