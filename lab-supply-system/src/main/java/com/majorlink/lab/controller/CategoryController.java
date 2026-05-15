package com.majorlink.lab.controller;

import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.entity.SupplyCategory;
import com.majorlink.lab.mapper.SupplyCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 耗材分类接口
 * GET /api/category/list  获取所有分类（用于前端下拉选择）
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final SupplyCategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<List<SupplyCategory>> list() {
        return Result.success(categoryMapper.selectAll());
    }
}
