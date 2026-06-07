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
@TableName("det_oos_process_log")
@ApiModel("OOS处理流程记录表")
public class DetOosProcessLog extends BaseEntity {

    @ApiModelProperty("OOS记录ID")
    private Long oosId;

    @ApiModelProperty("OOS编号")
    private String oosNo;

    @ApiModelProperty("流程节点 发现 分配 调查 审核 关闭")
    private String processNode;

    @ApiModelProperty("处理内容")
    private String processContent;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("附件地址列表(JSON格式)")
    private String attachmentUrls;

    @ApiModelProperty("备注")
    private String remark;
}
