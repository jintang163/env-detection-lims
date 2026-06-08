package com.lims.equipment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.EquipmentUsageQuery;
import com.lims.equipment.dto.EquipmentUsageSaveDTO;
import com.lims.equipment.entity.EqEquipment;
import com.lims.equipment.entity.EqEquipmentUsage;
import com.lims.equipment.mapper.EqEquipmentMapper;
import com.lims.equipment.mapper.EqEquipmentUsageMapper;
import com.lims.equipment.service.EquipmentUsageService;
import com.lims.equipment.vo.EquipmentUsageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentUsageServiceImpl extends ServiceImpl<EqEquipmentUsageMapper, EqEquipmentUsage> implements EquipmentUsageService {

    @Autowired
    private EqEquipmentMapper equipmentMapper;

    private static final int RUNNING_NORMAL = 1;
    private static final int RUNNING_ANOMALY = 2;
    private static final int RUNNING_FAULT = 3;

    private static final int USAGE_IN_PROGRESS = 0;
    private static final int USAGE_COMPLETED = 1;

    private static final int EQUIPMENT_STATUS_IDLE = 0;
    private static final int EQUIPMENT_STATUS_IN_USE = 1;
    private static final int EQUIPMENT_STATUS_REPAIRING = 2;

    private String getRunningStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 1: return "正常";
            case 2: return "异常";
            case 3: return "故障";
            default: return "";
        }
    }

    private String getUsageStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "使用中";
            case 1: return "已完成";
            default: return "";
        }
    }

    private int calculateUsageMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return (int) Duration.between(start, end).toMinutes();
    }

    private EquipmentUsageVO convertToVO(EqEquipmentUsage entity) {
        EquipmentUsageVO vo = BeanUtil.copyProperties(entity, EquipmentUsageVO.class);
        vo.setRunningStatusName(getRunningStatusName(entity.getRunningStatus()));
        vo.setUsageStatusName(getUsageStatusName(entity.getUsageStatus()));
        return vo;
    }

    @Override
    public PageResult<EquipmentUsageVO> selectPage(EquipmentUsageQuery query) {
        LambdaQueryWrapper<EqEquipmentUsage> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentId() != null) {
            wrapper.eq(EqEquipmentUsage::getEquipmentId, query.getEquipmentId());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqEquipmentUsage::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqEquipmentUsage::getEquipmentName, query.getEquipmentName());
        }
        if (query.getUserId() != null) {
            wrapper.eq(EqEquipmentUsage::getUserId, query.getUserId());
        }
        if (StrUtil.isNotBlank(query.getUserName())) {
            wrapper.like(EqEquipmentUsage::getUserName, query.getUserName());
        }
        if (StrUtil.isNotBlank(query.getSampleNo())) {
            wrapper.like(EqEquipmentUsage::getSampleNo, query.getSampleNo());
        }
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(EqEquipmentUsage::getTaskNo, query.getTaskNo());
        }
        if (query.getTaskId() != null) {
            wrapper.eq(EqEquipmentUsage::getTaskId, query.getTaskId());
        }
        if (query.getRunningStatus() != null) {
            wrapper.eq(EqEquipmentUsage::getRunningStatus, query.getRunningStatus());
        }
        if (query.getUsageStatus() != null) {
            wrapper.eq(EqEquipmentUsage::getUsageStatus, query.getUsageStatus());
        }
        if (query.getStartTimeStart() != null) {
            wrapper.ge(EqEquipmentUsage::getStartTime, query.getStartTimeStart());
        }
        if (query.getStartTimeEnd() != null) {
            wrapper.le(EqEquipmentUsage::getStartTime, query.getStartTimeEnd());
        }
        wrapper.orderByDesc(EqEquipmentUsage::getStartTime);

        IPage<EqEquipmentUsage> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EquipmentUsageVO> voList = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUsage(EquipmentUsageSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EqEquipmentUsage usage = BeanUtil.copyProperties(dto, EqEquipmentUsage.class);
        if (usage.getUsageStatus() == null) {
            usage.setUsageStatus(USAGE_IN_PROGRESS);
        }
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            usage.setUsageMinutes(calculateUsageMinutes(dto.getStartTime(), dto.getEndTime()));
        }
        this.save(usage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUsage(EquipmentUsageSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("记录ID不能为空");
        }
        EqEquipmentUsage existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("使用记录不存在");
        }
        EqEquipmentUsage usage = BeanUtil.copyProperties(dto, EqEquipmentUsage.class);
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            usage.setUsageMinutes(calculateUsageMinutes(dto.getStartTime(), dto.getEndTime()));
        }
        this.updateById(usage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsage(Long id) {
        EqEquipmentUsage usage = this.getById(id);
        if (usage == null) {
            throw new BizException("使用记录不存在");
        }
        this.removeById(id);
    }

    @Override
    public EquipmentUsageVO getUsageDetail(Long id) {
        EqEquipmentUsage usage = this.getById(id);
        if (usage == null) {
            throw new BizException("使用记录不存在");
        }
        return convertToVO(usage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startUsage(EquipmentUsageSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        if (equipment.getStatus() != null && equipment.getStatus() == EQUIPMENT_STATUS_IN_USE) {
            throw new BizException("设备正在使用中");
        }
        if (equipment.getStatus() != null && equipment.getStatus() == EQUIPMENT_STATUS_REPAIRING) {
            throw new BizException("设备正在维修中，无法使用");
        }

        EqEquipmentUsage usage = BeanUtil.copyProperties(dto, EqEquipmentUsage.class);
        usage.setStartTime(LocalDateTime.now());
        usage.setUsageStatus(USAGE_IN_PROGRESS);
        usage.setRunningStatus(RUNNING_NORMAL);
        this.save(usage);

        equipment.setStatus(EQUIPMENT_STATUS_IN_USE);
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void endUsage(Long id, Integer runningStatus, String anomalyDescription) {
        EqEquipmentUsage usage = this.getById(id);
        if (usage == null) {
            throw new BizException("使用记录不存在");
        }
        if (usage.getUsageStatus() == USAGE_COMPLETED) {
            throw new BizException("该使用记录已结束");
        }

        LocalDateTime endTime = LocalDateTime.now();
        usage.setEndTime(endTime);
        usage.setUsageMinutes(calculateUsageMinutes(usage.getStartTime(), endTime));
        usage.setRunningStatus(runningStatus);
        usage.setAnomalyDescription(anomalyDescription);
        usage.setUsageStatus(USAGE_COMPLETED);
        this.updateById(usage);

        EqEquipment equipment = equipmentMapper.selectById(usage.getEquipmentId());
        if (equipment != null) {
            if (runningStatus == RUNNING_FAULT) {
                equipment.setStatus(EQUIPMENT_STATUS_REPAIRING);
            } else {
                equipment.setStatus(EQUIPMENT_STATUS_IDLE);
            }
            equipmentMapper.updateById(equipment);
        }
    }

    @Override
    public List<EquipmentUsageVO> getUsageByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<EqEquipmentUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqEquipmentUsage::getEquipmentId, equipmentId);
        wrapper.orderByDesc(EqEquipmentUsage::getStartTime);
        List<EqEquipmentUsage> list = this.list(wrapper);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<EquipmentUsageVO> getUsageByTaskId(Long taskId) {
        LambdaQueryWrapper<EqEquipmentUsage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqEquipmentUsage::getTaskId, taskId);
        wrapper.orderByDesc(EqEquipmentUsage::getStartTime);
        List<EqEquipmentUsage> list = this.list(wrapper);
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }
}
