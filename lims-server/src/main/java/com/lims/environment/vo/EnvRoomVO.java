package com.lims.environment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("房间列表VO")
public class EnvRoomVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("房间编号")
    private String roomNo;

    @ApiModelProperty("房间名称")
    private String roomName;

    @ApiModelProperty("楼栋")
    private String building;

    @ApiModelProperty("楼层")
    private String floor;

    @ApiModelProperty("面积")
    private BigDecimal area;

    @ApiModelProperty("房间类型")
    private String roomType;

    @ApiModelProperty("房间类型名称")
    private String roomTypeName;

    @ApiModelProperty("温度最小值")
    private BigDecimal temperatureMin;

    @ApiModelProperty("温度最大值")
    private BigDecimal temperatureMax;

    @ApiModelProperty("湿度最小值")
    private BigDecimal humidityMin;

    @ApiModelProperty("湿度最大值")
    private BigDecimal humidityMax;

    @ApiModelProperty("压差最小值")
    private BigDecimal pressureMin;

    @ApiModelProperty("压差最大值")
    private BigDecimal pressureMax;

    @ApiModelProperty("噪音最大值")
    private BigDecimal noiseMax;

    @ApiModelProperty("所属部门ID")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("状态：0停用 1启用")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
