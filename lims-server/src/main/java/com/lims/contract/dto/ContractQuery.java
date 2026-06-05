package com.lims.contract.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("合同查询DTO")
public class ContractQuery extends PageQuery {

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

    @ApiModelProperty("合同状态")
    private Integer status;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("签约开始日期")
    private LocalDate signDateStart;

    @ApiModelProperty("签约结束日期")
    private LocalDate signDateEnd;

    @ApiModelProperty("到期开始日期")
    private LocalDate expireDateStart;

    @ApiModelProperty("到期结束日期")
    private LocalDate expireDateEnd;
}
