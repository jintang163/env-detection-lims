package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.DetectionTaskQuery;
import com.lims.detection.dto.DetectionTaskSaveDTO;
import com.lims.detection.dto.TaskAssignDTO;
import com.lims.detection.dto.TaskReviewDTO;
import com.lims.detection.service.DetectionTaskService;
import com.lims.detection.vo.AssignmentRecommendationVO;
import com.lims.detection.vo.DetectionTaskDetailVO;
import com.lims.detection.vo.DetectionTaskVO;
import com.lims.detection.vo.TaskBoardStatsVO;
import com.lims.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "检测任务管理")
@RestController
@RequestMapping("/detection/task")
public class DetectionTaskController {

    @Autowired
    private DetectionTaskService detectionTaskService;

    @ApiOperation("分页查询检测任务")
    @GetMapping("/page")
    public Result<PageResult<DetectionTaskVO>> selectPage(DetectionTaskQuery query) {
        return Result.success(detectionTaskService.selectPage(query));
    }

    @ApiOperation("获取检测任务详情")
    @GetMapping("/{id}")
    public Result<DetectionTaskDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(detectionTaskService.getDetail(id));
    }

    @ApiOperation("新增检测任务")
    @PostMapping
    public Result<Void> saveTask(@RequestBody @Validated DetectionTaskSaveDTO dto) {
        detectionTaskService.saveTask(dto);
        return Result.success();
    }

    @ApiOperation("修改检测任务")
    @PutMapping
    public Result<Void> updateTask(@RequestBody @Validated DetectionTaskSaveDTO dto) {
        detectionTaskService.updateTask(dto);
        return Result.success();
    }

    @ApiOperation("删除检测任务")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        detectionTaskService.deleteTask(id);
        return Result.success();
    }

    @ApiOperation("分配检测任务（支持批量）")
    @PostMapping("/assign")
    public Result<Void> assignTasks(@RequestBody @Validated TaskAssignDTO dto) {
        detectionTaskService.assignTasks(dto);
        return Result.success();
    }

    @ApiOperation("接收任务")
    @PostMapping("/accept/{taskId}")
    public Result<Void> acceptTask(@PathVariable Long taskId) {
        detectionTaskService.acceptTask(taskId);
        return Result.success();
    }

    @ApiOperation("开始检测")
    @PostMapping("/start/{taskId}")
    public Result<Void> startTask(@PathVariable Long taskId) {
        detectionTaskService.startTask(taskId);
        return Result.success();
    }

    @ApiOperation("提交数据录入")
    @PostMapping("/submitDataEntry/{taskId}")
    public Result<Void> submitDataEntry(@PathVariable Long taskId) {
        detectionTaskService.submitDataEntry(taskId);
        return Result.success();
    }

    @ApiOperation("提交审核")
    @PostMapping("/submitReview/{taskId}")
    public Result<Void> submitReview(@PathVariable Long taskId) {
        detectionTaskService.submitReview(taskId);
        return Result.success();
    }

    @ApiOperation("审核任务")
    @PostMapping("/review")
    public Result<Void> reviewTask(@RequestBody @Validated TaskReviewDTO dto) {
        detectionTaskService.reviewTask(dto);
        return Result.success();
    }

    @ApiOperation("暂停任务")
    @PostMapping("/pause/{taskId}")
    public Result<Void> pauseTask(@PathVariable Long taskId, @RequestParam String reason) {
        detectionTaskService.pauseTask(taskId, reason);
        return Result.success();
    }

    @ApiOperation("重启任务")
    @PostMapping("/resume/{taskId}")
    public Result<Void> resumeTask(@PathVariable Long taskId) {
        detectionTaskService.resumeTask(taskId);
        return Result.success();
    }

    @ApiOperation("终止任务")
    @PostMapping("/terminate/{taskId}")
    public Result<Void> terminateTask(@PathVariable Long taskId, @RequestParam String reason) {
        detectionTaskService.terminateTask(taskId, reason);
        return Result.success();
    }

    @ApiOperation("抢单")
    @PostMapping("/grab/{taskId}")
    public Result<Void> grabTask(@PathVariable Long taskId) {
        Long userId = SecurityUtils.getCurrentUserId();
        String userName = SecurityUtils.getCurrentUsername();
        detectionTaskService.grabTask(taskId, userId, userName);
        return Result.success();
    }

    @ApiOperation("获取我的任务列表")
    @GetMapping("/myTasks")
    public Result<List<DetectionTaskVO>> getMyTasks() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(detectionTaskService.getMyTasks(userId));
    }

    @ApiOperation("获取抢单池列表")
    @GetMapping("/grabOrderPool")
    public Result<List<DetectionTaskVO>> getGrabOrderPool() {
        return Result.success(detectionTaskService.getGrabOrderPool());
    }

    @ApiOperation("获取任务分配推荐")
    @GetMapping("/assignmentRecommendations/{taskId}")
    public Result<List<AssignmentRecommendationVO>> getAssignmentRecommendations(@PathVariable Long taskId) {
        return Result.success(detectionTaskService.getAssignmentRecommendations(taskId));
    }

    @ApiOperation("获取任务看板统计")
    @GetMapping("/boardStats")
    public Result<TaskBoardStatsVO> getBoardStats() {
        return Result.success(detectionTaskService.getBoardStats());
    }

    @ApiOperation("根据状态获取任务列表")
    @GetMapping("/byStatus/{status}")
    public Result<List<DetectionTaskVO>> getTasksByStatus(@PathVariable Integer status) {
        return Result.success(detectionTaskService.getTasksByStatus(status));
    }
}
