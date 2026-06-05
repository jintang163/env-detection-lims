package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("采样计划列表VO")
public class SamplingPlanVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("关联委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("计划采样日期")
    private LocalDate planDate;

    @ApiModelProperty("采样类型")
    private String samplingType;

    @ApiModelProperty("采样员姓名列表(逗号分隔)")
    private String samplerNames;

    @ApiModelProperty("点位数量")
    private Integer pointCount;

    @ApiModelProperty("预计样品数量")
    private Integer sampleCount;

    @ApiModelProperty("状态：0草稿 1待分配 2已分配 3采样中 4已完成 5已取消")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
