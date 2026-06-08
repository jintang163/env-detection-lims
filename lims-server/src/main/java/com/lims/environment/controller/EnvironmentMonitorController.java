package com.lims.environment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.environment.dto.EnvMonitorDataQuery;
import com.lims.environment.dto.EnvMonitorDataSaveDTO;
import com.lims.environment.dto.EnvMonitorThresholdSaveDTO;
import com.lims.environment.dto.EnvWarningHandleDTO;
import com.lims.environment.dto.EnvWarningQuery;
import com.lims.environment.service.EnvironmentMonitorService;
import com.lims.environment.vo.EnvMonitorDataVO;
import com.lims.environment.vo.EnvMonitorStatsVO;
import com.lims.environment.vo.EnvMonitorThresholdVO;
import com.lims.environment.vo.EnvWarningRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "环境监控")
@RestController
@RequestMapping("/environment/monitor")
public class EnvironmentMonitorController {

    @Autowired
    private EnvironmentMonitorService environmentMonitorService;

    @ApiOperation("分页查询监控数据")
    @GetMapping("/data/page")
    public Result<PageResult<EnvMonitorDataVO>> selectMonitorDataPage(EnvMonitorDataQuery query) {
        return Result.success(environmentMonitorService.selectMonitorDataPage(query));
    }

    @ApiOperation("获取监控数据详情")
    @GetMapping("/data/{id}")
    public Result<EnvMonitorDataVO> getMonitorDataDetail(@PathVariable Long id) {
        return Result.success(environmentMonitorService.getMonitorDataDetail(id));
    }

    @ApiOperation("新增监控数据")
    @PostMapping("/data")
    public Result<Void> addMonitorData(@RequestBody @Validated EnvMonitorDataSaveDTO dto) {
        environmentMonitorService.addMonitorData(dto);
        return Result.success();
    }

    @ApiOperation("修改监控数据")
    @PutMapping("/data")
    public Result<Void> updateMonitorData(@RequestBody @Validated EnvMonitorDataSaveDTO dto) {
        environmentMonitorService.updateMonitorData(dto);
        return Result.success();
    }

    @ApiOperation("删除监控数据")
    @DeleteMapping("/data/{id}")
    public Result<Void> deleteMonitorData(@PathVariable Long id) {
        environmentMonitorService.deleteMonitorData(id);
        return Result.success();
    }

    @ApiOperation("获取实时数据")
    @GetMapping("/data/realtime/{monitorPoint}")
    public Result<List<EnvMonitorDataVO>> getRealtimeData(@PathVariable String monitorPoint) {
        return Result.success(environmentMonitorService.getRealtimeData(monitorPoint));
    }

    @ApiOperation("获取历史数据用于图表")
    @GetMapping("/data/history")
    public Result<List<EnvMonitorDataVO>> getHistoryData(
            @RequestParam String monitorPoint,
            @RequestParam Integer monitorType,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(environmentMonitorService.getHistoryData(monitorPoint, monitorType, startTime, endTime));
    }

    @ApiOperation("分页查询阈值配置")
    @GetMapping("/threshold/page")
    public Result<PageResult<EnvMonitorThresholdVO>> selectThresholdPage() {
        return Result.success(environmentMonitorService.selectThresholdPage());
    }

    @ApiOperation("新增阈值配置")
    @PostMapping("/threshold")
    public Result<Void> addThreshold(@RequestBody @Validated EnvMonitorThresholdSaveDTO dto) {
        environmentMonitorService.addThreshold(dto);
        return Result.success();
    }

    @ApiOperation("修改阈值配置")
    @PutMapping("/threshold")
    public Result<Void> updateThreshold(@RequestBody @Validated EnvMonitorThresholdSaveDTO dto) {
        environmentMonitorService.updateThreshold(dto);
        return Result.success();
    }

    @ApiOperation("删除阈值配置")
    @DeleteMapping("/threshold/{id}")
    public Result<Void> deleteThreshold(@PathVariable Long id) {
        environmentMonitorService.deleteThreshold(id);
        return Result.success();
    }

    @ApiOperation("启用/禁用阈值")
    @PutMapping("/threshold/{id}/toggle")
    public Result<Void> toggleThreshold(@PathVariable Long id, @RequestParam Integer isEnabled) {
        environmentMonitorService.toggleThreshold(id, isEnabled);
        return Result.success();
    }

    @ApiOperation("分页查询预警记录")
    @GetMapping("/warning/page")
    public Result<PageResult<EnvWarningRecordVO>> selectWarningPage(EnvWarningQuery query) {
        return Result.success(environmentMonitorService.selectWarningPage(query));
    }

    @ApiOperation("处理预警")
    @PostMapping("/warning/handle")
    public Result<Void> handleWarning(@RequestBody @Validated EnvWarningHandleDTO dto) {
        environmentMonitorService.handleWarning(dto);
        return Result.success();
    }

    @ApiOperation("忽略预警")
    @PutMapping("/warning/{id}/ignore")
    public Result<Void> ignoreWarning(@PathVariable Long id) {
        environmentMonitorService.ignoreWarning(id);
        return Result.success();
    }

    @ApiOperation("获取监控统计信息")
    @GetMapping("/stats")
    public Result<EnvMonitorStatsVO> getStats() {
        return Result.success(environmentMonitorService.getStats());
    }
}
