package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_data")
@ApiModel("质控数据表")
public class QcData extends BaseEntity {

    @ApiModelProperty("质控计划ID")
    private Long planId;

    @ApiModelProperty("样品ID")
    private Long sampleId;

    @ApiModelProperty("序列号")
    private Integer seqNo;

    @ApiModelProperty("批号")
    private String batchNo;

    @ApiModelProperty("测量时间")
    private LocalDateTime measureDate;

    @ApiModelProperty("测量值")
    private BigDecimal measuredValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("均值")
    private BigDecimal meanValue;

    @ApiModelProperty("标准偏差")
    private BigDecimal sdValue;

    @ApiModelProperty("Z分数")
    private BigDecimal zScore;

    @ApiModelProperty("CUSUM正值")
    private BigDecimal cusumPos;

    @ApiModelProperty("CUSUM负值")
    private BigDecimal cusumNeg;

    @ApiModelProperty("状态 normal-正常 outlier-异常")
    private String status;

    @ApiModelProperty("违规规则ID列表(JSON格式)")
    private String violatedRules;

    @ApiModelProperty("检测项目")
    private String project;

    @ApiModelProperty("检测方法名称")
    private String methodName;

    @ApiModelProperty("操作人员")
    private String operator;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;
}
