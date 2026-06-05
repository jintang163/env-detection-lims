package com.lims.customer.entity;

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
@TableName("cus_customer_credit")
@ApiModel("客户信用记录表")
public class CusCustomerCredit extends BaseEntity {

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("变更类型 1增加 2扣减")
    private Integer changeType;

    @ApiModelProperty("变更前分数")
    private Integer beforeScore;

    @ApiModelProperty("变更分数")
    private Integer changeScore;

    @ApiModelProperty("变更后分数")
    private Integer afterScore;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("变更说明")
    private String changeDescription;

    @ApiModelProperty("关联业务类型 1订单 2合同 3投诉 4其他")
    private Integer businessType;

    @ApiModelProperty("关联业务ID")
    private Long businessId;

    @ApiModelProperty("关联业务编号")
    private String businessNo;

    @ApiModelProperty("涉及金额")
    private BigDecimal involvedAmount;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
