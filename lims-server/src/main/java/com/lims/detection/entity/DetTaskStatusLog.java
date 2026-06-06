package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("det_task_status_log")
@ApiModel("检测任务状态流转日志表")
public class DetTaskStatusLog implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("日志ID")
    private Long id;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("变更前状态")
    private Integer beforeStatus;

    @ApiModelProperty("变更后状态")
    private Integer afterStatus;

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
