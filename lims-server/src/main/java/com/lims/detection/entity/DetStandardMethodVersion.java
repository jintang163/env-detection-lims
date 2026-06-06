package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("det_standard_method_version")
@ApiModel("标准方法版本历史表")
public class DetStandardMethodVersion implements Serializable {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("版本记录ID")
    private Long id;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("方法编号")
    private String methodCode;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("变更类型")
    private String changeType;

    @ApiModelProperty("变更内容")
    private String changeContent;

    @ApiModelProperty("变更原因")
    private String changeReason;

    @ApiModelProperty("变更前内容(JSON)")
    private String beforeContent;

    @ApiModelProperty("变更后内容(JSON)")
    private String afterContent;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人姓名")
    private String operatorName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
