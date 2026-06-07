package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("OOS记录保存DTO")
public class OosRecordSaveDTO {

    @ApiModelProperty("OOS记录ID（更新时传）")
    private Long id;

    @ApiModelProperty("检测数据记录ID")
    @NotNull(message = "检测数据记录ID不能为空")
    private Long dataRecordId;

    @ApiModelProperty("检测数据明细ID")
    private Long dataItemId;

    @ApiModelProperty("超标等级 1一般 2严重 3重大")
    private Integer oosLevel;

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

    @ApiModelProperty("备注")
    private String remark;
}
