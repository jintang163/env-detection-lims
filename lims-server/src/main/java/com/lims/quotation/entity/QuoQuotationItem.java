package com.lims.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quo_quotation_item")
@ApiModel("报价单明细表")
public class QuoQuotationItem extends BaseEntity {

    @ApiModelProperty("报价单ID")
    private Long quotationId;

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("项目分类")
    private String itemCategory;

    @ApiModelProperty("检测标准ID")
    private Long standardId;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("标准名称")
    private String standardName;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("小计")
    private BigDecimal subtotal;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("备注")
    private String remark;
}
