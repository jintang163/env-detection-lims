package com.lims.environment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvFacilityMaintenancePlanQuery;
import com.lims.environment.dto.EnvFacilityMaintenancePlanSaveDTO;
import com.lims.environment.dto.EnvFacilityMaintenanceQuery;
import com.lims.environment.dto.EnvFacilityMaintenanceSaveDTO;
import com.lims.environment.entity.EnvFacility;
import com.lims.environment.entity.EnvFacilityMaintenance;
import com.lims.environment.entity.EnvFacilityMaintenancePlan;
import com.lims.environment.mapper.EnvFacilityMaintenanceMapper;
import com.lims.environment.mapper.EnvFacilityMaintenancePlanMapper;
import com.lims.environment.mapper.EnvFacilityMapper;
import com.lims.environment.service.FacilityMaintenanceService;
import com.lims.environment.vo.EnvFacilityMaintenancePlanVO;
import com.lims.environment.vo.EnvFacilityMaintenanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityMaintenanceServiceImpl extends ServiceImpl<EnvFacilityMaintenanceMapper, EnvFacilityMaintenance> implements FacilityMaintenanceService {

    @Autowired
    private EnvFacilityMaintenancePlanMapper maintenancePlanMapper;

    @Autowired
    private EnvFacilityMapper facilityMapper;

    private static final int MAINTENANCE_TYPE_DAILY = 1;
    private static final int MAINTENANCE_TYPE_REGULAR = 2;
    private static final int MAINTENANCE_TYPE_PREVENTIVE = 3;
    private static final int MAINTENANCE_TYPE_REPAIR = 4;

    private static final int RESULT_GOOD = 1;
    private static final int RESULT_NORMAL = 2;
    private static final int RESULT_NEED_REPAIR = 3;

    private static final int PLAN_STATUS_PENDING = 0;
    private static final int PLAN_STATUS_COMPLETED = 1;
    private static final int PLAN_STATUS_EXPIRED = 2;

    private String getMaintenanceTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "日常保养";
            case 2: return "定期维护";
            case 3: return "预防性维护";
            case 4: return "故障维修";
            default: return "";
        }
    }

    private String getResultName(Integer result) {
        if (result == null) return "";
        switch (result) {
            case 1: return "良好";
            case 2: return "一般";
            case 3: return "需维修";
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

    @Override
    public PageResult<EnvFacilityMaintenanceVO> selectRecordPage(EnvFacilityMaintenanceQuery query) {
        LambdaQueryWrapper<EnvFacilityMaintenance> wrapper = new LambdaQueryWrapper<>();
        if (query.getFacilityId() != null) {
            wrapper.eq(EnvFacilityMaintenance::getFacilityId, query.getFacilityId());
        }
        if (StrUtil.isNotBlank(query.getFacilityNo())) {
            wrapper.like(EnvFacilityMaintenance::getFacilityNo, query.getFacilityNo());
        }
        if (query.getMaintenanceType() != null) {
            wrapper.eq(EnvFacilityMaintenance::getMaintenanceType, query.getMaintenanceType());
        }
        if (query.getMaintenanceDateStart() != null) {
            wrapper.ge(EnvFacilityMaintenance::getMaintenanceDate, query.getMaintenanceDateStart());
        }
        if (query.getMaintenanceDateEnd() != null) {
            wrapper.le(EnvFacilityMaintenance::getMaintenanceDate, query.getMaintenanceDateEnd());
        }
        if (query.getMaintainerId() != null) {
            wrapper.eq(EnvFacilityMaintenance::getMaintainerId, query.getMaintainerId());
        }
        if (query.getResult() != null) {
            wrapper.eq(EnvFacilityMaintenance::getResult, query.getResult());
        }
        wrapper.orderByDesc(EnvFacilityMaintenance::getMaintenanceDate);

        IPage<EnvFacilityMaintenance> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvFacilityMaintenanceVO> voList = page.getRecords().stream().map(entity -> {
            EnvFacilityMaintenanceVO vo = BeanUtil.copyProperties(entity, EnvFacilityMaintenanceVO.class);
            vo.setMaintenanceTypeName(getMaintenanceTypeName(entity.getMaintenanceType()));
            vo.setResultName(getResultName(entity.getResult()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaintenanceRecord(EnvFacilityMaintenanceSaveDTO dto) {
        EnvFacility facility = facilityMapper.selectById(dto.getFacilityId());
        if (facility == null) {
            throw new BizException("设施不存在");
        }
        EnvFacilityMaintenance record = BeanUtil.copyProperties(dto, EnvFacilityMaintenance.class);
        if (StrUtil.isBlank(record.getFacilityNo())) {
            record.setFacilityNo(facility.getFacilityNo());
        }
        if (StrUtil.isBlank(record.getFacilityName())) {
            record.setFacilityName(facility.getFacilityName());
        }
        this.save(record);

        if (dto.getMaintenanceDate() != null) {
            facility.setLastMaintenanceDate(dto.getMaintenanceDate());
            if (dto.getNextMaintenanceDate() != null) {
                facility.setNextMaintenanceDate(dto.getNextMaintenanceDate());
            }
            facilityMapper.updateById(facility);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaintenanceRecord(EnvFacilityMaintenanceSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("维护记录ID不能为空");
        }
        EnvFacilityMaintenance existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("维护记录不存在");
        }
        EnvFacilityMaintenance record = BeanUtil.copyProperties(dto, EnvFacilityMaintenance.class);
        this.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaintenanceRecord(Long id) {
        EnvFacilityMaintenance record = this.getById(id);
        if (record == null) {
            throw new BizException("维护记录不存在");
        }
        this.removeById(id);
    }

    @Override
    public EnvFacilityMaintenanceVO getMaintenanceRecordDetail(Long id) {
        EnvFacilityMaintenance record = this.getById(id);
        if (record == null) {
            throw new BizException("维护记录不存在");
        }
        EnvFacilityMaintenanceVO vo = BeanUtil.copyProperties(record, EnvFacilityMaintenanceVO.class);
        vo.setMaintenanceTypeName(getMaintenanceTypeName(record.getMaintenanceType()));
        vo.setResultName(getResultName(record.getResult()));
        return vo;
    }

    @Override
    public List<EnvFacilityMaintenanceVO> getRecordByFacility(Long facilityId) {
        LambdaQueryWrapper<EnvFacilityMaintenance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvFacilityMaintenance::getFacilityId, facilityId);
        wrapper.orderByDesc(EnvFacilityMaintenance::getMaintenanceDate);
        List<EnvFacilityMaintenance> list = this.list(wrapper);
        return list.stream().map(entity -> {
            EnvFacilityMaintenanceVO vo = BeanUtil.copyProperties(entity, EnvFacilityMaintenanceVO.class);
            vo.setMaintenanceTypeName(getMaintenanceTypeName(entity.getMaintenanceType()));
            vo.setResultName(getResultName(entity.getResult()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<EnvFacilityMaintenancePlanVO> selectPlanPage(EnvFacilityMaintenancePlanQuery query) {
        LambdaQueryWrapper<EnvFacilityMaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        if (query.getFacilityId() != null) {
            wrapper.eq(EnvFacilityMaintenancePlan::getFacilityId, query.getFacilityId());
        }
        if (StrUtil.isNotBlank(query.getFacilityNo())) {
            wrapper.like(EnvFacilityMaintenancePlan::getFacilityNo, query.getFacilityNo());
        }
        if (query.getMaintenanceType() != null) {
            wrapper.eq(EnvFacilityMaintenancePlan::getMaintenanceType, query.getMaintenanceType());
        }
        if (query.getNextMaintenanceDateStart() != null) {
            wrapper.ge(EnvFacilityMaintenancePlan::getNextMaintenanceDate, query.getNextMaintenanceDateStart());
        }
        if (query.getNextMaintenanceDateEnd() != null) {
            wrapper.le(EnvFacilityMaintenancePlan::getNextMaintenanceDate, query.getNextMaintenanceDateEnd());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EnvFacilityMaintenancePlan::getStatus, query.getStatus());
        }
        if (query.getManagerId() != null) {
            wrapper.eq(EnvFacilityMaintenancePlan::getManagerId, query.getManagerId());
        }
        wrapper.orderByAsc(EnvFacilityMaintenancePlan::getNextMaintenanceDate);

        IPage<EnvFacilityMaintenancePlan> page = maintenancePlanMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvFacilityMaintenancePlanVO> voList = page.getRecords().stream().map(entity -> {
            EnvFacilityMaintenancePlanVO vo = BeanUtil.copyProperties(entity, EnvFacilityMaintenancePlanVO.class);
            vo.setMaintenanceTypeName(getMaintenanceTypeName(entity.getMaintenanceType()));
            vo.setStatusName(getPlanStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMaintenancePlan(EnvFacilityMaintenancePlanSaveDTO dto) {
        EnvFacility facility = facilityMapper.selectById(dto.getFacilityId());
        if (facility == null) {
            throw new BizException("设施不存在");
        }
        EnvFacilityMaintenancePlan plan = BeanUtil.copyProperties(dto, EnvFacilityMaintenancePlan.class);
        if (StrUtil.isBlank(plan.getFacilityNo())) {
            plan.setFacilityNo(facility.getFacilityNo());
        }
        if (StrUtil.isBlank(plan.getFacilityName())) {
            plan.setFacilityName(facility.getFacilityName());
        }
        if (plan.getStatus() == null) {
            plan.setStatus(PLAN_STATUS_PENDING);
        }
        maintenancePlanMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaintenancePlan(EnvFacilityMaintenancePlanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("维护计划ID不能为空");
        }
        EnvFacilityMaintenancePlan existing = maintenancePlanMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("维护计划不存在");
        }
        EnvFacilityMaintenancePlan plan = BeanUtil.copyProperties(dto, EnvFacilityMaintenancePlan.class);
        maintenancePlanMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaintenancePlan(Long id) {
        EnvFacilityMaintenancePlan plan = maintenancePlanMapper.selectById(id);
        if (plan == null) {
            throw new BizException("维护计划不存在");
        }
        maintenancePlanMapper.deleteById(id);
    }

    @Override
    public List<EnvFacilityMaintenancePlanVO> getPlanByFacility(Long facilityId) {
        LambdaQueryWrapper<EnvFacilityMaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvFacilityMaintenancePlan::getFacilityId, facilityId);
        wrapper.orderByAsc(EnvFacilityMaintenancePlan::getNextMaintenanceDate);
        List<EnvFacilityMaintenancePlan> list = maintenancePlanMapper.selectList(wrapper);
        return list.stream().map(entity -> {
            EnvFacilityMaintenancePlanVO vo = BeanUtil.copyProperties(entity, EnvFacilityMaintenancePlanVO.class);
            vo.setMaintenanceTypeName(getMaintenanceTypeName(entity.getMaintenanceType()));
            vo.setStatusName(getPlanStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMaintenancePlan(Long planId, EnvFacilityMaintenanceSaveDTO recordDTO) {
        EnvFacilityMaintenancePlan plan = maintenancePlanMapper.selectById(planId);
        if (plan == null) {
            throw new BizException("维护计划不存在");
        }
        if (plan.getStatus() == PLAN_STATUS_COMPLETED) {
            throw new BizException("该计划已完成");
        }

        EnvFacilityMaintenance record = BeanUtil.copyProperties(recordDTO, EnvFacilityMaintenance.class);
        if (record.getFacilityId() == null) {
            record.setFacilityId(plan.getFacilityId());
        }
        if (StrUtil.isBlank(record.getFacilityNo())) {
            record.setFacilityNo(plan.getFacilityNo());
        }
        if (StrUtil.isBlank(record.getFacilityName())) {
            record.setFacilityName(plan.getFacilityName());
        }
        if (record.getMaintenanceType() == null) {
            record.setMaintenanceType(plan.getMaintenanceType());
        }
        if (StrUtil.isBlank(record.getContent()) && StrUtil.isNotBlank(plan.getContentTemplate())) {
            record.setContent(plan.getContentTemplate());
        }
        this.save(record);

        plan.setStatus(PLAN_STATUS_COMPLETED);
        plan.setLastMaintenanceDate(record.getMaintenanceDate());
        if (plan.getCycleDays() != null && record.getMaintenanceDate() != null) {
            plan.setNextMaintenanceDate(record.getMaintenanceDate().plusDays(plan.getCycleDays()));
        }
        maintenancePlanMapper.updateById(plan);

        EnvFacility facility = facilityMapper.selectById(plan.getFacilityId());
        if (facility != null && record.getMaintenanceDate() != null) {
            facility.setLastMaintenanceDate(record.getMaintenanceDate());
            if (plan.getNextMaintenanceDate() != null) {
                facility.setNextMaintenanceDate(plan.getNextMaintenanceDate());
            }
            facilityMapper.updateById(facility);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkMaintenancePlanStatus() {
        LambdaQueryWrapper<EnvFacilityMaintenancePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvFacilityMaintenancePlan::getStatus, PLAN_STATUS_PENDING);
        wrapper.lt(EnvFacilityMaintenancePlan::getNextMaintenanceDate, LocalDate.now());
        List<EnvFacilityMaintenancePlan> expiredPlans = maintenancePlanMapper.selectList(wrapper);
        for (EnvFacilityMaintenancePlan plan : expiredPlans) {
            plan.setStatus(PLAN_STATUS_EXPIRED);
            maintenancePlanMapper.updateById(plan);
        }
    }
}
