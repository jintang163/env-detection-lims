package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("考核记录VO")
public class AssessmentRecordVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("考核类型")
    private String assessmentType;

    @ApiModelProperty("考核周期")
    private String assessmentPeriod;

    @ApiModelProperty("考核日期")
    private LocalDate assessmentDate;

    @ApiModelProperty("考核人ID")
    private Long assessorId;

    @ApiModelProperty("考核人姓名")
    private String assessorName;

    @ApiModelProperty("工作业绩得分")
    private BigDecimal workPerformanceScore;

    @ApiModelProperty("工作态度得分")
    private BigDecimal workAttitudeScore;

    @ApiModelProperty("工作能力得分")
    private BigDecimal workAbilityScore;

    @ApiModelProperty("综合得分")
    private BigDecimal overallScore;

    @ApiModelProperty("考核结果")
    private String assessmentResult;

    @ApiModelProperty("考核意见")
    private String assessmentOpinion;

    @ApiModelProperty("改进建议")
    private String improvementSuggestion;

    @ApiModelProperty("人员意见")
    private String personnelOpinion;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
