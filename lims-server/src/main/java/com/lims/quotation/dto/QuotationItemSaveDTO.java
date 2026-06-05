package com.lims.quotation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("报价单明细保存参数")
public class QuotationItemSaveDTO {

    @ApiModelProperty("明细ID，修改时传")
    private Long id;

    @ApiModelProperty("检测项目ID")
    @NotNull(message = "检测项目ID不能为空")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    @NotBlank(message = "项目名称不能为空")
    private String itemName;

    @ApiModelProperty("项目分类")
    private String itemCategory;

    @ApiModelProperty("检测标准ID")
    @NotNull(message = "检测标准ID不能为空")
    private Long standardId;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("标准名称")
    private String standardName;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("单价")
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @ApiModelProperty("数量")
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("备注")
    private String remark;
}
