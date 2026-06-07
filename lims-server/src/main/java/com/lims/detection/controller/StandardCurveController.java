package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.StdCurveCalcDTO;
import com.lims.detection.dto.StdCurveQuery;
import com.lims.detection.dto.StdCurveSaveDTO;
import com.lims.detection.service.StandardCurveService;
import com.lims.detection.vo.StdCurveCalcResultVO;
import com.lims.detection.vo.StdCurvePointVO;
import com.lims.detection.vo.StdCurveVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "标准曲线管理")
@RestController
@RequestMapping("/detection/standardCurve")
public class StandardCurveController {

    @Autowired
    private StandardCurveService standardCurveService;

    @ApiOperation("分页查询曲线")
    @GetMapping("/page")
    public Result<PageResult<StdCurveVO>> selectPage(StdCurveQuery query) {
        return Result.success(standardCurveService.selectPage(query));
    }

    @ApiOperation("获取详情（含曲线点）")
    @GetMapping("/{id}")
    public Result<StdCurveVO> getDetail(@PathVariable Long id) {
        return Result.success(standardCurveService.getDetail(id));
    }

    @ApiOperation("保存曲线（自动计算参数）")
    @PostMapping
    public Result<Void> saveCurve(@RequestBody @Validated StdCurveSaveDTO dto) {
        standardCurveService.saveCurve(dto);
        return Result.success();
    }

    @ApiOperation("更新曲线")
    @PutMapping
    public Result<Void> updateCurve(@RequestBody @Validated StdCurveSaveDTO dto) {
        standardCurveService.updateCurve(dto);
        return Result.success();
    }

    @ApiOperation("作废曲线")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCurve(@PathVariable Long id) {
        standardCurveService.deleteCurve(id);
        return Result.success();
    }

    @ApiOperation("仅计算曲线参数（不保存）")
    @PostMapping("/calculate")
    public Result<StdCurveCalcResultVO> calculateCurve(@RequestBody StdCurveCalcDTO dto) {
        return Result.success(standardCurveService.calculateCurve(dto));
    }

    @ApiOperation("获取曲线点")
    @GetMapping("/{id}/points")
    public Result<List<StdCurvePointVO>> getCurvePoints(@PathVariable Long id) {
        return Result.success(standardCurveService.getCurvePoints(id));
    }

    @ApiOperation("获取项目有效曲线")
    @GetMapping("/valid/{itemCode}")
    public Result<List<StdCurveVO>> getValidCurves(@PathVariable String itemCode) {
        return Result.success(standardCurveService.getValidCurves(itemCode));
    }

    @ApiOperation("获取统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        return Result.success(standardCurveService.getStats());
    }

    @ApiOperation("验证曲线（用验证点计算回收率）")
    @PostMapping("/{id}/verify")
    public Result<Map<String, Object>> verifyCurve(
            @PathVariable Long id,
            @RequestParam List<BigDecimal> verifyConcentrations,
            @RequestParam List<BigDecimal> verifyResponses) {
        return Result.success(standardCurveService.verifyCurve(id, verifyConcentrations, verifyResponses));
    }
}
