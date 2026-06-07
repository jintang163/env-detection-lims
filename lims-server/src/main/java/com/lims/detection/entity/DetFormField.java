package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_form_field")
@ApiModel("动态表单字段配置表")
public class DetFormField extends BaseEntity {

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

    @ApiModelProperty("选项值(JSON格式，用于下拉选择)")
    private String options;

    @ApiModelProperty("输入提示")
    private String placeholder;

    @ApiModelProperty("字段说明")
    private String description;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;
}
