package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("任务状态流转日志VO")
public class TaskStatusLogVO {

    @ApiModelProperty("日志ID")
    private Long id;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("变更前状态")
    private Integer beforeStatus;

    @ApiModelProperty("变更前状态名称")
    private String beforeStatusName;

    @ApiModelProperty("变更后状态")
    private Integer afterStatus;

    @ApiModelProperty("变更后状态名称")
    private String afterStatusName;

    @ApiModelProperty("操作类型")
    private String operateType;

    @ApiModelProperty("操作内容")
    private String operateContent;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;
}
