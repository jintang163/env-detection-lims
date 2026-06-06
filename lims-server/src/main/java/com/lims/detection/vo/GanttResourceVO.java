package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("甘特图资源VO")
public class GanttResourceVO {

    @ApiModelProperty("资源ID")
    private String id;

    @ApiModelProperty("资源名称")
    private String name;

    @ApiModelProperty("资源类型")
    private String type;

    @ApiModelProperty("父资源ID")
    private String parentId;
}
