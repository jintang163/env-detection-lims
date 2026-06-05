package com.lims.contract.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("履约跟踪保存DTO")
public class ContractPerformanceSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    @ApiModelProperty("履约节点名称")
    @NotBlank(message = "履约节点名称不能为空")
    private String nodeName;

    @ApiModelProperty("计划完成日期")
    @NotNull(message = "计划完成日期不能为空")
    private LocalDate planDate;

    @ApiModelProperty("实际完成日期")
    private LocalDate actualDate;

    @ApiModelProperty("节点描述")
    private String nodeDescription;

    @ApiModelProperty("完成情况说明")
    private String completionNote;

    @ApiModelProperty("节点金额")
    private BigDecimal nodeAmount;

    @ApiModelProperty("完成进度(%)")
    private Integer progress;

    @ApiModelProperty("状态：0未开始 1进行中 2已完成 3逾期")
    private Integer status;

    @ApiModelProperty("负责人ID")
    private Long managerId;

    @ApiModelProperty("负责人名称")
    private String managerName;

    @ApiModelProperty("附件")
    private String attachments;

    @ApiModelProperty("备注")
    private String remark;
}
