package com.lims.detection.vo;

import com.lims.detection.dto.QcSampleConfigDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("质控计划VO")
public class QcPlanVO {

    @ApiModelProperty("计划ID")
    private Long id;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("检测项目名称")
    private String projectName;

    @ApiModelProperty("检测方法名称")
    private String methodName;

    @ApiModelProperty("周期类型 DAILY每日 WEEKLY每周 MONTHLY每月 QUARTERLY每季度 YEARLY每年")
    private String cycleType;

    @ApiModelProperty("周期类型名称")
    private String cycleTypeName;

    @ApiModelProperty("状态 0草稿 1启用 2暂停 3结束")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("质控样配置列表")
    private List<QcSampleConfigDTO> qcSamples;

    @ApiModelProperty("是否启用提醒")
    private Boolean reminderEnabled;

    @ApiModelProperty("提醒类型 1邮件 2短信 3站内信")
    private List<String> reminderType;

    @ApiModelProperty("提前提醒天数")
    private Integer reminderAdvance;

    @ApiModelProperty("提醒人员")
    private String reminderUsers;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
