package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("委托单保存DTO")
public class EntrustSaveDTO {

    @ApiModelProperty("ID，修改时传入")
    private Long id;

    @ApiModelProperty("委托类型 1常规委托 2委托采样 3送样检测")
    private Integer entrustType;

    @NotNull(message = "客户ID不能为空")
    @ApiModelProperty("客户ID")
    private Long customerId;

    @NotBlank(message = "客户名称不能为空")
    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("关联合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型")
    private String sampleType;

    @ApiModelProperty("样品数量")
    private Integer sampleQuantity;

    @ApiModelProperty("采样地址")
    private String samplingAddress;

    @ApiModelProperty("采样经度")
    private BigDecimal samplingLongitude;

    @ApiModelProperty("采样纬度")
    private BigDecimal samplingLatitude;

    @ApiModelProperty("采样时间")
    private LocalDateTime samplingTime;

    @ApiModelProperty("送样时间")
    private LocalDateTime sampleSendTime;

    @ApiModelProperty("收样时间")
    private LocalDateTime sampleReceiveTime;

    @ApiModelProperty("期望报告时间")
    private LocalDateTime expectedReportTime;

    @ApiModelProperty("检测依据")
    private String detectionBasis;

    @ApiModelProperty("评价依据")
    private String evaluationBasis;

    @ApiModelProperty("折扣率%")
    private BigDecimal discountRate;

    @ApiModelProperty("是否加急 0否 1是")
    private Integer isUrgent;

    @ApiModelProperty("加急费")
    private BigDecimal urgentFee;

    @ApiModelProperty("是否分包 0否 1是")
    private Integer isSubcontract;

    @ApiModelProperty("分包金额")
    private BigDecimal subcontractAmount;

    @ApiModelProperty("是否调账 0否 1是")
    private Integer isAdjust;

    @ApiModelProperty("调账金额")
    private BigDecimal adjustAmount;

    @ApiModelProperty("调账原因")
    private String adjustReason;

    @Valid
    @NotNull(message = "检测项目列表不能为空")
    @ApiModelProperty("检测项目列表")
    private List<EntrustItemSaveDTO> items;

    @ApiModelProperty("备注")
    private String remark;
}
