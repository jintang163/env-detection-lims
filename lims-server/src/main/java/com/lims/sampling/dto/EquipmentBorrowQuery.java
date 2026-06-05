package com.lims.sampling.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("设备领用查询DTO")
public class EquipmentBorrowQuery extends PageQuery {

    @ApiModelProperty("领用单号")
    private String borrowNo;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("借用人ID")
    private Long borrowerId;

    @ApiModelProperty("借用人姓名")
    private String borrowerName;

    @ApiModelProperty("归还状态")
    private Integer returnStatus;

    @ApiModelProperty("领用日期开始")
    private LocalDate borrowDateStart;

    @ApiModelProperty("领用日期结束")
    private LocalDate borrowDateEnd;

    @ApiModelProperty("关联采样计划ID")
    private Long planId;
}
