package com.majorlink.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 出库记录实体
 * <p>
 * out_type 说明：
 * - APPLY: 经审批流的申请领用（最常见）
 * - DIRECT: 管理员直接出库（紧急情况）
 * - SCRAP: 报废处理
 * </p>
 */
@Data
public class StockOutRecord {
    private Long id;
    private Long supplyId;
    /** 出库数量 */
    private Integer quantity;
    /** 出库类型：APPLY/DIRECT/SCRAP */
    private String outType;
    /** 关联申请ID（outType=APPLY时有值） */
    private Long applicationId;
    /** 领用人ID */
    private Long receiverId;
    /** 操作人ID */
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================
    private String supplyName;
    private String supplyCode;
    private String receiverName;
    private String operatorName;
}
