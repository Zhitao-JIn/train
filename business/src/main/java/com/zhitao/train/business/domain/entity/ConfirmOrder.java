package com.zhitao.train.business.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "confirm_order", schema = "train_business")
public class ConfirmOrder {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 20)
    @NotNull
    @Column(name = "train_code", nullable = false, length = 20)
    private String trainCode;

    @Size(max = 20)
    @NotNull
    @Column(name = "start", nullable = false, length = 20)
    private String start;

    @Size(max = 20)
    @NotNull
    @Column(name = "end", nullable = false, length = 20)
    private String end;

    @NotNull
    @Column(name = "daily_train_ticket_id", nullable = false)
    private Long dailyTrainTicketId;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tickets", nullable = false)
    private Map<String, Object> tickets;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;


}