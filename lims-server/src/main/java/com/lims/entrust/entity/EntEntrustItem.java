package com.lims.entrust.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ent_entrust_item")
@ApiModel("委托单检测项目表")
public class EntEntrustItem extends BaseEntity {

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("检测标准ID")
    private Long standardId;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("标准名称")
    private String standardName;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("限值要求")
    private String limitValue;

    @ApiModelProperty("单价")
    private BigDecimal unitPrice;

    @ApiModelProperty("检测点数/数量")
    private Integer quantity;

    @ApiModelProperty("小计金额")
    private BigDecimal subtotal;

    @ApiModelProperty("是否分包 0否 1是")
    private Integer isSubcontract;

    @ApiModelProperty("分包单位")
    private String subcontractor;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("备注")
    private String remark;
}
