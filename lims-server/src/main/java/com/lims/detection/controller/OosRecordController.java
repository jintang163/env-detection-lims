package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.OosCloseDTO;
import com.lims.detection.dto.OosInvestigationDTO;
import com.lims.detection.dto.OosRecordQuery;
import com.lims.detection.dto.OosRecordSaveDTO;
import com.lims.detection.dto.OosReviewDTO;
import com.lims.detection.service.OosRecordService;
import com.lims.detection.vo.OosRecordDetailVO;
import com.lims.detection.vo.OosRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "超标预警(OOS)")
@RestController
@RequestMapping("/detection/oosRecord")
public class OosRecordController {

    @Autowired
    private OosRecordService oosRecordService;

    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult<OosRecordVO>> selectPage(OosRecordQuery query) {
        return Result.success(oosRecordService.selectPage(query));
    }

    @ApiOperation("获取详情")
    @GetMapping("/{id}")
    public Result<OosRecordDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(oosRecordService.getDetail(id));
    }

    @ApiOperation("手动创建OOS")
    @PostMapping
    public Result<Void> saveOosRecord(@RequestBody @Validated OosRecordSaveDTO dto) {
        oosRecordService.saveOosRecord(dto);
        return Result.success();
    }

    @ApiOperation("自动创建OOS")
    @PostMapping("/autoCreate")
    public Result<Void> autoCreateOos(@RequestParam Long dataRecordId, @RequestParam Long dataItemId) {
        oosRecordService.createOosRecord(dataRecordId, dataItemId);
        return Result.success();
    }

    @ApiOperation("启动调查")
    @PostMapping("/startInvestigation")
    public Result<Void> startInvestigation(@RequestBody @Validated OosInvestigationDTO dto) {
        oosRecordService.startInvestigation(dto);
        return Result.success();
    }

    @ApiOperation("完成调查")
    @PostMapping("/completeInvestigation")
    public Result<Void> completeInvestigation(@RequestBody @Validated OosInvestigationDTO dto) {
        oosRecordService.completeInvestigation(dto);
        return Result.success();
    }

    @ApiOperation("审核OOS")
    @PostMapping("/review")
    public Result<Void> reviewOos(@RequestBody @Validated OosReviewDTO dto) {
        oosRecordService.reviewOos(dto);
        return Result.success();
    }

    @ApiOperation("关闭OOS")
    @PostMapping("/close")
    public Result<Void> closeOos(@RequestBody @Validated OosCloseDTO dto) {
        oosRecordService.closeOos(dto);
        return Result.success();
    }

    @ApiOperation("分配调查人员")
    @PostMapping("/assign")
    public Result<Void> assignInvestigator(@RequestParam Long oosId, @RequestParam Long investigatorUserId, @RequestParam String investigatorUserName) {
        oosRecordService.assignInvestigator(oosId, investigatorUserId, investigatorUserName);
        return Result.success();
    }

    @ApiOperation("待调查列表")
    @GetMapping("/pendingInvestigation")
    public Result<List<OosRecordVO>> getPendingInvestigationList() {
        return Result.success(oosRecordService.getPendingInvestigationList());
    }

    @ApiOperation("待审核列表")
    @GetMapping("/pendingReview")
    public Result<List<OosRecordVO>> getPendingReviewList() {
        return Result.success(oosRecordService.getPendingReviewList());
    }

    @ApiOperation("待调查数量")
    @GetMapping("/stats/pendingInvestigationCount")
    public Result<Long> getPendingInvestigationCount() {
        return Result.success(oosRecordService.getPendingInvestigationCount());
    }

    @ApiOperation("待审核数量")
    @GetMapping("/stats/pendingReviewCount")
    public Result<Long> getPendingReviewCount() {
        return Result.success(oosRecordService.getPendingReviewCount());
    }

    @ApiOperation("未关闭总数")
    @GetMapping("/stats/openCount")
    public Result<Long> getOpenCount() {
        return Result.success(oosRecordService.getOpenOosCount());
    }
}
