package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("维护计划VO")
public class EnvFacilityMaintenancePlanVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设施ID")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("维护类型：1日常保养 2定期维护 3预防性维护")
    private Integer maintenanceType;

    @ApiModelProperty("维护类型名称")
    private String maintenanceTypeName;

    @ApiModelProperty("周期天数")
    private Integer cycleDays;

    @ApiModelProperty("上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("提前提醒天数")
    private Integer remindDays;

    @ApiModelProperty("状态：0停用 1启用")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("内容模板")
    private String contentTemplate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
