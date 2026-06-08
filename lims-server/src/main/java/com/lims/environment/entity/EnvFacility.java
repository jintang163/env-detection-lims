package com.lims.environment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("env_facility")
@ApiModel("设施")
public class EnvFacility extends BaseEntity {

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("设施类型")
    private Integer facilityType;

    @ApiModelProperty("型号")
    private String model;

    @ApiModelProperty("规格")
    private String specification;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("房间ID")
    private Long roomId;

    @ApiModelProperty("房间编号")
    private String roomNo;

    @ApiModelProperty("房间名称")
    private String roomName;

    @ApiModelProperty("安装日期")
    private LocalDate installDate;

    @ApiModelProperty("质保日期")
    private LocalDate warrantyDate;

    @ApiModelProperty("状态")
    private Integer status;

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
}
