package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.OriginalRecordQuery;
import com.lims.detection.dto.OriginalRecordSaveDTO;
import com.lims.detection.service.OriginalRecordService;
import com.lims.detection.vo.OriginalRecordDetailVO;
import com.lims.detection.vo.OriginalRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "原始记录管理")
@RestController
@RequestMapping("/detection/originalRecord")
public class OriginalRecordController {

    @Autowired
    private OriginalRecordService originalRecordService;

    @ApiOperation("分页查询原始记录")
    @GetMapping("/page")
    public Result<PageResult<OriginalRecordVO>> selectPage(OriginalRecordQuery query) {
        return Result.success(originalRecordService.selectPage(query));
    }

    @ApiOperation("获取原始记录详情")
    @GetMapping("/{id}")
    public Result<OriginalRecordDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(originalRecordService.getDetail(id));
    }

    @ApiOperation("保存原始记录")
    @PostMapping
    public Result<Void> saveOriginalRecord(@RequestBody @Validated OriginalRecordSaveDTO dto) {
        originalRecordService.saveOriginalRecord(dto);
        return Result.success();
    }

    @ApiOperation("更新原始记录")
    @PutMapping
    public Result<Void> updateOriginalRecord(@RequestBody @Validated OriginalRecordSaveDTO dto) {
        originalRecordService.updateOriginalRecord(dto);
        return Result.success();
    }

    @ApiOperation("提交原始记录")
    @PostMapping("/submit/{id}")
    public Result<Void> submitOriginalRecord(@PathVariable Long id) {
        originalRecordService.submitOriginalRecord(id);
        return Result.success();
    }

    @ApiOperation("归档原始记录")
    @PostMapping("/archive/{id}")
    public Result<Void> archiveOriginalRecord(@PathVariable Long id) {
        originalRecordService.archiveOriginalRecord(id);
        return Result.success();
    }

    @ApiOperation("删除原始记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOriginalRecord(@PathVariable Long id) {
        originalRecordService.deleteOriginalRecord(id);
        return Result.success();
    }

    @ApiOperation("预览原始记录（HTML）")
    @GetMapping("/preview/{id}")
    public Result<String> previewAsHtml(@PathVariable Long id) {
        return Result.success(originalRecordService.previewAsHtml(id));
    }

    @ApiOperation("检查权限")
    @GetMapping("/permission/{id}")
    public Result<Boolean> checkPermission(@PathVariable Long id, @RequestParam Long userId, @RequestParam String userRole) {
        return Result.success(originalRecordService.checkPermission(id, userId, userRole));
    }

    @ApiOperation("验证原始记录完整性")
    @GetMapping("/verify/{id}")
    public Result<Boolean> verifyOriginalRecordIntegrity(@PathVariable Long id) {
        return Result.success(originalRecordService.verifyOriginalRecordIntegrity(id));
    }
}
