package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("原始记录列表VO")
public class OriginalRecordVO {

    @ApiModelProperty("原始记录ID")
    private Long id;

    @ApiModelProperty("原始记录编号")
    private String recordNo;

    @ApiModelProperty("检测数据记录ID")
    private Long dataRecordId;

    @ApiModelProperty("检测数据记录编号")
    private String dataRecordNo;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品ID")
    private Long sampleId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("模板ID")
    private Long templateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("记录类型")
    private String recordType;

    @ApiModelProperty("记录标题")
    private String title;

    @ApiModelProperty("权限类型 1只读 2可写")
    private Integer permissionType;

    @ApiModelProperty("权限类型名称")
    private String permissionTypeName;

    @ApiModelProperty("编辑次数")
    private Integer editCount;

    @ApiModelProperty("最后编辑人ID")
    private Long lastEditUserId;

    @ApiModelProperty("最后编辑人姓名")
    private String lastEditUserName;

    @ApiModelProperty("最后编辑时间")
    private LocalDateTime lastEditTime;

    @ApiModelProperty("状态 0草稿 1编辑中 2已提交 3已归档")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("版本号")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
