package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pt_plan")
@ApiModel("能力验证计划表")
public class PtPlan extends BaseEntity {

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
}
