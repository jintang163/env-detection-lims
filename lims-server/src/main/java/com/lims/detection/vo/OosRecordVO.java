package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("OOS记录列表VO")
public class OosRecordVO {

    @ApiModelProperty("OOS记录ID")
    private Long id;

    @ApiModelProperty("OOS编号")
    private String oosNo;

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

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("检测项目编码")
    private String itemCode;

    @ApiModelProperty("检测项目名称")
    private String itemName;

    @ApiModelProperty("计量单位")
    private String unit;

    @ApiModelProperty("检测结果")
    private BigDecimal resultValue;

    @ApiModelProperty("限值要求")
    private String limitValue;

    @ApiModelProperty("偏差率(%)")
    private BigDecimal deviationRate;

    @ApiModelProperty("超标类型 OOS OOT")
    private String oosType;

    @ApiModelProperty("超标类型名称")
    private String oosTypeName;

    @ApiModelProperty("超标等级 1一般 2严重 3重大")
    private Integer oosLevel;

    @ApiModelProperty("超标等级名称")
    private String oosLevelName;

    @ApiModelProperty("发现时间")
    private LocalDateTime discoveryTime;

    @ApiModelProperty("发现人ID")
    private Long discoveryUserId;

    @ApiModelProperty("发现人姓名")
    private String discoveryUserName;

    @ApiModelProperty("调查人ID")
    private Long investigatorUserId;

    @ApiModelProperty("调查人姓名")
    private String investigatorUserName;

    @ApiModelProperty("状态 0待调查 1调查中 2待审核 3已完成 4已关闭")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("是否闭环 0否 1是")
    private Integer isClosed;

    @ApiModelProperty("是否闭环名称")
    private String isClosedName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
