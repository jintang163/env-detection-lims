package com.lims.equipment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.equipment.dto.EquipmentQuery;
import com.lims.equipment.dto.EquipmentSaveDTO;
import com.lims.equipment.service.EquipmentService;
import com.lims.equipment.vo.EquipmentDetailVO;
import com.lims.equipment.vo.EquipmentVO;
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

@Api(tags = "设备台账管理")
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @ApiOperation("分页查询设备台账")
    @GetMapping("/page")
    public Result<PageResult<EquipmentVO>> selectPage(EquipmentQuery query) {
        return Result.success(equipmentService.selectPage(query));
    }

    @ApiOperation("获取可用设备列表")
    @GetMapping("/available")
    public Result<List<EquipmentVO>> getAvailableEquipmentList() {
        return Result.success(equipmentService.getAvailableEquipmentList());
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
    public Result<EquipmentDetailVO> getEquipmentDetail(@PathVariable Long id) {
        return Result.success(equipmentService.getEquipmentDetail(id));
    }

    @ApiOperation("更新设备状态")
    @PutMapping("/status")
    public Result<Void> updateEquipmentStatus(@RequestParam Long id, @RequestParam Integer status) {
        equipmentService.updateEquipmentStatus(id, status);
        return Result.success();
    }
}
