package com.zhitao.train.member.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(name = "member", schema = "train_member")
public class Member {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @jakarta.validation.constraints.Size(max = 11)
    @Column(name = "mobile", length = 11)
    private String mobile;


    public Member() {
    }
    public Member(Long id, String mobile) {
        this.id = id;
        this.mobile = mobile;
    }
}