package com.lims.sampling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.sampling.dto.FieldSamplingDTO;
import com.lims.sampling.dto.SamplingTaskQuery;
import com.lims.sampling.entity.SmpSamplingTask;
import com.lims.sampling.vo.SamplingTaskDetailVO;
import com.lims.sampling.vo.SamplingTaskVO;

import java.util.List;

public interface SamplingTaskService extends IService<SmpSamplingTask> {

    PageResult<SamplingTaskVO> selectPage(SamplingTaskQuery query);

    List<SamplingTaskVO> getMyTasks(Long samplerId);

    List<SamplingTaskVO> getTasksByPlanId(Long planId);

    SamplingTaskDetailVO getTaskDetail(Long id);

    void downloadTask(Long taskId);

    void startSampling(Long taskId);

    void submitSampling(FieldSamplingDTO dto);

    void syncTask(Long taskId);

    void cancelTask(Long id, String reason);

    List<SamplingTaskVO> getOfflineTasks(Long samplerId);
}
