package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("甘特图数据VO")
public class GanttChartVO {

    @ApiModelProperty("资源类型 user/dept/equipment")
    private String resourceType;

    @ApiModelProperty("资源列表")
    private List<GanttResourceVO> resources;

    @ApiModelProperty("任务列表")
    private List<GanttTaskVO> tasks;
}
