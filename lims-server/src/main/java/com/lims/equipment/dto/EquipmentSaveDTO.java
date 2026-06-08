package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("设备保存DTO")
public class EquipmentSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备编号")
    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("设备型号")
    @NotBlank(message = "设备型号不能为空")
    private String model;

    @ApiModelProperty("设备规格")
    private String specification;

    @ApiModelProperty("设备类型")
    @NotBlank(message = "设备类型不能为空")
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
    @NotNull(message = "购置日期不能为空")
    private LocalDate purchaseDate;

    @ApiModelProperty("购置金额")
    private BigDecimal purchaseAmount;

    @ApiModelProperty("出厂编号")
    private String factoryNo;

    @ApiModelProperty("设备状态")
    @NotNull(message = "设备状态不能为空")
    private Integer status;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("技术参数")
    private String technicalParams;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("附件")
    private String attachments;
}
