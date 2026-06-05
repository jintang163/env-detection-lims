package com.lims.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_test_item")
@ApiModel("检测项目表")
public class DictTestItem extends BaseEntity {

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("项目分类")
    private String itemCategory;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("标准价格")
    private BigDecimal standardPrice;

    @ApiModelProperty("成本价格")
    private BigDecimal costPrice;

    @ApiModelProperty("检出限")
    private BigDecimal detectionLimit;

    @ApiModelProperty("定量限")
    private BigDecimal quantitationLimit;

    @ApiModelProperty("检测周期(天)")
    private Integer detectionCycle;

    @ApiModelProperty("检测说明")
    private String description;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;
}
