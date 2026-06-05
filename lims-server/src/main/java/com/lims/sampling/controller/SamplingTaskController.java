package com.lims.sampling.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.sampling.dto.FieldSamplingDTO;
import com.lims.sampling.dto.SamplingTaskQuery;
import com.lims.sampling.service.SamplingTaskService;
import com.lims.sampling.vo.SamplingTaskDetailVO;
import com.lims.sampling.vo.SamplingTaskVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "采样任务管理")
@RestController
@RequestMapping("/sampling/task")
public class SamplingTaskController {

    @Autowired
    private SamplingTaskService samplingTaskService;

    @ApiOperation("分页查询采样任务")
    @GetMapping("/page")
    public Result<PageResult<SamplingTaskVO>> selectPage(SamplingTaskQuery query) {
        return Result.success(samplingTaskService.selectPage(query));
    }

    @ApiOperation("获取我的采样任务列表")
    @GetMapping("/myTasks/{samplerId}")
    public Result<List<SamplingTaskVO>> getMyTasks(@PathVariable Long samplerId) {
        return Result.success(samplingTaskService.getMyTasks(samplerId));
    }

    @ApiOperation("根据计划ID获取任务列表")
    @GetMapping("/byPlan/{planId}")
    public Result<List<SamplingTaskVO>> getTasksByPlanId(@PathVariable Long planId) {
        return Result.success(samplingTaskService.getTasksByPlanId(planId));
    }

    @ApiOperation("获取采样任务详情")
    @GetMapping("/{id}")
    public Result<SamplingTaskDetailVO> getTaskDetail(@PathVariable Long id) {
        return Result.success(samplingTaskService.getTaskDetail(id));
    }

    @ApiOperation("下载采样任务（移动端）")
    @PostMapping("/download/{taskId}")
    public Result<Void> downloadTask(@PathVariable Long taskId) {
        samplingTaskService.downloadTask(taskId);
        return Result.success();
    }

    @ApiOperation("开始采样（移动端）")
    @PostMapping("/start/{taskId}")
    public Result<Void> startSampling(@PathVariable Long taskId) {
        samplingTaskService.startSampling(taskId);
        return Result.success();
    }

    @ApiOperation("提交采样结果（移动端）")
    @PostMapping("/submit")
    public Result<Void> submitSampling(@RequestBody @Validated FieldSamplingDTO dto) {
        samplingTaskService.submitSampling(dto);
        return Result.success();
    }

    @ApiOperation("同步任务（移动端离线数据同步）")
    @PostMapping("/sync/{taskId}")
    public Result<Void> syncTask(@PathVariable Long taskId) {
        samplingTaskService.syncTask(taskId);
        return Result.success();
    }

    @ApiOperation("取消采样任务")
    @PostMapping("/cancel/{id}")
    public Result<Void> cancelTask(@PathVariable Long id, @RequestParam String reason) {
        samplingTaskService.cancelTask(id, reason);
        return Result.success();
    }

    @ApiOperation("获取离线任务列表（移动端）")
    @GetMapping("/offline/{samplerId}")
    public Result<List<SamplingTaskVO>> getOfflineTasks(@PathVariable Long samplerId) {
        return Result.success(samplingTaskService.getOfflineTasks(samplerId));
    }
}
