package com.lims.equipment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.EquipmentQuery;
import com.lims.equipment.dto.EquipmentSaveDTO;
import com.lims.equipment.entity.EqCalibrationPlan;
import com.lims.equipment.entity.EqCalibrationRecord;
import com.lims.equipment.entity.EqEquipment;
import com.lims.equipment.entity.EqEquipmentUsage;
import com.lims.equipment.entity.EqMaintenanceRecord;
import com.lims.equipment.entity.EqRepairRequest;
import com.lims.equipment.mapper.EqCalibrationPlanMapper;
import com.lims.equipment.mapper.EqCalibrationRecordMapper;
import com.lims.equipment.mapper.EqEquipmentMapper;
import com.lims.equipment.mapper.EqEquipmentUsageMapper;
import com.lims.equipment.mapper.EqMaintenanceRecordMapper;
import com.lims.equipment.mapper.EqRepairRequestMapper;
import com.lims.equipment.service.CalibrationService;
import com.lims.equipment.service.EquipmentService;
import com.lims.equipment.service.EquipmentUsageService;
import com.lims.equipment.service.MaintenanceService;
import com.lims.equipment.vo.CalibrationPlanVO;
import com.lims.equipment.vo.CalibrationRecordVO;
import com.lims.equipment.vo.EquipmentDetailVO;
import com.lims.equipment.vo.EquipmentUsageVO;
import com.lims.equipment.vo.EquipmentVO;
import com.lims.equipment.vo.MaintenanceRecordVO;
import com.lims.equipment.vo.RepairRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EqEquipmentMapper, EqEquipment> implements EquipmentService {

    @Autowired
    private EqCalibrationPlanMapper calibrationPlanMapper;

    @Autowired
    private EqCalibrationRecordMapper calibrationRecordMapper;

    @Autowired
    private EqEquipmentUsageMapper equipmentUsageMapper;

    @Autowired
    private EqMaintenanceRecordMapper maintenanceRecordMapper;

    @Autowired
    private EqRepairRequestMapper repairRequestMapper;

    @Autowired
    private CalibrationService calibrationService;

    @Autowired
    private EquipmentUsageService equipmentUsageService;

    @Autowired
    private MaintenanceService maintenanceService;

    private static final int STATUS_IDLE = 0;
    private static final int STATUS_IN_USE = 1;
    private static final int STATUS_REPAIRING = 2;
    private static final int STATUS_DISABLED = 3;
    private static final int STATUS_SCRAPPED = 4;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "闲置";
            case 1: return "在用";
            case 2: return "维修中";
            case 3: return "停用";
            case 4: return "报废";
            default: return "";
        }
    }

    @Override
    public PageResult<EquipmentVO> selectPage(EquipmentQuery query) {
        LambdaQueryWrapper<EqEquipment> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqEquipment::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqEquipment::getEquipmentName, query.getEquipmentName());
        }
        if (StrUtil.isNotBlank(query.getModel())) {
            wrapper.like(EqEquipment::getModel, query.getModel());
        }
        if (StrUtil.isNotBlank(query.getEquipmentType())) {
            wrapper.eq(EqEquipment::getEquipmentType, query.getEquipmentType());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(EqEquipment::getDeptId, query.getDeptId());
        }
        if (query.getSupplierId() != null) {
            wrapper.eq(EqEquipment::getSupplierId, query.getSupplierId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EqEquipment::getStatus, query.getStatus());
        }
        if (query.getManagerId() != null) {
            wrapper.eq(EqEquipment::getManagerId, query.getManagerId());
        }
        if (query.getPurchaseDateStart() != null) {
            wrapper.ge(EqEquipment::getPurchaseDate, query.getPurchaseDateStart());
        }
        if (query.getPurchaseDateEnd() != null) {
            wrapper.le(EqEquipment::getPurchaseDate, query.getPurchaseDateEnd());
        }
        wrapper.orderByDesc(EqEquipment::getCreateTime);

        IPage<EqEquipment> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EquipmentVO> voList = page.getRecords().stream().map(entity -> {
            EquipmentVO vo = BeanUtil.copyProperties(entity, EquipmentVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEquipment(EquipmentSaveDTO dto) {
        LambdaQueryWrapper<EqEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqEquipment::getEquipmentNo, dto.getEquipmentNo());
        if (this.count(wrapper) > 0) {
            throw new BizException("设备编号已存在");
        }
        EqEquipment equipment = BeanUtil.copyProperties(dto, EqEquipment.class);
        this.save(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEquipment(EquipmentSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("设备ID不能为空");
        }
        EqEquipment existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("设备不存在");
        }
        if (!existing.getEquipmentNo().equals(dto.getEquipmentNo())) {
            LambdaQueryWrapper<EqEquipment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EqEquipment::getEquipmentNo, dto.getEquipmentNo());
            wrapper.ne(EqEquipment::getId, dto.getId());
            if (this.count(wrapper) > 0) {
                throw new BizException("设备编号已存在");
            }
        }
        EqEquipment equipment = BeanUtil.copyProperties(dto, EqEquipment.class);
        this.updateById(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEquipment(Long id) {
        EqEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        this.removeById(id);
    }

    @Override
    public EquipmentDetailVO getEquipmentDetail(Long id) {
        EqEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EquipmentDetailVO vo = BeanUtil.copyProperties(equipment, EquipmentDetailVO.class);
        vo.setStatusName(getStatusName(equipment.getStatus()));

        List<CalibrationPlanVO> calibrationPlans = calibrationService.getPlansByEquipmentId(id);
        List<CalibrationRecordVO> calibrationRecords = calibrationService.getRecordsByEquipmentId(id);
        List<EquipmentUsageVO> usageRecords = equipmentUsageService.getUsageByEquipmentId(id);
        List<MaintenanceRecordVO> maintenanceRecords = maintenanceService.getMaintenanceByEquipmentId(id);
        List<RepairRequestVO> repairRequests = maintenanceService.getRepairByEquipmentId(id);

        vo.setCalibrationPlans(calibrationPlans);
        vo.setCalibrationRecords(calibrationRecords);
        vo.setUsageRecords(usageRecords);
        vo.setMaintenanceRecords(maintenanceRecords);
        vo.setRepairRequests(repairRequests);

        return vo;
    }

    @Override
    public List<EquipmentVO> getAvailableEquipmentList() {
        LambdaQueryWrapper<EqEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(EqEquipment::getStatus, STATUS_IDLE, STATUS_IN_USE);
        wrapper.orderByAsc(EqEquipment::getEquipmentNo);
        List<EqEquipment> list = this.list(wrapper);
        return list.stream().map(entity -> {
            EquipmentVO vo = BeanUtil.copyProperties(entity, EquipmentVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEquipmentStatus(Long id, Integer status) {
        EqEquipment equipment = this.getById(id);
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        equipment.setStatus(status);
        this.updateById(equipment);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        LambdaQueryWrapper<EqEquipment> wrapper;

        wrapper = new LambdaQueryWrapper<>();
        stats.put("total", this.count(wrapper));

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqEquipment::getStatus, STATUS_IN_USE);
        stats.put("inUse", this.count(wrapper));

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqEquipment::getStatus, STATUS_REPAIRING);
        stats.put("repairing", this.count(wrapper));

        wrapper = new LambdaQueryWrapper<>();
        wrapper.le(EqEquipment::getNextCalibrationDate, LocalDate.now().plusDays(30));
        wrapper.ge(EqEquipment::getNextCalibrationDate, LocalDate.now());
        stats.put("needCalibration", this.count(wrapper));

        return stats;
    }
}
