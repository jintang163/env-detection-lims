package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("std_curve_point")
@ApiModel("标准曲线上样点表")
public class StdCurvePoint {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("曲线ID")
    private Long curveId;

    @ApiModelProperty("点号")
    private Integer pointNo;

    @ApiModelProperty("浓度")
    private BigDecimal concentration;

    @ApiModelProperty("响应值1")
    private BigDecimal response1;

    @ApiModelProperty("响应值2")
    private BigDecimal response2;

    @ApiModelProperty("响应值3")
    private BigDecimal response3;

    @ApiModelProperty("响应平均值")
    private BigDecimal responseAvg;

    @ApiModelProperty("响应值RSD")
    private BigDecimal responseRsd;

    @ApiModelProperty("计算值")
    private BigDecimal calculatedValue;

    @ApiModelProperty("残差")
    private BigDecimal residual;

    @ApiModelProperty("相对误差")
    private BigDecimal relativeError;

    @ApiModelProperty("是否有效 0否 1是")
    private Integer isValid;

    @ApiModelProperty("备注")
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
