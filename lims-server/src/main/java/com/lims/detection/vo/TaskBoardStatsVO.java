package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务进度看板统计VO")
public class TaskBoardStatsVO {

    @ApiModelProperty("待分配任务数量")
    private Long pendingAssignCount;

    @ApiModelProperty("待接收任务数量")
    private Long pendingAcceptCount;

    @ApiModelProperty("进行中任务数量")
    private Long inProgressCount;

    @ApiModelProperty("数据录入中任务数量")
    private Long dataEntryCount;

    @ApiModelProperty("审核中任务数量")
    private Long reviewingCount;

    @ApiModelProperty("已完成任务数量")
    private Long completedCount;

    @ApiModelProperty("已暂停任务数量")
    private Long pausedCount;

    @ApiModelProperty("已终止任务数量")
    private Long terminatedCount;

    @ApiModelProperty("今日新增任务数量")
    private Long todayNewCount;

    @ApiModelProperty("今日完成任务数量")
    private Long todayCompletedCount;

    @ApiModelProperty("加急任务数量")
    private Long urgentCount;

    @ApiModelProperty("抢单池任务数量")
    private Long grabOrderCount;

    @ApiModelProperty("各部门任务数量统计")
    private List<DeptTaskStatsVO> deptStats;

    @ApiModelProperty("各人员任务数量统计")
    private List<UserTaskStatsVO> userStats;
}
