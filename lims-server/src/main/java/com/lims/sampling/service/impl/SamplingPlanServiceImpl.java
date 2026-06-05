package com.lims.sampling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.sampling.dto.*;
import com.lims.sampling.entity.*;
import com.lims.sampling.mapper.*;
import com.lims.sampling.service.SamplingPlanService;
import com.lims.sampling.vo.SamplingPlanDetailVO;
import com.lims.sampling.vo.SamplingPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SamplingPlanServiceImpl extends ServiceImpl<SmpSamplingPlanMapper, SmpSamplingPlan> implements SamplingPlanService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private SmpSamplingPointMapper samplingPointMapper;

    @Autowired
    private SmpSamplingTaskMapper samplingTaskMapper;

    @Autowired
    private SmpSampleRecordMapper sampleRecordMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PENDING_ASSIGN = 1;
    private static final int STATUS_ASSIGNED = 2;
    private static final int STATUS_SAMPLING = 3;
    private static final int STATUS_COMPLETED = 4;
    private static final int STATUS_CANCELLED = 5;

    private String getPlanStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "待分配";
            case 2: return "已分配";
            case 3: return "采样中";
            case 4: return "已完成";
            case 5: return "已取消";
            default: return "";
        }
    }

    @Override
    public PageResult<SamplingPlanVO> selectPage(SamplingPlanQuery query) {
        LambdaQueryWrapper<SmpSamplingPlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getPlanNo())) {
            wrapper.like(SmpSamplingPlan::getPlanNo, query.getPlanNo());
        }
        if (StrUtil.isNotBlank(query.getPlanName())) {
            wrapper.like(SmpSamplingPlan::getPlanName, query.getPlanName());
        }
        if (StrUtil.isNotBlank(query.getEntrustNo())) {
            wrapper.like(SmpSamplingPlan::getEntrustNo, query.getEntrustNo());
        }
        if (StrUtil.isNotBlank(query.getCustomerName())) {
            wrapper.like(SmpSamplingPlan::getCustomerName, query.getCustomerName());
        }
        if (StrUtil.isNotBlank(query.getSamplingType())) {
            wrapper.eq(SmpSamplingPlan::getSamplingType, query.getSamplingType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SmpSamplingPlan::getStatus, query.getStatus());
        }
        if (query.getSamplerId() != null) {
            wrapper.apply("FIND_IN_SET(" + query.getSamplerId() + ", sampler_ids)");
        }
        if (query.getPlanDateStart() != null) {
            wrapper.ge(SmpSamplingPlan::getPlanDate, query.getPlanDateStart());
        }
        if (query.getPlanDateEnd() != null) {
            wrapper.le(SmpSamplingPlan::getPlanDate, query.getPlanDateEnd());
        }
        wrapper.orderByDesc(SmpSamplingPlan::getCreateTime);

        Page<SmpSamplingPlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SmpSamplingPlan> pageResult = this.page(page, wrapper);

        IPage<SamplingPlanVO> voPage = pageResult.convert(plan -> {
            SamplingPlanVO vo = BeanUtil.copyProperties(plan, SamplingPlanVO.class);
            vo.setStatusName(getPlanStatusName(plan.getStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPlan(SamplingPlanSaveDTO dto) {
        SmpSamplingPlan plan = BeanUtil.copyProperties(dto, SmpSamplingPlan.class);
        plan.setPlanNo(codeGenerator.generateSamplingPlanNo());
        plan.setStatus(STATUS_DRAFT);

        if (dto.getSamplerIdList() != null && !dto.getSamplerIdList().isEmpty()) {
            plan.setSamplerIds(dto.getSamplerIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getSamplerNameList() != null && !dto.getSamplerNameList().isEmpty()) {
            plan.setSamplerNames(String.join(",", dto.getSamplerNameList()));
        }
        if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
            plan.setEquipmentIds(dto.getEquipmentIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
            plan.setEquipmentNames(String.join(",", dto.getEquipmentNameList()));
        }
        if (dto.getContainerIdList() != null && !dto.getContainerIdList().isEmpty()) {
            plan.setContainerIds(dto.getContainerIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getContainerNameList() != null && !dto.getContainerNameList().isEmpty()) {
            plan.setContainerNames(String.join(",", dto.getContainerNameList()));
        }

        int pointCount = dto.getPointList() != null ? dto.getPointList().size() : 0;
        plan.setPointCount(pointCount);

        int sampleCount = 0;
        if (dto.getPointList() != null) {
            for (SamplingPointSaveDTO point : dto.getPointList()) {
                sampleCount += point.getExpectedCount() != null ? point.getExpectedCount() : 1;
            }
        }
        plan.setSampleCount(sampleCount);

        this.save(plan);

        if (dto.getPointList() != null && !dto.getPointList().isEmpty()) {
            int sortOrder = 1;
            for (SamplingPointSaveDTO pointDto : dto.getPointList()) {
                SmpSamplingPoint point = BeanUtil.copyProperties(pointDto, SmpSamplingPoint.class);
                point.setPlanId(plan.getId());
                point.setSortOrder(sortOrder++);
                if (StrUtil.isBlank(point.getPointCode())) {
                    point.setPointCode("P" + String.format("%03d", sortOrder - 1));
                }
                samplingPointMapper.insert(point);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(SamplingPlanSaveDTO dto) {
        SmpSamplingPlan plan = this.getById(dto.getId());
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        if (plan.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可修改");
        }

        BeanUtil.copyProperties(dto, plan, "id", "planNo", "status", "createBy", "createTime");

        if (dto.getSamplerIdList() != null && !dto.getSamplerIdList().isEmpty()) {
            plan.setSamplerIds(dto.getSamplerIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getSamplerNameList() != null && !dto.getSamplerNameList().isEmpty()) {
            plan.setSamplerNames(String.join(",", dto.getSamplerNameList()));
        }
        if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
            plan.setEquipmentIds(dto.getEquipmentIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
            plan.setEquipmentNames(String.join(",", dto.getEquipmentNameList()));
        }
        if (dto.getContainerIdList() != null && !dto.getContainerIdList().isEmpty()) {
            plan.setContainerIds(dto.getContainerIdList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        }
        if (dto.getContainerNameList() != null && !dto.getContainerNameList().isEmpty()) {
            plan.setContainerNames(String.join(",", dto.getContainerNameList()));
        }

        int pointCount = dto.getPointList() != null ? dto.getPointList().size() : 0;
        plan.setPointCount(pointCount);

        int sampleCount = 0;
        if (dto.getPointList() != null) {
            for (SamplingPointSaveDTO point : dto.getPointList()) {
                sampleCount += point.getExpectedCount() != null ? point.getExpectedCount() : 1;
            }
        }
        plan.setSampleCount(sampleCount);

        this.updateById(plan);

        samplingPointMapper.delete(new LambdaQueryWrapper<SmpSamplingPoint>()
                .eq(SmpSamplingPoint::getPlanId, plan.getId()));

        if (dto.getPointList() != null && !dto.getPointList().isEmpty()) {
            int sortOrder = 1;
            for (SamplingPointSaveDTO pointDto : dto.getPointList()) {
                SmpSamplingPoint point = BeanUtil.copyProperties(pointDto, SmpSamplingPoint.class);
                point.setPlanId(plan.getId());
                point.setSortOrder(sortOrder++);
                if (StrUtil.isBlank(point.getPointCode())) {
                    point.setPointCode("P" + String.format("%03d", sortOrder - 1));
                }
                samplingPointMapper.insert(point);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePlan(Long id) {
        SmpSamplingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        if (plan.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可删除");
        }

        samplingPointMapper.delete(new LambdaQueryWrapper<SmpSamplingPoint>()
                .eq(SmpSamplingPoint::getPlanId, id));

        samplingTaskMapper.delete(new LambdaQueryWrapper<SmpSamplingTask>()
                .eq(SmpSamplingTask::getPlanId, id));

        this.removeById(id);
    }

    @Override
    public SamplingPlanDetailVO getPlanDetail(Long id) {
        SmpSamplingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        SamplingPlanDetailVO vo = BeanUtil.copyProperties(plan, SamplingPlanDetailVO.class);
        vo.setStatusName(getPlanStatusName(plan.getStatus()));

        List<SmpSamplingPoint> pointList = samplingPointMapper.selectList(
                new LambdaQueryWrapper<SmpSamplingPoint>()
                        .eq(SmpSamplingPoint::getPlanId, id)
                        .orderByAsc(SmpSamplingPoint::getSortOrder)
        );
        vo.setPointList(pointList);

        List<SmpSamplingTask> taskList = samplingTaskMapper.selectList(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getPlanId, id)
                        .orderByDesc(SmpSamplingTask::getCreateTime)
        );
        vo.setTaskList(taskList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPlan(Long id) {
        SmpSamplingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        if (plan.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可提交");
        }

        Long pointCount = samplingPointMapper.selectCount(
                new LambdaQueryWrapper<SmpSamplingPoint>().eq(SmpSamplingPoint::getPlanId, id)
        );
        if (pointCount == 0) {
            throw new BizException("请先添加采样点位");
        }

        plan.setStatus(STATUS_PENDING_ASSIGN);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(TaskAssignDTO dto) {
        SmpSamplingPlan plan = this.getById(dto.getPlanId());
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        if (plan.getStatus() != STATUS_PENDING_ASSIGN && plan.getStatus() != STATUS_ASSIGNED) {
            throw new BizException("计划状态不正确，无法分配任务");
        }

        for (TaskAssignDTO.TaskAssignItem item : dto.getTaskList()) {
            SmpSamplingPoint point = samplingPointMapper.selectById(item.getPointId());
            if (point == null) {
                throw new BizException("点位不存在");
            }

            SmpSamplingTask existingTask = samplingTaskMapper.selectOne(
                    new LambdaQueryWrapper<SmpSamplingTask>()
                            .eq(SmpSamplingTask::getPlanId, dto.getPlanId())
                            .eq(SmpSamplingTask::getPointId, item.getPointId())
            );

            SmpSamplingTask task;
            if (existingTask != null) {
                task = existingTask;
            } else {
                task = new SmpSamplingTask();
                task.setTaskNo(codeGenerator.generateSamplingTaskNo());
                task.setPlanId(dto.getPlanId());
                task.setPlanNo(plan.getPlanNo());
                task.setPlanName(plan.getPlanName());
                task.setPointId(point.getId());
                task.setPointCode(point.getPointCode());
                task.setPointName(point.getPointName());
                task.setPointAddress(point.getPointAddress());
                task.setLongitude(point.getLongitude());
                task.setLatitude(point.getLatitude());
                task.setStatus(0);
                task.setSyncStatus(0);
            }
            task.setSamplerId(item.getSamplerId());
            task.setSamplerName(item.getSamplerName());
            task.setAssignTime(LocalDateTime.now());
            task.setPlanSamplingDate(dto.getPlanSamplingDate());

            if (existingTask != null) {
                samplingTaskMapper.updateById(task);
            } else {
                samplingTaskMapper.insert(task);
            }
        }

        plan.setStatus(STATUS_ASSIGNED);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelPlan(Long id, String reason) {
        SmpSamplingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("采样计划不存在");
        }
        if (plan.getStatus() == STATUS_SAMPLING) {
            throw new BizException("采样中的计划无法取消");
        }
        if (plan.getStatus() == STATUS_COMPLETED) {
            throw new BizException("已完成的计划无法取消");
        }

        plan.setStatus(STATUS_CANCELLED);
        plan.setRemark(StrUtil.isNotBlank(plan.getRemark())
                ? plan.getRemark() + "; 取消原因：" + reason
                : "取消原因：" + reason);
        this.updateById(plan);

        samplingTaskMapper.update(
                null,
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getPlanId, id)
                        .eq(SmpSamplingTask::getStatus, 0)
                        .or()
                        .eq(SmpSamplingTask::getStatus, 1)
                        .set(SmpSamplingTask::getStatus, 5)
        );
    }
}
