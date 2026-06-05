package com.lims.sampling.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.sampling.dto.EquipmentBorrowQuery;
import com.lims.sampling.dto.EquipmentBorrowSaveDTO;
import com.lims.sampling.entity.SmpEquipmentBorrow;
import com.lims.sampling.service.EquipmentBorrowService;
import com.lims.sampling.vo.EquipmentBorrowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "设备领用归还管理")
@RestController
@RequestMapping("/sampling/equipment-borrow")
public class EquipmentBorrowController {

    @Autowired
    private EquipmentBorrowService equipmentBorrowService;

    @ApiOperation("分页查询设备领用记录")
    @GetMapping("/page")
    public Result<PageResult<EquipmentBorrowVO>> selectPage(EquipmentBorrowQuery query) {
        return Result.success(equipmentBorrowService.selectPage(query));
    }

    @ApiOperation("设备领用登记")
    @PostMapping("/borrow")
    public Result<Void> borrowEquipment(@RequestBody @Validated EquipmentBorrowSaveDTO dto) {
        equipmentBorrowService.borrowEquipment(dto);
        return Result.success();
    }

    @ApiOperation("设备归还登记")
    @PostMapping("/return/{id}")
    public Result<Void> returnEquipment(
            @PathVariable Long id,
            @RequestParam(required = false) String returnCheck,
            @RequestParam(required = false) Integer returnStatus,
            @RequestParam(required = false) String damageDesc) {
        equipmentBorrowService.returnEquipment(id, returnCheck, returnStatus, damageDesc);
        return Result.success();
    }

    @ApiOperation("获取领用记录详情")
    @GetMapping("/{id}")
    public Result<SmpEquipmentBorrow> getBorrowDetail(@PathVariable Long id) {
        return Result.success(equipmentBorrowService.getBorrowDetail(id));
    }
}
