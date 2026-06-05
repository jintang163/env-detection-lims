package com.lims.sampling.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.sampling.dto.EquipmentQuery;
import com.lims.sampling.dto.EquipmentSaveDTO;
import com.lims.sampling.entity.SmpEquipment;
import com.lims.sampling.service.EquipmentService;
import com.lims.sampling.vo.EquipmentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "设备管理")
@RestController
@RequestMapping("/sampling/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @ApiOperation("分页查询设备列表")
    @GetMapping("/page")
    public Result<PageResult<EquipmentVO>> selectPage(EquipmentQuery query) {
        return Result.success(equipmentService.selectPage(query));
    }

    @ApiOperation("获取可用设备列表")
    @GetMapping("/available")
    public Result<List<EquipmentVO>> getAvailableEquipments(@RequestParam(required = false) String equipmentType) {
        return Result.success(equipmentService.getAvailableEquipments(equipmentType));
    }

    @ApiOperation("新增设备")
    @PostMapping
    public Result<Void> addEquipment(@RequestBody @Validated EquipmentSaveDTO dto) {
        equipmentService.addEquipment(dto);
        return Result.success();
    }

    @ApiOperation("修改设备")
    @PutMapping
    public Result<Void> updateEquipment(@RequestBody @Validated EquipmentSaveDTO dto) {
        equipmentService.updateEquipment(dto);
        return Result.success();
    }

    @ApiOperation("删除设备")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return Result.success();
    }

    @ApiOperation("获取设备详情")
    @GetMapping("/{id}")
    public Result<SmpEquipment> getEquipmentDetail(@PathVariable Long id) {
        return Result.success(equipmentService.getEquipmentDetail(id));
    }
}
