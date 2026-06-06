package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("检测任务保存DTO")
public class DetectionTaskSaveDTO {

    @ApiModelProperty("任务ID（更新时传）")
    private Long id;

    @ApiModelProperty("任务名称")
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    @ApiModelProperty("关联委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("关联样品ID")
    private Long sampleId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("检测项目ID列表")
    private List<Long> testItemIdList;

    @ApiModelProperty("检测项目名称列表")
    private List<String> testItemNameList;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法编号")
    private String methodCode;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("所需设备ID列表")
    private List<Long> equipmentIdList;

    @ApiModelProperty("所需设备名称列表")
    private List<String> equipmentNameList;

    @ApiModelProperty("优先级 1高 2中 3低")
    private Integer priority;

    @ApiModelProperty("是否加急 0否 1是")
    private Integer isUrgent;

    @ApiModelProperty("计划开始日期")
    private LocalDate planStartDate;

    @ApiModelProperty("计划完成日期")
    private LocalDate planEndDate;

    @ApiModelProperty("预计检测天数")
    private Integer expectedDays;

    @ApiModelProperty("是否放入抢单池 0否 1是")
    private Integer isGrabOrder;

    @ApiModelProperty("抢单截止时间")
    private LocalDateTime grabOrderExpireTime;

    @ApiModelProperty("备注")
    private String remark;
}
