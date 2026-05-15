package com.majorlink.lab.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 入库请求DTO
 */
@Data
public class StockInDTO {
    @NotNull(message = "耗材ID不能为空")
    private Long supplyId;

    @NotNull(message = "入库数量不能为空")
    @Min(value = 1, message = "入库数量必须大于0")
    private Integer quantity;

    private String batchNo;
    private BigDecimal unitPrice;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private String supplier;
    private String remark;
}
