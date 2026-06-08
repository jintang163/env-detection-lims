package com.lims.equipment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("eq_equipment")
@ApiModel("设备台账")
public class EqEquipment extends BaseEntity {

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备型号")
    private String model;

    @ApiModelProperty("设备规格")
    private String specification;

    @ApiModelProperty("设备类型")
    private String equipmentType;

    @ApiModelProperty("所属部门ID")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

    @ApiModelProperty("存放地点")
    private String location;

    @ApiModelProperty("供应商ID")
    private Long supplierId;

    @ApiModelProperty("供应商名称")
    private String supplierName;

    @ApiModelProperty("购置日期")
    private LocalDate purchaseDate;

    @ApiModelProperty("购置金额")
    private BigDecimal purchaseAmount;

    @ApiModelProperty("出厂编号")
    private String factoryNo;

    @ApiModelProperty("设备状态：0闲置 1在用 2维修中 3停用 4报废")
    private Integer status;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("技术参数")
    private String technicalParams;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("上次校准日期")
    private LocalDate lastCalibrationDate;

    @ApiModelProperty("下次校准日期")
    private LocalDate nextCalibrationDate;

    @ApiModelProperty("校准周期(天)")
    private Integer calibrationCycle;

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

    @ApiModelProperty("附件")
    private String attachments;
}
