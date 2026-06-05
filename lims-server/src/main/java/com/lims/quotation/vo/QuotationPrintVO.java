package com.lims.quotation.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("报价单打印VO")
public class QuotationPrintVO {

    @ApiModelProperty("报价单编号")
    private String quotationNo;

    @ApiModelProperty("报价单名称")
    private String quotationName;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("关联合同ID")
    private Long contractId;

    @ApiModelProperty("报价有效期")
    private LocalDate validDate;

    @ApiModelProperty("报价日期")
    private LocalDate quotationDate;

    @ApiModelProperty("报价总额")
    private BigDecimal totalAmount;

    @ApiModelProperty("折扣率%")
    private BigDecimal discountRate;

    @ApiModelProperty("折后金额")
    private BigDecimal actualAmount;

    @ApiModelProperty("报价说明")
    private String quotationContent;

    @ApiModelProperty("总金额（大写）")
    private String totalAmountCn;

    @ApiModelProperty("实际金额（大写）")
    private String actualAmountCn;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("明细列表")
    private List<QuotationItemVO> items;
}
