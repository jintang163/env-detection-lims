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
@TableName("det_data_audit_log")
@ApiModel("数据变更操作日志表")
public class DetDataAuditLog extends BaseEntity {

    @ApiModelProperty("数据类型 data_record original_record oos_record")
    private String dataType;

    @ApiModelProperty("数据ID")
    private Long dataId;

    @ApiModelProperty("数据编号")
    private String dataNo;

    @ApiModelProperty("操作类型 create update delete submit review")
    private String operateType;

    @ApiModelProperty("修改字段名")
    private String fieldName;

    @ApiModelProperty("原值")
    private String oldValue;

    @ApiModelProperty("新值")
    private String newValue;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("IP地址")
    private String ipAddress;

    @ApiModelProperty("用户代理")
    private String userAgent;

    @ApiModelProperty("是否篡改尝试 0否 1是")
    private Integer tamperAttempt;

    @ApiModelProperty("拦截原因")
    private String blockReason;

    @ApiModelProperty("备注")
    private String remark;
}
