package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("动态表单字段VO")
public class FormFieldVO {

    @ApiModelProperty("字段ID")
    private Long id;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法编号")
    private String methodCode;

    @ApiModelProperty("字段编码")
    private String fieldCode;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("字段类型 input number select date textarea file")
    private String fieldType;

    @ApiModelProperty("数据类型 string number boolean date")
    private String dataType;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("检出限")
    private BigDecimal detectionLimit;

    @ApiModelProperty("定量限")
    private BigDecimal quantitationLimit;

    @ApiModelProperty("最小值")
    private BigDecimal minValue;

    @ApiModelProperty("最大值")
    private BigDecimal maxValue;

    @ApiModelProperty("精度（小数位数）")
    private Integer precisionScale;

    @ApiModelProperty("是否必填 0否 1是")
    private Integer required;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("选项值列表")
    private List<String> options;

    @ApiModelProperty("输入提示")
    private String placeholder;

    @ApiModelProperty("字段说明")
    private String description;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;
}
