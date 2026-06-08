package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("证书VO")
public class PersonnelCertificateVO {

    @ApiModelProperty("ID")
    private Long id;

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

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("到期状态：即将到期/已过期/正常")
    private String expiryStatus;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
