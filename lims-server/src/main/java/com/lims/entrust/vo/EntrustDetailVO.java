package com.lims.entrust.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("委托单详情VO")
public class EntrustDetailVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("委托类型")
    private Integer entrustType;

    @ApiModelProperty("委托类型名称")
    private String entrustTypeName;

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

    @ApiModelProperty("是否加急")
    private Integer isUrgent;

    @ApiModelProperty("加急费")
    private BigDecimal urgentFee;

    @ApiModelProperty("是否分包")
    private Integer isSubcontract;

    @ApiModelProperty("分包金额")
    private BigDecimal subcontractAmount;

    @ApiModelProperty("是否调账")
    private Integer isAdjust;

    @ApiModelProperty("调账金额")
    private BigDecimal adjustAmount;

    @ApiModelProperty("调账原因")
    private String adjustReason;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("合同评审状态")
    private Integer approvalStatus;

    @ApiModelProperty("合同评审状态名称")
    private String approvalStatusName;

    @ApiModelProperty("评审意见")
    private String reviewOpinion;

    @ApiModelProperty("报告编号")
    private String reportNo;

    @ApiModelProperty("报告地址")
    private String reportUrl;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("检测项目列表")
    private List<EntrustItemVO> items;

    @ApiModelProperty("状态流转记录")
    private List<StatusLogVO> statusLogs;

    @ApiModelProperty("评审记录")
    private List<ReviewLogVO> reviewLogs;

    @ApiModelProperty("分包记录")
    private List<SubcontractVO> subcontracts;

    @Data
    @ApiModel("状态流转记录VO")
    public static class StatusLogVO {
        @ApiModelProperty("变更前状态")
        private Integer beforeStatus;
        @ApiModelProperty("变更前状态名称")
        private String beforeStatusName;
        @ApiModelProperty("变更后状态")
        private Integer afterStatus;
        @ApiModelProperty("变更后状态名称")
        private String afterStatusName;
        @ApiModelProperty("操作类型")
        private String operateType;
        @ApiModelProperty("操作内容")
        private String operateContent;
        @ApiModelProperty("操作人姓名")
        private String operatorName;
        @ApiModelProperty("操作时间")
        private LocalDateTime operateTime;
    }

    @Data
    @ApiModel("评审记录VO")
    public static class ReviewLogVO {
        @ApiModelProperty("评审节点")
        private String reviewNode;
        @ApiModelProperty("评审人姓名")
        private String reviewerName;
        @ApiModelProperty("评审意见")
        private String reviewOpinion;
        @ApiModelProperty("评审结果")
        private Integer reviewResult;
        @ApiModelProperty("评审结果名称")
        private String reviewResultName;
        @ApiModelProperty("评审时间")
        private LocalDateTime reviewTime;
    }
}
