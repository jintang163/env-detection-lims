package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("分配候选人VO")
public class AssigneeCandidateVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("匹配度评分(0-100)")
    private BigDecimal matchScore;

    @ApiModelProperty("资质匹配度")
    private BigDecimal qualificationScore;

    @ApiModelProperty("当前工作量")
    private Integer currentWorkload;

    @ApiModelProperty("设备可用性")
    private BigDecimal equipmentAvailability;

    @ApiModelProperty("预计完成时间(小时)")
    private BigDecimal estimatedHours;

    @ApiModelProperty("推荐理由")
    private String recommendationReason;
}
