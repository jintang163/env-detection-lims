package com.lims.sampling.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("采样计划查询DTO")
public class SamplingPlanQuery extends PageQuery {

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("采样类型")
    private String samplingType;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("采样员ID")
    private Long samplerId;

    @ApiModelProperty("计划采样日期开始")
    private LocalDate planDateStart;

    @ApiModelProperty("计划采样日期结束")
    private LocalDate planDateEnd;
}
