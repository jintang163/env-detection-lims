package com.lims.equipment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("设备详情VO")
public class EquipmentDetailVO {

    @ApiModelProperty("主键ID")
    private Long id;

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

    @ApiModelProperty("设备状态名称")
    private String statusName;

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

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

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

    @ApiModelProperty("校准计划列表")
    private List<CalibrationPlanVO> calibrationPlans;

    @ApiModelProperty("校准记录列表")
    private List<CalibrationRecordVO> calibrationRecords;

    @ApiModelProperty("使用记录列表")
    private List<EquipmentUsageVO> usageRecords;

    @ApiModelProperty("维护记录列表")
    private List<MaintenanceRecordVO> maintenanceRecords;

    @ApiModelProperty("维修申请列表")
    private List<RepairRequestVO> repairRequests;
}
