package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("分配方案推荐VO")
public class AssignmentRecommendationVO {

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("推荐分配人员列表")
    private List<AssigneeCandidateVO> candidates;
}
