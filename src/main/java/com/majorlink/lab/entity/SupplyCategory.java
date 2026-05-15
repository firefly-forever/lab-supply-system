package com.majorlink.lab.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 耗材分类实体
 */
@Data
public class SupplyCategory {
    private Long id;
    /** 父分类ID，0表示顶级分类 */
    private Long parentId;
    private String name;
    private String code;
    /** 排序（升序） */
    private Integer sort;
    private String remark;
    private LocalDateTime createTime;
}
