package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.StabilitySchemeQuery;
import com.lims.detection.dto.StabilitySchemeSaveDTO;
import com.lims.detection.dto.StabilityTestResultDTO;
import com.lims.detection.service.StabilityService;
import com.lims.detection.vo.StabilitySchemeVO;
import com.lims.detection.vo.StabilityTrendVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Api(tags = "稳定性考察管理")
@RestController
@RequestMapping("/detection/stability")
public class StabilityController {

    @Autowired
    private StabilityService stabilityService;

    @ApiOperation("分页查询方案")
    @GetMapping("/page")
    public Result<PageResult<StabilitySchemeVO>> selectPage(StabilitySchemeQuery query) {
        return Result.success(stabilityService.selectPage(query));
    }

    @ApiOperation("获取详情（含检测点）")
    @GetMapping("/{id}")
    public Result<StabilitySchemeVO> getDetail(@PathVariable Long id) {
        return Result.success(stabilityService.getDetail(id));
    }

    @ApiOperation("创建方案")
    @PostMapping
    public Result<Void> createScheme(@RequestBody @Validated StabilitySchemeSaveDTO dto) {
        stabilityService.createScheme(dto);
        return Result.success();
    }

    @ApiOperation("更新方案")
    @PutMapping
    public Result<Void> updateScheme(@RequestBody @Validated StabilitySchemeSaveDTO dto) {
        stabilityService.updateScheme(dto);
        return Result.success();
    }

    @ApiOperation("删除方案")
    @DeleteMapping("/{id}")
    public Result<Void> deleteScheme(@PathVariable Long id) {
        stabilityService.deleteScheme(id);
        return Result.success();
    }

    @ApiOperation("启动考察")
    @PostMapping("/{id}/start")
    public Result<Void> startScheme(@PathVariable Long id) {
        stabilityService.startScheme(id);
        return Result.success();
    }

    @ApiOperation("录入检测结果")
    @PostMapping("/point/{pointId}/record")
    public Result<Void> recordResult(@PathVariable Long pointId, @RequestBody @Validated StabilityTestResultDTO dto) {
        stabilityService.recordResult(pointId, dto);
        return Result.success();
    }

    @ApiOperation("获取趋势图数据")
    @GetMapping("/{id}/trend")
    public Result<List<StabilityTrendVO>> getTrendData(@PathVariable Long id) {
        return Result.success(stabilityService.getTrendData(id));
    }

    @ApiOperation("预估保质期")
    @GetMapping("/{id}/estimate")
    public Result<Integer> estimateShelfLife(@PathVariable Long id) {
        return Result.success(stabilityService.estimateShelfLife(id));
    }

    @ApiOperation("生成考察报告")
    @GetMapping("/{id}/generateReport")
    public void generateReport(@PathVariable Long id, HttpServletResponse response) throws Exception {
        byte[] data = stabilityService.generateReport(id);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=stability-report.xlsx");
        try (OutputStream out = response.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }

    @ApiOperation("获取统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(stabilityService.getStats());
    }
}
