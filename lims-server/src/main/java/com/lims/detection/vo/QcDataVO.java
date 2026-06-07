package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("质控数据VO（含Z分数、Cusum等）")
public class QcDataVO {

    @ApiModelProperty("数据ID")
    private Long id;

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("序号")
    private Integer seqNo;

    @ApiModelProperty("检测项目")
    private String project;

    @ApiModelProperty("质控样ID")
    private Long sampleId;

    @ApiModelProperty("质控样名称")
    private String sampleName;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("检测值")
    private BigDecimal testValue;

    @ApiModelProperty("测定值(前端期望字段)")
    private BigDecimal measuredValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("理论值/均值")
    private BigDecimal mean;

    @ApiModelProperty("标准差")
    private BigDecimal sd;

    @ApiModelProperty("Z分数")
    private BigDecimal zScore;

    @ApiModelProperty("累积和CUSUM-正")
    private BigDecimal cusumPos;

    @ApiModelProperty("累积和CUSUM-负")
    private BigDecimal cusumNeg;

    @ApiModelProperty("状态 0在控 1警告 2失控")
    private Integer status;

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
}
