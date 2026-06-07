package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("检测数据查询DTO")
public class DataRecordQuery extends PageQuery {

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("录入人ID")
    private Long entryUserId;

    @ApiModelProperty("录入人姓名")
    private String entryUserName;

    @ApiModelProperty("审核状态 0待审核 1一级审核中 2一级审核通过 3一级审核驳回 4二级审核中 5二级审核通过 6二级审核驳回")
    private Integer reviewStatus;

    @ApiModelProperty("状态 0草稿 1已录入 2审核中 3已完成 4已退回")
    private Integer status;

    @ApiModelProperty("是否有OOS 0否 1是")
    private Integer hasOos;

    @ApiModelProperty("数据来源 1手动录入 2仪器导入 3文件导入")
    private Integer dataSource;

    @ApiModelProperty("检测日期开始")
    private LocalDate testDateStart;

    @ApiModelProperty("检测日期结束")
    private LocalDate testDateEnd;

    @ApiModelProperty("录入时间开始")
    private LocalDate entryTimeStart;

    @ApiModelProperty("录入时间结束")
    private LocalDate entryTimeEnd;
}
