package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@ApiModel("检测数据列表VO")
public class DataRecordVO {

    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("检测任务ID")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品ID")
    private Long sampleId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法编号")
    private String methodCode;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("检测日期")
    private LocalDate testDate;

    @ApiModelProperty("检测时间")
    private LocalTime testTime;

    @ApiModelProperty("数据来源 1手动录入 2仪器导入 3文件导入")
    private Integer dataSource;

    @ApiModelProperty("数据来源名称")
    private String dataSourceName;

    @ApiModelProperty("录入人ID")
    private Long entryUserId;

    @ApiModelProperty("录入人姓名")
    private String entryUserName;

    @ApiModelProperty("录入时间")
    private LocalDateTime entryTime;

    @ApiModelProperty("审核状态 0待审核 1一级审核中 2一级审核通过 3一级审核驳回 4二级审核中 5二级审核通过 6二级审核驳回")
    private Integer reviewStatus;

    @ApiModelProperty("审核状态名称")
    private String reviewStatusName;

    @ApiModelProperty("是否有OOS 0否 1是")
    private Integer hasOos;

    @ApiModelProperty("OOS数量")
    private Integer oosCount;

    @ApiModelProperty("状态 0草稿 1已录入 2审核中 3已完成 4已退回")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
