package com.majorlink.lab.service.impl;

import com.majorlink.lab.common.exception.BusinessException;
import com.majorlink.lab.common.result.ResultCode;
import com.majorlink.lab.dto.SupplyItemDTO;
import com.majorlink.lab.entity.SupplyItem;
import com.majorlink.lab.mapper.SupplyItemMapper;
import com.majorlink.lab.service.SupplyItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 耗材管理服务实现
 */
@Service
@RequiredArgsConstructor
public class SupplyItemServiceImpl implements SupplyItemService {

    private final SupplyItemMapper supplyItemMapper;

    @Override
    public PageInfo<SupplyItem> getList(String keyword, Long categoryId, Integer status,
                                         int page, int size) {
        // PageHelper 分页（需在查询前调用，只对紧接着的第一条SQL生效）
        PageHelper.startPage(page, size);
        List<SupplyItem> list = supplyItemMapper.selectList(keyword, categoryId, status);
        return new PageInfo<>(list);
    }

    @Override
    public SupplyItem getById(Long id) {
        SupplyItem item = supplyItemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(ResultCode.SUPPLY_NOT_FOUND);
        }
        return item;
    }

    @Override
    public List<SupplyItem> getLowStockItems() {
        return supplyItemMapper.selectLowStockItems();
    }

    @Override
    public void addSupply(SupplyItemDTO dto) {
        // 校验编号唯一性
        SupplyItem existing = supplyItemMapper.selectByCode(dto.getCode());
        if (existing != null) {
            throw new BusinessException(ResultCode.SUPPLY_CODE_DUPLICATE);
        }

        SupplyItem item = new SupplyItem();
        copyDtoToEntity(dto, item);
        item.setQuantity(0); // 新增耗材初始库存为0，通过入库操作增加
        supplyItemMapper.insert(item);
    }

    @Override
    public void updateSupply(SupplyItemDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("编辑时必须传入耗材ID");
        }
        // 确认耗材存在
        getById(dto.getId());

        SupplyItem item = new SupplyItem();
        copyDtoToEntity(dto, item);
        item.setId(dto.getId());
        supplyItemMapper.update(item);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        getById(id); // 存在性校验
        supplyItemMapper.updateStatus(id, status);
    }

    /** DTO -> Entity 字段复制 */
    private void copyDtoToEntity(SupplyItemDTO dto, SupplyItem item) {
        item.setCategoryId(dto.getCategoryId());
        item.setName(dto.getName());
        item.setCode(dto.getCode());
        item.setSpecification(dto.getSpecification());
        item.setUnit(dto.getUnit());
        item.setManufacturer(dto.getManufacturer());
        item.setStorageCondition(dto.getStorageCondition());
        item.setWarningQuantity(dto.getWarningQuantity() != null ? dto.getWarningQuantity() : 10);
        item.setIsHazardous(dto.getIsHazardous() != null ? dto.getIsHazardous() : 0);
        item.setRemark(dto.getRemark());
    }
}
