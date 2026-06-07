package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("CAPA统计VO")
public class CapaStatsVO {

    @ApiModelProperty("总数")
    private Integer total;

    @ApiModelProperty("待审批")
    private Integer pendingApproval;

    @ApiModelProperty("进行中")
    private Integer inProgress;

    @ApiModelProperty("待验证")
    private Integer pendingVerification;

    @ApiModelProperty("已关闭")
    private Integer closed;

    @ApiModelProperty("已逾期")
    private Integer overdue;
}
