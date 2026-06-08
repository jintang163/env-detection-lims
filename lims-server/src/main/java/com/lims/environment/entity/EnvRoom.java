package com.lims.environment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("env_room")
@ApiModel("实验室房间")
public class EnvRoom extends BaseEntity {

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

    @ApiModelProperty("温度下限")
    private BigDecimal temperatureMin;

    @ApiModelProperty("温度上限")
    private BigDecimal temperatureMax;

    @ApiModelProperty("湿度下限")
    private BigDecimal humidityMin;

    @ApiModelProperty("湿度上限")
    private BigDecimal humidityMax;

    @ApiModelProperty("压力下限")
    private BigDecimal pressureMin;

    @ApiModelProperty("压力上限")
    private BigDecimal pressureMax;

    @ApiModelProperty("噪音上限")
    private BigDecimal noiseMax;

    @ApiModelProperty("所属部门ID")
    private Long deptId;

    @ApiModelProperty("所属部门名称")
    private String deptName;

    @ApiModelProperty("管理员ID")
    private Long managerId;

    @ApiModelProperty("管理员名称")
    private String managerName;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
