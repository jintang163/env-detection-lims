package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("采样任务列表VO")
public class SamplingTaskVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("采样计划ID")
    private Long planId;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("点位ID")
    private Long pointId;

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

    @ApiModelProperty("采样员ID")
    private Long samplerId;

    @ApiModelProperty("采样员姓名")
    private String samplerName;

    @ApiModelProperty("分配时间")
    private LocalDateTime assignTime;

    @ApiModelProperty("计划采样日期")
    private LocalDate planSamplingDate;

    @ApiModelProperty("实际采样日期")
    private LocalDateTime actualSamplingDate;

    @ApiModelProperty("现场温度(℃)")
    private BigDecimal temperature;

    @ApiModelProperty("现场湿度(%)")
    private BigDecimal humidity;

    @ApiModelProperty("现场pH值")
    private BigDecimal ph;

    @ApiModelProperty("天气")
    private String weather;

    @ApiModelProperty("质控样标记 0否 1是")
    private Integer qcSampleFlag;

    @ApiModelProperty("实际采样数量")
    private Integer sampleCount;

    @ApiModelProperty("状态：0待下载 1已下载 2采样中 3已完成 4已上传 5已取消")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("同步状态 0未同步 1已同步")
    private Integer syncStatus;

    @ApiModelProperty("离线标记 0否 1是")
    private Integer offlineFlag;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
