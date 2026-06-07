package com.lims.detection.controller;

import com.lims.common.result.Result;
import com.lims.detection.dto.DataReviewSaveDTO;
import com.lims.detection.service.DataReviewService;
import com.lims.detection.vo.DataReviewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "数据审核")
@RestController
@RequestMapping("/detection/dataReview")
public class DataReviewController {

    @Autowired
    private DataReviewService dataReviewService;

    @ApiOperation("获取审核记录列表")
    @GetMapping("/list/{dataRecordId}")
    public Result<List<DataReviewVO>> getReviewList(@PathVariable Long dataRecordId) {
        return Result.success(dataReviewService.getReviewListByDataRecordId(dataRecordId));
    }

    @ApiOperation("一级审核")
    @PostMapping("/firstReview")
    public Result<Void> firstReview(@RequestBody @Validated DataReviewSaveDTO dto) {
        dataReviewService.firstReview(dto);
        return Result.success();
    }

    @ApiOperation("二级审核")
    @PostMapping("/secondReview")
    public Result<Void> secondReview(@RequestBody @Validated DataReviewSaveDTO dto) {
        dataReviewService.secondReview(dto);
        return Result.success();
    }

    @ApiOperation("驳回")
    @PostMapping("/reject")
    public Result<Void> reject(@RequestParam Long dataRecordId, @RequestParam Integer reviewLevel, @RequestParam String rejectReason) {
        dataReviewService.rejectData(dataRecordId, reviewLevel, rejectReason);
        return Result.success();
    }

    @ApiOperation("获取我的待审核任务")
    @GetMapping("/myTasks")
    public Result<List<DataReviewVO>> getMyTasks(@RequestParam Long userId) {
        return Result.success(dataReviewService.getMyReviewTasks(userId));
    }

    @ApiOperation("待审核数量")
    @GetMapping("/pendingCount")
    public Result<Long> getPendingCount(@RequestParam Long userId, @RequestParam Integer reviewLevel) {
        return Result.success(dataReviewService.getPendingReviewCount(userId, reviewLevel));
    }
}
