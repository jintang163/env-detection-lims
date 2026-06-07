package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("能力验证结果保存")
public class PtResultSaveDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("能力验证计划ID")
    private Long ptPlanId;

    @ApiModelProperty("能力验证样品ID")
    private Long ptSampleId;

    @ApiModelProperty("检测值")
    private BigDecimal detectedValue;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("检测日期")
    private LocalDateTime measureDate;

    @ApiModelProperty("操作人员")
    private String operator;

    @ApiModelProperty("仪器")
    private String instrument;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("不确定度")
    private BigDecimal uncertainty;
}
