package com.majorlink.lab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批请求DTO（教师操作）
 * approved=true 表示通过，false 表示拒绝
 */
@Data
public class ApproveDTO {
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;

    @NotNull(message = "审批结果不能为空")
    private Boolean approved;

    /** 审批意见（通过时填写，可选） */
    private String approveRemark;

    /** 拒绝原因（拒绝时必填） */
    private String rejectReason;
}
