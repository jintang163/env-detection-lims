package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("任务分配记录VO")
public class TaskAssignmentVO {

    @ApiModelProperty("分配记录ID")
    private Long id;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("分配类型 1手动分配 2智能分配 3抢单 4改派")
    private Integer assignmentType;

    @ApiModelProperty("分配类型名称")
    private String assignmentTypeName;

    @ApiModelProperty("分配对象类型 1个人 2科室")
    private Integer assigneeType;

    @ApiModelProperty("原科室ID")
    private Long oldDeptId;

    @ApiModelProperty("原科室名称")
    private String oldDeptName;

    @ApiModelProperty("原分配人员ID")
    private Long oldAssigneeId;

    @ApiModelProperty("原分配人员姓名")
    private String oldAssigneeName;

    @ApiModelProperty("新科室ID")
    private Long newDeptId;

    @ApiModelProperty("新科室名称")
    private String newDeptName;

    @ApiModelProperty("新分配人员ID")
    private Long newAssigneeId;

    @ApiModelProperty("新分配人员姓名")
    private String newAssigneeName;

    @ApiModelProperty("分配原因")
    private String assignReason;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("备注")
    private String remark;
}
