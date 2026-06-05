package com.lims.customer.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("客户查询参数")
public class CustomerQuery extends PageQuery {

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("客户类型 1企业客户 2个人客户 3政府机构")
    private Integer customerType;

    @ApiModelProperty("客户等级 1VIP 2重要 3普通 4潜在")
    private Integer customerLevel;

    @ApiModelProperty("是否公海客户 0否 1是")
    private Integer isPublic;

    @ApiModelProperty("客户状态 0潜在 1意向 2成交 3流失")
    private Integer customerStatus;

    @ApiModelProperty("客户来源 1线上推广 2线下拜访 3转介绍 4展会 5其他")
    private Integer customerSource;

    @ApiModelProperty("所属省份")
    private String province;

    @ApiModelProperty("所属城市")
    private String city;

    @ApiModelProperty("行业类别")
    private String industry;

    @ApiModelProperty("信用等级 A/B/C/D")
    private String creditLevel;

    @ApiModelProperty("归属业务员ID")
    private Long ownerId;

    @ApiModelProperty("归属部门ID")
    private Long deptId;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
