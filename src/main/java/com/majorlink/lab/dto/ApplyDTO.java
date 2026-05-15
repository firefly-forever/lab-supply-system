package com.majorlink.lab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 领用申请DTO（学生提交）
 */
@Data
public class ApplyDTO {
    @NotNull(message = "耗材ID不能为空")
    private Long supplyId;

    @NotNull(message = "申请数量不能为空")
    @Min(value = 1, message = "申请数量必须大于0")
    private Integer quantity;

    @NotBlank(message = "用途说明不能为空")
    private String purpose;
}
