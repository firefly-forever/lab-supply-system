package com.majorlink.lab.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入库记录实体
 */
@Data
public class StockInRecord {
    private Long id;
    private Long supplyId;
    /** 批次号 */
    private String batchNo;
    /** 本次入库数量 */
    private Integer quantity;
    /** 单价（元） */
    private BigDecimal unitPrice;
    /** 总价（元） */
    private BigDecimal totalPrice;
    /** 生产日期 */
    private LocalDate productionDate;
    /** 有效期至 */
    private LocalDate expiryDate;
    /** 供应商 */
    private String supplier;
    /** 操作人ID */
    private Long operatorId;
    private String remark;
    private LocalDateTime createTime;

    // ==================== 非数据库字段 ====================

    /** 耗材名称（关联查询） */
    private String supplyName;
    /** 耗材编号（关联查询） */
    private String supplyCode;
    /** 操作人姓名（关联查询） */
    private String operatorName;
}
