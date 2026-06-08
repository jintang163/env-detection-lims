package com.lims.equipment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("维护记录列表VO")
public class MaintenanceRecordVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("维护类型：1日常保养 2定期维护 3预防性维护")
    private Integer maintenanceType;

    @ApiModelProperty("维护类型名称")
    private String maintenanceTypeName;

    @ApiModelProperty("维护日期")
    private LocalDate maintenanceDate;

    @ApiModelProperty("维护人ID")
    private Long maintainerId;

    @ApiModelProperty("维护人名称")
    private String maintainerName;

    @ApiModelProperty("维护内容")
    private String content;

    @ApiModelProperty("维护结果：1良好 2一般 3需维修")
    private Integer result;

    @ApiModelProperty("维护结果名称")
    private String resultName;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("维护费用")
    private BigDecimal cost;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
