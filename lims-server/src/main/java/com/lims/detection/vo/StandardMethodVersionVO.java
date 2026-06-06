package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("标准方法版本历史VO")
public class StandardMethodVersionVO {

    @ApiModelProperty("版本记录ID")
    private Long id;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("方法编号")
    private String methodCode;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("变更类型")
    private String changeType;

    @ApiModelProperty("变更内容")
    private String changeContent;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
