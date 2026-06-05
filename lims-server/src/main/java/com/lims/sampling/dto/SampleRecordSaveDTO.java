package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("样品记录保存DTO")
public class SampleRecordSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("二维码内容")
    private String qrCode;

    @ApiModelProperty("采样任务ID")
    private Long taskId;

    @ApiModelProperty("采样计划ID")
    private Long planId;

    @ApiModelProperty("点位ID")
    private Long pointId;

    @ApiModelProperty("点位编号")
    private String pointCode;

    @ApiModelProperty("点位名称")
    private String pointName;

    @ApiModelProperty("样品类型")
    private String sampleType;

    @ApiModelProperty("样品名称")
    @NotBlank(message = "样品名称不能为空")
    private String sampleName;

    @ApiModelProperty("采样时间")
    private LocalDateTime samplingTime;

    @ApiModelProperty("采样温度(℃)")
    private BigDecimal temperature;

    @ApiModelProperty("pH值")
    private BigDecimal ph;

    @ApiModelProperty("样品体积(L)")
    private BigDecimal volume;

    @ApiModelProperty("容器类型")
    private String containerType;

    @ApiModelProperty("防腐剂")
    private String preservative;

    @ApiModelProperty("保存条件")
    private String storageCondition;

    @ApiModelProperty("有效期至")
    private LocalDateTime expireTime;

    @ApiModelProperty("质控样标记 0否 1是")
    private Integer qcFlag;

    @ApiModelProperty("质控样类型")
    private String qcType;

    @ApiModelProperty("检测项目(JSON格式)")
    private String testItems;

    @ApiModelProperty("样品照片列表")
    private List<String> photoList;

    @ApiModelProperty("备注")
    private String remark;
}
