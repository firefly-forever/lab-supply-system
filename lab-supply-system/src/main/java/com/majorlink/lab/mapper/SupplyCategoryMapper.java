package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.SupplyCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 耗材分类数据访问层
 */
@Mapper
public interface SupplyCategoryMapper {

    /** 查询所有分类 */
    List<SupplyCategory> selectAll();

    /** 根据ID查询 */
    SupplyCategory selectById(@Param("id") Long id);

    /** 新增分类 */
    int insert(SupplyCategory category);
}
