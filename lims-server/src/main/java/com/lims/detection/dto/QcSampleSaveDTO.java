package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("质控样品保存DTO")
public class QcSampleSaveDTO {

    @ApiModelProperty("样品ID（更新时传）")
    private Long id;

    @ApiModelProperty("样品编号")
    @NotBlank(message = "样品编号不能为空")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    @NotBlank(message = "样品名称不能为空")
    private String sampleName;

    @ApiModelProperty("样品类型 1标准物质 2质控样 3空白样 4平行样")
    @NotBlank(message = "样品类型不能为空")
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

    @ApiModelProperty("备注")
    private String remark;
}
