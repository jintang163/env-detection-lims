package com.lims.quotation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("报价单保存参数")
public class QuotationSaveDTO {

    @ApiModelProperty("报价单ID，修改时传")
    private Long id;

    @ApiModelProperty("报价单名称")
    @NotBlank(message = "报价单名称不能为空")
    private String quotationName;

    @ApiModelProperty("客户ID")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @ApiModelProperty("客户名称")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @ApiModelProperty("关联合同ID")
    private Long contractId;

    @ApiModelProperty("报价有效期")
    @NotNull(message = "报价有效期不能为空")
    private LocalDate validDate;

    @ApiModelProperty("报价日期")
    @NotNull(message = "报价日期不能为空")
    private LocalDate quotationDate;

    @ApiModelProperty("折扣率%")
    @NotNull(message = "折扣率不能为空")
    private BigDecimal discountRate;

    @ApiModelProperty("报价说明")
    private String quotationContent;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("明细列表")
    @NotEmpty(message = "明细列表不能为空")
    @Valid
    private List<QuotationItemSaveDTO> items;
}
