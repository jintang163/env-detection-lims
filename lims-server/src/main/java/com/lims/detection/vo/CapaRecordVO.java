package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("CAPA记录VO")
public class CapaRecordVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

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

    @ApiModelProperty("处理日志列表")
    private List<CapaProcessLogVO> processLogs;
}
