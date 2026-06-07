package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("质控样品VO")
public class QcSampleVO {

    @ApiModelProperty("样品ID")
    private Long id;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3空白样 4平行样")
    private String sampleType;

    @ApiModelProperty("样品类型名称")
    private String sampleTypeName;

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

    @ApiModelProperty("过期状态 0未过期 1即将过期 2已过期")
    private Integer expireStatus;

    @ApiModelProperty("过期状态名称")
    private String expireStatusName;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("溯源信息")
    private String traceability;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
