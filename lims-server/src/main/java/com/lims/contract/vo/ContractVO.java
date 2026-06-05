package com.lims.contract.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("合同列表VO")
public class ContractVO {

    @ApiModelProperty("主键ID")
    private Long id;

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

    @ApiModelProperty("合同状态名称")
    private String statusName;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
