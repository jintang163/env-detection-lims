package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("能力验证计划VO")
public class PtPlanVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("能力验证计划编号")
    private String ptNo;

    @ApiModelProperty("能力验证计划名称")
    private String ptName;

    @ApiModelProperty("组织者")
    private String organizer;

    @ApiModelProperty("能力验证类型")
    private String ptType;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("项目代码")
    private String itemCode;

    @ApiModelProperty("基质")
    private String matrix;

    @ApiModelProperty("报名截止日期")
    private LocalDateTime registerDeadline;

    @ApiModelProperty("样品发放日期")
    private LocalDateTime sampleDispatchDate;

    @ApiModelProperty("结果上报截止日期")
    private LocalDateTime resultDeadline;

    @ApiModelProperty("结果反馈日期")
    private LocalDateTime feedbackDate;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("报名费")
    private BigDecimal registrationFee;

    @ApiModelProperty("联系人")
    private String contactPerson;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("样品列表")
    private List<PtSampleVO> samples;

    @ApiModelProperty("结果列表")
    private List<PtResultVO> results;
}
