package com.lims.sampling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("smp_sampling_point")
@ApiModel("采样点位表")
public class SmpSamplingPoint extends BaseEntity {

    @ApiModelProperty("采样计划ID")
    private Long planId;

    @ApiModelProperty("点位编号")
    private String pointCode;

    @ApiModelProperty("点位名称")
    private String pointName;

    @ApiModelProperty("点位地址")
    private String pointAddress;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @ApiModelProperty("采样深度")
    private String samplingDepth;

    @ApiModelProperty("采样方法")
    private String samplingMethod;

    @ApiModelProperty("样品类型")
    private String sampleType;

    @ApiModelProperty("预计采样数量")
    private Integer expectedCount;

    @ApiModelProperty("检测项目(JSON格式)")
    private String testItems;

    @ApiModelProperty("排序")
    private Integer sortOrder;

    @ApiModelProperty("备注")
    private String remark;
}
