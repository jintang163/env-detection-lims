package com.lims.environment.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.environment.dto.EnvFacilityQuery;
import com.lims.environment.dto.EnvFacilitySaveDTO;
import com.lims.environment.service.FacilityService;
import com.lims.environment.vo.EnvFacilityStatsVO;
import com.lims.environment.vo.EnvFacilityVO;
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

@Api(tags = "设施管理")
@RestController
@RequestMapping("/environment/facility")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @ApiOperation("分页查询设施列表")
    @GetMapping("/page")
    public Result<PageResult<EnvFacilityVO>> selectPage(EnvFacilityQuery query) {
        return Result.success(facilityService.selectPage(query));
    }

    @ApiOperation("获取所有正常设施列表")
    @GetMapping("/list")
    public Result<List<EnvFacilityVO>> getNormalFacilityList() {
        return Result.success(facilityService.getNormalFacilityList());
    }

    @ApiOperation("获取设施详情")
    @GetMapping("/{id}")
    public Result<EnvFacilityVO> getFacilityDetail(@PathVariable Long id) {
        return Result.success(facilityService.getFacilityDetail(id));
    }

    @ApiOperation("新增设施")
    @PostMapping
    public Result<Void> addFacility(@RequestBody @Validated EnvFacilitySaveDTO dto) {
        facilityService.addFacility(dto);
        return Result.success();
    }

    @ApiOperation("修改设施")
    @PutMapping
    public Result<Void> updateFacility(@RequestBody @Validated EnvFacilitySaveDTO dto) {
        facilityService.updateFacility(dto);
        return Result.success();
    }

    @ApiOperation("删除设施")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return Result.success();
    }

    @ApiOperation("更新设施状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateFacilityStatus(@PathVariable Long id, @RequestParam Integer status) {
        facilityService.updateFacilityStatus(id, status);
        return Result.success();
    }

    @ApiOperation("获取设施统计信息")
    @GetMapping("/stats")
    public Result<EnvFacilityStatsVO> getStats() {
        return Result.success(facilityService.getStats());
    }
}
