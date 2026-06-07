package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("CAPA处理DTO")
public class CapaProcessDTO {

    @ApiModelProperty("CAPA ID")
    private Long id;

    @ApiModelProperty("操作内容")
    private String operationContent;

    @ApiModelProperty("操作状态")
    private Integer operationStatus;

    @ApiModelProperty("附件列表")
    private List<String> attachments;
}
