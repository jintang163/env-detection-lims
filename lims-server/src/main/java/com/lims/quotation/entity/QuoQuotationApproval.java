package com.lims.quotation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quo_quotation_approval")
@ApiModel("报价单审批记录表")
public class QuoQuotationApproval extends BaseEntity {

    @ApiModelProperty("报价单ID")
    private Long quotationId;

    @ApiModelProperty("审批节点")
    private String approvalNode;

    @ApiModelProperty("审批人ID")
    private Long approverId;

    @ApiModelProperty("审批人姓名")
    private String approverName;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("审批结果 1通过 2驳回")
    private Integer approvalResult;

    @ApiModelProperty("审批时间")
    private LocalDateTime approvalTime;
}
