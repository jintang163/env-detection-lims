package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.*;
import com.lims.detection.service.*;
import com.lims.detection.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

@Api(tags = "质量与质控管理")
@RestController
@RequestMapping("/detection/qualityControl")
public class QualityControlController {

    @Autowired
    private QcRuleService qcRuleService;

    @Autowired
    private QcSampleService qcSampleService;

    @Autowired
    private QcSamplePrepareService qcSamplePrepareService;

    @Autowired
    private QcPlanService qcPlanService;

    @Autowired
    private QcRecordService qcRecordService;

    @Autowired
    private QualityControlService qualityControlService;

    @ApiOperation("分页查询质控规则")
    @GetMapping("/rule/page")
    public Result<PageResult<QcRuleVO>> selectRulePage(QcRuleQuery query) {
        return Result.success(qcRuleService.selectPage(query));
    }

    @ApiOperation("获取质控规则统计数据")
    @GetMapping("/rule/stats")
    public Result<QcRuleStatsVO> getRuleStats() {
        return Result.success(qcRuleService.getStats());
    }

    @ApiOperation("获取质控规则详情")
    @GetMapping("/rule/{id}")
    public Result<QcRuleVO> getRuleDetail(@PathVariable Long id) {
        return Result.success(qcRuleService.getDetail(id));
    }

    @ApiOperation("获取所有启用的质控规则")
    @GetMapping("/rule/enabled")
    public Result<List<QcRuleVO>> getEnabledRules() {
        return Result.success(qcRuleService.getEnabledRules());
    }

    @ApiOperation("保存质控规则")
    @PostMapping("/rule")
    public Result<Void> saveRule(@RequestBody @Validated QcRuleSaveDTO dto) {
        qcRuleService.save(dto);
        return Result.success();
    }

    @ApiOperation("更新质控规则")
    @PutMapping("/rule")
    public Result<Void> updateRule(@RequestBody @Validated QcRuleSaveDTO dto) {
        qcRuleService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除质控规则")
    @DeleteMapping("/rule/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        qcRuleService.delete(id);
        return Result.success();
    }

    @ApiOperation("启用/禁用质控规则")
    @PutMapping("/rule/{id}/toggle")
    public Result<Void> toggleRule(@PathVariable Long id, @RequestParam Integer enabled) {
        qcRuleService.toggle(id, enabled);
        return Result.success();
    }

    @ApiOperation("分页查询质控样品")
    @GetMapping("/sample/page")
    public Result<PageResult<QcSampleVO>> selectSamplePage(QcSampleQuery query) {
        return Result.success(qcSampleService.selectPage(query));
    }

    @ApiOperation("获取质控样品统计数据")
    @GetMapping("/sample/stats")
    public Result<QcSampleStatsVO> getSampleStats() {
        return Result.success(qcSampleService.getStats());
    }

    @ApiOperation("获取质控样品详情")
    @GetMapping("/sample/{id}")
    public Result<QcSampleVO> getSampleDetail(@PathVariable Long id) {
        return Result.success(qcSampleService.getDetail(id));
    }

    @ApiOperation("获取所有有效质控样品")
    @GetMapping("/sample/valid")
    public Result<List<QcSampleVO>> getValidSamples() {
        return Result.success(qcSampleService.getValidSamples());
    }

    @ApiOperation("保存质控样品")
    @PostMapping("/sample")
    public Result<Void> saveSample(@RequestBody @Validated QcSampleSaveDTO dto) {
        qcSampleService.save(dto);
        return Result.success();
    }

    @ApiOperation("更新质控样品")
    @PutMapping("/sample")
    public Result<Void> updateSample(@RequestBody @Validated QcSampleSaveDTO dto) {
        qcSampleService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除质控样品")
    @DeleteMapping("/sample/{id}")
    public Result<Void> deleteSample(@PathVariable Long id) {
        qcSampleService.delete(id);
        return Result.success();
    }

    @ApiOperation("分页查询配制记录")
    @GetMapping("/prepare/page")
    public Result<PageResult<QcSamplePrepareVO>> selectPreparePage(QcSamplePrepareQuery query) {
        return Result.success(qcSamplePrepareService.selectPage(query));
    }

    @ApiOperation("获取配制记录详情")
    @GetMapping("/prepare/{id}")
    public Result<QcSamplePrepareVO> getPrepareDetail(@PathVariable Long id) {
        return Result.success(qcSamplePrepareService.getDetail(id));
    }

    @ApiOperation("保存配制记录")
    @PostMapping("/prepare")
    public Result<Void> savePrepare(@RequestBody @Validated QcSamplePrepareSaveDTO dto) {
        qcSamplePrepareService.save(dto);
        return Result.success();
    }

    @ApiOperation("分页查询质控计划")
    @GetMapping("/plan/page")
    public Result<PageResult<QcPlanVO>> selectPlanPage(QcPlanQuery query) {
        return Result.success(qcPlanService.selectPage(query));
    }

    @ApiOperation("获取质控计划统计数据")
    @GetMapping("/plan/stats")
    public Result<QcPlanStatsVO> getPlanStats() {
        return Result.success(qcPlanService.getStats());
    }

    @ApiOperation("获取质控计划详情")
    @GetMapping("/plan/{id}")
    public Result<QcPlanVO> getPlanDetail(@PathVariable Long id) {
        return Result.success(qcPlanService.getDetail(id));
    }

    @ApiOperation("获取所有执行中的质控计划")
    @GetMapping("/plan/active")
    public Result<List<QcPlanVO>> getActivePlans() {
        return Result.success(qcPlanService.getActivePlans());
    }

    @ApiOperation("保存质控计划")
    @PostMapping("/plan")
    public Result<Void> savePlan(@RequestBody @Validated QcPlanSaveDTO dto) {
        qcPlanService.save(dto);
        return Result.success();
    }

    @ApiOperation("更新质控计划")
    @PutMapping("/plan")
    public Result<Void> updatePlan(@RequestBody @Validated QcPlanSaveDTO dto) {
        qcPlanService.update(dto);
        return Result.success();
    }

    @ApiOperation("删除质控计划")
    @DeleteMapping("/plan/{id}")
    public Result<Void> deletePlan(@PathVariable Long id) {
        qcPlanService.delete(id);
        return Result.success();
    }

    @ApiOperation("暂停质控计划")
    @PutMapping("/plan/{id}/pause")
    public Result<Void> pausePlan(@PathVariable Long id) {
        qcPlanService.pause(id);
        return Result.success();
    }

    @ApiOperation("恢复质控计划")
    @PutMapping("/plan/{id}/resume")
    public Result<Void> resumePlan(@PathVariable Long id) {
        qcPlanService.resume(id);
        return Result.success();
    }

    @ApiOperation("分页查询质控记录")
    @GetMapping("/record/page")
    public Result<PageResult<QcRecordVO>> selectRecordPage(QcRecordQuery query) {
        return Result.success(qcRecordService.selectPage(query));
    }

    @ApiOperation("获取质控记录详情")
    @GetMapping("/record/{id}")
    public Result<QcRecordVO> getRecordDetail(@PathVariable Long id) {
        return Result.success(qcRecordService.getDetail(id));
    }

    @ApiOperation("执行质控记录（提交测定值）")
    @PostMapping("/record/{id}/execute")
    public Result<Void> executeRecord(@PathVariable Long id, @RequestParam BigDecimal measuredValue, @RequestParam String operator) {
        qcRecordService.execute(id, measuredValue, operator);
        return Result.success();
    }

    @ApiOperation("获取质控图数据")
    @GetMapping("/chart/data")
    public Result<QcChartDataVO> getChartData(QcChartDataQuery query) {
        return Result.success(qualityControlService.getChartData(query));
    }

    @ApiOperation("执行质控分析")
    @PostMapping("/analyze")
    public Result<QcAnalyzeResultVO> analyze(@RequestBody QcChartDataQuery query) {
        return Result.success(qualityControlService.analyze(query));
    }

    @ApiOperation("导出质控报告")
    @GetMapping("/report/export")
    public void exportReport(QcChartDataQuery query, HttpServletResponse response) throws Exception {
        byte[] data = qualityControlService.exportReport(query);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=quality-control-report.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }
}
