package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("校准记录保存DTO")
public class CalibrationRecordSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("校准计划ID")
    private Long planId;

    @ApiModelProperty("校准类型：1校准 2检定")
    @NotNull(message = "校准类型不能为空")
    private Integer calibrationType;

    @ApiModelProperty("校准日期")
    @NotNull(message = "校准日期不能为空")
    private LocalDate calibrationDate;

    @ApiModelProperty("校准单位")
    @NotBlank(message = "校准单位不能为空")
    private String calibrationUnit;

    @ApiModelProperty("证书编号")
    @NotBlank(message = "证书编号不能为空")
    private String certificateNo;

    @ApiModelProperty("校准结果：1合格 2不合格 3部分合格")
    @NotNull(message = "校准结果不能为空")
    private Integer result;

    @ApiModelProperty("有效期至")
    @NotNull(message = "有效期至不能为空")
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
}
