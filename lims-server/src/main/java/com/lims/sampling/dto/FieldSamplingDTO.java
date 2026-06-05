package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("现场采样DTO")
public class FieldSamplingDTO {

    @ApiModelProperty("任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @ApiModelProperty("实际采样日期")
    @NotNull(message = "实际采样日期不能为空")
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

    @ApiModelProperty("现场照片列表")
    private List<String> sitePhotoList;

    @ApiModelProperty("质控样标记 0否 1是")
    private Integer qcSampleFlag;

    @ApiModelProperty("质控样类型 1平行样 2空白样 3加标样")
    private String qcSampleType;

    @ApiModelProperty("保存条件")
    private String storageCondition;

    @ApiModelProperty("实际采样数量")
    private Integer sampleCount;

    @ApiModelProperty("离线标记 0否 1是")
    private Integer offlineFlag;

    @ApiModelProperty("样品列表")
    private List<SampleRecordSaveDTO> sampleList;

    @ApiModelProperty("备注")
    private String remark;
}
