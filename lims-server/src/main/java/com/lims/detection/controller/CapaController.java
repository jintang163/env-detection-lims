package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.CapaProcessDTO;
import com.lims.detection.dto.CapaQuery;
import com.lims.detection.dto.CapaSaveDTO;
import com.lims.detection.dto.CapaVerificationDTO;
import com.lims.detection.service.CapaService;
import com.lims.detection.vo.CapaProcessLogVO;
import com.lims.detection.vo.CapaRecordVO;
import com.lims.detection.vo.CapaStatsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "纠正预防措施(CAPA)")
@RestController
@RequestMapping("/detection/capa")
public class CapaController {

    @Autowired
    private CapaService capaService;

    @ApiOperation("分页查询CAPA")
    @GetMapping("/page")
    public Result<PageResult<CapaRecordVO>> selectPage(CapaQuery query) {
        return Result.success(capaService.selectPage(query));
    }

    @ApiOperation("获取CAPA详情")
    @GetMapping("/{id}")
    public Result<CapaRecordVO> getDetail(@PathVariable Long id) {
        return Result.success(capaService.getDetail(id));
    }

    @ApiOperation("创建CAPA")
    @PostMapping
    public Result<Long> createCapa(@RequestBody @Validated CapaSaveDTO dto) {
        return Result.success(capaService.createCapa(dto));
    }

    @ApiOperation("更新CAPA")
    @PutMapping
    public Result<Void> updateCapa(@RequestBody @Validated CapaSaveDTO dto) {
        capaService.updateCapa(dto);
        return Result.success();
    }

    @ApiOperation("删除CAPA")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCapa(@PathVariable Long id) {
        capaService.deleteCapa(id);
        return Result.success();
    }

    @ApiOperation("提交审批")
    @PostMapping("/{id}/submit")
    public Result<Void> submitApproval(@PathVariable Long id) {
        capaService.submitApproval(id);
        return Result.success();
    }

    @ApiOperation("审批通过")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody @Validated CapaProcessDTO dto) {
        capaService.approve(id, dto);
        return Result.success();
    }

    @ApiOperation("审批驳回")
    @PostMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id, @RequestBody @Validated CapaProcessDTO dto) {
        capaService.reject(id, dto);
        return Result.success();
    }

    @ApiOperation("执行完成")
    @PostMapping("/{id}/executeComplete")
    public Result<Void> executeComplete(@PathVariable Long id, @RequestBody @Validated CapaProcessDTO dto) {
        capaService.executeComplete(id, dto);
        return Result.success();
    }

    @ApiOperation("验证通过")
    @PostMapping("/{id}/verifyPass")
    public Result<Void> verifyPass(@PathVariable Long id, @RequestBody @Validated CapaVerificationDTO dto) {
        capaService.verifyPass(id, dto);
        return Result.success();
    }

    @ApiOperation("验证不通过")
    @PostMapping("/{id}/verifyFail")
    public Result<Void> verifyFail(@PathVariable Long id, @RequestBody @Validated CapaProcessDTO dto) {
        capaService.verifyFail(id, dto);
        return Result.success();
    }

    @ApiOperation("关闭CAPA")
    @PostMapping("/{id}/close")
    public Result<Void> closeCapa(@PathVariable Long id, @RequestBody @Validated CapaProcessDTO dto) {
        capaService.closeCapa(id, dto);
        return Result.success();
    }

    @ApiOperation("获取处理日志")
    @GetMapping("/{id}/logs")
    public Result<List<CapaProcessLogVO>> getProcessLogs(@PathVariable Long id) {
        return Result.success(capaService.getProcessLogs(id));
    }

    @ApiOperation("获取统计")
    @GetMapping("/stats")
    public Result<CapaStatsVO> getStats() {
        return Result.success(capaService.getStats());
    }

    @ApiOperation("获取超期CAPA")
    @GetMapping("/overdue")
    public Result<List<CapaRecordVO>> getOverdueCapa() {
        return Result.success(capaService.getOverdueCapa());
    }
}
