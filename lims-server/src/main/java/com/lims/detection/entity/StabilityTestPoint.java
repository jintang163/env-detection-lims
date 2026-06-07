package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("stability_test_point")
@ApiModel("稳定性考察检测点表")
public class StabilityTestPoint extends BaseEntity {

    @ApiModelProperty("方案ID")
    private Long schemeId;

    @ApiModelProperty("点号")
    private Integer pointNo;

    @ApiModelProperty("考察天数")
    private Integer testDay;

    @ApiModelProperty("计划检测日期")
    private LocalDateTime planDate;

    @ApiModelProperty("实际检测日期")
    private LocalDateTime actualDate;

    @ApiModelProperty("任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("检测值1")
    private BigDecimal testValue1;

    @ApiModelProperty("检测值2")
    private BigDecimal testValue2;

    @ApiModelProperty("检测平均值")
    private BigDecimal testValueAvg;

    @ApiModelProperty("RSD")
    private BigDecimal rsd;

    @ApiModelProperty("偏差率")
    private BigDecimal deviationRate;

    @ApiModelProperty("是否可接受 0否 1是")
    private Integer isAcceptable;

    @ApiModelProperty("检测人")
    private String operator;

    @ApiModelProperty("状态 0待检测 1检测中 2已完成")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
