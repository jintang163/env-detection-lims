package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("CAPA保存DTO")
public class CapaSaveDTO {

    @ApiModelProperty("CAPA ID（更新时传）")
    private Long id;

    @ApiModelProperty("CAPA类型")
    private String capaType;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("来源ID")
    private Long sourceId;

    @ApiModelProperty("来源编号")
    private String sourceNo;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("问题描述")
    private String description;

    @ApiModelProperty("根本原因")
    private String rootCause;

    @ApiModelProperty("根本原因分析方法")
    private String rootCauseMethod;

    @ApiModelProperty("严重程度")
    private String severityLevel;

    @ApiModelProperty("优先级")
    private String priority;

    @ApiModelProperty("风险评估")
    private String riskAssessment;

    @ApiModelProperty("拟采取措施")
    private String proposedMeasures;

    @ApiModelProperty("执行人ID")
    private Long implementor;

    @ApiModelProperty("执行人姓名")
    private String implementorName;

    @ApiModelProperty("验证人ID")
    private Long verifier;

    @ApiModelProperty("验证人姓名")
    private String verifierName;

    @ApiModelProperty("计划完成日期")
    private LocalDateTime planCompleteDate;

    @ApiModelProperty("备注")
    private String remark;
}
