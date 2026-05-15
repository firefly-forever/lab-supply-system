package com.majorlink.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 领用申请实体
 * <p>
 * 审批流核心表：学生提交申请(PENDING) → 教师审批(APPROVED/REJECTED)
 * 审批通过后，Service层自动触发出库逻辑并扣减库存
 * </p>
 */
@Data
public class SupplyApplication {
    private Long id;
    /** 申请的耗材ID */
    private Long supplyId;
    /** 申请数量 */
    private Integer quantity;
    /** 用途说明（实验名称/目的） */
    private String purpose;
    /** 申请人ID（学生） */
    private Long applicantId;
    /** 审批人ID（教师，审批后填入） */
    private Long approverId;
    /**
     * 申请状态（状态机）
     * PENDING → APPROVED/REJECTED
     */
    private String status;
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    /** 审批意见（通过时填写） */
    private String approveRemark;
    /** 拒绝原因（拒绝时必填） */
    private String rejectReason;

    // ==================== 非数据库字段 ====================

    /** 耗材名称 */
    private String supplyName;
    /** 耗材编号 */
    private String supplyCode;
    /** 申请人姓名 */
    private String applicantName;
    /** 申请人院系 */
    private String applicantDepartment;
    /** 审批人姓名 */
    private String approverName;
}
