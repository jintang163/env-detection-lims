package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("CAPA处理日志VO")
public class CapaProcessLogVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("CAPA记录ID")
    private Long capaId;

    @ApiModelProperty("操作类型")
    private String operationType;

    @ApiModelProperty("操作状态")
    private String operationStatus;

    @ApiModelProperty("操作内容")
    private String operationContent;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    @ApiModelProperty("备注")
    private String remark;
}
