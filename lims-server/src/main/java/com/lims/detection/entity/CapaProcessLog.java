package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("capa_process_log")
@ApiModel("CAPA处理流程日志表")
public class CapaProcessLog {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("CAPA记录ID")
    private Long capaId;

    @ApiModelProperty("操作类型")
    private String operationType;

    @ApiModelProperty("操作状态")
    private String operationStatus;

    @ApiModelProperty("操作内容")
    private String operationContent;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    @ApiModelProperty("备注")
    private String remark;
}
