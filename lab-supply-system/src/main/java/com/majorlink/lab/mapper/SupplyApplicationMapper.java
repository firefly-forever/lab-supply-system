package com.majorlink.lab.mapper;

import com.majorlink.lab.entity.SupplyApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 领用申请数据访问层
 */
@Mapper
public interface SupplyApplicationMapper {

    /**
     * 查询申请列表
     *
     * @param applicantId 申请人ID（学生查自己的申请时传入；管理员/教师查所有时传null）
     * @param status      申请状态筛选（传null查所有状态）
     */
    List<SupplyApplication> selectList(@Param("applicantId") Long applicantId,
                                       @Param("status") String status);

    /** 根据ID查询（包含关联的耗材名、申请人名、审批人名） */
    SupplyApplication selectById(@Param("id") Long id);

    /** 查询待审批数量（用于首页统计） */
    int countPending();

    /** 新增申请 */
    int insert(SupplyApplication application);

    /**
     * 审批申请（更新状态、审批人、审批时间、审批意见/拒绝原因）
     * 只更新审批相关字段，其他字段不变
     */
    int approve(SupplyApplication application);
}
