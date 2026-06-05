package com.lims.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quo_quotation")
@ApiModel("报价单主表")
public class QuoQuotation extends BaseEntity {

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

    @ApiModelProperty("关联委托单ID")
    private Long entrustId;

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

    @ApiModelProperty("状态 0草稿 1审批中 2已通过 3已驳回 4已确认 5已作废")
    private Integer status;

    @ApiModelProperty("审批状态 0待审批 1审批中 2已通过 3已驳回")
    private Integer approvalStatus;

    @ApiModelProperty("客户确认人ID")
    private Long confirmUserId;

    @ApiModelProperty("客户确认时间")
    private LocalDateTime confirmTime;

    @ApiModelProperty("是否已转委托 0否 1是")
    private Integer isConverted;

    @ApiModelProperty("转委托单ID")
    private Long convertEntrustId;

    @ApiModelProperty("备注")
    private String remark;
}
