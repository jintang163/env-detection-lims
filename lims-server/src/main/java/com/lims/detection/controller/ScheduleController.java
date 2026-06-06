package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.ScheduleGenerateDTO;
import com.lims.detection.service.ScheduleService;
import com.lims.detection.vo.GanttChartVO;
import com.lims.detection.vo.SchedulePlanVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "智能排程管理")
@RestController
@RequestMapping("/detection/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("生成排程计划")
    @PostMapping("/generate")
    public Result<List<SchedulePlanVO>> generateSchedulePlans(@RequestBody @Validated ScheduleGenerateDTO dto) {
        return Result.success(scheduleService.generateSchedulePlans(dto));
    }

    @ApiOperation("分页查询排程计划")
    @GetMapping("/page")
    public Result<PageResult<SchedulePlanVO>> selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(scheduleService.selectPage(pageNum, pageSize));
    }

    @ApiOperation("获取排程计划详情")
    @GetMapping("/{id}")
    public Result<SchedulePlanVO> getDetail(@PathVariable Long id) {
        return Result.success(scheduleService.getDetail(id));
    }

    @ApiOperation("应用排程计划")
    @PostMapping("/apply/{id}")
    public Result<Void> applySchedulePlan(@PathVariable Long id) {
        scheduleService.applySchedulePlan(id);
        return Result.success();
    }

    @ApiOperation("获取甘特图数据")
    @GetMapping("/gantt")
    public Result<GanttChartVO> getGanttChart(
            @RequestParam(defaultValue = "user") String resourceType,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return Result.success(scheduleService.getGanttChart(resourceType, startDate, endDate));
    }
}
