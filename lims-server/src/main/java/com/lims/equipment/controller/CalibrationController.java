package com.lims.equipment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.equipment.dto.CalibrationPlanQuery;
import com.lims.equipment.dto.CalibrationPlanSaveDTO;
import com.lims.equipment.dto.CalibrationRecordQuery;
import com.lims.equipment.dto.CalibrationRecordSaveDTO;
import com.lims.equipment.service.CalibrationService;
import com.lims.equipment.vo.CalibrationPlanVO;
import com.lims.equipment.vo.CalibrationRecordVO;
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

@Api(tags = "设备校准/检定管理")
@RestController
@RequestMapping("/equipment/calibration")
public class CalibrationController {

    @Autowired
    private CalibrationService calibrationService;

    @ApiOperation("分页查询校准计划")
    @GetMapping("/plan/page")
    public Result<PageResult<CalibrationPlanVO>> selectPlanPage(CalibrationPlanQuery query) {
        return Result.success(calibrationService.selectPlanPage(query));
    }

    @ApiOperation("获取即将到期的校准计划")
    @GetMapping("/plan/upcoming")
    public Result<List<CalibrationPlanVO>> getUpcomingCalibrations() {
        return Result.success(calibrationService.getUpcomingCalibrations());
    }

    @ApiOperation("新增校准计划")
    @PostMapping("/plan")
    public Result<Void> addPlan(@RequestBody @Validated CalibrationPlanSaveDTO dto) {
        calibrationService.addPlan(dto);
        return Result.success();
    }

    @ApiOperation("修改校准计划")
    @PutMapping("/plan")
    public Result<Void> updatePlan(@RequestBody @Validated CalibrationPlanSaveDTO dto) {
        calibrationService.updatePlan(dto);
        return Result.success();
    }

    @ApiOperation("删除校准计划")
    @DeleteMapping("/plan/{id}")
    public Result<Void> deletePlan(@PathVariable Long id) {
        calibrationService.deletePlan(id);
        return Result.success();
    }

    @ApiOperation("获取校准计划详情")
    @GetMapping("/plan/{id}")
    public Result<CalibrationPlanVO> getPlanDetail(@PathVariable Long id) {
        return Result.success(calibrationService.getPlanDetail(id));
    }

    @ApiOperation("分页查询校准记录")
    @GetMapping("/record/page")
    public Result<PageResult<CalibrationRecordVO>> selectRecordPage(CalibrationRecordQuery query) {
        return Result.success(calibrationService.selectRecordPage(query));
    }

    @ApiOperation("查询设备的校准记录")
    @GetMapping("/record/equipment/{equipmentId}")
    public Result<List<CalibrationRecordVO>> getRecordsByEquipmentId(@PathVariable Long equipmentId) {
        return Result.success(calibrationService.getRecordsByEquipmentId(equipmentId));
    }

    @ApiOperation("新增校准记录")
    @PostMapping("/record")
    public Result<Void> addRecord(@RequestBody @Validated CalibrationRecordSaveDTO dto) {
        calibrationService.addRecord(dto);
        return Result.success();
    }

    @ApiOperation("修改校准记录")
    @PutMapping("/record")
    public Result<Void> updateRecord(@RequestBody @Validated CalibrationRecordSaveDTO dto) {
        calibrationService.updateRecord(dto);
        return Result.success();
    }

    @ApiOperation("删除校准记录")
    @DeleteMapping("/record/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        calibrationService.deleteRecord(id);
        return Result.success();
    }

    @ApiOperation("获取校准记录详情")
    @GetMapping("/record/{id}")
    public Result<CalibrationRecordVO> getRecordDetail(@PathVariable Long id) {
        return Result.success(calibrationService.getRecordDetail(id));
    }

    @ApiOperation("检查并更新过期校准计划状态")
    @PostMapping("/plan/check-status")
    public Result<Void> checkAndUpdatePlanStatus() {
        calibrationService.checkAndUpdatePlanStatus();
        return Result.success();
    }
}
