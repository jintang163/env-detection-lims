package com.lims.environment.entity;

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
@TableName("env_facility_maintenance")
@ApiModel("设施维护记录")
public class EnvFacilityMaintenance extends BaseEntity {

    @ApiModelProperty("设施ID")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("维护类型")
    private Integer maintenanceType;

    @ApiModelProperty("维护日期")
    private LocalDate maintenanceDate;

    @ApiModelProperty("维护人ID")
    private Long maintainerId;

    @ApiModelProperty("维护人名称")
    private String maintainerName;

    @ApiModelProperty("维护单位")
    private String maintenanceUnit;

    @ApiModelProperty("维护内容")
    private String content;

    @ApiModelProperty("维护结果")
    private Integer result;

    @ApiModelProperty("故障描述")
    private String faultDescription;

    @ApiModelProperty("更换部件")
    private String replacedParts;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("维护费用")
    private BigDecimal cost;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
