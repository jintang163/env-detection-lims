package com.lims.customer.entity;

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
@TableName("cus_customer")
@ApiModel("客户主表")
public class CusCustomer extends BaseEntity {

    @ApiModelProperty("客户编号")
    private String customerNo;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("客户简称")
    private String shortName;

    @ApiModelProperty("客户类型 1企业客户 2个人客户 3政府机构")
    private Integer customerType;

    @ApiModelProperty("客户等级 1VIP 2重要 3普通 4潜在")
    private Integer customerLevel;

    @ApiModelProperty("客户来源 1线上推广 2线下拜访 3转介绍 4展会 5其他")
    private Integer customerSource;

    @ApiModelProperty("行业类别")
    private String industry;

    @ApiModelProperty("统一社会信用代码")
    private String creditCode;

    @ApiModelProperty("法人代表")
    private String legalPerson;

    @ApiModelProperty("注册资本")
    private BigDecimal registeredCapital;

    @ApiModelProperty("成立日期")
    private LocalDate establishDate;

    @ApiModelProperty("所属省份")
    private String province;

    @ApiModelProperty("所属城市")
    private String city;

    @ApiModelProperty("所属区县")
    private String district;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("经度")
    private BigDecimal longitude;

    @ApiModelProperty("纬度")
    private BigDecimal latitude;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("联系邮箱")
    private String contactEmail;

    @ApiModelProperty("传真")
    private String fax;

    @ApiModelProperty("官网")
    private String website;

    @ApiModelProperty("客户简介")
    private String description;

    @ApiModelProperty("营业执照")
    private String businessLicense;

    @ApiModelProperty("信用分数")
    private Integer creditScore;

    @ApiModelProperty("信用等级 A/B/C/D")
    private String creditLevel;

    @ApiModelProperty("是否公海客户 0否 1是")
    private Integer isPublic;

    @ApiModelProperty("公海进入时间")
    private LocalDate publicEnterTime;

    @ApiModelProperty("公海原因")
    private String publicReason;

    @ApiModelProperty("归属业务员ID")
    private Long ownerId;

    @ApiModelProperty("归属业务员姓名")
    private String ownerName;

    @ApiModelProperty("归属部门ID")
    private Long deptId;

    @ApiModelProperty("上次跟进时间")
    private LocalDate lastFollowTime;

    @ApiModelProperty("下次跟进时间")
    private LocalDate nextFollowTime;

    @ApiModelProperty("客户状态 0潜在 1意向 2成交 3流失")
    private Integer customerStatus;

    @ApiModelProperty("成交日期")
    private LocalDate dealDate;

    @ApiModelProperty("累计成交金额")
    private BigDecimal totalDealAmount;

    @ApiModelProperty("累计成交次数")
    private Integer totalDealCount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
