package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@ApiModel("设备保存DTO")
public class EquipmentSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("设备类型 1采样设备 2监测设备 3样品容器")
    @NotBlank(message = "设备类型不能为空")
    private String equipmentType;

    @ApiModelProperty("规格型号")
    private String specification;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("购置日期")
    private LocalDate purchaseDate;

    @ApiModelProperty("上次校准日期")
    private LocalDate lastCalibrationDate;

    @ApiModelProperty("下次校准日期")
    private LocalDate nextCalibrationDate;

    @ApiModelProperty("校准周期(天)")
    private Integer calibrationCycle;

    @ApiModelProperty("状态 0停用 1正常 2维修中 3已报废")
    private Integer status;

    @ApiModelProperty("存放位置")
    private String storageLocation;

    @ApiModelProperty("备注")
    private String remark;
}
