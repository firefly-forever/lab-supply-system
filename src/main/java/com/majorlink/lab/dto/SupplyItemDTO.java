package com.majorlink.lab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 耗材新增/编辑DTO
 */
@Data
public class SupplyItemDTO {
    /** 编辑时传入，新增时不传 */
    private Long id;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotBlank(message = "耗材名称不能为空")
    private String name;

    @NotBlank(message = "耗材编号不能为空")
    private String code;

    private String specification;

    @NotBlank(message = "计量单位不能为空")
    private String unit;

    private String manufacturer;
    private String storageCondition;
    private Integer warningQuantity;
    private Integer isHazardous;
    private String remark;
}
