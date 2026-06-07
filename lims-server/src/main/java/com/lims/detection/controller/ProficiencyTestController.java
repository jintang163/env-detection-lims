package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.PtPlanQuery;
import com.lims.detection.dto.PtPlanSaveDTO;
import com.lims.detection.dto.PtResultReportDTO;
import com.lims.detection.dto.PtResultSaveDTO;
import com.lims.detection.service.ProficiencyTestService;
import com.lims.detection.vo.PtPlanVO;
import com.lims.detection.vo.PtResultVO;
import com.lims.detection.vo.PtYoudenDataVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "能力验证管理")
@RestController
@RequestMapping("/detection/proficiencyTest")
public class ProficiencyTestController {

    @Autowired
    private ProficiencyTestService proficiencyTestService;

    @ApiOperation("分页查询能力验证计划")
    @GetMapping("/page")
    public Result<PageResult<PtPlanVO>> selectPage(PtPlanQuery query) {
        return Result.success(proficiencyTestService.selectPage(query));
    }

    @ApiOperation("获取能力验证计划详情")
    @GetMapping("/{id}")
    public Result<PtPlanVO> getDetail(@PathVariable Long id) {
        return Result.success(proficiencyTestService.getDetail(id));
    }

    @ApiOperation("保存能力验证计划")
    @PostMapping
    public Result<Void> save(@RequestBody @Validated PtPlanSaveDTO dto) {
        proficiencyTestService.save(dto);
        return Result.success();
    }

    @ApiOperation("更新能力验证计划")
    @PutMapping
    public Result<Void> update(@RequestBody @Validated PtPlanSaveDTO dto) {
        proficiencyTestService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除能力验证计划")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        proficiencyTestService.delete(id);
        return Result.success();
    }

    @ApiOperation("能力验证报名")
    @PostMapping("/{id}/register")
    public Result<Void> register(@PathVariable Long id) {
        proficiencyTestService.register(id);
        return Result.success();
    }

    @ApiOperation("接收样品")
    @PostMapping("/{id}/receiveSample")
    public Result<Void> receiveSample(@PathVariable Long id) {
        proficiencyTestService.receiveSample(id);
        return Result.success();
    }

    @ApiOperation("保存检测结果")
    @PostMapping("/result/save")
    public Result<Void> saveResult(@RequestBody @Validated PtResultSaveDTO dto) {
        proficiencyTestService.saveResult(dto);
        return Result.success();
    }

    @ApiOperation("上报结果")
    @PostMapping("/result/report")
    public Result<Void> reportResult(@RequestBody @Validated PtResultReportDTO dto) {
        proficiencyTestService.reportResult(dto);
        return Result.success();
    }

    @ApiOperation("获取Z比分数分析")
    @GetMapping("/{id}/zscore")
    public Result<List<PtResultVO>> getZScoreAnalysis(@PathVariable Long id) {
        return Result.success(proficiencyTestService.getZScoreAnalysis(id));
    }

    @ApiOperation("获取Youden图数据")
    @GetMapping("/{id}/youden")
    public Result<List<PtYoudenDataVO>> getYoudenData(@PathVariable Long id) {
        return Result.success(proficiencyTestService.getYoudenData(id));
    }

    @ApiOperation("获取统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(proficiencyTestService.getStats());
    }
}
