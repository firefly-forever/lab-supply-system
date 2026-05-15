package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.StockInRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 入库记录数据访问层
 */
@Mapper
public interface StockInRecordMapper {

    /** 查询入库记录列表（可按耗材ID筛选） */
    List<StockInRecord> selectList(@Param("supplyId") Long supplyId);

    /** 根据ID查询 */
    StockInRecord selectById(@Param("id") Long id);

    /** 新增入库记录 */
    int insert(StockInRecord record);
}
