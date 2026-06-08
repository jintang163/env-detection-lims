package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Validated
@ApiModel("培训评估保存DTO")
public class TrainingEvaluationSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("培训计划ID")
    @NotNull(message = "培训计划ID不能为空")
    private Long trainingPlanId;

    @ApiModelProperty("人员ID")
    @NotNull(message = "人员ID不能为空")
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
}
