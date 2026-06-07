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
@TableName("stability_scheme")
@ApiModel("稳定性考察方案表")
public class StabilityScheme extends BaseEntity {

    @ApiModelProperty("方案编号")
    private String schemeNo;

    @ApiModelProperty("方案名称")
    private String schemeName;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品类型")
    private String sampleType;

    @ApiModelProperty("检测项目名称")
    private String itemName;

    @ApiModelProperty("检测项目编码")
    private String itemCode;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("初始值")
    private BigDecimal initialValue;

    @ApiModelProperty("可接受标准")
    private String acceptanceCriterion;

    @ApiModelProperty("方案类型")
    private String schemeType;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("温度")
    private BigDecimal temperature;

    @ApiModelProperty("湿度")
    private BigDecimal humidity;

    @ApiModelProperty("考察天数")
    private Integer durationDays;

    @ApiModelProperty("预计有效期")
    private Integer estimatedShelfLife;

    @ApiModelProperty("开始日期")
    private LocalDateTime startDate;

    @ApiModelProperty("结束日期")
    private LocalDateTime endDate;

    @ApiModelProperty("状态 0草稿 1进行中 2已完成 3已取消")
    private Integer status;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("备注")
    private String remark;
}
