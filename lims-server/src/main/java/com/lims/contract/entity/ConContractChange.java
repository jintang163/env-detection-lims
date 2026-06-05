package com.lims.contract.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("con_contract_change")
@ApiModel("合同变更记录表")
public class ConContractChange extends BaseEntity {

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("变更编号")
    private String changeNo;

    @ApiModelProperty("变更类型")
    private String changeType;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("变更前内容(JSON)")
    private String beforeContent;

    @ApiModelProperty("变更后内容(JSON)")
    private String afterContent;

    @ApiModelProperty("变更说明")
    private String changeDescription;

    @ApiModelProperty("变更日期")
    private LocalDate changeDate;

    @ApiModelProperty("申请人ID")
    private Long applicantId;

    @ApiModelProperty("申请人名称")
    private String applicantName;

    @ApiModelProperty("审批状态：0待审批 1审批通过 2审批驳回")
    private Integer approvalStatus;

    @ApiModelProperty("审批人ID")
    private Long approverId;

    @ApiModelProperty("审批人名称")
    private String approverName;

    @ApiModelProperty("审批意见")
    private String approvalOpinion;

    @ApiModelProperty("审批时间")
    private LocalDate approvalDate;
}
