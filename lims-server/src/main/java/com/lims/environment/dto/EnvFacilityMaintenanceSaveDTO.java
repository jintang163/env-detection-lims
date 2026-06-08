package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("维护记录保存DTO")
public class EnvFacilityMaintenanceSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设施ID")
    @NotNull(message = "设施ID不能为空")
    private Long facilityId;

    @ApiModelProperty("设施编号")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    private String facilityName;

    @ApiModelProperty("维护类型")
    @NotNull(message = "维护类型不能为空")
    private Integer maintenanceType;

    @ApiModelProperty("维护日期")
    @NotNull(message = "维护日期不能为空")
    private LocalDate maintenanceDate;

    @ApiModelProperty("维护人ID")
    private Long maintainerId;

    @ApiModelProperty("维护人名称")
    private String maintainerName;

    @ApiModelProperty("维护单位")
    private String maintenanceUnit;

    @ApiModelProperty("维护内容")
    @NotBlank(message = "维护内容不能为空")
    private String content;

    @ApiModelProperty("维护结果")
    @NotNull(message = "维护结果不能为空")
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
