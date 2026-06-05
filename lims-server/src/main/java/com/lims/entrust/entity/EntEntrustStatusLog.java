package com.lims.entrust.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ent_entrust_status_log")
@ApiModel("委托单状态流转记录表")
public class EntEntrustStatusLog extends BaseEntity {

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("变更前状态")
    private Integer beforeStatus;

    @ApiModelProperty("变更后状态")
    private Integer afterStatus;

    @ApiModelProperty("操作类型")
    private String operateType;

    @ApiModelProperty("操作内容")
    private String operateContent;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;
}
