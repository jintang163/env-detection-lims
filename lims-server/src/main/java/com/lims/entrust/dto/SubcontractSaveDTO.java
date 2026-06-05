package com.lims.entrust.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ApiModel("分包保存DTO")
public class SubcontractSaveDTO {

    @ApiModelProperty("ID，修改时传入")
    private Long id;

    @NotNull(message = "委托单ID不能为空")
    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("分包商ID")
    private Long subcontractorId;

    @NotBlank(message = "分包商名称不能为空")
    @ApiModelProperty("分包商名称")
    private String subcontractorName;

    @ApiModelProperty("分包项目")
    private String subcontractItems;

    @ApiModelProperty("分包金额")
    private BigDecimal subcontractAmount;

    @ApiModelProperty("送样日期")
    private LocalDate sendDate;

    @ApiModelProperty("预计返回日期")
    private LocalDate expectReturnDate;

    @ApiModelProperty("实际返回日期")
    private LocalDate actualReturnDate;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
