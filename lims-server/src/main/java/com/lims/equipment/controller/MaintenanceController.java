package com.lims.equipment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.equipment.dto.MaintenanceRecordQuery;
import com.lims.equipment.dto.MaintenanceRecordSaveDTO;
import com.lims.equipment.dto.RepairConfirmDTO;
import com.lims.equipment.dto.RepairHandleDTO;
import com.lims.equipment.dto.RepairRequestQuery;
import com.lims.equipment.dto.RepairRequestSaveDTO;
import com.lims.equipment.service.MaintenanceService;
import com.lims.equipment.vo.MaintenanceRecordVO;
import com.lims.equipment.vo.RepairRequestVO;
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

@Api(tags = "设备维护/维修管理")
@RestController
@RequestMapping("/equipment/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @ApiOperation("分页查询维护记录")
    @GetMapping("/record/page")
    public Result<PageResult<MaintenanceRecordVO>> selectMaintenancePage(MaintenanceRecordQuery query) {
        return Result.success(maintenanceService.selectMaintenancePage(query));
    }

    @ApiOperation("查询设备的维护记录")
    @GetMapping("/record/equipment/{equipmentId}")
    public Result<List<MaintenanceRecordVO>> getMaintenanceByEquipmentId(@PathVariable Long equipmentId) {
        return Result.success(maintenanceService.getMaintenanceByEquipmentId(equipmentId));
    }

    @ApiOperation("新增维护记录")
    @PostMapping("/record")
    public Result<Void> addMaintenanceRecord(@RequestBody @Validated MaintenanceRecordSaveDTO dto) {
        maintenanceService.addMaintenanceRecord(dto);
        return Result.success();
    }

    @ApiOperation("修改维护记录")
    @PutMapping("/record")
    public Result<Void> updateMaintenanceRecord(@RequestBody @Validated MaintenanceRecordSaveDTO dto) {
        maintenanceService.updateMaintenanceRecord(dto);
        return Result.success();
    }

    @ApiOperation("删除维护记录")
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteMaintenanceRecord(@PathVariable Long id) {
        maintenanceService.deleteMaintenanceRecord(id);
        return Result.success();
    }

    @ApiOperation("获取维护记录详情")
    @GetMapping("/record/{id}")
    public Result<MaintenanceRecordVO> getMaintenanceRecordDetail(@PathVariable Long id) {
        return Result.success(maintenanceService.getMaintenanceRecordDetail(id));
    }

    @ApiOperation("分页查询维修申请")
    @GetMapping("/repair/page")
    public Result<PageResult<RepairRequestVO>> selectRepairPage(RepairRequestQuery query) {
        return Result.success(maintenanceService.selectRepairPage(query));
    }

    @ApiOperation("查询设备的维修申请")
    @GetMapping("/repair/equipment/{equipmentId}")
    public Result<List<RepairRequestVO>> getRepairByEquipmentId(@PathVariable Long equipmentId) {
        return Result.success(maintenanceService.getRepairByEquipmentId(equipmentId));
    }

    @ApiOperation("提交维修申请")
    @PostMapping("/repair")
    public Result<Void> submitRepairRequest(@RequestBody @Validated RepairRequestSaveDTO dto) {
        maintenanceService.submitRepairRequest(dto);
        return Result.success();
    }

    @ApiOperation("修改维修申请")
    @PutMapping("/repair")
    public Result<Void> updateRepairRequest(@RequestBody @Validated RepairRequestSaveDTO dto) {
        maintenanceService.updateRepairRequest(dto);
        return Result.success();
    }

    @ApiOperation("删除维修申请")
    @DeleteMapping("/repair/{id}")
    public Result<Void> deleteRepairRequest(@PathVariable Long id) {
        maintenanceService.deleteRepairRequest(id);
        return Result.success();
    }

    @ApiOperation("获取维修申请详情")
    @GetMapping("/repair/{id}")
    public Result<RepairRequestVO> getRepairRequestDetail(@PathVariable Long id) {
        return Result.success(maintenanceService.getRepairRequestDetail(id));
    }

    @ApiOperation("受理/处理维修")
    @PostMapping("/repair/handle")
    public Result<Void> handleRepair(@RequestBody @Validated RepairHandleDTO dto) {
        maintenanceService.handleRepair(dto);
        return Result.success();
    }

    @ApiOperation("确认维修结果")
    @PostMapping("/repair/confirm")
    public Result<Void> confirmRepair(@RequestBody @Validated RepairConfirmDTO dto) {
        maintenanceService.confirmRepair(dto);
        return Result.success();
    }

    @ApiOperation("驳回维修申请")
    @PostMapping("/repair/reject/{id}")
    public Result<Void> rejectRepair(@PathVariable Long id, @RequestParam String reason) {
        maintenanceService.rejectRepair(id, reason);
        return Result.success();
    }
}
