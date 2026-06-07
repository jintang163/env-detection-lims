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
@TableName("det_original_record_version")
@ApiModel("原始记录版本历史表")
public class DetOriginalRecordVersion extends BaseEntity {

    @ApiModelProperty("原始记录ID")
    private Long originalRecordId;

    @ApiModelProperty("原始记录编号")
    private String originalRecordNo;

    @ApiModelProperty("版本号")
    private Integer version;

    @ApiModelProperty("记录内容(JSON格式)")
    private String content;

    @ApiModelProperty("HTML格式内容")
    private String htmlContent;

    @ApiModelProperty("内容哈希值")
    private String dataHash;

    @ApiModelProperty("变更类型 创建 修改 提交 归档")
    private String changeType;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;
}
