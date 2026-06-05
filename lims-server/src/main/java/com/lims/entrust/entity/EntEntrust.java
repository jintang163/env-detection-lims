package com.lims.entrust.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ent_entrust")
@ApiModel("委托单主表")
public class EntEntrust extends BaseEntity {

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("委托类型 1常规委托 2委托采样 3送样检测")
    private Integer entrustType;

    @ApiModelProperty("客户ID")
    private Long customerId;

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

    @ApiModelProperty("实际报告时间")
    private LocalDateTime actualReportTime;

    @ApiModelProperty("检测依据")
    private String detectionBasis;

    @ApiModelProperty("评价依据")
    private String evaluationBasis;

    @ApiModelProperty("总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("折扣率%")
    private BigDecimal discountRate;

    @ApiModelProperty("折扣金额")
    private BigDecimal discountAmount;

    @ApiModelProperty("实际金额")
    private BigDecimal actualAmount;

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

    @ApiModelProperty("状态 0草稿 1待受理 2已受理 3采样中 4检测中 5报告编制中 6报告审核中 7已完成 8已取消")
    private Integer status;

    @ApiModelProperty("合同评审状态 0待评审 1评审中 2评审通过 3评审驳回")
    private Integer approvalStatus;

    @ApiModelProperty("评审意见")
    private String reviewOpinion;

    @ApiModelProperty("报告编号")
    private String reportNo;

    @ApiModelProperty("报告地址")
    private String reportUrl;

    @ApiModelProperty("备注")
    private String remark;
}
