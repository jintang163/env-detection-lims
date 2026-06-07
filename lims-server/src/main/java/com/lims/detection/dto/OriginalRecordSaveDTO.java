package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("原始记录保存DTO")
public class OriginalRecordSaveDTO {

    @ApiModelProperty("原始记录ID（更新时传）")
    private Long id;

    @ApiModelProperty("检测数据记录ID")
    @NotNull(message = "检测数据记录ID不能为空")
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

    @ApiModelProperty("记录类型 水质检测 空气检测 土壤检测")
    private String recordType;

    @ApiModelProperty("记录标题")
    private String title;

    @ApiModelProperty("记录内容(JSON格式，富文本)")
    private String content;

    @ApiModelProperty("HTML格式内容（用于预览）")
    private String htmlContent;

    @ApiModelProperty("签名信息(JSON格式)")
    private String signatureInfo;

    @ApiModelProperty("附件地址列表")
    private List<String> attachmentUrlList;

    @ApiModelProperty("附件名称列表")
    private List<String> attachmentNameList;

    @ApiModelProperty("权限类型 1只读 2可写")
    private Integer permissionType;

    @ApiModelProperty("授权用户ID列表")
    private List<Long> permissionUserIdList;

    @ApiModelProperty("授权角色编码列表")
    private List<String> permissionRoleList;

    @ApiModelProperty("变更原因（版本历史用）")
    private String changeReason;

    @ApiModelProperty("备注")
    private String remark;
}
