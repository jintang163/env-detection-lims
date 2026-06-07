package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("OOS记录查询DTO")
public class OosRecordQuery extends PageQuery {

    @ApiModelProperty("OOS编号")
    private String oosNo;

    @ApiModelProperty("检测数据记录编号")
    private String dataRecordNo;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("检测项目名称")
    private String itemName;

    @ApiModelProperty("超标类型 OOS OOT")
    private String oosType;

    @ApiModelProperty("超标等级 1一般 2严重 3重大")
    private Integer oosLevel;

    @ApiModelProperty("状态 0待调查 1调查中 2待审核 3已完成 4已关闭")
    private Integer status;

    @ApiModelProperty("是否闭环 0否 1是")
    private Integer isClosed;

    @ApiModelProperty("发现人ID")
    private Long discoveryUserId;

    @ApiModelProperty("调查人ID")
    private Long investigatorUserId;

    @ApiModelProperty("发现时间开始")
    private LocalDate discoveryTimeStart;

    @ApiModelProperty("发现时间结束")
    private LocalDate discoveryTimeEnd;
}
