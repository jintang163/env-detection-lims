package com.lims.contract.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("合同变更保存DTO")
public class ContractChangeSaveDTO {

    @ApiModelProperty("合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @ApiModelProperty("变更类型")
    @NotBlank(message = "变更类型不能为空")
    private String changeType;

    @ApiModelProperty("变更原因")
    @NotBlank(message = "变更原因不能为空")
    private String changeReason;

    @ApiModelProperty("变更说明")
    private String changeDescription;

    @ApiModelProperty("变更日期")
    private LocalDate changeDate;

    @ApiModelProperty("变更后-合同名称")
    private String contractName;

    @ApiModelProperty("变更后-合同类型")
    private String contractType;

    @ApiModelProperty("变更后-客户ID")
    private Long customerId;

    @ApiModelProperty("变更后-客户名称")
    private String customerName;

    @ApiModelProperty("变更后-签约日期")
    private LocalDate signDate;

    @ApiModelProperty("变更后-生效日期")
    private LocalDate effectiveDate;

    @ApiModelProperty("变更后-到期日期")
    private LocalDate expireDate;

    @ApiModelProperty("变更后-合同金额")
    private BigDecimal contractAmount;

    @ApiModelProperty("变更后-负责人ID")
    private Long managerId;

    @ApiModelProperty("变更后-负责人名称")
    private String managerName;

    @ApiModelProperty("变更后-部门ID")
    private Long deptId;

    @ApiModelProperty("变更后-部门名称")
    private String deptName;

    @ApiModelProperty("变更后-合同内容")
    private String content;

    @ApiModelProperty("变更后-附件")
    private String attachments;

    @ApiModelProperty("变更后-备注")
    private String remark;
}
