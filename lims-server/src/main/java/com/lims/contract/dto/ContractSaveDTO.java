package com.lims.contract.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("合同保存DTO")
public class ContractSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("合同名称")
    @NotBlank(message = "合同名称不能为空")
    private String contractName;

    @ApiModelProperty("合同类型")
    @NotBlank(message = "合同类型不能为空")
    private String contractType;

    @ApiModelProperty("客户ID")
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @ApiModelProperty("客户名称")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @ApiModelProperty("签约日期")
    @NotNull(message = "签约日期不能为空")
    private LocalDate signDate;

    @ApiModelProperty("生效日期")
    @NotNull(message = "生效日期不能为空")
    private LocalDate effectiveDate;

    @ApiModelProperty("到期日期")
    @NotNull(message = "到期日期不能为空")
    private LocalDate expireDate;

    @ApiModelProperty("合同金额")
    @NotNull(message = "合同金额不能为空")
    private BigDecimal contractAmount;

    @ApiModelProperty("负责人ID")
    @NotNull(message = "负责人ID不能为空")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    @NotBlank(message = "负责人名称不能为空")
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
