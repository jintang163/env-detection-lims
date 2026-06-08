package com.lims.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("维修处理DTO")
public class RepairHandleDTO {

    @ApiModelProperty("维修申请ID")
    @NotNull(message = "维修申请ID不能为空")
    private Long id;

    @ApiModelProperty("受理人ID")
    private Long handlerId;

    @ApiModelProperty("受理人名称")
    private String handlerName;

    @ApiModelProperty("维修单位")
    private String repairUnit;

    @ApiModelProperty("维修人员")
    private String repairPerson;

    @ApiModelProperty("维修开始时间")
    private LocalDateTime repairStartTime;

    @ApiModelProperty("维修结束时间")
    private LocalDateTime repairEndTime;

    @ApiModelProperty("维修内容")
    private String repairContent;

    @ApiModelProperty("更换部件")
    private String replacedParts;

    @ApiModelProperty("维修费用")
    private BigDecimal repairCost;

    @ApiModelProperty("维修结果：1修复 2部分修复 3无法修复")
    @NotNull(message = "维修结果不能为空")
    private Integer repairResult;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
