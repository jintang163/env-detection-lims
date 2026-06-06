package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("人员任务统计VO")
public class UserTaskStatsVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("任务总数")
    private Long totalCount;

    @ApiModelProperty("待处理数量")
    private Long pendingCount;

    @ApiModelProperty("进行中数量")
    private Long inProgressCount;

    @ApiModelProperty("已完成数量")
    private Long completedCount;

    @ApiModelProperty("今日完成数量")
    private Long todayCompletedCount;
}
