package com.lims.sampling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("smp_sampling_task")
@ApiModel("采样任务表")
public class SmpSamplingTask extends BaseEntity {

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

    @ApiModelProperty("实际采样经度")
    private BigDecimal actualLongitude;

    @ApiModelProperty("实际采样纬度")
    private BigDecimal actualLatitude;

    @ApiModelProperty("现场温度(℃)")
    private BigDecimal temperature;

    @ApiModelProperty("现场湿度(%)")
    private BigDecimal humidity;

    @ApiModelProperty("现场pH值")
    private BigDecimal ph;

    @ApiModelProperty("流量")
    private BigDecimal flowRate;

    @ApiModelProperty("天气")
    private String weather;

    @ApiModelProperty("现场照片(JSON格式)")
    private String sitePhotos;

    @ApiModelProperty("质控样标记 0否 1是")
    private Integer qcSampleFlag;

    @ApiModelProperty("质控样类型 1平行样 2空白样 3加标样")
    private String qcSampleType;

    @ApiModelProperty("保存条件")
    private String storageCondition;

    @ApiModelProperty("实际采样数量")
    private Integer sampleCount;

    @ApiModelProperty("状态：0待下载 1已下载 2采样中 3已完成 4已上传 5已取消")
    private Integer status;

    @ApiModelProperty("同步状态 0未同步 1已同步")
    private Integer syncStatus;

    @ApiModelProperty("离线标记 0否 1是")
    private Integer offlineFlag;

    @ApiModelProperty("备注")
    private String remark;
}
