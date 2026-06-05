package com.lims.entrust.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("分包信息VO")
public class SubcontractVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("分包编号")
    private String subcontractNo;

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("分包商ID")
    private Long subcontractorId;

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

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
