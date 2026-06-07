package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("OOS流程日志VO")
public class OosProcessLogVO {

    @ApiModelProperty("日志ID")
    private Long id;

    @ApiModelProperty("OOS记录ID")
    private Long oosId;

    @ApiModelProperty("OOS编号")
    private String oosNo;

    @ApiModelProperty("流程节点 发现 分配 调查 审核 关闭")
    private String processNode;

    @ApiModelProperty("流程节点名称")
    private String processNodeName;

    @ApiModelProperty("处理内容")
    private String processContent;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("附件地址列表")
    private List<String> attachmentUrlList;

    @ApiModelProperty("备注")
    private String remark;
}
