package com.lims.entrust.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("委托单列表VO")
public class EntrustVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("委托类型名称")
    private String entrustTypeName;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("关联合同编号")
    private String contractNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型")
    private String sampleType;

    @ApiModelProperty("检测项目数量")
    private Integer itemCount;

    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("实际金额")
    private BigDecimal actualAmount;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("是否加急")
    private Integer isUrgent;

    @ApiModelProperty("是否分包")
    private Integer isSubcontract;

    @ApiModelProperty("合同评审状态名称")
    private String approvalStatusName;

    @ApiModelProperty("期望报告时间")
    private LocalDateTime expectedReportTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("备注")
    private String remark;
}
