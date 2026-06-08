package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Validated
@ApiModel("培训计划保存DTO")
public class TrainingPlanSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("计划编号")
    @NotBlank(message = "计划编号不能为空")
    private String planNo;

    @ApiModelProperty("计划名称")
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @ApiModelProperty("培训类型")
    @NotBlank(message = "培训类型不能为空")
    private String trainingType;

    @ApiModelProperty("培训内容")
    private String trainingContent;

    @ApiModelProperty("培训讲师")
    private String trainer;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("培训地点")
    private String trainingLocation;

    @ApiModelProperty("培训时长(小时)")
    private BigDecimal trainingHours;

    @ApiModelProperty("目标人员")
    private String targetPersonnel;

    @ApiModelProperty("目标部门ID集合")
    private String targetDeptIds;

    @ApiModelProperty("培训预算")
    private BigDecimal budget;

    @ApiModelProperty("状态：0草稿 1已发布 2进行中 3已完成 4已取消")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("培训人员列表")
    private List<TrainingParticipantSaveDTO> participantList;
}
