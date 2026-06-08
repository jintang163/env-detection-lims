package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("维护记录保存DTO")
public class MaintenanceRecordSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设备ID")
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    @NotBlank(message = "设备编号不能为空")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @ApiModelProperty("维护类型：1日常保养 2定期维护 3预防性维护")
    @NotNull(message = "维护类型不能为空")
    private Integer maintenanceType;

    @ApiModelProperty("维护日期")
    @NotNull(message = "维护日期不能为空")
    private LocalDate maintenanceDate;

    @ApiModelProperty("维护人ID")
    private Long maintainerId;

    @ApiModelProperty("维护人名称")
    private String maintainerName;

    @ApiModelProperty("维护内容")
    @NotBlank(message = "维护内容不能为空")
    private String content;

    @ApiModelProperty("维护结果：1良好 2一般 3需维修")
    @NotNull(message = "维护结果不能为空")
    private Integer result;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("维护费用")
    private BigDecimal cost;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
