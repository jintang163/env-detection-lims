package com.lims.personnel.entity;

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
@TableName("per_training_evaluation")
@ApiModel("培训效果评估表")
public class PerTrainingEvaluation extends BaseEntity {

    @ApiModelProperty("培训计划ID")
    private Long trainingPlanId;

    @ApiModelProperty("人员ID")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("培训内容评分")
    private BigDecimal contentScore;

    @ApiModelProperty("讲师评分")
    private BigDecimal trainerScore;

    @ApiModelProperty("组织安排评分")
    private BigDecimal organizationScore;

    @ApiModelProperty("实用性评分")
    private BigDecimal usefulnessScore;

    @ApiModelProperty("综合评分")
    private BigDecimal overallScore;

    @ApiModelProperty("建议")
    private String suggestion;

    @ApiModelProperty("评估时间")
    private LocalDateTime evaluateTime;
}
