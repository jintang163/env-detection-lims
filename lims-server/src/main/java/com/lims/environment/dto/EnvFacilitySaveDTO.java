package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("设施保存DTO")
public class EnvFacilitySaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("设施编号")
    @NotBlank(message = "设施编号不能为空")
    private String facilityNo;

    @ApiModelProperty("设施名称")
    @NotBlank(message = "设施名称不能为空")
    private String facilityName;

    @ApiModelProperty("设施类型")
    @NotNull(message = "设施类型不能为空")
    private Integer facilityType;

    @ApiModelProperty("型号")
    private String model;

    @ApiModelProperty("规格")
    private String specification;

    @ApiModelProperty("生产厂家")
    private String manufacturer;

    @ApiModelProperty("房间ID")
    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @ApiModelProperty("房间编号")
    private String roomNo;

    @ApiModelProperty("房间名称")
    private String roomName;

    @ApiModelProperty("安装日期")
    private LocalDate installDate;

    @ApiModelProperty("保修截止日期")
    private LocalDate warrantyDate;

    @ApiModelProperty("状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty("上次维护日期")
    private LocalDate lastMaintenanceDate;

    @ApiModelProperty("下次维护日期")
    private LocalDate nextMaintenanceDate;

    @ApiModelProperty("维护周期")
    private Integer maintenanceCycle;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
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
