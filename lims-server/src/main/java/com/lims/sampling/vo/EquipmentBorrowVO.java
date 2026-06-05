package com.lims.sampling.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("设备领用列表VO")
public class EquipmentBorrowVO {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("领用单号")
    private String borrowNo;

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("规格型号")
    private String specification;

    @ApiModelProperty("关联采样计划ID")
    private Long planId;

    @ApiModelProperty("计划编号")
    private String planNo;

    @ApiModelProperty("借用人ID")
    private Long borrowerId;

    @ApiModelProperty("借用人姓名")
    private String borrowerName;

    @ApiModelProperty("领用日期")
    private LocalDate borrowDate;

    @ApiModelProperty("预计归还日期")
    private LocalDate expectedReturnDate;

    @ApiModelProperty("实际归还日期")
    private LocalDate actualReturnDate;

    @ApiModelProperty("领用原因")
    private String borrowReason;

    @ApiModelProperty("归还状态 0未归还 1已归还 2逾期 3损坏 4丢失")
    private Integer returnStatus;

    @ApiModelProperty("归还状态名称")
    private String returnStatusName;

    @ApiModelProperty("处理人ID")
    private Long handlerId;

    @ApiModelProperty("处理人姓名")
    private String handlerName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
