package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_sample")
@ApiModel("质控样品表")
public class QcSample extends BaseEntity {

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3加标样")
    private String sampleType;

    @ApiModelProperty("批号")
    private String batchNo;

    @ApiModelProperty("浓度值")
    private BigDecimal concentration;

    @ApiModelProperty("浓度单位")
    private String unit;

    @ApiModelProperty("不确定度")
    private String uncertainty;

    @ApiModelProperty("库存数量")
    private Integer stockQuantity;

    @ApiModelProperty("配制日期")
    private LocalDate prepareDate;

    @ApiModelProperty("有效期至")
    private LocalDate expireDate;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("溯源信息")
    private String traceability;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
