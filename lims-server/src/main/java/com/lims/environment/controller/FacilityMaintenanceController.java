package com.lims.environment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.environment.dto.EnvFacilityMaintenancePlanQuery;
import com.lims.environment.dto.EnvFacilityMaintenancePlanSaveDTO;
import com.lims.environment.dto.EnvFacilityMaintenanceQuery;
import com.lims.environment.dto.EnvFacilityMaintenanceSaveDTO;
import com.lims.environment.service.FacilityMaintenanceService;
import com.lims.environment.vo.EnvFacilityMaintenancePlanVO;
import com.lims.environment.vo.EnvFacilityMaintenanceVO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "设施维护管理")
@RestController
@RequestMapping("/environment/facility/maintenance")
public class FacilityMaintenanceController {

    @Autowired
    private FacilityMaintenanceService facilityMaintenanceService;

    @ApiOperation("分页查询维护记录")
    @GetMapping("/record/page")
    public Result<PageResult<EnvFacilityMaintenanceVO>> selectMaintenanceRecordPage(EnvFacilityMaintenanceQuery query) {
        return Result.success(facilityMaintenanceService.selectMaintenanceRecordPage(query));
    }

    @ApiOperation("获取维护记录详情")
    @GetMapping("/record/{id}")
    public Result<EnvFacilityMaintenanceVO> getMaintenanceRecordDetail(@PathVariable Long id) {
        return Result.success(facilityMaintenanceService.getMaintenanceRecordDetail(id));
    }

    @ApiOperation("根据设施ID查询维护记录")
    @GetMapping("/record/facility/{facilityId}")
    public Result<List<EnvFacilityMaintenanceVO>> getMaintenanceRecordByFacilityId(@PathVariable Long facilityId) {
        return Result.success(facilityMaintenanceService.getMaintenanceRecordByFacilityId(facilityId));
    }

    @ApiOperation("新增维护记录")
    @PostMapping("/record")
    public Result<Void> addMaintenanceRecord(@RequestBody @Validated EnvFacilityMaintenanceSaveDTO dto) {
        facilityMaintenanceService.addMaintenanceRecord(dto);
        return Result.success();
    }

    @ApiOperation("修改维护记录")
    @PutMapping("/record")
    public Result<Void> updateMaintenanceRecord(@RequestBody @Validated EnvFacilityMaintenanceSaveDTO dto) {
        facilityMaintenanceService.updateMaintenanceRecord(dto);
        return Result.success();
    }

    @ApiOperation("删除维护记录")
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteMaintenanceRecord(@PathVariable Long id) {
        facilityMaintenanceService.deleteMaintenanceRecord(id);
        return Result.success();
    }

    @ApiOperation("分页查询维护计划")
    @GetMapping("/plan/page")
    public Result<PageResult<EnvFacilityMaintenancePlanVO>> selectMaintenancePlanPage(EnvFacilityMaintenancePlanQuery query) {
        return Result.success(facilityMaintenanceService.selectMaintenancePlanPage(query));
    }

    @ApiOperation("根据设施ID查询维护计划")
    @GetMapping("/plan/facility/{facilityId}")
    public Result<List<EnvFacilityMaintenancePlanVO>> getMaintenancePlanByFacilityId(@PathVariable Long facilityId) {
        return Result.success(facilityMaintenanceService.getMaintenancePlanByFacilityId(facilityId));
    }

    @ApiOperation("新增维护计划")
    @PostMapping("/plan")
    public Result<Void> addMaintenancePlan(@RequestBody @Validated EnvFacilityMaintenancePlanSaveDTO dto) {
        facilityMaintenanceService.addMaintenancePlan(dto);
        return Result.success();
    }

    @ApiOperation("修改维护计划")
    @PutMapping("/plan")
    public Result<Void> updateMaintenancePlan(@RequestBody @Validated EnvFacilityMaintenancePlanSaveDTO dto) {
        facilityMaintenanceService.updateMaintenancePlan(dto);
        return Result.success();
    }

    @ApiOperation("删除维护计划")
    @DeleteMapping("/plan/{id}")
    public Result<Void> deleteMaintenancePlan(@PathVariable Long id) {
        facilityMaintenanceService.deleteMaintenancePlan(id);
        return Result.success();
    }

    @ApiOperation("完成维护计划")
    @PostMapping("/plan/{planId}/complete")
    public Result<Void> completeMaintenancePlan(@PathVariable Long planId, @RequestBody @Validated EnvFacilityMaintenanceSaveDTO recordDTO) {
        facilityMaintenanceService.completeMaintenancePlan(planId, recordDTO);
        return Result.success();
    }
}
