package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.SupplyItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 耗材数据访问层
 */
@Mapper
public interface SupplyItemMapper {

    /** 查询所有耗材（支持按名称/编号/分类模糊搜索） */
    List<SupplyItem> selectList(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("status") Integer status);

    /** 根据ID查询（包含分类名称） */
    SupplyItem selectById(@Param("id") Long id);

    /** 根据编号查询（用于编号唯一性校验） */
    SupplyItem selectByCode(@Param("code") String code);

    /**
     * 查询低库存预警耗材
     * quantity <= warning_quantity 的耗材列表，用于首页预警
     */
    List<SupplyItem> selectLowStockItems();

    /** 新增耗材 */
    int insert(SupplyItem item);

    /** 更新耗材基本信息 */
    int update(SupplyItem item);

    /**
     * 增加库存（入库时调用）
     * 使用 quantity + delta 的原子更新方式，避免并发问题
     *
     * @param id    耗材ID
     * @param delta 增加数量（正数）
     */
    int increaseQuantity(@Param("id") Long id, @Param("delta") Integer delta);

    /**
     * 减少库存（出库时调用）
     * WHERE quantity >= delta 确保不出现负库存（乐观锁兜底）
     *
     * @param id    耗材ID
     * @param delta 减少数量（正数）
     * @return 影响行数，0表示库存不足更新失败
     */
    int decreaseQuantity(@Param("id") Long id, @Param("delta") Integer delta);

    /** 修改状态 */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
