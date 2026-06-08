package com.lims.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("eq_calibration_record")
@ApiModel("设备校准记录")
public class EqCalibrationRecord extends BaseEntity {

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

    @ApiModelProperty("校准日期")
    private LocalDate calibrationDate;

    @ApiModelProperty("校准单位")
    private String calibrationUnit;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("校准结果：1合格 2不合格 3部分合格")
    private Integer result;

    @ApiModelProperty("有效期至")
    private LocalDate validUntil;

    @ApiModelProperty("校准人ID")
    private Long calibratorId;

    @ApiModelProperty("校准人名称")
    private String calibratorName;

    @ApiModelProperty("校准费用")
    private java.math.BigDecimal cost;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("校准结论")
    private String conclusion;

    @ApiModelProperty("备注")
    private String remark;
}
