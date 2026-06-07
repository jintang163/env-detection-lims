package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("OOS调查DTO")
public class OosInvestigationDTO {

    @ApiModelProperty("OOS记录ID")
    @NotNull(message = "OOS记录ID不能为空")
    private Long oosId;

    @ApiModelProperty("调查结果")
    private String investigationResult;

    @ApiModelProperty("原因分析")
    private String causeAnalysis;

    @ApiModelProperty("处理措施")
    private String handlingMeasure;

    @ApiModelProperty("纠正措施")
    private String correctiveAction;

    @ApiModelProperty("预防措施")
    private String preventiveAction;

    @ApiModelProperty("调查人ID")
    private Long investigatorUserId;

    @ApiModelProperty("调查人姓名")
    private String investigatorUserName;

    @ApiModelProperty("调查结束时间")
    private LocalDateTime investigationEndTime;

    @ApiModelProperty("附件地址列表")
    private List<String> attachmentUrlList;

    @ApiModelProperty("备注")
    private String remark;
}
