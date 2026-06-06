package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("智能排程方案VO")
public class SchedulePlanVO {

    @ApiModelProperty("排程方案ID")
    private Long id;

    @ApiModelProperty("方案编号")
    private String planNo;

    @ApiModelProperty("方案名称")
    private String planName;

    @ApiModelProperty("任务ID列表")
    private List<Long> taskIdList;

    @ApiModelProperty("任务数量")
    private Integer taskCount;

    @ApiModelProperty("排程日期")
    private LocalDate scheduleDate;

    @ApiModelProperty("方案综合评分")
    private BigDecimal totalScore;

    @ApiModelProperty("资源利用率(%)")
    private BigDecimal resourceUtilization;

    @ApiModelProperty("预计完成时间")
    private LocalDateTime estimatedCompletionTime;

    @ApiModelProperty("排程结果详情")
    private String scheduleResult;

    @ApiModelProperty("是否已采用 0否 1是")
    private Integer isUsed;

    @ApiModelProperty("生成人ID")
    private Long operatorId;

    @ApiModelProperty("生成人姓名")
    private String operatorName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
