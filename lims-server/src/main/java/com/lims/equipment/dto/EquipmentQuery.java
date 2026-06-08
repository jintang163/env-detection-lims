package com.lims.equipment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("设备查询DTO")
public class EquipmentQuery extends PageQuery {

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("设备型号")
    private String model;

    @ApiModelProperty("设备类型")
    private String equipmentType;

    @ApiModelProperty("所属部门ID")
    private Long deptId;

    @ApiModelProperty("供应商ID")
    private Long supplierId;

    @ApiModelProperty("设备状态")
    private Integer status;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("购置开始日期")
    private LocalDate purchaseDateStart;

    @ApiModelProperty("购置结束日期")
    private LocalDate purchaseDateEnd;
}
