package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.StockOutRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出库记录数据访问层
 */
@Mapper
public interface StockOutRecordMapper {

    /** 查询出库记录列表（可按耗材ID筛选） */
    List<StockOutRecord> selectList(@Param("supplyId") Long supplyId);

    /** 新增出库记录 */
    int insert(StockOutRecord record);
}
