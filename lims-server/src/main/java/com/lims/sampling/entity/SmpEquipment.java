package com.lims.sampling.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("smp_equipment")
@ApiModel("设备表")
public class SmpEquipment extends BaseEntity {

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备类型 1采样设备 2监测设备 3样品容器")
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

    @ApiModelProperty("领用状态 0未领用 1已领用")
    private Integer borrowStatus;

    @ApiModelProperty("当前借用人ID")
    private Long currentBorrowerId;

    @ApiModelProperty("当前借用人姓名")
    private String currentBorrowerName;

    @ApiModelProperty("存放位置")
    private String storageLocation;

    @ApiModelProperty("备注")
    private String remark;
}
