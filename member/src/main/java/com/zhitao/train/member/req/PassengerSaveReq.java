package com.zhitao.train.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerSaveReq {

    /**
     * id
     */
    private Long id;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * 姓名
     */
    @NotBlank(message = "【姓名】不能为空")
    private String name;

    /**
     * 身份证
     */
    @NotBlank(message = "【身份证】不能为空")
    private String idCard;

    /**
     * 旅客类型|枚举[PassengerTypeEnum]
     */
    @NotBlank(message = "【旅客类型】不能为空")
    private String type;

}

