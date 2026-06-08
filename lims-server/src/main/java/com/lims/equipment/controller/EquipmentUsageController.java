package com.lims.equipment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.equipment.dto.EquipmentUsageQuery;
import com.lims.equipment.dto.EquipmentUsageSaveDTO;
import com.lims.equipment.service.EquipmentUsageService;
import com.lims.equipment.vo.EquipmentUsageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@Api(tags = "设备使用记录管理")
@RestController
@RequestMapping("/equipment/usage")
public class EquipmentUsageController {

    @Autowired
    private EquipmentUsageService equipmentUsageService;

    @ApiOperation("分页查询设备使用记录")
    @GetMapping("/page")
    public Result<PageResult<EquipmentUsageVO>> selectPage(EquipmentUsageQuery query) {
        return Result.success(equipmentUsageService.selectPage(query));
    }

    @ApiOperation("查询设备的使用记录")
    @GetMapping("/equipment/{equipmentId}")
    public Result<List<EquipmentUsageVO>> getUsageByEquipmentId(@PathVariable Long equipmentId) {
        return Result.success(equipmentUsageService.getUsageByEquipmentId(equipmentId));
    }

    @ApiOperation("查询任务的设备使用记录")
    @GetMapping("/task/{taskId}")
    public Result<List<EquipmentUsageVO>> getUsageByTaskId(@PathVariable Long taskId) {
        return Result.success(equipmentUsageService.getUsageByTaskId(taskId));
    }

    @ApiOperation("新增设备使用记录")
    @PostMapping
    public Result<Void> addUsage(@RequestBody @Validated EquipmentUsageSaveDTO dto) {
        equipmentUsageService.addUsage(dto);
        return Result.success();
    }

    @ApiOperation("修改设备使用记录")
    @PutMapping
    public Result<Void> updateUsage(@RequestBody @Validated EquipmentUsageSaveDTO dto) {
        equipmentUsageService.updateUsage(dto);
        return Result.success();
    }

    @ApiOperation("删除设备使用记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUsage(@PathVariable Long id) {
        equipmentUsageService.deleteUsage(id);
        return Result.success();
    }

    @ApiOperation("获取设备使用记录详情")
    @GetMapping("/{id}")
    public Result<EquipmentUsageVO> getUsageDetail(@PathVariable Long id) {
        return Result.success(equipmentUsageService.getUsageDetail(id));
    }

    @ApiOperation("开始使用设备")
    @PostMapping("/start")
    public Result<Void> startUsage(@RequestBody @Validated EquipmentUsageSaveDTO dto) {
        equipmentUsageService.startUsage(dto);
        return Result.success();
    }

    @ApiOperation("结束使用设备")
    @PostMapping("/end/{id}")
    public Result<Void> endUsage(
            @PathVariable Long id,
            @RequestParam Integer runningStatus,
            @RequestParam(required = false) String anomalyDescription) {
        equipmentUsageService.endUsage(id, runningStatus, anomalyDescription);
        return Result.success();
    }
}
