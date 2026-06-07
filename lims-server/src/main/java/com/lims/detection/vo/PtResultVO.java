package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("能力验证结果VO")
public class PtResultVO {

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

    @ApiModelProperty("能力验证计划ID")
    private Long ptPlanId;

    @ApiModelProperty("能力验证样品ID")
    private Long ptSampleId;

    @ApiModelProperty("结果编号")
    private String resultNo;

    @ApiModelProperty("检测值")
    private BigDecimal detectedValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("检测日期")
    private LocalDateTime measureDate;

    @ApiModelProperty("操作人员")
    private String operator;

    @ApiModelProperty("检测仪器")
    private String instrument;

    @ApiModelProperty("检测方法名称")
    private String methodName;

    @ApiModelProperty("不确定度")
    private BigDecimal uncertainty;

    @ApiModelProperty("Z比分")
    private BigDecimal zScore;

    @ApiModelProperty("Z比分类型")
    private String zScoreType;

    @ApiModelProperty("评价结果")
    private String evaluation;

    @ApiModelProperty("报告状态")
    private String reportStatus;

    @ApiModelProperty("报告日期")
    private LocalDateTime reportDate;

    @ApiModelProperty("反馈结果")
    private String feedbackResult;

    @ApiModelProperty("反馈文件")
    private String feedbackFile;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品代码")
    private String sampleCode;

    @ApiModelProperty("评价文本")
    private String evaluationText;
}
