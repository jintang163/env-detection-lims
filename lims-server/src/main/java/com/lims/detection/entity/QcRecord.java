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
@TableName("qc_record")
@ApiModel("质控执行记录表")
public class QcRecord extends BaseEntity {

    @ApiModelProperty("质控计划ID")
    private Long planId;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("执行时间")
    private LocalDateTime executeDate;

    @ApiModelProperty("批号")
    private String batchNo;

    @ApiModelProperty("质控样品ID")
    private Long qcSampleId;

    @ApiModelProperty("质控样品类型")
    private String qcSampleType;

    @ApiModelProperty("质控样品名称")
    private String qcSampleName;

    @ApiModelProperty("测量值")
    private BigDecimal measuredValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("操作人员")
    private String operator;

    @ApiModelProperty("状态 0草稿 1已执行 2已审核")
    private Integer status;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
