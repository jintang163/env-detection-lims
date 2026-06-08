package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("培训统计VO")
public class TrainingStatisticsVO {

    @ApiModelProperty("计划总数")
    private Integer totalPlans;

    @ApiModelProperty("已完成计划数")
    private Integer completedPlans;

    @ApiModelProperty("进行中计划数")
    private Integer ongoingPlans;

    @ApiModelProperty("总培训时长(小时)")
    private BigDecimal totalTrainingHours;

    @ApiModelProperty("培训总人次")
    private Integer totalParticipants;
}
