package com.majorlink.lab.service.impl;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.ResultCode;
import com.majorlink.lab.config.UserContext;
import com.majorlink.lab.dto.ApplyDTO;
import com.majorlink.lab.dto.ApproveDTO;
import com.majorlink.lab.dto.StockInDTO;
import com.majorlink.lab.entity.*;
import com.majorlink.lab.mapper.*;
import com.majorlink.lab.service.StockService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存操作服务实现
 * <p>
 * 核心业务逻辑：入库、领用申请、审批出库。
 * 注意：涉及库存变更的方法均加 @Transactional，
 * 保证审批→扣库存→写出库记录的原子性。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final SupplyItemMapper supplyItemMapper;
    private final StockInRecordMapper stockInRecordMapper;
    private final StockOutRecordMapper stockOutRecordMapper;
    private final SupplyApplicationMapper applicationMapper;

    // ========== 入库 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(StockInDTO dto) {
        // 1. 校验耗材存在
        SupplyItem item = supplyItemMapper.selectById(dto.getSupplyId());
        if (item == null) {
            throw new BusinessException(ResultCode.SUPPLY_NOT_FOUND);
        }

        // 2. 写入库记录
        StockInRecord record = new StockInRecord();
        record.setSupplyId(dto.getSupplyId());
        record.setBatchNo(dto.getBatchNo());
        record.setQuantity(dto.getQuantity());
        record.setUnitPrice(dto.getUnitPrice());
        // 如果填了单价，自动计算总价
        if (dto.getUnitPrice() != null) {
            record.setTotalPrice(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }
        record.setProductionDate(dto.getProductionDate());
        record.setExpiryDate(dto.getExpiryDate());
        record.setSupplier(dto.getSupplier());
        record.setOperatorId(UserContext.getCurrentUserId());
        record.setRemark(dto.getRemark());
        stockInRecordMapper.insert(record);

        // 3. 更新库存（quantity + delta）
        supplyItemMapper.increaseQuantity(dto.getSupplyId(), dto.getQuantity());

        log.info("入库成功：耗材[{}]，数量+{}，操作人[{}]",
                item.getName(), dto.getQuantity(), UserContext.getCurrentUsername());
    }

    @Override
    public PageInfo<StockInRecord> getStockInList(Long supplyId, int page, int size) {
        PageHelper.startPage(page, size);
        List<StockInRecord> list = stockInRecordMapper.selectList(supplyId);
        return new PageInfo<>(list);
    }

    // ========== 领用申请（审批流核心） ==========

    @Override
    public void applyForSupply(ApplyDTO dto) {
        // 1. 校验耗材存在且正常
        SupplyItem item = supplyItemMapper.selectById(dto.getSupplyId());
        if (item == null || item.getStatus() == 0) {
            throw new BusinessException(ResultCode.SUPPLY_NOT_FOUND);
        }

        // 2. 前置库存检查（注意：这里只是提示，实际扣减在审批通过时）
        //    申请数量超过当前库存时给出警告（不阻止，教师审批时再做最终校验）
        if (dto.getQuantity() > item.getQuantity()) {
            throw new BusinessException("申请数量（" + dto.getQuantity() + "）超过当前库存（"
                    + item.getQuantity() + "），请调整申请数量");
        }

        // 3. 写申请记录，状态为 PENDING
        SupplyApplication application = new SupplyApplication();
        application.setSupplyId(dto.getSupplyId());
        application.setQuantity(dto.getQuantity());
        application.setPurpose(dto.getPurpose());
        application.setApplicantId(UserContext.getCurrentUserId());
        application.setStatus("PENDING");
        applicationMapper.insert(application);

        log.info("领用申请提交：耗材[{}]，申请数量{}，申请人[{}]",
                item.getName(), dto.getQuantity(), UserContext.getCurrentUsername());
    }

    /**
     * 审批申请（核心审批流逻辑）
     * <p>
     * 使用 @Transactional 保证以下操作的原子性：
     * - 更新申请状态
     * - 扣减库存（仅审批通过时）
     * - 写出库记录（仅审批通过时）
     * 任何步骤失败，事务整体回滚。
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveApplication(ApproveDTO dto) {
        // 1. 查询申请记录
        SupplyApplication application = applicationMapper.selectById(dto.getApplicationId());
        if (application == null) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_FOUND);
        }

        // 2. 状态校验：只有 PENDING 状态才能审批
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException(ResultCode.APPLICATION_NOT_PENDING);
        }

        // 3. 拒绝时必须填写原因
        if (!dto.getApproved() && !StringUtils.hasText(dto.getRejectReason())) {
            throw new BusinessException("拒绝申请时必须填写拒绝原因");
        }

        Long currentUserId = UserContext.getCurrentUserId();

        if (dto.getApproved()) {
            // ===== 审批通过 =====

            // 3a. 再次校验库存（防止并发情况下库存变化）
            SupplyItem item = supplyItemMapper.selectById(application.getSupplyId());
            if (item == null) {
                throw new BusinessException(ResultCode.SUPPLY_NOT_FOUND);
            }
            if (item.getQuantity() < application.getQuantity()) {
                throw new BusinessException("当前库存不足（剩余" + item.getQuantity()
                        + "），无法审批通过。请先入库或告知申请人调整数量。");
            }

            // 3b. 更新申请状态为 APPROVED
            application.setStatus("APPROVED");
            application.setApproverId(currentUserId);
            application.setApproveRemark(dto.getApproveRemark());
            applicationMapper.approve(application);

            // 3c. 扣减库存（原子SQL，quantity >= delta 时才成功）
            int affected = supplyItemMapper.decreaseQuantity(application.getSupplyId(), application.getQuantity());
            if (affected == 0) {
                // 扣减失败说明并发情况下库存不足，抛异常触发事务回滚
                throw new BusinessException(ResultCode.SUPPLY_STOCK_NOT_ENOUGH);
            }

            // 3d. 生成出库记录
            StockOutRecord outRecord = new StockOutRecord();
            outRecord.setSupplyId(application.getSupplyId());
            outRecord.setQuantity(application.getQuantity());
            outRecord.setOutType("APPLY");
            outRecord.setApplicationId(application.getId());
            outRecord.setReceiverId(application.getApplicantId()); // 领用人=申请人
            outRecord.setOperatorId(currentUserId);                // 操作人=审批人
            outRecord.setRemark("申请审批出库，申请ID: " + application.getId());
            stockOutRecordMapper.insert(outRecord);

            log.info("申请[{}]审批通过：耗材[{}]，出库数量{}，审批人[{}]",
                    application.getId(), application.getSupplyName(),
                    application.getQuantity(), UserContext.getCurrentUsername());

        } else {
            // ===== 审批拒绝 =====
            application.setStatus("REJECTED");
            application.setApproverId(currentUserId);
            application.setRejectReason(dto.getRejectReason());
            applicationMapper.approve(application);

            log.info("申请[{}]被拒绝，原因：{}，审批人[{}]",
                    application.getId(), dto.getRejectReason(), UserContext.getCurrentUsername());
        }
    }

    @Override
    public PageInfo<SupplyApplication> getApplicationList(boolean onlyMine, String status,
                                                           int page, int size) {
        PageHelper.startPage(page, size);
        // 学生只看自己的，教师/管理员看全部
        Long applicantId = onlyMine ? UserContext.getCurrentUserId() : null;
        List<SupplyApplication> list = applicationMapper.selectList(applicantId, status);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<StockOutRecord> getStockOutList(Long supplyId, int page, int size) {
        PageHelper.startPage(page, size);
        List<StockOutRecord> list = stockOutRecordMapper.selectList(supplyId);
        return new PageInfo<>(list);
    }
}
