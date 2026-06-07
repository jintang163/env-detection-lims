package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("数据审核保存DTO")
public class DataReviewSaveDTO {

    @ApiModelProperty("检测数据记录ID")
    @NotNull(message = "检测数据记录ID不能为空")
    private Long dataRecordId;

    @ApiModelProperty("审核级别 1一级审核(自审) 2二级审核(科室负责人)")
    @NotNull(message = "审核级别不能为空")
    private Integer reviewLevel;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;

    @ApiModelProperty("审核结果 1通过 2驳回")
    @NotNull(message = "审核结果不能为空")
    private Integer reviewResult;

    @ApiModelProperty("驳回原因")
    private String rejectReason;

    @ApiModelProperty("修正后数据")
    private List<DataRecordItemSaveDTO> correctedItemList;

    @ApiModelProperty("备注")
    private String remark;
}
