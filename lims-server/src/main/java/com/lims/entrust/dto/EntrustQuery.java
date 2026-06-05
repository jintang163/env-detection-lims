package com.lims.entrust.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("委托单查询DTO")
public class EntrustQuery extends PageQuery {

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("委托类型 1常规委托 2委托采样 3送样检测")
    private Integer entrustType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("是否加急")
    private Integer isUrgent;

    @ApiModelProperty("是否分包")
    private Integer isSubcontract;

    @ApiModelProperty("合同评审状态")
    private Integer approvalStatus;

    @ApiModelProperty("关联合同ID")
    private Long contractId;

    @ApiModelProperty("创建开始日期")
    private LocalDateTime createTimeStart;

    @ApiModelProperty("创建结束日期")
    private LocalDateTime createTimeEnd;

    @ApiModelProperty("期望报告开始日期")
    private LocalDateTime expectedReportStart;

    @ApiModelProperty("期望报告结束日期")
    private LocalDateTime expectedReportEnd;
}
