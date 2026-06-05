package com.lims.sampling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("smp_sampling_plan")
@ApiModel("采样计划表")
public class SmpSamplingPlan extends BaseEntity {

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

    @ApiModelProperty("采样员ID列表(逗号分隔)")
    private String samplerIds;

    @ApiModelProperty("采样员姓名列表(逗号分隔)")
    private String samplerNames;

    @ApiModelProperty("设备ID列表(逗号分隔)")
    private String equipmentIds;

    @ApiModelProperty("设备名称列表(逗号分隔)")
    private String equipmentNames;

    @ApiModelProperty("容器ID列表(逗号分隔)")
    private String containerIds;

    @ApiModelProperty("容器名称列表(逗号分隔)")
    private String containerNames;

    @ApiModelProperty("点位数量")
    private Integer pointCount;

    @ApiModelProperty("预计样品数量")
    private Integer sampleCount;

    @ApiModelProperty("状态：0草稿 1待分配 2已分配 3采样中 4已完成 5已取消")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
