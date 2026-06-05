package com.lims.sampling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.sampling.dto.EquipmentQuery;
import com.lims.sampling.dto.EquipmentSaveDTO;
import com.lims.sampling.entity.SmpEquipment;
import com.lims.sampling.mapper.SmpEquipmentMapper;
import com.lims.sampling.service.EquipmentService;
import com.lims.sampling.vo.EquipmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl extends ServiceImpl<SmpEquipmentMapper, SmpEquipment> implements EquipmentService {

    @Autowired
    private CodeGenerator codeGenerator;

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_REPAIR = 2;
    private static final int STATUS_SCRAP = 3;

    private static final int BORROW_STATUS_AVAILABLE = 0;
    private static final int BORROW_STATUS_BORROWED = 1;

    private String getEquipmentTypeName(String type) {
        if (type == null) return "";
        switch (type) {
            case "1": return "采样设备";
            case "2": return "监测设备";
            case "3": return "样品容器";
            default: return "";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "停用";
            case 1: return "正常";
            case 2: return "维修中";
            case 3: return "已报废";
            default: return "";
        }
    }

    private String getBorrowStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "未领用";
            case 1: return "已领用";
            default: return "";
        }
    }

    @Override
    public PageResult<EquipmentVO> selectPage(EquipmentQuery query) {
        LambdaQueryWrapper<SmpEquipment> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(SmpEquipment::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(SmpEquipment::getEquipmentName, query.getEquipmentName());
        }
        if (StrUtil.isNotBlank(query.getEquipmentType())) {
            wrapper.eq(SmpEquipment::getEquipmentType, query.getEquipmentType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SmpEquipment::getStatus, query.getStatus());
        }
        if (query.getBorrowStatus() != null) {
            wrapper.eq(SmpEquipment::getBorrowStatus, query.getBorrowStatus());
        }
        if (StrUtil.isNotBlank(query.getStorageLocation())) {
            wrapper.like(SmpEquipment::getStorageLocation, query.getStorageLocation());
        }
        wrapper.orderByDesc(SmpEquipment::getCreateTime);

        Page<SmpEquipment> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SmpEquipment> pageResult = this.page(page, wrapper);

        IPage<EquipmentVO> voPage = pageResult.convert(equipment -> {
            EquipmentVO vo = BeanUtil.copyProperties(equipment, EquipmentVO.class);
            vo.setEquipmentTypeName(getEquipmentTypeName(equipment.getEquipmentType()));
            vo.setStatusName(getStatusName(equipment.getStatus()));
            vo.setBorrowStatusName(getBorrowStatusName(equipment.getBorrowStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public List<EquipmentVO> getAvailableEquipments(String equipmentType) {
        LambdaQueryWrapper<SmpEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmpEquipment::getStatus, STATUS_NORMAL)
                .eq(SmpEquipment::getBorrowStatus, BORROW_STATUS_AVAILABLE);
        if (StrUtil.isNotBlank(equipmentType)) {
            wrapper.eq(SmpEquipment::getEquipmentType, equipmentType);
        }
        wrapper.orderByAsc(SmpEquipment::getEquipmentNo);

        List<SmpEquipment> list = this.list(wrapper);
        return list.stream().map(equipment -> {
            EquipmentVO vo = BeanUtil.copyProperties(equipment, EquipmentVO.class);
            vo.setEquipmentTypeName(getEquipmentTypeName(equipment.getEquipmentType()));
            vo.setStatusName(getStatusName(equipment.getStatus()));
            vo.setBorrowStatusName(getBorrowStatusName(equipment.getBorrowStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEquipment(EquipmentSaveDTO dto) {
        SmpEquipment equipment = BeanUtil.copyProperties(dto, SmpEquipment.class);
        if (StrUtil.isBlank(equipment.getEquipmentNo())) {
            equipment.setEquipmentNo(codeGenerator.generateEquipmentNo());
        }
        if (equipment.getStatus() == null) {
            equipment.setStatus(STATUS_NORMAL);
        }
        if (equipment.getBorrowStatus() == null) {
            equipment.setBorrowStatus(BORROW_STATUS_AVAILABLE);
        }
        this.save(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEquipment(EquipmentSaveDTO dto) {
        SmpEquipment equipment = this.getById(dto.getId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        BeanUtil.copyProperties(dto, equipment, "id", "equipmentNo", "createBy", "createTime");
        this.updateById(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(Long id) {
        SmpEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        if (equipment.getBorrowStatus() == BORROW_STATUS_BORROWED) {
            throw new BizException("设备已领用，无法删除");
        }
        this.removeById(id);
    }

    @Override
    public SmpEquipment getEquipmentDetail(Long id) {
        SmpEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        return equipment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBorrowStatus(Long equipmentId, Integer borrowStatus, Long borrowerId, String borrowerName) {
        SmpEquipment equipment = this.getById(equipmentId);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        equipment.setBorrowStatus(borrowStatus);
        equipment.setCurrentBorrowerId(borrowerId);
        equipment.setCurrentBorrowerName(borrowerName);
        this.updateById(equipment);
    }
}
