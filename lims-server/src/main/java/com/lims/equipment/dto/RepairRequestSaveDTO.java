package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("维修申请保存DTO")
public class RepairRequestSaveDTO {

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

    @ApiModelProperty("故障描述")
    @NotBlank(message = "故障描述不能为空")
    private String faultDescription;

    @ApiModelProperty("故障发生时间")
    private LocalDateTime faultTime;

    @ApiModelProperty("申请人ID")
    private Long applicantId;

    @ApiModelProperty("申请人名称")
    private String applicantName;

    @ApiModelProperty("紧急程度：1一般 2紧急 3特急")
    @NotNull(message = "紧急程度不能为空")
    private Integer urgency;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
