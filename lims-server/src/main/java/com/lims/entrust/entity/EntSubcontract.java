package com.lims.entrust.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ent_subcontract")
@ApiModel("分包信息表")
public class EntSubcontract extends BaseEntity {

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

    @ApiModelProperty("状态 0草稿 1已送出 2检测中 3已返回 4已完成")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
