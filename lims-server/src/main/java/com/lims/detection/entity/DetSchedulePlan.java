package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("det_schedule_plan")
@ApiModel("智能排程方案表")
public class DetSchedulePlan implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("排程方案ID")
    private Long id;

    @ApiModelProperty("方案编号")
    private String planNo;

    @ApiModelProperty("方案名称")
    private String planName;

    @ApiModelProperty("任务ID列表(JSON格式)")
    private String taskIds;

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

    @ApiModelProperty("排程结果详情(JSON格式)")
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
