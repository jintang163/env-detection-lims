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
@TableName("det_data_review")
@ApiModel("数据审核记录表")
public class DetDataReview extends BaseEntity {

    @ApiModelProperty("检测数据记录ID")
    private Long dataRecordId;

    @ApiModelProperty("检测数据记录编号")
    private String dataRecordNo;

    @ApiModelProperty("审核级别 1一级审核(自审) 2二级审核(科室负责人)")
    private Integer reviewLevel;

    @ApiModelProperty("审核人ID")
    private Long reviewerId;

    @ApiModelProperty("审核人姓名")
    private String reviewerName;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;

    @ApiModelProperty("审核结果 1通过 2驳回")
    private Integer reviewResult;

    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty("驳回原因")
    private String rejectReason;

    @ApiModelProperty("修正后数据(JSON格式)")
    private String correctedData;

    @ApiModelProperty("是否修改数据 0否 1是")
    private Integer isModified;

    @ApiModelProperty("备注")
    private String remark;
}
