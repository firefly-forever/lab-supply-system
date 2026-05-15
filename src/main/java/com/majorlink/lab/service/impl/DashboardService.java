package com.majorlink.lab.service.impl;

import com.majorlink.lab.entity.SupplyItem;
import com.majorlink.lab.mapper.SupplyApplicationMapper;
import com.majorlink.lab.mapper.SupplyItemMapper;
import com.majorlink.lab.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 首页仪表盘数据服务
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SupplyItemMapper supplyItemMapper;
    private final SupplyApplicationMapper applicationMapper;

    /**
     * 获取首页仪表盘数据
     * <p>
     * 核心亮点：低库存预警，quantity <= warning_quantity 的耗材会在首页醒目提示
     * </p>
     */
    public DashboardVO getDashboardData() {
        DashboardVO vo = new DashboardVO();

        // 1. 耗材总种数（只统计启用状态）
        List<SupplyItem> allItems = supplyItemMapper.selectList(null, null, 1);
        vo.setTotalSupplyCount(allItems.size());

        // 2. 待审批申请数量
        vo.setPendingApplicationCount(applicationMapper.countPending());

        // 3. 低库存预警
        List<SupplyItem> lowStockItems = supplyItemMapper.selectLowStockItems();
        vo.setLowStockItems(lowStockItems);
        vo.setLowStockCount(lowStockItems.size());

        // 今日入库/出库计数（简化处理，可后续扩展）
        vo.setTodayStockInCount(0);
        vo.setTodayStockOutCount(0);

        return vo;
    }
}
