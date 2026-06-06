package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("任务分配DTO")
public class TaskAssignDTO {

    @ApiModelProperty("任务ID列表")
    @NotEmpty(message = "任务ID列表不能为空")
    private List<Long> taskIdList;

    @ApiModelProperty("分配类型 1手动分配 2智能分配 3抢单 4改派")
    @NotNull(message = "分配类型不能为空")
    private Integer assignmentType;

    @ApiModelProperty("分配对象类型 1个人 2科室")
    @NotNull(message = "分配对象类型不能为空")
    private Integer assigneeType;

    @ApiModelProperty("分配科室ID")
    private Long deptId;

    @ApiModelProperty("分配科室名称")
    private String deptName;

    @ApiModelProperty("分配人员ID")
    private Long assigneeId;

    @ApiModelProperty("分配人员姓名")
    private String assigneeName;

    @ApiModelProperty("分配原因")
    private String assignReason;

    @ApiModelProperty("分配时间")
    private LocalDateTime assignTime;

    @ApiModelProperty("计划开始日期")
    private LocalDateTime planStartDate;

    @ApiModelProperty("计划完成日期")
    private LocalDateTime planEndDate;
}
