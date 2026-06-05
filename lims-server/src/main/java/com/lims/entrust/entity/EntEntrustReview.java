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
@TableName("ent_entrust_review")
@ApiModel("委托单评审记录表")
public class EntEntrustReview extends BaseEntity {

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("评审节点")
    private String reviewNode;

    @ApiModelProperty("评审人ID")
    private Long reviewerId;

    @ApiModelProperty("评审人姓名")
    private String reviewerName;

    @ApiModelProperty("评审意见")
    private String reviewOpinion;

    @ApiModelProperty("评审结果 1通过 2驳回")
    private Integer reviewResult;

    @ApiModelProperty("评审时间")
    private LocalDateTime reviewTime;
}
