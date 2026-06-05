package com.lims.quotation.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("报价单详情VO")
public class QuotationDetailVO {

    @ApiModelProperty("主键ID")
    private Long id;

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

    @ApiModelProperty("状态 0草稿 1审批中 2已通过 3已驳回 4已确认 5已作废")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("创建人")
    private String createByName;

    @ApiModelProperty("明细列表")
    private List<QuotationItemVO> items;

    @ApiModelProperty("审批记录列表")
    private List<QuotationApprovalRecordVO> approvalRecords;

    @Data
    @ApiModel("审批记录VO")
    public static class QuotationApprovalRecordVO {

        @ApiModelProperty("审批节点")
        private String approvalNode;

        @ApiModelProperty("审批人姓名")
        private String approverName;

        @ApiModelProperty("审批结果 1通过 2驳回")
        private Integer approvalResult;

        @ApiModelProperty("审批结果名称")
        private String approvalResultName;

        @ApiModelProperty("审批意见")
        private String approvalOpinion;

        @ApiModelProperty("审批时间")
        private LocalDateTime approvalTime;
    }
}
