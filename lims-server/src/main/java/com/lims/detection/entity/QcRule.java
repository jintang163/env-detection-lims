package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("qc_rule")
@ApiModel("质控规则表")
public class QcRule extends BaseEntity {

    @ApiModelProperty("规则编号")
    private String ruleCode;

    @ApiModelProperty("规则名称")
    private String ruleName;

    @ApiModelProperty("规则类型 1西屋规则 2自定义规则")
    private Integer ruleType;

    @ApiModelProperty("规则描述")
    private String description;

    @ApiModelProperty("标准差倍数")
    private BigDecimal sdMultiple;

    @ApiModelProperty("连续点数")
    private Integer consecutivePoints;

    @ApiModelProperty("是否批内")
    private Boolean withinRun;

    @ApiModelProperty("是否批间")
    private Boolean acrossRun;

    @ApiModelProperty("计算公式")
    private String formula;

    @ApiModelProperty("是否启用 0否 1是")
    private Integer enabled;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
