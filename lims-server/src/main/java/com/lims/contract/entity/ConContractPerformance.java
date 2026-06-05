package com.lims.contract.entity;

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
@TableName("con_contract_performance")
@ApiModel("合同履约跟踪表")
public class ConContractPerformance extends BaseEntity {

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractNo;

    @ApiModelProperty("履约节点名称")
    private String nodeName;

    @ApiModelProperty("计划完成日期")
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
