package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("质控计划保存DTO")
public class QcPlanSaveDTO {

    @ApiModelProperty("计划ID（更新时传）")
    private Long id;

    @ApiModelProperty("计划编号")
    @NotBlank(message = "计划编号不能为空")
    private String planNo;

    @ApiModelProperty("计划名称")
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @ApiModelProperty("检测项目名称")
    private String projectName;

    @ApiModelProperty("检测方法名称")
    private String methodName;

    @ApiModelProperty("周期类型 1每日 2每周 3每月 4每批次")
    private String cycleType;

    @ApiModelProperty("状态 0草稿 1启用 2停用")
    private Integer status;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("质控样配置列表")
    @Valid
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
}
