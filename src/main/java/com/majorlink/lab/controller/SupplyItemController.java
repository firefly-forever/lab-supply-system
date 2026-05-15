package com.majorlink.lab.controller;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.config.UserContext;
import com.majorlink.lab.dto.SupplyItemDTO;
import com.majorlink.lab.entity.SupplyItem;
import com.majorlink.lab.service.SupplyItemService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 耗材管理接口
 *
 * GET    /api/supply/list          查询耗材列表（分页）
 * GET    /api/supply/{id}          查询耗材详情
 * POST   /api/supply               新增耗材（管理员）
 * PUT    /api/supply               更新耗材（管理员）
 * PUT    /api/supply/{id}/status   修改状态（管理员）
 */
@RestController
@RequestMapping("/supply")
@RequiredArgsConstructor
public class SupplyItemController {

    private final SupplyItemService supplyItemService;

    /**
     * 查询耗材列表（分页）
     *
     * @param keyword    关键词（耗材名/编号/厂家，模糊搜索）
     * @param categoryId 分类ID（可选过滤）
     * @param status     状态（1正常 0停用，不传查全部）
     * @param page       页码（默认1）
     * @param size       每页条数（默认10）
     */
    @GetMapping("/list")
    public Result<PageInfo<SupplyItem>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(supplyItemService.getList(keyword, categoryId, status, page, size));
    }

    /** 查询耗材详情 */
    @GetMapping("/{id}")
    public Result<SupplyItem> getById(@PathVariable Long id) {
        return Result.success(supplyItemService.getById(id));
    }

    /**
     * 新增耗材（仅管理员）
     * 初始库存为0，通过入库操作增加
     */
    @PostMapping
    public Result<String> add(@Valid @RequestBody SupplyItemDTO dto) {
        checkAdminPermission();
        supplyItemService.addSupply(dto);
        return Result.success("耗材添加成功");
    }

    /** 更新耗材信息（仅管理员） */
    @PutMapping
    public Result<String> update(@Valid @RequestBody SupplyItemDTO dto) {
        checkAdminPermission();
        supplyItemService.updateSupply(dto);
        return Result.success("耗材信息更新成功");
    }

    /**
     * 修改耗材状态（仅管理员）
     *
     * @param id     耗材ID
     * @param status 1=启用 0=停用
     */
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        checkAdminPermission();
        supplyItemService.updateStatus(id, status);
        return Result.success(status == 1 ? "已启用" : "已停用");
    }

    /** 权限校验：仅管理员可操作耗材基础信息 */
    private void checkAdminPermission() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "仅管理员可执行此操作");
        }
    }
}
