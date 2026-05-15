package com.majorlink.lab.service;

import com.majorlink.lab.dto.SupplyItemDTO;
import com.majorlink.lab.entity.SupplyItem;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 耗材管理服务接口
 */
public interface SupplyItemService {

    /** 分页查询耗材列表 */
    PageInfo<SupplyItem> getList(String keyword, Long categoryId, Integer status, int page, int size);

    /** 根据ID查询耗材详情 */
    SupplyItem getById(Long id);

    /** 查询低库存预警耗材 */
    List<SupplyItem> getLowStockItems();

    /** 新增耗材 */
    void addSupply(SupplyItemDTO dto);

    /** 更新耗材信息 */
    void updateSupply(SupplyItemDTO dto);

    /** 修改耗材状态 */
    void updateStatus(Long id, Integer status);
}
