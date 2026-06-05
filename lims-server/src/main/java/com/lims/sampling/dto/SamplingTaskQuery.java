package com.lims.sampling.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("采样任务查询DTO")
public class SamplingTaskQuery extends PageQuery {

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("点位名称")
    private String pointName;

    @ApiModelProperty("采样员ID")
    private Long samplerId;

    @ApiModelProperty("采样员姓名")
    private String samplerName;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("同步状态")
    private Integer syncStatus;

    @ApiModelProperty("计划采样日期开始")
    private LocalDate planSamplingDateStart;

    @ApiModelProperty("计划采样日期结束")
    private LocalDate planSamplingDateEnd;

    @ApiModelProperty("离线标记")
    private Integer offlineFlag;
}
