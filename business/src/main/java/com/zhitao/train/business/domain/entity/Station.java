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
@Table(name = "station", schema = "train_business")
public class Station {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Size(max = 50)
    @NotNull
    @Column(name = "name_pinyin", nullable = false, length = 50)
    private String namePinyin;

    @Size(max = 50)
    @NotNull
    @Column(name = "name_py", nullable = false, length = 50)
    private String namePy;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}