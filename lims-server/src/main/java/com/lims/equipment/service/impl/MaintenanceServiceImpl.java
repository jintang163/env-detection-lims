package com.lims.equipment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.MaintenanceRecordQuery;
import com.lims.equipment.dto.MaintenanceRecordSaveDTO;
import com.lims.equipment.dto.RepairConfirmDTO;
import com.lims.equipment.dto.RepairHandleDTO;
import com.lims.equipment.dto.RepairRequestQuery;
import com.lims.equipment.dto.RepairRequestSaveDTO;
import com.lims.equipment.entity.EqEquipment;
import com.lims.equipment.entity.EqMaintenanceRecord;
import com.lims.equipment.entity.EqRepairRequest;
import com.lims.equipment.mapper.EqEquipmentMapper;
import com.lims.equipment.mapper.EqMaintenanceRecordMapper;
import com.lims.equipment.mapper.EqRepairRequestMapper;
import com.lims.equipment.service.MaintenanceService;
import com.lims.equipment.vo.MaintenanceRecordVO;
import com.lims.equipment.vo.RepairRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaintenanceServiceImpl extends ServiceImpl<EqMaintenanceRecordMapper, EqMaintenanceRecord> implements MaintenanceService {

    @Autowired
    private EqRepairRequestMapper repairRequestMapper;

    @Autowired
    private EqEquipmentMapper equipmentMapper;

    private static final int MAINTENANCE_DAILY = 1;
    private static final int MAINTENANCE_REGULAR = 2;
    private static final int MAINTENANCE_PREVENTIVE = 3;

    private static final int MAINTENANCE_RESULT_GOOD = 1;
    private static final int MAINTENANCE_RESULT_NORMAL = 2;
    private static final int MAINTENANCE_RESULT_NEED_REPAIR = 3;

    private static final int URGENCY_NORMAL = 1;
    private static final int URGENCY_URGENT = 2;
    private static final int URGENCY_VERY_URGENT = 3;

    private static final int REPAIR_STATUS_PENDING = 0;
    private static final int REPAIR_STATUS_REPAIRING = 1;
    private static final int REPAIR_STATUS_COMPLETED = 2;
    private static final int REPAIR_STATUS_REJECTED = 3;

    private static final int REPAIR_RESULT_FIXED = 1;
    private static final int REPAIR_RESULT_PARTIAL = 2;
    private static final int REPAIR_RESULT_CANNOT_FIX = 3;

    private static final int EQUIPMENT_STATUS_REPAIRING = 2;
    private static final int EQUIPMENT_STATUS_IDLE = 0;

    private String getMaintenanceTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "日常保养";
            case 2: return "定期维护";
            case 3: return "预防性维护";
            default: return "";
        }
    }

    private String getMaintenanceResultName(Integer result) {
        if (result == null) return "";
        switch (result) {
            case 1: return "良好";
            case 2: return "一般";
            case 3: return "需维修";
            default: return "";
        }
    }

    private String getUrgencyName(Integer urgency) {
        if (urgency == null) return "";
        switch (urgency) {
            case 1: return "一般";
            case 2: return "紧急";
            case 3: return "特急";
            default: return "";
        }
    }

    private String getRepairStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待受理";
            case 1: return "维修中";
            case 2: return "已完成";
            case 3: return "已驳回";
            default: return "";
        }
    }

    private String getRepairResultName(Integer result) {
        if (result == null) return "";
        switch (result) {
            case 1: return "修复";
            case 2: return "部分修复";
            case 3: return "无法修复";
            default: return "";
        }
    }

    private MaintenanceRecordVO convertMaintenanceToVO(EqMaintenanceRecord entity) {
        MaintenanceRecordVO vo = BeanUtil.copyProperties(entity, MaintenanceRecordVO.class);
        vo.setMaintenanceTypeName(getMaintenanceTypeName(entity.getMaintenanceType()));
        vo.setResultName(getMaintenanceResultName(entity.getResult()));
        return vo;
    }

    private RepairRequestVO convertRepairToVO(EqRepairRequest entity) {
        RepairRequestVO vo = BeanUtil.copyProperties(entity, RepairRequestVO.class);
        vo.setUrgencyName(getUrgencyName(entity.getUrgency()));
        vo.setStatusName(getRepairStatusName(entity.getStatus()));
        vo.setRepairResultName(getRepairResultName(entity.getRepairResult()));
        return vo;
    }

    @Override
    public PageResult<MaintenanceRecordVO> selectMaintenancePage(MaintenanceRecordQuery query) {
        LambdaQueryWrapper<EqMaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentId() != null) {
            wrapper.eq(EqMaintenanceRecord::getEquipmentId, query.getEquipmentId());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqMaintenanceRecord::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqMaintenanceRecord::getEquipmentName, query.getEquipmentName());
        }
        if (query.getMaintenanceType() != null) {
            wrapper.eq(EqMaintenanceRecord::getMaintenanceType, query.getMaintenanceType());
        }
        if (query.getMaintainerId() != null) {
            wrapper.eq(EqMaintenanceRecord::getMaintainerId, query.getMaintainerId());
        }
        if (query.getResult() != null) {
            wrapper.eq(EqMaintenanceRecord::getResult, query.getResult());
        }
        if (query.getMaintenanceDateStart() != null) {
            wrapper.ge(EqMaintenanceRecord::getMaintenanceDate, query.getMaintenanceDateStart());
        }
        if (query.getMaintenanceDateEnd() != null) {
            wrapper.le(EqMaintenanceRecord::getMaintenanceDate, query.getMaintenanceDateEnd());
        }
        wrapper.orderByDesc(EqMaintenanceRecord::getMaintenanceDate);

        IPage<EqMaintenanceRecord> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<MaintenanceRecordVO> voList = page.getRecords().stream()
                .map(this::convertMaintenanceToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaintenanceRecord(MaintenanceRecordSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EqMaintenanceRecord record = BeanUtil.copyProperties(dto, EqMaintenanceRecord.class);
        this.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaintenanceRecord(MaintenanceRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("记录ID不能为空");
        }
        EqMaintenanceRecord existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("维护记录不存在");
        }
        EqMaintenanceRecord record = BeanUtil.copyProperties(dto, EqMaintenanceRecord.class);
        this.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaintenanceRecord(Long id) {
        EqMaintenanceRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("维护记录不存在");
        }
        this.removeById(id);
    }

    @Override
    public MaintenanceRecordVO getMaintenanceRecordDetail(Long id) {
        EqMaintenanceRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("维护记录不存在");
        }
        return convertMaintenanceToVO(record);
    }

    @Override
    public List<MaintenanceRecordVO> getMaintenanceByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<EqMaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqMaintenanceRecord::getEquipmentId, equipmentId);
        wrapper.orderByDesc(EqMaintenanceRecord::getMaintenanceDate);
        List<EqMaintenanceRecord> list = this.list(wrapper);
        return list.stream().map(this::convertMaintenanceToVO).collect(Collectors.toList());
    }

    @Override
    public PageResult<RepairRequestVO> selectRepairPage(RepairRequestQuery query) {
        LambdaQueryWrapper<EqRepairRequest> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentId() != null) {
            wrapper.eq(EqRepairRequest::getEquipmentId, query.getEquipmentId());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqRepairRequest::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqRepairRequest::getEquipmentName, query.getEquipmentName());
        }
        if (query.getApplicantId() != null) {
            wrapper.eq(EqRepairRequest::getApplicantId, query.getApplicantId());
        }
        if (query.getUrgency() != null) {
            wrapper.eq(EqRepairRequest::getUrgency, query.getUrgency());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EqRepairRequest::getStatus, query.getStatus());
        }
        if (query.getHandlerId() != null) {
            wrapper.eq(EqRepairRequest::getHandlerId, query.getHandlerId());
        }
        if (query.getRepairResult() != null) {
            wrapper.eq(EqRepairRequest::getRepairResult, query.getRepairResult());
        }
        if (query.getApplyTimeStart() != null) {
            wrapper.ge(EqRepairRequest::getApplyTime, query.getApplyTimeStart());
        }
        if (query.getApplyTimeEnd() != null) {
            wrapper.le(EqRepairRequest::getApplyTime, query.getApplyTimeEnd());
        }
        wrapper.orderByDesc(EqRepairRequest::getApplyTime);

        IPage<EqRepairRequest> page = repairRequestMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<RepairRequestVO> voList = page.getRecords().stream()
                .map(this::convertRepairToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRepairRequest(RepairRequestSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EqRepairRequest request = BeanUtil.copyProperties(dto, EqRepairRequest.class);
        request.setApplyTime(LocalDateTime.now());
        request.setStatus(REPAIR_STATUS_PENDING);
        if (request.getFaultTime() == null) {
            request.setFaultTime(LocalDateTime.now());
        }
        repairRequestMapper.insert(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRepairRequest(RepairRequestSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("申请ID不能为空");
        }
        EqRepairRequest existing = repairRequestMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("维修申请不存在");
        }
        if (existing.getStatus() != REPAIR_STATUS_PENDING) {
            throw new BizException("只能修改待受理状态的申请");
        }
        EqRepairRequest request = BeanUtil.copyProperties(dto, EqRepairRequest.class);
        repairRequestMapper.updateById(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepairRequest(Long id) {
        EqRepairRequest request = repairRequestMapper.selectById(id);
        if (request == null) {
            throw new BizException("维修申请不存在");
        }
        if (request.getStatus() != REPAIR_STATUS_PENDING) {
            throw new BizException("只能删除待受理状态的申请");
        }
        repairRequestMapper.deleteById(id);
    }

    @Override
    public RepairRequestVO getRepairRequestDetail(Long id) {
        EqRepairRequest request = repairRequestMapper.selectById(id);
        if (request == null) {
            throw new BizException("维修申请不存在");
        }
        return convertRepairToVO(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRepair(RepairHandleDTO dto) {
        EqRepairRequest request = repairRequestMapper.selectById(dto.getId());
        if (request == null) {
            throw new BizException("维修申请不存在");
        }
        if (request.getStatus() != REPAIR_STATUS_PENDING && request.getStatus() != REPAIR_STATUS_REPAIRING) {
            throw new BizException("申请状态不正确，无法处理");
        }

        BeanUtil.copyProperties(dto, request);
        request.setHandleTime(LocalDateTime.now());
        request.setStatus(REPAIR_STATUS_REPAIRING);
        repairRequestMapper.updateById(request);

        EqEquipment equipment = equipmentMapper.selectById(request.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus(EQUIPMENT_STATUS_REPAIRING);
            equipmentMapper.updateById(equipment);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmRepair(RepairConfirmDTO dto) {
        EqRepairRequest request = repairRequestMapper.selectById(dto.getId());
        if (request == null) {
            throw new BizException("维修申请不存在");
        }
        if (request.getStatus() != REPAIR_STATUS_REPAIRING) {
            throw new BizException("申请状态不正确，无法确认");
        }
        if (request.getRepairResult() == null) {
            throw new BizException("请先填写维修结果");
        }

        request.setConfirmerId(dto.getConfirmerId());
        request.setConfirmerName(dto.getConfirmerName());
        request.setConfirmTime(LocalDateTime.now());
        request.setConfirmOpinion(dto.getConfirmOpinion());

        if (Boolean.TRUE.equals(dto.getPassed())) {
            request.setStatus(REPAIR_STATUS_COMPLETED);
            EqEquipment equipment = equipmentMapper.selectById(request.getEquipmentId());
            if (equipment != null) {
                if (request.getRepairResult() == REPAIR_RESULT_CANNOT_FIX) {
                    equipment.setStatus(3);
                } else {
                    equipment.setStatus(EQUIPMENT_STATUS_IDLE);
                }
                equipmentMapper.updateById(equipment);
            }
        } else {
            request.setStatus(REPAIR_STATUS_REPAIRING);
        }

        repairRequestMapper.updateById(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectRepair(Long id, String reason) {
        EqRepairRequest request = repairRequestMapper.selectById(id);
        if (request == null) {
            throw new BizException("维修申请不存在");
        }
        if (request.getStatus() != REPAIR_STATUS_PENDING) {
            throw new BizException("只能驳回待受理状态的申请");
        }

        request.setStatus(REPAIR_STATUS_REJECTED);
        request.setRemark(reason);
        request.setHandleTime(LocalDateTime.now());
        repairRequestMapper.updateById(request);
    }

    @Override
    public List<RepairRequestVO> getRepairByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<EqRepairRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqRepairRequest::getEquipmentId, equipmentId);
        wrapper.orderByDesc(EqRepairRequest::getApplyTime);
        List<EqRepairRequest> list = repairRequestMapper.selectList(wrapper);
        return list.stream().map(this::convertRepairToVO).collect(Collectors.toList());
    }
}
