package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("原始记录查询DTO")
public class OriginalRecordQuery extends PageQuery {

    @ApiModelProperty("原始记录编号")
    private String recordNo;

    @ApiModelProperty("检测数据记录编号")
    private String dataRecordNo;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("记录标题")
    private String title;

    @ApiModelProperty("记录类型")
    private String recordType;

    @ApiModelProperty("状态 0草稿 1编辑中 2已提交 3已归档")
    private Integer status;

    @ApiModelProperty("权限类型 1只读 2可写")
    private Integer permissionType;

    @ApiModelProperty("创建人ID")
    private Long createBy;

    @ApiModelProperty("创建时间开始")
    private LocalDate createTimeStart;

    @ApiModelProperty("创建时间结束")
    private LocalDate createTimeEnd;
}
