package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("房间保存DTO")
public class EnvRoomSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("房间编号")
    @NotBlank(message = "房间编号不能为空")
    private String roomNo;

    @ApiModelProperty("房间名称")
    @NotBlank(message = "房间名称不能为空")
    private String roomName;

    @ApiModelProperty("所属楼栋")
    private String building;

    @ApiModelProperty("楼层")
    private String floor;

    @ApiModelProperty("面积")
    private BigDecimal area;

    @ApiModelProperty("房间类型")
    private String roomType;

    @ApiModelProperty("温度最小值")
    private BigDecimal temperatureMin;

    @ApiModelProperty("温度最大值")
    private BigDecimal temperatureMax;

    @ApiModelProperty("湿度最小值")
    private BigDecimal humidityMin;

    @ApiModelProperty("湿度最大值")
    private BigDecimal humidityMax;

    @ApiModelProperty("压力最小值")
    private BigDecimal pressureMin;

    @ApiModelProperty("压力最大值")
    private BigDecimal pressureMax;

    @ApiModelProperty("噪音最大值")
    private BigDecimal noiseMax;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
