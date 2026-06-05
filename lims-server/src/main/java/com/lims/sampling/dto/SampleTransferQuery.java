package com.lims.sampling.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("样品交接查询DTO")
public class SampleTransferQuery extends PageQuery {

    @ApiModelProperty("交接单编号")
    private String transferNo;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("交接类型")
    private Integer transferType;

    @ApiModelProperty("交接人ID")
    private Long samplerId;

    @ApiModelProperty("交接人姓名")
    private String samplerName;

    @ApiModelProperty("接收人ID")
    private Long receiverId;

    @ApiModelProperty("交接状态")
    private Integer transferStatus;

    @ApiModelProperty("交接时间开始")
    private LocalDateTime transferTimeStart;

    @ApiModelProperty("交接时间结束")
    private LocalDateTime transferTimeEnd;
}
