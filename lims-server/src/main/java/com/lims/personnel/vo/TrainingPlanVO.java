package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("培训计划VO")
public class TrainingPlanVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("培训类型")
    private String trainingType;

    @ApiModelProperty("培训类型名称")
    private String trainingTypeName;

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

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("培训人员列表")
    private List<TrainingParticipantVO> participantList;
}
