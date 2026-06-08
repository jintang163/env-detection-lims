package com.lims.equipment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("校准记录列表VO")
public class CalibrationRecordVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("校准计划ID")
    private Long planId;

    @ApiModelProperty("校准类型：1校准 2检定")
    private Integer calibrationType;

    @ApiModelProperty("校准类型名称")
    private String calibrationTypeName;

    @ApiModelProperty("校准日期")
    private LocalDate calibrationDate;

    @ApiModelProperty("校准单位")
    private String calibrationUnit;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("校准结果：1合格 2不合格 3部分合格")
    private Integer result;

    @ApiModelProperty("校准结果名称")
    private String resultName;

    @ApiModelProperty("有效期至")
    private LocalDate validUntil;

    @ApiModelProperty("校准人ID")
    private Long calibratorId;

    @ApiModelProperty("校准人名称")
    private String calibratorName;

    @ApiModelProperty("校准费用")
    private BigDecimal cost;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("校准结论")
    private String conclusion;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
