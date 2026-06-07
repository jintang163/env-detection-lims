package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("检测数据明细VO")
public class DataRecordItemVO {

    @ApiModelProperty("明细ID")
    private Long id;

    @ApiModelProperty("检测数据记录ID")
    private Long recordId;

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("表单字段ID")
    private Long fieldId;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("检测项目编码")
    private String itemCode;

    @ApiModelProperty("检测项目名称")
    private String itemName;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("检出限")
    private BigDecimal detectionLimit;

    @ApiModelProperty("定量限")
    private BigDecimal quantitationLimit;

    @ApiModelProperty("限值要求")
    private String limitValue;

    @ApiModelProperty("最小值")
    private BigDecimal minValue;

    @ApiModelProperty("最大值")
    private BigDecimal maxValue;

    @ApiModelProperty("结果值")
    private String resultValue;

    @ApiModelProperty("数值结果")
    private BigDecimal resultNumeric;

    @ApiModelProperty("是否未检出 0否 1是")
    private Integer isNd;

    @ApiModelProperty("是否超标 0否 1是")
    private Integer isOos;

    @ApiModelProperty("超标类型 OOS/OOT")
    private String oosType;

    @ApiModelProperty("原始结果（仪器读数）")
    private String originalResult;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("排序")
    private Integer sortOrder;
}
