package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("质控记录VO")
public class QcRecordVO {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("质控计划ID")
    private Long planId;

    @ApiModelProperty("质控计划名称")
    private String planName;

    @ApiModelProperty("检测项目")
    private String projectName;

    @ApiModelProperty("检测方法")
    private String methodName;

    @ApiModelProperty("质控样ID")
    private Long sampleId;

    @ApiModelProperty("质控样名称")
    private String sampleName;

    @ApiModelProperty("质控样类型")
    private String sampleType;

    @ApiModelProperty("浓度值")
    private BigDecimal concentration;

    @ApiModelProperty("浓度单位")
    private String unit;

    @ApiModelProperty("检测值")
    private BigDecimal measuredValue;

    @ApiModelProperty("Z分数")
    private BigDecimal zScore;

    @ApiModelProperty("状态 normal-正常 outlier-异常")
    private String status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("违反规则列表")
    private List<QcViolatedRuleVO> violatedRules;

    @ApiModelProperty("操作人员")
    private String operator;

    @ApiModelProperty("测量时间")
    private LocalDateTime measureDate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
