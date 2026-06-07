package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_data_trace")
@ApiModel("数据溯源关系表")
public class DetDataTrace extends BaseEntity {

    @ApiModelProperty("溯源类型 sample task equipment standard_substance")
    private String traceType;

    @ApiModelProperty("源节点ID")
    private Long sourceId;

    @ApiModelProperty("源节点类型")
    private String sourceType;

    @ApiModelProperty("源节点名称")
    private String sourceName;

    @ApiModelProperty("源节点编号")
    private String sourceNo;

    @ApiModelProperty("目标节点ID")
    private Long targetId;

    @ApiModelProperty("目标节点类型")
    private String targetType;

    @ApiModelProperty("目标节点名称")
    private String targetName;

    @ApiModelProperty("目标节点编号")
    private String targetNo;

    @ApiModelProperty("关系类型 belong_to use_for generate")
    private String relationType;

    @ApiModelProperty("关系描述")
    private String relationDesc;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
