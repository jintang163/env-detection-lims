package com.lims.sampling.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.sampling.dto.SamplingPlanQuery;
import com.lims.sampling.dto.SamplingPlanSaveDTO;
import com.lims.sampling.dto.TaskAssignDTO;
import com.lims.sampling.service.SamplingPlanService;
import com.lims.sampling.vo.SamplingPlanDetailVO;
import com.lims.sampling.vo.SamplingPlanVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "采样计划管理")
@RestController
@RequestMapping("/sampling/plan")
public class SamplingPlanController {

    @Autowired
    private SamplingPlanService samplingPlanService;

    @ApiOperation("分页查询采样计划")
    @GetMapping("/page")
    public Result<PageResult<SamplingPlanVO>> selectPage(SamplingPlanQuery query) {
        return Result.success(samplingPlanService.selectPage(query));
    }

    @ApiOperation("新增采样计划")
    @PostMapping
    public Result<Void> addPlan(@RequestBody @Validated SamplingPlanSaveDTO dto) {
        samplingPlanService.addPlan(dto);
        return Result.success();
    }

    @ApiOperation("修改采样计划")
    @PutMapping
    public Result<Void> updatePlan(@RequestBody @Validated SamplingPlanSaveDTO dto) {
        samplingPlanService.updatePlan(dto);
        return Result.success();
    }

    @ApiOperation("删除采样计划")
    @DeleteMapping("/{id}")
    public Result<Void> deletePlan(@PathVariable Long id) {
        samplingPlanService.deletePlan(id);
        return Result.success();
    }

    @ApiOperation("获取采样计划详情")
    @GetMapping("/{id}")
    public Result<SamplingPlanDetailVO> getPlanDetail(@PathVariable Long id) {
        return Result.success(samplingPlanService.getPlanDetail(id));
    }

    @ApiOperation("提交采样计划")
    @PostMapping("/submit/{id}")
    public Result<Void> submitPlan(@PathVariable Long id) {
        samplingPlanService.submitPlan(id);
        return Result.success();
    }

    @ApiOperation("分配采样任务")
    @PostMapping("/assign")
    public Result<Void> assignTask(@RequestBody @Validated TaskAssignDTO dto) {
        samplingPlanService.assignTask(dto);
        return Result.success();
    }

    @ApiOperation("取消采样计划")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelPlan(@PathVariable Long id, @RequestParam String reason) {
        samplingPlanService.cancelPlan(id, reason);
        return Result.success();
    }
}
