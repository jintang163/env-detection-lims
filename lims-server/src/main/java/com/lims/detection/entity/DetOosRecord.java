package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_oos_record")
@ApiModel("OOS超标预警记录表")
public class DetOosRecord extends BaseEntity {

    @ApiModelProperty("OOS编号")
    private String oosNo;

    @ApiModelProperty("检测数据记录ID")
    private Long dataRecordId;

    @ApiModelProperty("检测数据记录编号")
    private String dataRecordNo;

    @ApiModelProperty("检测数据明细ID")
    private Long dataItemId;

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

    @ApiModelProperty("最小限值")
    private BigDecimal limitMin;

    @ApiModelProperty("最大限值")
    private BigDecimal limitMax;

    @ApiModelProperty("偏差率(%)")
    private BigDecimal deviationRate;

    @ApiModelProperty("超标类型 OOS OOT")
    private String oosType;

    @ApiModelProperty("超标等级 1一般 2严重 3重大")
    private Integer oosLevel;

    @ApiModelProperty("发现时间")
    private LocalDateTime discoveryTime;

    @ApiModelProperty("发现人ID")
    private Long discoveryUserId;

    @ApiModelProperty("发现人姓名")
    private String discoveryUserName;

    @ApiModelProperty("调查结果")
    private String investigationResult;

    @ApiModelProperty("原因分析")
    private String causeAnalysis;

    @ApiModelProperty("处理措施")
    private String handlingMeasure;

    @ApiModelProperty("纠正措施")
    private String correctiveAction;

    @ApiModelProperty("预防措施")
    private String preventiveAction;

    @ApiModelProperty("调查人ID")
    private Long investigatorUserId;

    @ApiModelProperty("调查人姓名")
    private String investigatorUserName;

    @ApiModelProperty("调查开始时间")
    private LocalDateTime investigationStartTime;

    @ApiModelProperty("调查结束时间")
    private LocalDateTime investigationEndTime;

    @ApiModelProperty("审核人ID")
    private Long reviewUserId;

    @ApiModelProperty("审核人姓名")
    private String reviewUserName;

    @ApiModelProperty("审核意见")
    private String reviewOpinion;

    @ApiModelProperty("审核结果 1通过 2驳回")
    private Integer reviewResult;

    @ApiModelProperty("审核时间")
    private LocalDateTime reviewTime;

    @ApiModelProperty("关闭人ID")
    private Long closeUserId;

    @ApiModelProperty("关闭人姓名")
    private String closeUserName;

    @ApiModelProperty("关闭时间")
    private LocalDateTime closeTime;

    @ApiModelProperty("关闭意见")
    private String closeOpinion;

    @ApiModelProperty("状态 0待调查 1调查中 2待审核 3已完成 4已关闭")
    private Integer status;

    @ApiModelProperty("是否闭环 0否 1是")
    private Integer isClosed;

    @ApiModelProperty("备注")
    private String remark;
}
