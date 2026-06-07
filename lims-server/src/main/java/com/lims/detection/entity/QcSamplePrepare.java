package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_sample_prepare")
@ApiModel("质控样品配制记录表")
public class QcSamplePrepare extends BaseEntity {

    @ApiModelProperty("配制编号")
    private String prepareNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3加标样")
    private String sampleType;

    @ApiModelProperty("批号")
    private String batchNo;

    @ApiModelProperty("配制体积")
    private BigDecimal prepareVolume;

    @ApiModelProperty("体积单位")
    private String volumeUnit;

    @ApiModelProperty("浓度值")
    private BigDecimal concentration;

    @ApiModelProperty("浓度单位")
    private String unit;

    @ApiModelProperty("配制方法")
    private String prepareMethod;

    @ApiModelProperty("使用试剂")
    private String reagents;

    @ApiModelProperty("使用仪器")
    private String instruments;

    @ApiModelProperty("配制人")
    private String prepareBy;

    @ApiModelProperty("复核人")
    private String verifyBy;

    @ApiModelProperty("环境条件")
    private String environment;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
