package com.majorlink.lab.vo;

import com.majorlink.lab.entity.SupplyItem;
import lombok.Data;

import java.util.List;

/**
 * 首页仪表盘数据VO
 * <p>
 * 包含核心统计数字 + 低库存预警列表，
 * 便于前端首页一次请求加载所有需要的数据
 * </p>
 */
@Data
public class DashboardVO {
    /** 耗材总种数 */
    private Integer totalSupplyCount;
    /** 待审批申请数（教师/管理员首页重点关注） */
    private Integer pendingApplicationCount;
    /** 低库存预警数量 */
    private Integer lowStockCount;
    /** 低库存预警列表（详细信息，用于首页列表展示） */
    private List<SupplyItem> lowStockItems;
    /** 今日入库次数 */
    private Integer todayStockInCount;
    /** 今日出库次数 */
    private Integer todayStockOutCount;
}
