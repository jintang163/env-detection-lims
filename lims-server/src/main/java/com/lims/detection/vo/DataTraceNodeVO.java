package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("数据溯源节点VO")
public class DataTraceNodeVO {

    @ApiModelProperty("节点ID")
    private Long id;

    @ApiModelProperty("节点类型 sample task data_record original_record report equipment standard_substance")
    private String nodeType;

    @ApiModelProperty("节点类型名称")
    private String nodeTypeName;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("节点编号")
    private String nodeNo;

    @ApiModelProperty("关系类型 belong_to use_for generate")
    private String relationType;

    @ApiModelProperty("关系描述")
    private String relationDesc;

    @ApiModelProperty("子节点列表")
    private List<DataTraceNodeVO> children;
}
