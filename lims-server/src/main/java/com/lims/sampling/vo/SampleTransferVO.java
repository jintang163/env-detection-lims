package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("样品交接列表VO")
public class SampleTransferVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("交接单编号")
    private String transferNo;

    @ApiModelProperty("采样计划ID")
    private Long planId;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("交接类型 1采样交样 2样品送样")
    private Integer transferType;

    @ApiModelProperty("交接类型名称")
    private String transferTypeName;

    @ApiModelProperty("交接人ID(采样员)")
    private Long samplerId;

    @ApiModelProperty("交接人姓名")
    private String samplerName;

    @ApiModelProperty("接收人ID(样品管理员)")
    private Long receiverId;

    @ApiModelProperty("接收人姓名")
    private String receiverName;

    @ApiModelProperty("交接时间")
    private LocalDateTime transferTime;

    @ApiModelProperty("样品数量")
    private Integer sampleCount;

    @ApiModelProperty("交接状态 0待确认 1已确认 2已驳回")
    private Integer transferStatus;

    @ApiModelProperty("交接状态名称")
    private String transferStatusName;

    @ApiModelProperty("确认时间")
    private LocalDateTime confirmTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
