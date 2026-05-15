package com.majorlink.lab.controller;

import com.majorlink.lab.common.result.Result;
import com.majorlink.lab.service.impl.DashboardService;
import com.majorlink.lab.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页仪表盘接口
 * GET /api/dashboard
 * 返回：耗材总数、待审批数、低库存预警列表
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public Result<DashboardVO> getDashboard() {
        return Result.success(dashboardService.getDashboardData());
    }
}
