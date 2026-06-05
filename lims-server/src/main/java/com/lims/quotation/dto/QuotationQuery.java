package com.lims.quotation.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("报价单查询参数")
public class QuotationQuery extends PageQuery {

    @ApiModelProperty("报价单编号")
    private String quotationNo;

    @ApiModelProperty("报价单名称")
    private String quotationName;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("状态：0草稿 1审批中 2已通过 3已驳回 4已确认 5已作废")
    private Integer status;

    @ApiModelProperty("报价开始日期")
    private LocalDate quotationDateStart;

    @ApiModelProperty("报价结束日期")
    private LocalDate quotationDateEnd;

    @ApiModelProperty("是否已转委托")
    private Integer isConverted;
}
