package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("设备领用保存DTO")
public class EquipmentBorrowSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("领用单号")
    private String borrowNo;

    @ApiModelProperty("设备ID")
    @NotNull(message = "设备ID不能为空")
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
    @NotNull(message = "借用人ID不能为空")
    private Long borrowerId;

    @ApiModelProperty("借用人姓名")
    @NotBlank(message = "借用人姓名不能为空")
    private String borrowerName;

    @ApiModelProperty("领用日期")
    @NotNull(message = "领用日期不能为空")
    private LocalDate borrowDate;

    @ApiModelProperty("预计归还日期")
    @NotNull(message = "预计归还日期不能为空")
    private LocalDate expectedReturnDate;

    @ApiModelProperty("实际归还日期")
    private LocalDate actualReturnDate;

    @ApiModelProperty("领用原因")
    private String borrowReason;

    @ApiModelProperty("领用时检查情况")
    private String borrowCheck;

    @ApiModelProperty("归还时检查情况")
    private String returnCheck;

    @ApiModelProperty("归还状态 0未归还 1已归还 2逾期 3损坏 4丢失")
    private Integer returnStatus;

    @ApiModelProperty("损坏/丢失说明")
    private String damageDesc;

    @ApiModelProperty("处理人ID")
    private Long handlerId;

    @ApiModelProperty("处理人姓名")
    private String handlerName;

    @ApiModelProperty("备注")
    private String remark;
}
