package com.zhitao.train.member.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    private String token;
}
