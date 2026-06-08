package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("设施列表VO")
public class EnvFacilityVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("设施类型")
    private Integer facilityType;

    @ApiModelProperty("设施类型名称")
    private String facilityTypeName;

    @ApiModelProperty("型号")
    private String model;

    @ApiModelProperty("规格")
    private String specification;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("所属房间ID")
    private Long roomId;

    @ApiModelProperty("房间编号")
    private String roomNo;

    @ApiModelProperty("房间名称")
    private String roomName;

    @ApiModelProperty("安装日期")
    private LocalDate installDate;

    @ApiModelProperty("质保日期")
    private LocalDate warrantyDate;

    @ApiModelProperty("状态：0停用 1正常 2维修中 3故障")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("维护周期(天)")
    private Integer maintenanceCycle;

    @ApiModelProperty("所属部门ID")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("维护记录列表")
    private List<EnvFacilityMaintenanceVO> maintenanceRecords;

    @ApiModelProperty("维护计划列表")
    private List<EnvFacilityMaintenancePlanVO> maintenancePlans;
}
