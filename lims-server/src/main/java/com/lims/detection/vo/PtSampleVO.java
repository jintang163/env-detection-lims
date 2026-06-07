package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("能力验证样品VO")
public class PtSampleVO {

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

    @ApiModelProperty("能力验证计划ID")
    private Long ptPlanId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品代码")
    private String sampleCode;

    @ApiModelProperty("基质")
    private String matrix;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("指定值")
    private BigDecimal assignedValue;

    @ApiModelProperty("指定值不确定度")
    private BigDecimal assignedValueUncertainty;

    @ApiModelProperty("目标标准差")
    private BigDecimal targetSd;

    @ApiModelProperty("接收日期")
    private LocalDateTime receiveDate;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("有效期")
    private LocalDateTime expireDate;

    @ApiModelProperty("样品状态")
    private String sampleStatus;

    @ApiModelProperty("备注")
    private String remark;
}
