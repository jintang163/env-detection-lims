package com.lims.detection.controller;

import com.lims.common.result.Result;
import com.lims.detection.entity.DetDataTrace;
import com.lims.detection.service.DataTraceService;
import com.lims.detection.vo.DataTraceNodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "数据溯源")
@RestController
@RequestMapping("/detection/dataTrace")
public class DataTraceController {

    @Autowired
    private DataTraceService dataTraceService;

    @ApiOperation("获取溯源树")
    @GetMapping("/tree")
    public Result<DataTraceNodeVO> getTraceTree(@RequestParam String traceType, @RequestParam Long sourceId) {
        return Result.success(dataTraceService.getTraceTree(traceType, sourceId));
    }

    @ApiOperation("从样品溯源")
    @GetMapping("/tree/sample/{sampleId}")
    public Result<DataTraceNodeVO> getTraceTreeFromSample(@PathVariable Long sampleId) {
        return Result.success(dataTraceService.getTraceTreeFromSample(sampleId));
    }

    @ApiOperation("从任务溯源")
    @GetMapping("/tree/task/{taskId}")
    public Result<DataTraceNodeVO> getTraceTreeFromTask(@PathVariable Long taskId) {
        return Result.success(dataTraceService.getTraceTreeFromTask(taskId));
    }

    @ApiOperation("从仪器溯源")
    @GetMapping("/tree/equipment/{equipmentId}")
    public Result<DataTraceNodeVO> getTraceTreeFromEquipment(@PathVariable Long equipmentId) {
        return Result.success(dataTraceService.getTraceTreeFromEquipment(equipmentId));
    }

    @ApiOperation("为数据记录构建溯源关系")
    @PostMapping("/build/{dataRecordId}")
    public Result<Void> buildTraceForDataRecord(@PathVariable Long dataRecordId) {
        dataTraceService.buildTraceForDataRecord(dataRecordId);
        return Result.success();
    }

    @ApiOperation("查询溯源关系")
    @GetMapping("/relations")
    public Result<List<DetDataTrace>> getTraceRelations(@RequestParam String sourceType, @RequestParam Long sourceId) {
        return Result.success(dataTraceService.getTraceRelations(sourceType, sourceId));
    }

    @ApiOperation("查询反向溯源关系")
    @GetMapping("/reverseRelations")
    public Result<List<DetDataTrace>> getReverseTraceRelations(@RequestParam String targetType, @RequestParam Long targetId) {
        return Result.success(dataTraceService.getReverseTraceRelations(targetType, targetId));
    }
}
