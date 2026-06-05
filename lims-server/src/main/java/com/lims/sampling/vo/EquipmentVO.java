package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("设备列表VO")
public class EquipmentVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备类型 1采样设备 2监测设备 3样品容器")
    private String equipmentType;

    @ApiModelProperty("设备类型名称")
    private String equipmentTypeName;

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

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("领用状态 0未领用 1已领用")
    private Integer borrowStatus;

    @ApiModelProperty("领用状态名称")
    private String borrowStatusName;

    @ApiModelProperty("当前借用人ID")
    private Long currentBorrowerId;

    @ApiModelProperty("当前借用人姓名")
    private String currentBorrowerName;

    @ApiModelProperty("存放位置")
    private String storageLocation;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
