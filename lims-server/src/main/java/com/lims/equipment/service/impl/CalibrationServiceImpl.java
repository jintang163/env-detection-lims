package com.lims.equipment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.CalibrationPlanQuery;
import com.lims.equipment.dto.CalibrationPlanSaveDTO;
import com.lims.equipment.dto.CalibrationRecordQuery;
import com.lims.equipment.dto.CalibrationRecordSaveDTO;
import com.lims.equipment.entity.EqCalibrationPlan;
import com.lims.equipment.entity.EqCalibrationRecord;
import com.lims.equipment.entity.EqEquipment;
import com.lims.equipment.mapper.EqCalibrationPlanMapper;
import com.lims.equipment.mapper.EqCalibrationRecordMapper;
import com.lims.equipment.mapper.EqEquipmentMapper;
import com.lims.equipment.service.CalibrationService;
import com.lims.equipment.vo.CalibrationPlanVO;
import com.lims.equipment.vo.CalibrationRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalibrationServiceImpl extends ServiceImpl<EqCalibrationPlanMapper, EqCalibrationPlan> implements CalibrationService {

    @Autowired
    private EqCalibrationRecordMapper calibrationRecordMapper;

    @Autowired
    private EqEquipmentMapper equipmentMapper;

    private static final int TYPE_CALIBRATION = 1;
    private static final int TYPE_VERIFICATION = 2;

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_COMPLETED = 1;
    private static final int STATUS_EXPIRED = 2;

    private static final int RESULT_PASS = 1;
    private static final int RESULT_FAIL = 2;
    private static final int RESULT_PARTIAL = 3;

    private static final int DEFAULT_REMIND_DAYS = 30;

    private String getCalibrationTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "校准";
            case 2: return "检定";
            default: return "";
        }
    }

    private String getPlanStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待执行";
            case 1: return "已完成";
            case 2: return "已过期";
            default: return "";
        }
    }

    private String getResultName(Integer result) {
        if (result == null) return "";
        switch (result) {
            case 1: return "合格";
            case 2: return "不合格";
            case 3: return "部分合格";
            default: return "";
        }
    }

    private long calculateDaysUntilDue(LocalDate nextDate) {
        if (nextDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), nextDate);
    }

    private CalibrationPlanVO convertPlanToVO(EqCalibrationPlan entity) {
        CalibrationPlanVO vo = BeanUtil.copyProperties(entity, CalibrationPlanVO.class);
        vo.setCalibrationTypeName(getCalibrationTypeName(entity.getCalibrationType()));
        vo.setStatusName(getPlanStatusName(entity.getStatus()));
        long days = calculateDaysUntilDue(entity.getNextCalibrationDate());
        vo.setDaysUntilDue(days);
        Integer remindDays = entity.getRemindDays() != null ? entity.getRemindDays() : DEFAULT_REMIND_DAYS;
        vo.setUpcoming(days <= remindDays && days >= 0);
        return vo;
    }

    private CalibrationRecordVO convertRecordToVO(EqCalibrationRecord entity) {
        CalibrationRecordVO vo = BeanUtil.copyProperties(entity, CalibrationRecordVO.class);
        vo.setCalibrationTypeName(getCalibrationTypeName(entity.getCalibrationType()));
        vo.setResultName(getResultName(entity.getResult()));
        return vo;
    }

    @Override
    public PageResult<CalibrationPlanVO> selectPlanPage(CalibrationPlanQuery query) {
        LambdaQueryWrapper<EqCalibrationPlan> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentId() != null) {
            wrapper.eq(EqCalibrationPlan::getEquipmentId, query.getEquipmentId());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqCalibrationPlan::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqCalibrationPlan::getEquipmentName, query.getEquipmentName());
        }
        if (query.getCalibrationType() != null) {
            wrapper.eq(EqCalibrationPlan::getCalibrationType, query.getCalibrationType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EqCalibrationPlan::getStatus, query.getStatus());
        }
        if (query.getManagerId() != null) {
            wrapper.eq(EqCalibrationPlan::getManagerId, query.getManagerId());
        }
        if (query.getNextDateStart() != null) {
            wrapper.ge(EqCalibrationPlan::getNextCalibrationDate, query.getNextDateStart());
        }
        if (query.getNextDateEnd() != null) {
            wrapper.le(EqCalibrationPlan::getNextCalibrationDate, query.getNextDateEnd());
        }
        if (Boolean.TRUE.equals(query.getUpcoming())) {
            LocalDate today = LocalDate.now();
            Integer remindDays = query.getRemindDays() != null ? query.getRemindDays() : DEFAULT_REMIND_DAYS;
            wrapper.le(EqCalibrationPlan::getNextCalibrationDate, today.plusDays(remindDays));
            wrapper.ge(EqCalibrationPlan::getNextCalibrationDate, today);
            wrapper.eq(EqCalibrationPlan::getStatus, STATUS_PENDING);
        }
        wrapper.orderByAsc(EqCalibrationPlan::getNextCalibrationDate);

        IPage<EqCalibrationPlan> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<CalibrationPlanVO> voList = page.getRecords().stream()
                .map(this::convertPlanToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPlan(CalibrationPlanSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EqCalibrationPlan plan = BeanUtil.copyProperties(dto, EqCalibrationPlan.class);
        if (plan.getStatus() == null) {
            plan.setStatus(STATUS_PENDING);
        }
        if (plan.getRemindDays() == null) {
            plan.setRemindDays(DEFAULT_REMIND_DAYS);
        }
        this.save(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(CalibrationPlanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("计划ID不能为空");
        }
        EqCalibrationPlan existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("校准计划不存在");
        }
        EqCalibrationPlan plan = BeanUtil.copyProperties(dto, EqCalibrationPlan.class);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        EqCalibrationPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("校准计划不存在");
        }
        this.removeById(id);
    }

    @Override
    public CalibrationPlanVO getPlanDetail(Long id) {
        EqCalibrationPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("校准计划不存在");
        }
        return convertPlanToVO(plan);
    }

    @Override
    public PageResult<CalibrationRecordVO> selectRecordPage(CalibrationRecordQuery query) {
        LambdaQueryWrapper<EqCalibrationRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getEquipmentId() != null) {
            wrapper.eq(EqCalibrationRecord::getEquipmentId, query.getEquipmentId());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(EqCalibrationRecord::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(EqCalibrationRecord::getEquipmentName, query.getEquipmentName());
        }
        if (query.getCalibrationType() != null) {
            wrapper.eq(EqCalibrationRecord::getCalibrationType, query.getCalibrationType());
        }
        if (query.getResult() != null) {
            wrapper.eq(EqCalibrationRecord::getResult, query.getResult());
        }
        if (StrUtil.isNotBlank(query.getCalibrationUnit())) {
            wrapper.like(EqCalibrationRecord::getCalibrationUnit, query.getCalibrationUnit());
        }
        if (StrUtil.isNotBlank(query.getCertificateNo())) {
            wrapper.like(EqCalibrationRecord::getCertificateNo, query.getCertificateNo());
        }
        if (query.getCalibrationDateStart() != null) {
            wrapper.ge(EqCalibrationRecord::getCalibrationDate, query.getCalibrationDateStart());
        }
        if (query.getCalibrationDateEnd() != null) {
            wrapper.le(EqCalibrationRecord::getCalibrationDate, query.getCalibrationDateEnd());
        }
        wrapper.orderByDesc(EqCalibrationRecord::getCalibrationDate);

        IPage<EqCalibrationRecord> page = calibrationRecordMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<CalibrationRecordVO> voList = page.getRecords().stream()
                .map(this::convertRecordToVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRecord(CalibrationRecordSaveDTO dto) {
        EqEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        EqCalibrationRecord record = BeanUtil.copyProperties(dto, EqCalibrationRecord.class);
        calibrationRecordMapper.insert(record);

        equipment.setLastCalibrationDate(dto.getCalibrationDate());
        if (dto.getValidUntil() != null) {
            equipment.setNextCalibrationDate(dto.getValidUntil());
        }

        if (dto.getPlanId() != null) {
            EqCalibrationPlan plan = this.getById(dto.getPlanId());
            if (plan != null) {
                plan.setStatus(STATUS_COMPLETED);
                plan.setLastCalibrationDate(dto.getCalibrationDate());
                if (plan.getCycleMonths() != null && plan.getCycleMonths() > 0) {
                    LocalDate nextDate = dto.getCalibrationDate().plusMonths(plan.getCycleMonths());
                    plan.setNextCalibrationDate(nextDate);
                    plan.setStatus(STATUS_PENDING);
                    equipment.setNextCalibrationDate(nextDate);
                }
                this.updateById(plan);
            }
        }
        equipmentMapper.updateById(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecord(CalibrationRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("记录ID不能为空");
        }
        EqCalibrationRecord existing = calibrationRecordMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("校准记录不存在");
        }
        EqCalibrationRecord record = BeanUtil.copyProperties(dto, EqCalibrationRecord.class);
        calibrationRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long id) {
        EqCalibrationRecord record = calibrationRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("校准记录不存在");
        }
        calibrationRecordMapper.deleteById(id);
    }

    @Override
    public CalibrationRecordVO getRecordDetail(Long id) {
        EqCalibrationRecord record = calibrationRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("校准记录不存在");
        }
        return convertRecordToVO(record);
    }

    @Override
    public List<CalibrationPlanVO> getUpcomingCalibrations() {
        LocalDate today = LocalDate.now();
        LocalDate remindDate = today.plusDays(DEFAULT_REMIND_DAYS);
        LambdaQueryWrapper<EqCalibrationPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(EqCalibrationPlan::getNextCalibrationDate, remindDate);
        wrapper.ge(EqCalibrationPlan::getNextCalibrationDate, today);
        wrapper.eq(EqCalibrationPlan::getStatus, STATUS_PENDING);
        wrapper.orderByAsc(EqCalibrationPlan::getNextCalibrationDate);
        List<EqCalibrationPlan> list = this.list(wrapper);
        return list.stream().map(this::convertPlanToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdatePlanStatus() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<EqCalibrationPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(EqCalibrationPlan::getNextCalibrationDate, today);
        wrapper.eq(EqCalibrationPlan::getStatus, STATUS_PENDING);
        List<EqCalibrationPlan> expiredPlans = this.list(wrapper);
        for (EqCalibrationPlan plan : expiredPlans) {
            plan.setStatus(STATUS_EXPIRED);
        }
        if (!expiredPlans.isEmpty()) {
            this.updateBatchById(expiredPlans);
        }
    }

    @Override
    public List<CalibrationRecordVO> getRecordsByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<EqCalibrationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqCalibrationRecord::getEquipmentId, equipmentId);
        wrapper.orderByDesc(EqCalibrationRecord::getCalibrationDate);
        List<EqCalibrationRecord> list = calibrationRecordMapper.selectList(wrapper);
        return list.stream().map(this::convertRecordToVO).collect(Collectors.toList());
    }

    @Override
    public List<CalibrationPlanVO> getPlansByEquipmentId(Long equipmentId) {
        LambdaQueryWrapper<EqCalibrationPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EqCalibrationPlan::getEquipmentId, equipmentId);
        wrapper.orderByDesc(EqCalibrationPlan::getNextCalibrationDate);
        List<EqCalibrationPlan> list = this.list(wrapper);
        return list.stream().map(this::convertPlanToVO).collect(Collectors.toList());
    }
}
