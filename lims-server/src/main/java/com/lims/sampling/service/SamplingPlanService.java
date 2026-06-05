package com.lims.sampling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.sampling.dto.SamplingPlanQuery;
import com.lims.sampling.dto.SamplingPlanSaveDTO;
import com.lims.sampling.dto.TaskAssignDTO;
import com.lims.sampling.entity.SmpSamplingPlan;
import com.lims.sampling.vo.SamplingPlanDetailVO;
import com.lims.sampling.vo.SamplingPlanVO;

public interface SamplingPlanService extends IService<SmpSamplingPlan> {

    PageResult<SamplingPlanVO> selectPage(SamplingPlanQuery query);

    void addPlan(SamplingPlanSaveDTO dto);

    void updatePlan(SamplingPlanSaveDTO dto);

    void deletePlan(Long id);

    SamplingPlanDetailVO getPlanDetail(Long id);

    void submitPlan(Long id);

    void assignTask(TaskAssignDTO dto);

    void cancelPlan(Long id, String reason);
}
