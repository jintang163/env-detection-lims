package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("capa_record")
@ApiModel("CAPA记录表")
public class CapaRecord extends BaseEntity {

    @ApiModelProperty("CAPA编号")
    private String capaNo;

    @ApiModelProperty("CAPA类型 纠正/预防")
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

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("风险评估")
    private String riskAssessment;

    @ApiModelProperty("建议措施")
    private String proposedMeasures;

    @ApiModelProperty("实施人ID")
    private Long implementor;

    @ApiModelProperty("实施人姓名")
    private String implementorName;

    @ApiModelProperty("验证人ID")
    private Long verifier;

    @ApiModelProperty("验证人姓名")
    private String verifierName;

    @ApiModelProperty("批准人ID")
    private Long approver;

    @ApiModelProperty("批准人姓名")
    private String approverName;

    @ApiModelProperty("计划完成日期")
    private LocalDateTime planCompleteDate;

    @ApiModelProperty("实际完成日期")
    private LocalDateTime actualCompleteDate;

    @ApiModelProperty("验证日期")
    private LocalDateTime verificationDate;

    @ApiModelProperty("验证结果")
    private String verificationResult;

    @ApiModelProperty("验证证据")
    private String verificationEvidence;

    @ApiModelProperty("关闭日期")
    private LocalDateTime closeDate;

    @ApiModelProperty("有效性评审")
    private String effectivenessReview;

    @ApiModelProperty("备注")
    private String remark;
}
