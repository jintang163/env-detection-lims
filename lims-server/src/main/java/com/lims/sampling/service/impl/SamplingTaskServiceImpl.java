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
import com.lims.sampling.dto.FieldSamplingDTO;
import com.lims.sampling.dto.SampleRecordSaveDTO;
import com.lims.sampling.dto.SamplingTaskQuery;
import com.lims.sampling.entity.*;
import com.lims.sampling.mapper.*;
import com.lims.sampling.service.SamplingPlanService;
import com.lims.sampling.service.SamplingTaskService;
import com.lims.sampling.vo.SamplingTaskDetailVO;
import com.lims.sampling.vo.SamplingTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SamplingTaskServiceImpl extends ServiceImpl<SmpSamplingTaskMapper, SmpSamplingTask> implements SamplingTaskService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private SmpSampleRecordMapper sampleRecordMapper;

    @Autowired
    private SmpSamplingPlanMapper samplingPlanMapper;

    @Autowired
    private SmpSamplingPointMapper samplingPointMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_PENDING_DOWNLOAD = 0;
    private static final int STATUS_DOWNLOADED = 1;
    private static final int STATUS_SAMPLING = 2;
    private static final int STATUS_COMPLETED = 3;
    private static final int STATUS_UPLOADED = 4;
    private static final int STATUS_CANCELLED = 5;

    private static final int PLAN_STATUS_SAMPLING = 3;
    private static final int PLAN_STATUS_COMPLETED = 4;

    private String getTaskStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待下载";
            case 1: return "已下载";
            case 2: return "采样中";
            case 3: return "已完成";
            case 4: return "已上传";
            case 5: return "已取消";
            default: return "";
        }
    }

    @Override
    public PageResult<SamplingTaskVO> selectPage(SamplingTaskQuery query) {
        LambdaQueryWrapper<SmpSamplingTask> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(SmpSamplingTask::getTaskNo, query.getTaskNo());
        }
        if (StrUtil.isNotBlank(query.getPlanNo())) {
            wrapper.like(SmpSamplingTask::getPlanNo, query.getPlanNo());
        }
        if (StrUtil.isNotBlank(query.getPointName())) {
            wrapper.like(SmpSamplingTask::getPointName, query.getPointName());
        }
        if (query.getSamplerId() != null) {
            wrapper.eq(SmpSamplingTask::getSamplerId, query.getSamplerId());
        }
        if (StrUtil.isNotBlank(query.getSamplerName())) {
            wrapper.like(SmpSamplingTask::getSamplerName, query.getSamplerName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SmpSamplingTask::getStatus, query.getStatus());
        }
        if (query.getSyncStatus() != null) {
            wrapper.eq(SmpSamplingTask::getSyncStatus, query.getSyncStatus());
        }
        if (query.getOfflineFlag() != null) {
            wrapper.eq(SmpSamplingTask::getOfflineFlag, query.getOfflineFlag());
        }
        if (query.getPlanSamplingDateStart() != null) {
            wrapper.ge(SmpSamplingTask::getPlanSamplingDate, query.getPlanSamplingDateStart());
        }
        if (query.getPlanSamplingDateEnd() != null) {
            wrapper.le(SmpSamplingTask::getPlanSamplingDate, query.getPlanSamplingDateEnd());
        }
        wrapper.orderByDesc(SmpSamplingTask::getCreateTime);

        Page<SmpSamplingTask> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SmpSamplingTask> pageResult = this.page(page, wrapper);

        IPage<SamplingTaskVO> voPage = pageResult.convert(task -> {
            SamplingTaskVO vo = BeanUtil.copyProperties(task, SamplingTaskVO.class);
            vo.setStatusName(getTaskStatusName(task.getStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public List<SamplingTaskVO> getMyTasks(Long samplerId) {
        List<SmpSamplingTask> list = this.list(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getSamplerId, samplerId)
                        .ne(SmpSamplingTask::getStatus, STATUS_CANCELLED)
                        .orderByDesc(SmpSamplingTask::getCreateTime)
        );
        return list.stream().map(task -> {
            SamplingTaskVO vo = BeanUtil.copyProperties(task, SamplingTaskVO.class);
            vo.setStatusName(getTaskStatusName(task.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SamplingTaskVO> getTasksByPlanId(Long planId) {
        List<SmpSamplingTask> list = this.list(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getPlanId, planId)
                        .orderByAsc(SmpSamplingTask::getPointCode)
        );
        return list.stream().map(task -> {
            SamplingTaskVO vo = BeanUtil.copyProperties(task, SamplingTaskVO.class);
            vo.setStatusName(getTaskStatusName(task.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SamplingTaskDetailVO getTaskDetail(Long id) {
        SmpSamplingTask task = this.getById(id);
        if (task == null) {
            throw new BizException("采样任务不存在");
        }
        SamplingTaskDetailVO vo = BeanUtil.copyProperties(task, SamplingTaskDetailVO.class);
        vo.setStatusName(getTaskStatusName(task.getStatus()));

        List<SmpSampleRecord> sampleList = sampleRecordMapper.selectList(
                new LambdaQueryWrapper<SmpSampleRecord>()
                        .eq(SmpSampleRecord::getTaskId, id)
                        .orderByAsc(SmpSampleRecord::getSampleNo)
        );
        vo.setSampleList(sampleList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void downloadTask(Long taskId) {
        SmpSamplingTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("采样任务不存在");
        }
        if (task.getStatus() != STATUS_PENDING_DOWNLOAD) {
            throw new BizException("仅待下载状态的任务可下载");
        }

        task.setStatus(STATUS_DOWNLOADED);
        this.updateById(task);

        SmpSamplingPlan plan = samplingPlanMapper.selectById(task.getPlanId());
        if (plan != null && plan.getStatus() != PLAN_STATUS_SAMPLING) {
            plan.setStatus(PLAN_STATUS_SAMPLING);
            samplingPlanMapper.updateById(plan);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startSampling(Long taskId) {
        SmpSamplingTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("采样任务不存在");
        }
        if (task.getStatus() != STATUS_DOWNLOADED && task.getStatus() != STATUS_PENDING_DOWNLOAD) {
            throw new BizException("任务状态不正确，无法开始采样");
        }

        task.setStatus(STATUS_SAMPLING);
        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitSampling(FieldSamplingDTO dto) {
        SmpSamplingTask task = this.getById(dto.getTaskId());
        if (task == null) {
            throw new BizException("采样任务不存在");
        }
        if (task.getStatus() != STATUS_SAMPLING && task.getStatus() != STATUS_DOWNLOADED) {
            throw new BizException("任务状态不正确，无法提交采样");
        }

        BeanUtil.copyProperties(dto, task, "id", "taskNo", "planId", "planNo", "planName",
                "pointId", "pointCode", "pointName", "pointAddress", "longitude", "latitude",
                "samplerId", "samplerName", "assignTime", "planSamplingDate", "status", "syncStatus",
                "createBy", "createTime");

        if (dto.getSitePhotoList() != null && !dto.getSitePhotoList().isEmpty()) {
            try {
                task.setSitePhotos(objectMapper.writeValueAsString(dto.getSitePhotoList()));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化照片列表失败");
            }
        }

        task.setStatus(STATUS_COMPLETED);
        task.setSyncStatus(0);
        this.updateById(task);

        if (dto.getSampleList() != null && !dto.getSampleList().isEmpty()) {
            for (SampleRecordSaveDTO sampleDto : dto.getSampleList()) {
                SmpSampleRecord sample = BeanUtil.copyProperties(sampleDto, SmpSampleRecord.class);
                if (StrUtil.isBlank(sample.getSampleNo())) {
                    sample.setSampleNo(codeGenerator.generateSampleNo());
                }
                sample.setTaskId(task.getId());
                sample.setPlanId(task.getPlanId());
                sample.setPointId(task.getPointId());
                sample.setPointCode(task.getPointCode());
                sample.setPointName(task.getPointName());
                sample.setQrCode(sample.getSampleNo());
                sample.setSampleStatus("待交接");

                if (sampleDto.getPhotoList() != null && !sampleDto.getPhotoList().isEmpty()) {
                    try {
                        sample.setPhotos(objectMapper.writeValueAsString(sampleDto.getPhotoList()));
                    } catch (JsonProcessingException e) {
                        throw new BizException("序列化样品照片失败");
                    }
                }

                if (sampleDto.getId() != null) {
                    sampleRecordMapper.updateById(sample);
                } else {
                    sampleRecordMapper.insert(sample);
                }
            }
        }

        checkAndUpdatePlanStatus(task.getPlanId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncTask(Long taskId) {
        SmpSamplingTask task = this.getById(taskId);
        if (task == null) {
            throw new BizException("采样任务不存在");
        }

        task.setSyncStatus(1);
        task.setOfflineFlag(0);
        this.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long id, String reason) {
        SmpSamplingTask task = this.getById(id);
        if (task == null) {
            throw new BizException("采样任务不存在");
        }
        if (task.getStatus() == STATUS_COMPLETED || task.getStatus() == STATUS_UPLOADED) {
            throw new BizException("已完成的任务无法取消");
        }

        task.setStatus(STATUS_CANCELLED);
        task.setRemark(StrUtil.isNotBlank(task.getRemark())
                ? task.getRemark() + "; 取消原因：" + reason
                : "取消原因：" + reason);
        this.updateById(task);

        checkAndUpdatePlanStatus(task.getPlanId());
    }

    @Override
    public List<SamplingTaskVO> getOfflineTasks(Long samplerId) {
        List<SmpSamplingTask> list = this.list(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getSamplerId, samplerId)
                        .eq(SmpSamplingTask::getOfflineFlag, 1)
                        .ne(SmpSamplingTask::getStatus, STATUS_CANCELLED)
                        .orderByDesc(SmpSamplingTask::getCreateTime)
        );
        return list.stream().map(task -> {
            SamplingTaskVO vo = BeanUtil.copyProperties(task, SamplingTaskVO.class);
            vo.setStatusName(getTaskStatusName(task.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    private void checkAndUpdatePlanStatus(Long planId) {
        SmpSamplingPlan plan = samplingPlanMapper.selectById(planId);
        if (plan == null) return;

        Long totalCount = this.count(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getPlanId, planId)
                        .ne(SmpSamplingTask::getStatus, STATUS_CANCELLED)
        );

        Long completedCount = this.count(
                new LambdaQueryWrapper<SmpSamplingTask>()
                        .eq(SmpSamplingTask::getPlanId, planId)
                        .in(SmpSamplingTask::getStatus, STATUS_COMPLETED, STATUS_UPLOADED)
        );

        if (totalCount > 0 && totalCount.equals(completedCount)) {
            plan.setStatus(PLAN_STATUS_COMPLETED);
            samplingPlanMapper.updateById(plan);
        }
    }
}
