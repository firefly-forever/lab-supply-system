package com.majorlink.lab.service;

import com.majorlink.lab.dto.ApplyDTO;
import com.majorlink.lab.dto.ApproveDTO;
import com.majorlink.lab.dto.StockInDTO;
import com.majorlink.lab.entity.StockInRecord;
import com.majorlink.lab.entity.StockOutRecord;
import com.majorlink.lab.entity.SupplyApplication;
import com.github.pagehelper.PageInfo;

/**
 * 库存操作服务接口（入库、出库、申请审批）
 */
public interface StockService {

    // ==================== 入库 ====================

    /** 执行入库（管理员操作） */
    void stockIn(StockInDTO dto);

    /** 查询入库记录（分页） */
    PageInfo<StockInRecord> getStockInList(Long supplyId, int page, int size);

    // ==================== 领用申请（审批流） ====================

    /**
     * 学生提交领用申请
     * 申请后状态为 PENDING，等待教师审批
     */
    void applyForSupply(ApplyDTO dto);

    /**
     * 教师/管理员审批申请
     * <p>
     * 审批通过（approved=true）：
     *   1. 将申请状态更新为 APPROVED
     *   2. 扣减库存（decreaseQuantity）
     *   3. 生成出库记录
     * 拒绝（approved=false）：
     *   1. 将申请状态更新为 REJECTED，记录拒绝原因
     *   2. 不影响库存
     * </p>
     */
    void approveApplication(ApproveDTO dto);

    /**
     * 查询申请列表
     *
     * @param onlyMine  true=只看自己的申请（学生用），false=看全部（教师/管理员）
     * @param status    状态筛选
     */
    PageInfo<SupplyApplication> getApplicationList(boolean onlyMine, String status, int page, int size);

    // ==================== 出库记录 ====================

    /** 查询出库记录（分页） */
    PageInfo<StockOutRecord> getStockOutList(Long supplyId, int page, int size);
}
