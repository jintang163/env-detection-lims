package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("样品记录VO")
public class SampleRecordVO {

    @ApiModelProperty("主键ID")
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
    private String sampleName;

    @ApiModelProperty("样品状态 待交接 已交接 检测中 已完成")
    private String sampleStatus;

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

    @ApiModelProperty("样品照片(JSON格式)")
    private String photos;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
