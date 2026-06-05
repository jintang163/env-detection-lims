package com.lims.contract.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("con_contract")
@ApiModel("合同主表")
public class ConContract extends BaseEntity {

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("合同名称")
    private String contractName;

    @ApiModelProperty("合同类型")
    private String contractType;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("签约日期")
    private LocalDate signDate;

    @ApiModelProperty("生效日期")
    private LocalDate effectiveDate;

    @ApiModelProperty("到期日期")
    private LocalDate expireDate;

    @ApiModelProperty("合同金额")
    private BigDecimal contractAmount;

    @ApiModelProperty("已收金额")
    private BigDecimal receivedAmount;

    @ApiModelProperty("未收金额")
    private BigDecimal unpaidAmount;

    @ApiModelProperty("履约进度(%)")
    private Integer performanceProgress;

    @ApiModelProperty("合同状态：0草稿 1审批中 2已生效 3变更中 4已完成 5已终止")
    private Integer status;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("合同内容")
    private String content;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
