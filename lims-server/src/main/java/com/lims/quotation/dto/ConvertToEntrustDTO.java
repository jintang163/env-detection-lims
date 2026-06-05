package com.lims.quotation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("转委托单参数")
public class ConvertToEntrustDTO {

    @ApiModelProperty("报价单ID")
    @NotNull(message = "报价单ID不能为空")
    private Long quotationId;

    @ApiModelProperty("委托类型 1常规委托 2委托采样 3送样检测")
    @NotNull(message = "委托类型不能为空")
    private Integer entrustType;

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

    @ApiModelProperty("关联合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("折扣率%")
    private BigDecimal discountRate;

    @ApiModelProperty("是否加急 0否 1是")
    private Integer isUrgent;

    @ApiModelProperty("是否分包 0否 1是")
    private Integer isSubcontract;

    @ApiModelProperty("备注")
    private String remark;
}
