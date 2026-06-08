package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("证书预警VO")
public class CertificateWarningVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("人员ID")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("证书ID")
    private Long certificateId;

    @ApiModelProperty("证书类型")
    private String certificateType;

    @ApiModelProperty("证书名称")
    private String certificateName;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("到期日期")
    private LocalDate expiryDate;

    @ApiModelProperty("预警类型：1即将到期 2已过期")
    private Integer warningType;

    @ApiModelProperty("预警类型名称")
    private String warningTypeName;

    @ApiModelProperty("预警天数")
    private Integer warningDays;

    @ApiModelProperty("预警状态：0未处理 1已处理 2已忽略")
    private Integer warningStatus;

    @ApiModelProperty("预警状态名称")
    private String warningStatusName;

    @ApiModelProperty("处理结果")
    private String processResult;

    @ApiModelProperty("处理时间")
    private LocalDateTime processTime;

    @ApiModelProperty("处理人ID")
    private Long processUserId;

    @ApiModelProperty("处理人姓名")
    private String processUserName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
