package com.majorlink.lab.controller;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.config.UserContext;
import com.majorlink.lab.dto.ApplyDTO;
import com.majorlink.lab.dto.ApproveDTO;
import com.majorlink.lab.dto.StockInDTO;
import com.majorlink.lab.entity.StockInRecord;
import com.majorlink.lab.entity.StockOutRecord;
import com.majorlink.lab.entity.SupplyApplication;
import com.majorlink.lab.service.StockService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存操作接口（入库 / 申请领用 / 审批 / 出库记录）
 *
 * POST /api/stock/in                   执行入库（管理员）
 * GET  /api/stock/in/list              查询入库记录（分页）
 * POST /api/stock/apply                提交领用申请（学生）
 * POST /api/stock/approve              审批申请（教师/管理员）
 * GET  /api/stock/application/list     查询申请列表
 * GET  /api/stock/out/list             查询出库记录（分页）
 */
@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // ========== 入库操作（仅管理员） ==========

    /**
     * 执行入库
     * - 写入库记录
     * - 更新耗材库存（quantity += 入库数量）
     */
    @PostMapping("/in")
    public Result<String> stockIn(@Valid @RequestBody StockInDTO dto) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "仅管理员可执行入库操作");
        }
        stockService.stockIn(dto);
        return Result.success("入库成功");
    }

    /** 查询入库记录（分页），可按耗材ID筛选 */
    @GetMapping("/in/list")
    public Result<PageInfo<StockInRecord>> getStockInList(
            @RequestParam(required = false) Long supplyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(stockService.getStockInList(supplyId, page, size));
    }

    // ========== 领用申请（审批流入口，学生操作） ==========

    /**
     * 学生提交领用申请
     * 申请状态初始为 PENDING，等待教师审批
     */
    @PostMapping("/apply")
    public Result<String> apply(@Valid @RequestBody ApplyDTO dto) {
        // 角色限制：只有学生才能通过此接口提交申请
        // 教师/管理员如有需要可直接入库，不走审批流
        if ("ADMIN".equals(UserContext.getCurrentRole())) {
            throw new BusinessException("管理员请使用直接入库/出库接口");
        }
        stockService.applyForSupply(dto);
        return Result.success("申请提交成功，请等待教师审批");
    }

    /**
     * 审批领用申请（教师/管理员）
     * <p>
     * 审批通过时：
     *   1. 状态 PENDING -> APPROVED
     *   2. 扣减库存
     *   3. 生成出库记录
     * 审批拒绝时：
     *   1. 状态 PENDING -> REJECTED（填写拒绝原因）
     *   2. 库存不变
     * </p>
     */
    @PostMapping("/approve")
    public Result<String> approve(@Valid @RequestBody ApproveDTO dto) {
        if (!UserContext.isTeacherOrAdmin()) {
            throw new BusinessException(403, "仅教师或管理员可执行审批操作");
        }
        stockService.approveApplication(dto);
        String msg = Boolean.TRUE.equals(dto.getApproved()) ? "申请已通过，耗材已出库" : "申请已拒绝";
        return Result.success(msg);
    }

    // ========== 申请列表查询 ==========

    /**
     * 查询领用申请列表（分页）
     *
     * @param onlyMine true=只查自己的申请（学生端）；false=查全部（教师/管理员端）
     * @param status   状态筛选：PENDING / APPROVED / REJECTED（不传查全部）
     */
    @GetMapping("/application/list")
    public Result<PageInfo<SupplyApplication>> getApplicationList(
            @RequestParam(defaultValue = "false") boolean onlyMine,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 学生强制只能看自己的申请（防止越权）
        if ("STUDENT".equals(UserContext.getCurrentRole())) {
            onlyMine = true;
        }
        return Result.success(stockService.getApplicationList(onlyMine, status, page, size));
    }

    // ========== 出库记录查询 ==========

    /** 查询出库记录（分页），可按耗材ID筛选 */
    @GetMapping("/out/list")
    public Result<PageInfo<StockOutRecord>> getStockOutList(
            @RequestParam(required = false) Long supplyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(stockService.getStockOutList(supplyId, page, size));
    }
}
