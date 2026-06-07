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
@TableName("pt_sample")
@ApiModel("能力验证样品表")
public class PtSample extends BaseEntity {

    @ApiModelProperty("能力验证计划ID")
    private Long ptPlanId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("样品代码")
    private String sampleCode;

    @ApiModelProperty("基质")
    private String matrix;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("指定值")
    private BigDecimal assignedValue;

    @ApiModelProperty("指定值不确定度")
    private BigDecimal assignedValueUncertainty;

    @ApiModelProperty("目标标准差")
    private BigDecimal targetSd;

    @ApiModelProperty("接收日期")
    private LocalDateTime receiveDate;

    @ApiModelProperty("储存条件")
    private String storageCondition;

    @ApiModelProperty("有效期")
    private LocalDateTime expireDate;

    @ApiModelProperty("样品状态")
    private String sampleStatus;

    @ApiModelProperty("备注")
    private String remark;
}
