package com.lims.detection.service.impl;

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
import com.lims.detection.dto.ScheduleGenerateDTO;
import com.lims.detection.entity.*;
import com.lims.detection.mapper.*;
import com.lims.detection.service.ScheduleService;
import com.lims.detection.vo.*;
import com.lims.security.utils.SecurityUtils;
import com.lims.system.entity.SysDept;
import com.lims.system.entity.SysUser;
import com.lims.system.mapper.SysDeptMapper;
import com.lims.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl extends ServiceImpl<DetSchedulePlanMapper, DetSchedulePlan> implements ScheduleService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private DetDetectionTaskMapper detectionTaskMapper;

    @Autowired
    private DetEquipmentUsageMapper equipmentUsageMapper;

    @Autowired
    private DetUserQualificationMapper userQualificationMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getStrategyName(Integer strategy) {
        if (strategy == null) return "";
        switch (strategy) {
            case 1: return "人员资质优先";
            case 2: return "设备空闲优先";
            case 3: return "优先级优先";
            case 4: return "综合平衡";
            default: return "";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已应用";
            case 2: return "已取消";
            default: return "";
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<SchedulePlanVO> generateSchedulePlans(ScheduleGenerateDTO dto) {
        List<SchedulePlanVO> result = new ArrayList<>();

        for (Long taskId : dto.getTaskIdList()) {
            DetDetectionTask task = detectionTaskMapper.selectById(taskId);
            if (task == null) {
                continue;
            }

            SchedulePlanVO plan = new SchedulePlanVO();
            plan.setPlanNo(codeGenerator.generateSchedulePlanNo());
            plan.setTaskId(taskId);
            plan.setTaskNo(task.getTaskNo());
            plan.setTaskName(task.getTaskName());
            plan.setPriority(task.getPriority());
            plan.setPriorityName(getPriorityName(task.getPriority()));
            plan.setGenerateTime(LocalDateTime.now());
            plan.setGenerateStrategy(dto.getStrategy());
            plan.setGenerateStrategyName(getStrategyName(dto.getStrategy()));
            plan.setStatus(0);
            plan.setStatusName("草稿");

            List<GanttTaskVO> scheduleTasks = generateTaskSchedule(task, dto);
            plan.setScheduleTasks(scheduleTasks);

            try {
                plan.setTaskScheduleJson(objectMapper.writeValueAsString(scheduleTasks));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化排程数据失败");
            }

            DetSchedulePlan entity = BeanUtil.copyProperties(plan, DetSchedulePlan.class);
            entity.setCreateBy(SecurityUtils.getCurrentUserId());
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            this.save(entity);

            plan.setId(entity.getId());
            result.add(plan);
        }

        return result;
    }

    @Override
    public PageResult<SchedulePlanVO> selectPage(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DetSchedulePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(DetSchedulePlan::getCreateTime);

        Page<DetSchedulePlan> page = new Page<>(pageNum, pageSize);
        IPage<DetSchedulePlan> pageResult = this.page(page, wrapper);

        IPage<SchedulePlanVO> voPage = pageResult.convert(plan -> {
            SchedulePlanVO vo = BeanUtil.copyProperties(plan, SchedulePlanVO.class);
            vo.setPriorityName(getPriorityName(plan.getPriority()));
            vo.setGenerateStrategyName(getStrategyName(plan.getGenerateStrategy()));
            vo.setStatusName(getStatusName(plan.getStatus()));
            try {
                if (StrUtil.isNotBlank(plan.getTaskScheduleJson())) {
                    List<GanttTaskVO> tasks = objectMapper.readValue(
                            plan.getTaskScheduleJson(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, GanttTaskVO.class));
                    vo.setScheduleTasks(tasks);
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析排程数据失败");
            }
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public SchedulePlanVO getDetail(Long id) {
        DetSchedulePlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("排程计划不存在");
        }

        SchedulePlanVO vo = BeanUtil.copyProperties(plan, SchedulePlanVO.class);
        vo.setPriorityName(getPriorityName(plan.getPriority()));
        vo.setGenerateStrategyName(getStrategyName(plan.getGenerateStrategy()));
        vo.setStatusName(getStatusName(plan.getStatus()));
        try {
            if (StrUtil.isNotBlank(plan.getTaskScheduleJson())) {
                List<GanttTaskVO> tasks = objectMapper.readValue(
                        plan.getTaskScheduleJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, GanttTaskVO.class));
                vo.setScheduleTasks(tasks);
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析排程数据失败");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applySchedulePlan(Long id) {
        DetSchedulePlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("排程计划不存在");
        }
        if (plan.getStatus() == null || plan.getStatus() != 0) {
            throw new BizException("排程计划状态不正确，不能应用");
        }

        plan.setStatus(1);
        plan.setApplyTime(LocalDateTime.now());
        plan.setApplyBy(SecurityUtils.getCurrentUserId());
        plan.setUpdateTime(LocalDateTime.now());
        this.updateById(plan);

        try {
            if (StrUtil.isNotBlank(plan.getTaskScheduleJson())) {
                List<GanttTaskVO> tasks = objectMapper.readValue(
                        plan.getTaskScheduleJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, GanttTaskVO.class));
                for (GanttTaskVO task : tasks) {
                    DetDetectionTask detectionTask = detectionTaskMapper.selectById(task.getTaskId());
                    if (detectionTask != null) {
                        detectionTask.setAssigneeId(task.getAssigneeId());
                        detectionTask.setAssigneeName(task.getAssigneeName());
                        detectionTask.setDeptId(task.getDeptId());
                        detectionTask.setPlanStartDate(task.getStartDate());
                        detectionTask.setPlanEndDate(task.getEndDate());
                        detectionTask.setUpdateTime(LocalDateTime.now());
                        detectionTaskMapper.updateById(detectionTask);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析排程数据失败");
        }
    }

    @Override
    public GanttChartVO getGanttChart(String resourceType, LocalDate startDate, LocalDate endDate) {
        GanttChartVO gantt = new GanttChartVO();
        gantt.setStartDate(startDate);
        gantt.setEndDate(endDate);

        List<GanttResourceVO> resources = new ArrayList<>();
        List<GanttTaskVO> tasks = new ArrayList<>();

        if ("user".equals(resourceType)) {
            List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1));
            for (SysUser user : users) {
                GanttResourceVO resource = new GanttResourceVO();
                resource.setResourceId(String.valueOf(user.getId()));
                resource.setResourceName(user.getRealName());
                resource.setResourceType("user");
                if (user.getDeptId() != null) {
                    SysDept dept = deptMapper.selectById(user.getDeptId());
                    if (dept != null) {
                        resource.setParentId(String.valueOf(dept.getId()));
                    }
                }
                resources.add(resource);

                List<DetDetectionTask> userTasks = detectionTaskMapper.selectList(
                        new LambdaQueryWrapper<DetDetectionTask>()
                                .eq(DetDetectionTask::getAssigneeId, user.getId())
                                .ne(DetDetectionTask::getStatus, 7)
                                .ge(DetDetectionTask::getPlanStartDate, startDate)
                                .le(DetDetectionTask::getPlanEndDate, endDate)
                );
                for (DetDetectionTask task : userTasks) {
                    GanttTaskVO ganttTask = convertToGanttTask(task, "user", String.valueOf(user.getId()));
                    tasks.add(ganttTask);
                }
            }

            List<SysDept> depts = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().eq(SysDept::getStatus, 1));
            for (SysDept dept : depts) {
                GanttResourceVO resource = new GanttResourceVO();
                resource.setResourceId(String.valueOf(dept.getId()));
                resource.setResourceName(dept.getDeptName());
                resource.setResourceType("dept");
                resources.add(resource);
            }
        } else if ("dept".equals(resourceType)) {
            List<SysDept> depts = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().eq(SysDept::getStatus, 1));
            for (SysDept dept : depts) {
                GanttResourceVO resource = new GanttResourceVO();
                resource.setResourceId(String.valueOf(dept.getId()));
                resource.setResourceName(dept.getDeptName());
                resource.setResourceType("dept");
                resources.add(resource);

                List<DetDetectionTask> deptTasks = detectionTaskMapper.selectList(
                        new LambdaQueryWrapper<DetDetectionTask>()
                                .eq(DetDetectionTask::getDeptId, dept.getId())
                                .ne(DetDetectionTask::getStatus, 7)
                                .ge(DetDetectionTask::getPlanStartDate, startDate)
                                .le(DetDetectionTask::getPlanEndDate, endDate)
                );
                for (DetDetectionTask task : deptTasks) {
                    GanttTaskVO ganttTask = convertToGanttTask(task, "dept", String.valueOf(dept.getId()));
                    tasks.add(ganttTask);
                }
            }
        } else if ("equipment".equals(resourceType)) {
            List<DetEquipmentUsage> usages = equipmentUsageMapper.selectList(
                    new LambdaQueryWrapper<DetEquipmentUsage>()
                            .ge(DetEquipmentUsage::getPlanStartTime, startDate.atStartOfDay())
                            .le(DetEquipmentUsage::getPlanEndTime, endDate.atTime(LocalTime.MAX))
                            .groupBy(DetEquipmentUsage::getEquipmentId)
            );

            Set<Long> equipmentIds = usages.stream()
                    .map(DetEquipmentUsage::getEquipmentId)
                    .collect(Collectors.toSet());

            for (Long equipmentId : equipmentIds) {
                GanttResourceVO resource = new GanttResourceVO();
                resource.setResourceId(String.valueOf(equipmentId));
                DetEquipmentUsage firstUsage = usages.stream()
                        .filter(u -> u.getEquipmentId().equals(equipmentId))
                        .findFirst()
                        .orElse(null);
                resource.setResourceName(firstUsage != null ? firstUsage.getEquipmentName() : "设备-" + equipmentId);
                resource.setResourceType("equipment");
                resources.add(resource);

                List<DetEquipmentUsage> equipmentUsages = equipmentUsageMapper.selectList(
                        new LambdaQueryWrapper<DetEquipmentUsage>()
                                .eq(DetEquipmentUsage::getEquipmentId, equipmentId)
                                .ge(DetEquipmentUsage::getPlanStartTime, startDate.atStartOfDay())
                                .le(DetEquipmentUsage::getPlanEndTime, endDate.atTime(LocalTime.MAX))
                );
                for (DetEquipmentUsage usage : equipmentUsages) {
                    DetDetectionTask task = detectionTaskMapper.selectById(usage.getTaskId());
                    if (task != null) {
                        GanttTaskVO ganttTask = convertToGanttTask(task, "equipment", String.valueOf(equipmentId));
                        ganttTask.setAssigneeId(usage.getUserId());
                        ganttTask.setAssigneeName(usage.getUserName());
                        tasks.add(ganttTask);
                    }
                }
            }
        }

        gantt.setResources(resources);
        gantt.setTasks(tasks);
        return gantt;
    }

    private List<GanttTaskVO> generateTaskSchedule(DetDetectionTask task, ScheduleGenerateDTO dto) {
        List<GanttTaskVO> tasks = new ArrayList<>();

        List<Long> taskItemIds = new ArrayList<>();
        try {
            if (StrUtil.isNotBlank(task.getTestItemIds())) {
                taskItemIds = objectMapper.readValue(task.getTestItemIds(), List.class);
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetUserQualification> qualifications = userQualificationMapper.selectList(
                new LambdaQueryWrapper<DetUserQualification>().eq(DetUserQualification::getStatus, 1));
        Map<Long, List<Long>> userTestItemsMap = new HashMap<>();
        for (DetUserQualification qual : qualifications) {
            try {
                if (StrUtil.isNotBlank(qual.getTestItemIds())) {
                    List<Long> itemIds = objectMapper.readValue(qual.getTestItemIds(), List.class);
                    userTestItemsMap.computeIfAbsent(qual.getUserId(), k -> new ArrayList<>()).addAll(itemIds);
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析JSON数据失败");
            }
        }

        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1));
        List<AssigneeCandidateVO> candidates = new ArrayList<>();

        for (SysUser user : users) {
            AssigneeCandidateVO candidate = new AssigneeCandidateVO();
            candidate.setUserId(user.getId());
            candidate.setUserName(user.getRealName());
            candidate.setDeptId(user.getDeptId());

            BigDecimal qualScore = BigDecimal.ZERO;
            List<Long> userItems = userTestItemsMap.getOrDefault(user.getId(), Collections.emptyList());
            if (!taskItemIds.isEmpty() && !userItems.isEmpty()) {
                int matchCount = 0;
                for (Long itemId : taskItemIds) {
                    if (userItems.contains(itemId)) {
                        matchCount++;
                    }
                }
                qualScore = new BigDecimal(matchCount * 100.0 / taskItemIds.size()).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            candidate.setQualificationScore(qualScore);

            long workload = detectionTaskMapper.selectCount(
                    new LambdaQueryWrapper<DetDetectionTask>()
                            .eq(DetDetectionTask::getAssigneeId, user.getId())
                            .in(DetDetectionTask::getStatus, 1, 2, 3, 4));
            candidate.setCurrentWorkload((int) workload);

            BigDecimal equipmentAvail = new BigDecimal(80 + Math.random() * 20).setScale(2, BigDecimal.ROUND_HALF_UP);
            candidate.setEquipmentAvailability(equipmentAvail);

            BigDecimal workloadFactor = workload > 5 ? new BigDecimal(0.5) : workload > 3 ? new BigDecimal(0.8) : BigDecimal.ONE;
            BigDecimal matchScore = qualScore.multiply(new BigDecimal(0.5))
                    .add(equipmentAvail.multiply(new BigDecimal(0.2)))
                    .add(new BigDecimal(100 - workload * 10).max(BigDecimal.ZERO).multiply(new BigDecimal(0.3)));
            matchScore = matchScore.multiply(workloadFactor).setScale(2, BigDecimal.ROUND_HALF_UP);
            candidate.setMatchScore(matchScore);

            candidates.add(candidate);
        }

        candidates.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));

        AssigneeCandidateVO selected = candidates.isEmpty() ? null : candidates.get(0);

        GanttTaskVO ganttTask = new GanttTaskVO();
        ganttTask.setTaskId(task.getId());
        ganttTask.setTaskNo(task.getTaskNo());
        ganttTask.setTaskName(task.getTaskName());
        ganttTask.setPriority(task.getPriority());
        ganttTask.setPriorityName(getPriorityName(task.getPriority()));
        if (selected != null) {
            ganttTask.setAssigneeId(selected.getUserId());
            ganttTask.setAssigneeName(selected.getUserName());
            ganttTask.setDeptId(selected.getDeptId());
        }
        LocalDate startDate = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now().plusDays(1);
        int days = task.getExpectedDays() != null ? task.getExpectedDays() : 3;
        ganttTask.setStartDate(startDate);
        ganttTask.setEndDate(startDate.plusDays(days - 1));
        ganttTask.setProgress(task.getProgress() != null ? task.getProgress() : BigDecimal.ZERO);
        ganttTask.setStatus(task.getStatus());
        ganttTask.setStatusName(getTaskStatusName(task.getStatus()));
        ganttTask.setResourceId(selected != null ? String.valueOf(selected.getUserId()) : null);
        tasks.add(ganttTask);

        return tasks;
    }

    private GanttTaskVO convertToGanttTask(DetDetectionTask task, String resourceType, String resourceId) {
        GanttTaskVO vo = new GanttTaskVO();
        vo.setTaskId(task.getId());
        vo.setTaskNo(task.getTaskNo());
        vo.setTaskName(task.getTaskName());
        vo.setPriority(task.getPriority());
        vo.setPriorityName(getPriorityName(task.getPriority()));
        vo.setAssigneeId(task.getAssigneeId());
        vo.setAssigneeName(task.getAssigneeName());
        vo.setDeptId(task.getDeptId());
        vo.setStartDate(task.getPlanStartDate());
        vo.setEndDate(task.getPlanEndDate());
        vo.setProgress(task.getProgress() != null ? task.getProgress() : BigDecimal.ZERO);
        vo.setStatus(task.getStatus());
        vo.setStatusName(getTaskStatusName(task.getStatus()));
        vo.setResourceId(resourceId);
        return vo;
    }

    private String getPriorityName(Integer priority) {
        if (priority == null) return "";
        switch (priority) {
            case 1: return "高";
            case 2: return "中";
            case 3: return "低";
            default: return "";
        }
    }

    private String getTaskStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待分配";
            case 1: return "待接收";
            case 2: return "进行中";
            case 3: return "数据录入";
            case 4: return "审核中";
            case 5: return "已完成";
            case 6: return "已暂停";
            case 7: return "已终止";
            default: return "";
        }
    }
}
