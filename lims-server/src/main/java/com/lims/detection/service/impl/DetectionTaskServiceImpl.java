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
import com.lims.detection.dto.DetectionTaskQuery;
import com.lims.detection.dto.DetectionTaskSaveDTO;
import com.lims.detection.dto.TaskAssignDTO;
import com.lims.detection.dto.TaskReviewDTO;
import com.lims.detection.entity.*;
import com.lims.detection.mapper.*;
import com.lims.detection.service.DetectionTaskService;
import com.lims.detection.vo.*;
import com.lims.detection.websocket.DetectionTaskWebSocketHandler;
import com.lims.personnel.entity.PerPersonnelAuthorization;
import com.lims.personnel.mapper.PerPersonnelAuthorizationMapper;
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
public class DetectionTaskServiceImpl extends ServiceImpl<DetDetectionTaskMapper, DetDetectionTask> implements DetectionTaskService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private DetTaskAssignmentMapper taskAssignmentMapper;

    @Autowired
    private DetTaskStatusLogMapper statusLogMapper;

    @Autowired
    private DetEquipmentUsageMapper equipmentUsageMapper;

    @Autowired
    private DetUserQualificationMapper userQualificationMapper;

    @Autowired
    private PerPersonnelAuthorizationMapper personnelAuthorizationMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private DetectionTaskWebSocketHandler webSocketHandler;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_PENDING_ASSIGN = 0;
    private static final int STATUS_PENDING_ACCEPT = 1;
    private static final int STATUS_IN_PROGRESS = 2;
    private static final int STATUS_DATA_ENTRY = 3;
    private static final int STATUS_REVIEWING = 4;
    private static final int STATUS_COMPLETED = 5;
    private static final int STATUS_PAUSED = 6;
    private static final int STATUS_TERMINATED = 7;

    private String getStatusName(Integer status) {
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

    private String getPriorityName(Integer priority) {
        if (priority == null) return "";
        switch (priority) {
            case 1: return "高";
            case 2: return "中";
            case 3: return "低";
            default: return "";
        }
    }

    private String getAssignTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "手动分配";
            case 2: return "智能分配";
            case 3: return "抢单";
            case 4: return "改派";
            default: return "";
        }
    }

    private String getUsageStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待使用";
            case 1: return "使用中";
            case 2: return "已完成";
            default: return "";
        }
    }

    @Override
    public PageResult<DetectionTaskVO> selectPage(DetectionTaskQuery query) {
        LambdaQueryWrapper<DetDetectionTask> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(DetDetectionTask::getTaskNo, query.getTaskNo());
        }
        if (StrUtil.isNotBlank(query.getTaskName())) {
            wrapper.like(DetDetectionTask::getTaskName, query.getTaskName());
        }
        if (StrUtil.isNotBlank(query.getEntrustNo())) {
            wrapper.like(DetDetectionTask::getEntrustNo, query.getEntrustNo());
        }
        if (StrUtil.isNotBlank(query.getSampleNo())) {
            wrapper.like(DetDetectionTask::getSampleNo, query.getSampleNo());
        }
        if (StrUtil.isNotBlank(query.getCustomerName())) {
            wrapper.like(DetDetectionTask::getCustomerName, query.getCustomerName());
        }
        if (query.getMethodId() != null) {
            wrapper.eq(DetDetectionTask::getMethodId, query.getMethodId());
        }
        if (query.getPriority() != null) {
            wrapper.eq(DetDetectionTask::getPriority, query.getPriority());
        }
        if (query.getIsUrgent() != null) {
            wrapper.eq(DetDetectionTask::getIsUrgent, query.getIsUrgent());
        }
        if (query.getAssignType() != null) {
            wrapper.eq(DetDetectionTask::getAssignType, query.getAssignType());
        }
        if (query.getAssigneeType() != null) {
            wrapper.eq(DetDetectionTask::getAssigneeType, query.getAssigneeType());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(DetDetectionTask::getDeptId, query.getDeptId());
        }
        if (query.getAssigneeId() != null) {
            wrapper.eq(DetDetectionTask::getAssigneeId, query.getAssigneeId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DetDetectionTask::getStatus, query.getStatus());
        }
        if (query.getIsGrabOrder() != null) {
            wrapper.eq(DetDetectionTask::getIsGrabOrder, query.getIsGrabOrder());
        }
        if (query.getPlanStartDateStart() != null) {
            wrapper.ge(DetDetectionTask::getPlanStartDate, query.getPlanStartDateStart());
        }
        if (query.getPlanStartDateEnd() != null) {
            wrapper.le(DetDetectionTask::getPlanStartDate, query.getPlanStartDateEnd());
        }
        if (query.getPlanEndDateStart() != null) {
            wrapper.ge(DetDetectionTask::getPlanEndDate, query.getPlanEndDateStart());
        }
        if (query.getPlanEndDateEnd() != null) {
            wrapper.le(DetDetectionTask::getPlanEndDate, query.getPlanEndDateEnd());
        }
        wrapper.orderByDesc(DetDetectionTask::getPriority, DetDetectionTask::getCreateTime);

        Page<DetDetectionTask> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<DetDetectionTask> pageResult = this.page(page, wrapper);

        IPage<DetectionTaskVO> voPage = pageResult.convert(task -> convertToVO(task));

        return PageResult.of(voPage);
    }

    @Override
    public DetectionTaskDetailVO getDetail(Long id) {
        DetDetectionTask task = this.getById(id);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }

        DetectionTaskDetailVO vo = BeanUtil.copyProperties(task, DetectionTaskDetailVO.class);
        vo.setStatusName(getStatusName(task.getStatus()));
        vo.setPriorityName(getPriorityName(task.getPriority()));
        vo.setAssignTypeName(getAssignTypeName(task.getAssignType()));

        try {
            if (StrUtil.isNotBlank(task.getTestItemIds())) {
                vo.setTestItemIdList(objectMapper.readValue(task.getTestItemIds(), List.class));
            }
            if (StrUtil.isNotBlank(task.getTestItemNames())) {
                vo.setTestItemNameList(objectMapper.readValue(task.getTestItemNames(), List.class));
            }
            if (StrUtil.isNotBlank(task.getEquipmentIds())) {
                vo.setEquipmentIdList(objectMapper.readValue(task.getEquipmentIds(), List.class));
            }
            if (StrUtil.isNotBlank(task.getEquipmentNames())) {
                vo.setEquipmentNameList(objectMapper.readValue(task.getEquipmentNames(), List.class));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetTaskStatusLog> statusLogList = statusLogMapper.selectList(
                new LambdaQueryWrapper<DetTaskStatusLog>()
                        .eq(DetTaskStatusLog::getTaskId, id)
                        .orderByDesc(DetTaskStatusLog::getOperateTime)
        );
        vo.setStatusLogList(statusLogList.stream().map(log -> {
            TaskStatusLogVO logVO = BeanUtil.copyProperties(log, TaskStatusLogVO.class);
            logVO.setBeforeStatusName(getStatusName(log.getBeforeStatus()));
            logVO.setAfterStatusName(getStatusName(log.getAfterStatus()));
            return logVO;
        }).collect(Collectors.toList()));

        List<DetTaskAssignment> assignmentList = taskAssignmentMapper.selectList(
                new LambdaQueryWrapper<DetTaskAssignment>()
                        .eq(DetTaskAssignment::getTaskId, id)
                        .orderByDesc(DetTaskAssignment::getOperateTime)
        );
        vo.setAssignmentList(assignmentList.stream().map(assign -> {
            TaskAssignmentVO assignVO = BeanUtil.copyProperties(assign, TaskAssignmentVO.class);
            assignVO.setAssignmentTypeName(getAssignTypeName(assign.getAssignmentType()));
            return assignVO;
        }).collect(Collectors.toList()));

        List<DetEquipmentUsage> equipmentUsageList = equipmentUsageMapper.selectList(
                new LambdaQueryWrapper<DetEquipmentUsage>()
                        .eq(DetEquipmentUsage::getTaskId, id)
                        .orderByAsc(DetEquipmentUsage::getPlanStartTime)
        );
        vo.setEquipmentUsageList(equipmentUsageList.stream().map(usage -> {
            EquipmentUsageVO usageVO = BeanUtil.copyProperties(usage, EquipmentUsageVO.class);
            usageVO.setUsageStatusName(getUsageStatusName(usage.getUsageStatus()));
            return usageVO;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTask(DetectionTaskSaveDTO dto) {
        DetDetectionTask task = BeanUtil.copyProperties(dto, DetDetectionTask.class);
        task.setTaskNo(codeGenerator.generateDetectionTaskNo());
        task.setStatus(STATUS_PENDING_ASSIGN);
        task.setProgress(BigDecimal.ZERO);

        if (task.getPriority() == null) {
            task.setPriority(2);
        }
        if (task.getIsUrgent() == null) {
            task.setIsUrgent(0);
        }
        if (task.getIsGrabOrder() == null) {
            task.setIsGrabOrder(0);
        }

        try {
            if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                task.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
            }
            if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                task.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
            }
            if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                task.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
            }
            if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                task.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.save(task);
        saveStatusLog(task.getId(), task.getTaskNo(), null, STATUS_PENDING_ASSIGN, "创建", "创建检测任务");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTask(DetectionTaskSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("任务ID不能为空");
        }

        DetDetectionTask oldTask = this.getById(dto.getId());
        if (oldTask == null) {
            throw new BizException("检测任务不存在");
        }

        if (oldTask.getStatus() > STATUS_PENDING_ASSIGN && oldTask.getStatus() != STATUS_PAUSED) {
            throw new BizException("任务已分配，不能编辑");
        }

        DetDetectionTask task = BeanUtil.copyProperties(dto, DetDetectionTask.class);
        task.setTaskNo(oldTask.getTaskNo());
        task.setStatus(oldTask.getStatus());
        task.setProgress(oldTask.getProgress());

        try {
            if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                task.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
            }
            if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                task.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
            }
            if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                task.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
            }
            if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                task.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(Long id) {
        DetDetectionTask task = this.getById(id);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() > STATUS_PENDING_ASSIGN) {
            throw new BizException("任务已分配，不能删除");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTasks(TaskAssignDTO dto) {
        for (Long taskId : dto.getTaskIdList()) {
            DetDetectionTask task = this.getById(taskId);
            if (task == null) {
                throw new BizException("检测任务不存在，ID: " + taskId);
            }
            if (task.getStatus() != STATUS_PENDING_ASSIGN && task.getStatus() != STATUS_PAUSED) {
                throw new BizException("任务状态不正确，不能分配，任务编号: " + task.getTaskNo());
            }

            if (dto.getAssigneeId() != null && dto.getAssigneeType() == 1) {
                checkPersonnelQualification(dto.getAssigneeId(), dto.getAssigneeName(), task);
            }

            DetTaskAssignment assignment = new DetTaskAssignment();
            assignment.setTaskId(taskId);
            assignment.setTaskNo(task.getTaskNo());
            assignment.setAssignmentType(dto.getAssignmentType());
            assignment.setAssigneeType(dto.getAssigneeType());
            assignment.setOldDeptId(task.getDeptId());
            assignment.setOldDeptName(task.getDeptName());
            assignment.setOldAssigneeId(task.getAssigneeId());
            assignment.setOldAssigneeName(task.getAssigneeName());
            assignment.setNewDeptId(dto.getDeptId());
            assignment.setNewDeptName(dto.getDeptName());
            assignment.setNewAssigneeId(dto.getAssigneeId());
            assignment.setNewAssigneeName(dto.getAssigneeName());
            assignment.setAssignReason(dto.getAssignReason());
            assignment.setOperatorId(SecurityUtils.getCurrentUserId());
            assignment.setOperatorName(SecurityUtils.getCurrentUsername());
            assignment.setOperateTime(LocalDateTime.now());
            taskAssignmentMapper.insert(assignment);

            Integer oldStatus = task.getStatus();
            task.setAssignType(dto.getAssignmentType());
            task.setAssigneeType(dto.getAssigneeType());
            task.setDeptId(dto.getDeptId());
            task.setDeptName(dto.getDeptName());
            task.setAssigneeId(dto.getAssigneeId());
            task.setAssigneeName(dto.getAssigneeName());
            task.setAssignTime(dto.getAssignTime() != null ? dto.getAssignTime() : LocalDateTime.now());
            task.setPlanStartDate(dto.getPlanStartDate() != null ? dto.getPlanStartDate().toLocalDate() : task.getPlanStartDate());
            task.setPlanEndDate(dto.getPlanEndDate() != null ? dto.getPlanEndDate().toLocalDate() : task.getPlanEndDate());
            task.setIsGrabOrder(0);
            task.setStatus(STATUS_PENDING_ACCEPT);
            this.updateById(task);

            saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_PENDING_ACCEPT, "分配", "分配给: " + (dto.getAssigneeName() != null ? dto.getAssigneeName() : dto.getDeptName()));

            createEquipmentUsage(task);

            WebSocketMessageVO wsMessage = new WebSocketMessageVO();
            wsMessage.setType("TASK_ASSIGNED");
            wsMessage.setTitle("新任务分配");
            wsMessage.setContent("任务【" + task.getTaskName() + "】已分配给您");
            wsMessage.setData(convertToVO(task));
            wsMessage.setSender(SecurityUtils.getCurrentUsername());
            if (dto.getAssigneeId() != null) {
                webSocketHandler.sendToUser(dto.getAssigneeId(), wsMessage);
            } else if (dto.getDeptId() != null) {
                webSocketHandler.sendToDept(dto.getDeptId(), wsMessage);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptTask(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_PENDING_ACCEPT) {
            throw new BizException("任务状态不正确，不能接收");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        if (task.getAssigneeId() != null && !task.getAssigneeId().equals(currentUserId)) {
            throw new BizException("您不是该任务的负责人，不能接收");
        }

        Integer oldStatus = task.getStatus();
        task.setStatus(STATUS_IN_PROGRESS);
        task.setAcceptTime(LocalDateTime.now());
        task.setActualStartTime(LocalDateTime.now());
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_IN_PROGRESS, "接收", currentUserName + " 接收任务");

        updateEquipmentUsageStatus(taskId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTask(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_IN_PROGRESS) {
            throw new BizException("任务状态不正确，不能开始");
        }

        Integer oldStatus = task.getStatus();
        task.setStatus(STATUS_IN_PROGRESS);
        if (task.getActualStartTime() == null) {
            task.setActualStartTime(LocalDateTime.now());
        }
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_IN_PROGRESS, "开始检测", "开始进行检测");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitDataEntry(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_IN_PROGRESS && task.getStatus() != STATUS_DATA_ENTRY) {
            throw new BizException("任务状态不正确，不能提交数据录入");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        Integer oldStatus = task.getStatus();
        task.setStatus(STATUS_DATA_ENTRY);
        task.setDataEntryUserId(currentUserId);
        task.setDataEntryUserName(currentUserName);
        task.setProgress(new BigDecimal(50));
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_DATA_ENTRY, "数据录入", currentUserName + " 提交数据录入");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_DATA_ENTRY) {
            throw new BizException("任务状态不正确，不能提交审核");
        }

        Integer oldStatus = task.getStatus();
        task.setStatus(STATUS_REVIEWING);
        task.setProgress(new BigDecimal(80));
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_REVIEWING, "提交审核", "提交审核");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewTask(TaskReviewDTO dto) {
        DetDetectionTask task = this.getById(dto.getTaskId());
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_REVIEWING) {
            throw new BizException("任务状态不正确，不能审核");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        Integer oldStatus = task.getStatus();
        task.setReviewerId(currentUserId);
        task.setReviewerName(currentUserName);
        task.setReviewOpinion(dto.getReviewOpinion());
        task.setReviewResult(dto.getReviewResult());
        task.setReviewTime(LocalDateTime.now());

        if (dto.getReviewResult() == 1) {
            task.setStatus(STATUS_COMPLETED);
            task.setActualEndTime(LocalDateTime.now());
            task.setProgress(new BigDecimal(100));
            saveStatusLog(dto.getTaskId(), task.getTaskNo(), oldStatus, STATUS_COMPLETED, "审核通过", "审核通过，任务完成");
            updateEquipmentUsageStatus(dto.getTaskId(), 2);
        } else {
            task.setStatus(STATUS_DATA_ENTRY);
            task.setProgress(new BigDecimal(50));
            saveStatusLog(dto.getTaskId(), task.getTaskNo(), oldStatus, STATUS_DATA_ENTRY, "审核驳回", "审核驳回: " + dto.getReviewOpinion());
        }

        this.updateById(task);

        WebSocketMessageVO wsMessage = new WebSocketMessageVO();
        wsMessage.setData(convertToVO(task));
        wsMessage.setSender(currentUserName);
        if (dto.getReviewResult() == 1) {
            wsMessage.setType("TASK_COMPLETED");
            wsMessage.setTitle("任务审核通过");
            wsMessage.setContent("任务【" + task.getTaskName() + "】已审核通过，任务完成");
        } else {
            wsMessage.setType("TASK_REJECTED");
            wsMessage.setTitle("任务审核驳回");
            wsMessage.setContent("任务【" + task.getTaskName() + "】审核被驳回: " + dto.getReviewOpinion());
        }
        if (task.getAssigneeId() != null) {
            webSocketHandler.sendToUser(task.getAssigneeId(), wsMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pauseTask(Long taskId, String reason) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() == STATUS_COMPLETED || task.getStatus() == STATUS_TERMINATED || task.getStatus() == STATUS_PAUSED) {
            throw new BizException("任务状态不正确，不能暂停");
        }

        Integer oldStatus = task.getStatus();
        task.setBeforePauseStatus(oldStatus);
        task.setStatus(STATUS_PAUSED);
        task.setPauseReason(reason);
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_PAUSED, "暂停", "暂停原因: " + reason);
        updateEquipmentUsageStatus(taskId, 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resumeTask(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() != STATUS_PAUSED) {
            throw new BizException("任务状态不正确，不能重启");
        }

        Integer oldStatus = task.getStatus();
        Integer resumeStatus = task.getBeforePauseStatus() != null ? task.getBeforePauseStatus() : STATUS_IN_PROGRESS;
        task.setStatus(resumeStatus);
        task.setPauseReason(null);
        task.setBeforePauseStatus(null);
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, resumeStatus, "重启", "任务重启");
        updateEquipmentUsageStatus(taskId, 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateTask(Long taskId, String reason) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getStatus() == STATUS_COMPLETED || task.getStatus() == STATUS_TERMINATED) {
            throw new BizException("任务已完成或已终止，不能重复操作");
        }

        Integer oldStatus = task.getStatus();
        task.setStatus(STATUS_TERMINATED);
        task.setTerminateReason(reason);
        task.setActualEndTime(LocalDateTime.now());
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_TERMINATED, "终止", "终止原因: " + reason);
        updateEquipmentUsageStatus(taskId, 2);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grabTask(Long taskId, Long userId, String userName) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }
        if (task.getIsGrabOrder() == null || task.getIsGrabOrder() != 1) {
            throw new BizException("该任务不在抢单池中");
        }
        if (task.getStatus() != STATUS_PENDING_ASSIGN) {
            throw new BizException("任务已被抢或已分配");
        }
        if (task.getGrabOrderExpireTime() != null && task.getGrabOrderExpireTime().isBefore(LocalDateTime.now())) {
            throw new BizException("抢单已过期");
        }

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        DetTaskAssignment assignment = new DetTaskAssignment();
        assignment.setTaskId(taskId);
        assignment.setTaskNo(task.getTaskNo());
        assignment.setAssignmentType(3);
        assignment.setAssigneeType(1);
        assignment.setNewAssigneeId(userId);
        assignment.setNewAssigneeName(userName);
        assignment.setNewDeptId(user.getDeptId());
        assignment.setAssignReason("抢单");
        assignment.setOperatorId(userId);
        assignment.setOperatorName(userName);
        assignment.setOperateTime(LocalDateTime.now());
        taskAssignmentMapper.insert(assignment);

        Integer oldStatus = task.getStatus();
        task.setAssignType(3);
        task.setAssigneeType(1);
        task.setAssigneeId(userId);
        task.setAssigneeName(userName);
        task.setDeptId(user.getDeptId());
        task.setAssignTime(LocalDateTime.now());
        task.setIsGrabOrder(0);
        task.setStatus(STATUS_PENDING_ACCEPT);
        this.updateById(task);

        saveStatusLog(taskId, task.getTaskNo(), oldStatus, STATUS_PENDING_ACCEPT, "抢单", userName + " 抢单成功");

        createEquipmentUsage(task);
    }

    @Override
    public List<DetectionTaskVO> getMyTasks(Long userId) {
        List<DetDetectionTask> list = this.list(
                new LambdaQueryWrapper<DetDetectionTask>()
                        .eq(DetDetectionTask::getAssigneeId, userId)
                        .ne(DetDetectionTask::getStatus, STATUS_TERMINATED)
                        .ne(DetDetectionTask::getStatus, STATUS_COMPLETED)
                        .orderByAsc(DetDetectionTask::getPriority, DetDetectionTask::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<DetectionTaskVO> getGrabOrderPool() {
        List<DetDetectionTask> list = this.list(
                new LambdaQueryWrapper<DetDetectionTask>()
                        .eq(DetDetectionTask::getIsGrabOrder, 1)
                        .eq(DetDetectionTask::getStatus, STATUS_PENDING_ASSIGN)
                        .and(wrapper -> wrapper.isNull(DetDetectionTask::getGrabOrderExpireTime)
                                .or()
                                .gt(DetDetectionTask::getGrabOrderExpireTime, LocalDateTime.now()))
                        .orderByDesc(DetDetectionTask::getPriority, DetDetectionTask::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<AssignmentRecommendationVO> getAssignmentRecommendations(Long taskId) {
        DetDetectionTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }

        List<AssignmentRecommendationVO> result = new ArrayList<>();
        AssignmentRecommendationVO recommendation = new AssignmentRecommendationVO();
        recommendation.setTaskId(taskId);
        recommendation.setTaskNo(task.getTaskNo());
        recommendation.setTaskName(task.getTaskName());

        List<Long> taskItemIds = new ArrayList<>();
        try {
            if (StrUtil.isNotBlank(task.getTestItemIds())) {
                taskItemIds = objectMapper.readValue(task.getTestItemIds(), List.class);
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetUserQualification> qualifications = userQualificationMapper.selectList(
                new LambdaQueryWrapper<DetUserQualification>()
                        .eq(DetUserQualification::getStatus, 1)
        );

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

        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1)
        );

        List<AssigneeCandidateVO> candidates = new ArrayList<>();
        for (SysUser user : users) {
            AssigneeCandidateVO candidate = new AssigneeCandidateVO();
            candidate.setUserId(user.getId());
            candidate.setUserName(user.getRealName());
            candidate.setDeptId(user.getDeptId());

            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    candidate.setDeptName(dept.getDeptName());
                }
            }

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

            long workload = this.count(
                    new LambdaQueryWrapper<DetDetectionTask>()
                            .eq(DetDetectionTask::getAssigneeId, user.getId())
                            .in(DetDetectionTask::getStatus, STATUS_PENDING_ACCEPT, STATUS_IN_PROGRESS, STATUS_DATA_ENTRY, STATUS_REVIEWING)
            );
            candidate.setCurrentWorkload((int) workload);

            BigDecimal equipmentAvail = new BigDecimal(80 + Math.random() * 20).setScale(2, BigDecimal.ROUND_HALF_UP);
            candidate.setEquipmentAvailability(equipmentAvail);

            BigDecimal workloadFactor = workload > 5 ? new BigDecimal(0.5) : workload > 3 ? new BigDecimal(0.8) : BigDecimal.ONE;
            BigDecimal matchScore = qualScore.multiply(new BigDecimal(0.5))
                    .add(equipmentAvail.multiply(new BigDecimal(0.2)))
                    .add(new BigDecimal(100 - workload * 10).max(BigDecimal.ZERO).multiply(new BigDecimal(0.3)));
            matchScore = matchScore.multiply(workloadFactor).setScale(2, BigDecimal.ROUND_HALF_UP);
            candidate.setMatchScore(matchScore);

            int expectedHours = (task.getExpectedDays() != null ? task.getExpectedDays() : 3) * 8;
            candidate.setEstimatedHours(new BigDecimal(expectedHours));

            StringBuilder reason = new StringBuilder();
            if (qualScore.compareTo(new BigDecimal(80)) >= 0) {
                reason.append("资质完全匹配；");
            } else if (qualScore.compareTo(new BigDecimal(50)) >= 0) {
                reason.append("资质部分匹配；");
            } else {
                reason.append("资质匹配度较低；");
            }
            if (candidate.getCurrentWorkload() <= 2) {
                reason.append("当前工作量适中；");
            } else {
                reason.append("当前工作量较大；");
            }
            candidate.setRecommendationReason(reason.toString());

            candidates.add(candidate);
        }

        candidates.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));
        recommendation.setCandidates(candidates.subList(0, Math.min(5, candidates.size())));
        result.add(recommendation);

        return result;
    }

    @Override
    public TaskBoardStatsVO getBoardStats() {
        TaskBoardStatsVO stats = new TaskBoardStatsVO();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

        stats.setPendingAssignCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_PENDING_ASSIGN)));
        stats.setPendingAcceptCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_PENDING_ACCEPT)));
        stats.setInProgressCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_IN_PROGRESS)));
        stats.setDataEntryCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_DATA_ENTRY)));
        stats.setReviewingCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_REVIEWING)));
        stats.setCompletedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_COMPLETED)));
        stats.setPausedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_PAUSED)));
        stats.setTerminatedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getStatus, STATUS_TERMINATED)));

        stats.setTodayNewCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                .ge(DetDetectionTask::getCreateTime, todayStart)
                .le(DetDetectionTask::getCreateTime, todayEnd)));

        stats.setTodayCompletedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                .eq(DetDetectionTask::getStatus, STATUS_COMPLETED)
                .ge(DetDetectionTask::getActualEndTime, todayStart)
                .le(DetDetectionTask::getActualEndTime, todayEnd)));

        stats.setUrgentCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                .eq(DetDetectionTask::getIsUrgent, 1)
                .ne(DetDetectionTask::getStatus, STATUS_COMPLETED)
                .ne(DetDetectionTask::getStatus, STATUS_TERMINATED)));

        stats.setGrabOrderCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                .eq(DetDetectionTask::getIsGrabOrder, 1)
                .eq(DetDetectionTask::getStatus, STATUS_PENDING_ASSIGN)));

        List<DeptTaskStatsVO> deptStats = new ArrayList<>();
        List<SysDept> depts = deptMapper.selectList(new LambdaQueryWrapper<SysDept>().eq(SysDept::getStatus, 1));
        for (SysDept dept : depts) {
            DeptTaskStatsVO deptStat = new DeptTaskStatsVO();
            deptStat.setDeptId(dept.getId());
            deptStat.setDeptName(dept.getDeptName());
            deptStat.setTotalCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getDeptId, dept.getId())));
            deptStat.setPendingCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getDeptId, dept.getId())
                    .in(DetDetectionTask::getStatus, STATUS_PENDING_ASSIGN, STATUS_PENDING_ACCEPT)));
            deptStat.setInProgressCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getDeptId, dept.getId())
                    .in(DetDetectionTask::getStatus, STATUS_IN_PROGRESS, STATUS_DATA_ENTRY, STATUS_REVIEWING)));
            deptStat.setCompletedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getDeptId, dept.getId())
                    .eq(DetDetectionTask::getStatus, STATUS_COMPLETED)));
            deptStats.add(deptStat);
        }
        stats.setDeptStats(deptStats);

        List<UserTaskStatsVO> userStats = new ArrayList<>();
        List<SysUser> users = userMapper.selectList(new LambdaQueryWrapper<SysUser>().eq(SysUser::getStatus, 1));
        for (SysUser user : users) {
            UserTaskStatsVO userStat = new UserTaskStatsVO();
            userStat.setUserId(user.getId());
            userStat.setUserName(user.getRealName());
            userStat.setDeptId(user.getDeptId());
            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    userStat.setDeptName(dept.getDeptName());
                }
            }
            userStat.setTotalCount(this.count(new LambdaQueryWrapper<DetDetectionTask>().eq(DetDetectionTask::getAssigneeId, user.getId())));
            userStat.setPendingCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getAssigneeId, user.getId())
                    .in(DetDetectionTask::getStatus, STATUS_PENDING_ACCEPT)));
            userStat.setInProgressCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getAssigneeId, user.getId())
                    .in(DetDetectionTask::getStatus, STATUS_IN_PROGRESS, STATUS_DATA_ENTRY, STATUS_REVIEWING)));
            userStat.setCompletedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getAssigneeId, user.getId())
                    .eq(DetDetectionTask::getStatus, STATUS_COMPLETED)));
            userStat.setTodayCompletedCount(this.count(new LambdaQueryWrapper<DetDetectionTask>()
                    .eq(DetDetectionTask::getAssigneeId, user.getId())
                    .eq(DetDetectionTask::getStatus, STATUS_COMPLETED)
                    .ge(DetDetectionTask::getActualEndTime, todayStart)
                    .le(DetDetectionTask::getActualEndTime, todayEnd)));
            userStats.add(userStat);
        }
        stats.setUserStats(userStats);

        return stats;
    }

    @Override
    public List<DetectionTaskVO> getTasksByStatus(Integer status) {
        List<DetDetectionTask> list = this.list(
                new LambdaQueryWrapper<DetDetectionTask>()
                        .eq(DetDetectionTask::getStatus, status)
                        .orderByAsc(DetDetectionTask::getPriority, DetDetectionTask::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private DetectionTaskVO convertToVO(DetDetectionTask task) {
        DetectionTaskVO vo = BeanUtil.copyProperties(task, DetectionTaskVO.class);
        vo.setStatusName(getStatusName(task.getStatus()));
        vo.setPriorityName(getPriorityName(task.getPriority()));
        vo.setAssignTypeName(getAssignTypeName(task.getAssignType()));
        return vo;
    }

    private void saveStatusLog(Long taskId, String taskNo, Integer beforeStatus, Integer afterStatus, String operateType, String operateContent) {
        DetTaskStatusLog log = new DetTaskStatusLog();
        log.setTaskId(taskId);
        log.setTaskNo(taskNo);
        log.setBeforeStatus(beforeStatus);
        log.setAfterStatus(afterStatus);
        log.setOperateType(operateType);
        log.setOperateContent(operateContent);
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setOperateTime(LocalDateTime.now());
        statusLogMapper.insert(log);

        if (beforeStatus != null && !beforeStatus.equals(afterStatus)) {
            DetDetectionTask task = this.getById(taskId);
            if (task != null) {
                WebSocketMessageVO wsMessage = new WebSocketMessageVO();
                wsMessage.setType("TASK_STATUS_CHANGED");
                wsMessage.setTitle("任务状态变更");
                wsMessage.setContent("任务【" + task.getTaskName() + "】状态从【" + getStatusName(beforeStatus) + "】变更为【" + getStatusName(afterStatus) + "】");
                wsMessage.setData(convertToVO(task));
                wsMessage.setSender(SecurityUtils.getCurrentUsername());
                webSocketHandler.broadcast(wsMessage);
            }
        }
    }

    private void createEquipmentUsage(DetDetectionTask task) {
        try {
            if (StrUtil.isNotBlank(task.getEquipmentIds())) {
                List<Long> equipmentIds = objectMapper.readValue(task.getEquipmentIds(), List.class);
                List<String> equipmentNames = StrUtil.isNotBlank(task.getEquipmentNames())
                        ? objectMapper.readValue(task.getEquipmentNames(), List.class)
                        : Collections.emptyList();

                for (int i = 0; i < equipmentIds.size(); i++) {
                    DetEquipmentUsage usage = new DetEquipmentUsage();
                    usage.setTaskId(task.getId());
                    usage.setTaskNo(task.getTaskNo());
                    usage.setEquipmentId(equipmentIds.get(i));
                    if (i < equipmentNames.size()) {
                        usage.setEquipmentName(equipmentNames.get(i));
                    }
                    usage.setUserId(task.getAssigneeId());
                    usage.setUserName(task.getAssigneeName());
                    if (task.getPlanStartDate() != null) {
                        usage.setPlanStartTime(task.getPlanStartDate().atStartOfDay());
                    }
                    if (task.getPlanEndDate() != null) {
                        usage.setPlanEndTime(task.getPlanEndDate().atTime(18, 0));
                    }
                    usage.setUsageStatus(0);
                    usage.setCreateTime(LocalDateTime.now());
                    usage.setUpdateTime(LocalDateTime.now());
                    equipmentUsageMapper.insert(usage);
                }
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析设备ID列表失败");
        }
    }

    private void updateEquipmentUsageStatus(Long taskId, Integer status) {
        List<DetEquipmentUsage> usages = equipmentUsageMapper.selectList(
                new LambdaQueryWrapper<DetEquipmentUsage>().eq(DetEquipmentUsage::getTaskId, taskId)
        );
        for (DetEquipmentUsage usage : usages) {
            usage.setUsageStatus(status);
            if (status == 1) {
                usage.setActualStartTime(LocalDateTime.now());
            } else if (status == 2) {
                usage.setActualEndTime(LocalDateTime.now());
            }
            usage.setUpdateTime(LocalDateTime.now());
            equipmentUsageMapper.updateById(usage);
        }
    }

    private void checkPersonnelQualification(Long assigneeId, String assigneeName, DetDetectionTask task) {
        if (StrUtil.isBlank(task.getTestItemIds())) {
            return;
        }

        try {
            List<Long> taskItemIds = objectMapper.readValue(task.getTestItemIds(), List.class);
            if (taskItemIds.isEmpty()) {
                return;
            }

            LocalDate today = LocalDate.now();
            List<PerPersonnelAuthorization> authorizations = personnelAuthorizationMapper.selectList(
                    new LambdaQueryWrapper<PerPersonnelAuthorization>()
                            .eq(PerPersonnelAuthorization::getPersonnelId, assigneeId)
                            .eq(PerPersonnelAuthorization::getStatus, 1)
                            .ge(PerPersonnelAuthorization::getExpiryDate, today)
            );

            Set<Long> authorizedItemIds = authorizations.stream()
                    .map(PerPersonnelAuthorization::getItemId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<Long> unauthorizedItems = new ArrayList<>();
            for (Long itemId : taskItemIds) {
                if (!authorizedItemIds.contains(itemId)) {
                    unauthorizedItems.add(itemId);
                }
            }

            if (!unauthorizedItems.isEmpty()) {
                try {
                    List<String> taskItemNames = StrUtil.isNotBlank(task.getTestItemNames())
                            ? objectMapper.readValue(task.getTestItemNames(), List.class)
                            : Collections.emptyList();

                    List<String> unauthorizedItemNames = new ArrayList<>();
                    for (int i = 0; i < taskItemIds.size(); i++) {
                        if (unauthorizedItems.contains(taskItemIds.get(i))) {
                            if (i < taskItemNames.size()) {
                                unauthorizedItemNames.add(taskItemNames.get(i));
                            } else {
                                unauthorizedItemNames.add("项目ID:" + taskItemIds.get(i));
                            }
                        }
                    }

                    String errorMsg = String.format("人员【%s】无以下检测项目的授权：%s，无法分配该任务。",
                            assigneeName, String.join("、", unauthorizedItemNames));
                    throw new BizException(errorMsg);
                } catch (JsonProcessingException e) {
                    throw new BizException("解析检测项目名称失败");
                }
            }

            for (PerPersonnelAuthorization auth : authorizations) {
                if (auth.getExpiryDate() != null) {
                    long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, auth.getExpiryDate());
                    if (daysUntilExpiry <= 30) {
                        log.warn("人员【{}】的授权【{}】将在 {} 天后到期，请及时处理。",
                                assigneeName, auth.getItemName(), daysUntilExpiry);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析检测项目ID列表失败");
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DetectionTaskServiceImpl.class);
}
