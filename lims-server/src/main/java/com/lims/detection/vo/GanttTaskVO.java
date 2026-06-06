package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("甘特图任务VO")
public class GanttTaskVO {

    @ApiModelProperty("任务ID")
    private String id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("资源ID")
    private String resourceId;

    @ApiModelProperty("开始时间")
    private LocalDateTime start;

    @ApiModelProperty("结束时间")
    private LocalDateTime end;

    @ApiModelProperty("进度(%)")
    private Integer progress;

    @ApiModelProperty("优先级 1高 2中 3低")
    private Integer priority;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("颜色")
    private String color;
}
