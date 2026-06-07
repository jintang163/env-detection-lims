package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("稳定性方案VO")
public class StabilitySchemeVO {

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

    @ApiModelProperty("检测点列表")
    private List<StabilityTestPointVO> testPoints;
}
