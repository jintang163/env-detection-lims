package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("检测项目保存DTO")
public class EntrustItemSaveDTO {

    @ApiModelProperty("ID，修改时传入")
    private Long id;

    @NotNull(message = "检测项目ID不能为空")
    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @NotNull(message = "检测标准ID不能为空")
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

    @NotNull(message = "检测点数/数量不能为空")
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
