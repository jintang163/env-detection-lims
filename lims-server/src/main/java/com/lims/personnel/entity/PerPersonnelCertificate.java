package com.lims.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("per_personnel_certificate")
@ApiModel("人员上岗证表")
public class PerPersonnelCertificate extends BaseEntity {

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("证书类型")
    private String certificateType;

    @ApiModelProperty("证书名称")
    private String certificateName;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("发证日期")
    private LocalDate issueDate;

    @ApiModelProperty("有效期至")
    private LocalDate expiryDate;

    @ApiModelProperty("发证机构")
    private String issuingAuthority;

    @ApiModelProperty("证书URL")
    private String certificateUrl;

    @ApiModelProperty("是否提醒 0否 1是")
    private Integer isRemind;

    @ApiModelProperty("提醒天数")
    private Integer remindDays;

    @ApiModelProperty("状态 0无效 1有效")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
