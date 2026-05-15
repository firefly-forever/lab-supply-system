package com.majorlink.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 耗材/试剂主表实体
 */
@Data
public class SupplyItem {
    private Long id;
    private Long categoryId;
    /** 耗材名称 */
    private String name;
    /** 耗材编号（唯一） */
    private String code;
    /** 规格型号 */
    private String specification;
    /** 计量单位 */
    private String unit;
    /** 生产厂家 */
    private String manufacturer;
    /** 存储条件 */
    private String storageCondition;
    /** 当前库存数量（核心字段，入库/出库时实时更新） */
    private Integer quantity;
    /** 低库存预警阈值（低于此值时在首页显示预警） */
    private Integer warningQuantity;
    /** 是否危险品：1是 0否 */
    private Integer isHazardous;
    /** 状态：1正常 0停用 */
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ==================== 非数据库字段，查询时关联填充 ====================

    /** 分类名称（关联 supply_category） */
    private String categoryName;

    /**
     * 判断当前库存是否低于预警阈值
     *
     * @return true表示库存不足，需要预警
     */
    public boolean isLowStock() {
        return this.quantity != null && this.warningQuantity != null
                && this.quantity <= this.warningQuantity;
    }
}
