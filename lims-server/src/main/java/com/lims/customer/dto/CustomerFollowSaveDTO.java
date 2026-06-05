package com.lims.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("客户跟进记录新增/修改参数")
public class CustomerFollowSaveDTO {

    @ApiModelProperty("主键ID")
    private Long id;

    @NotNull(message = "客户ID不能为空")
    @ApiModelProperty(value = "客户ID", required = true)
    private Long customerId;

    @NotNull(message = "跟进类型不能为空")
    @ApiModelProperty(value = "跟进类型 1电话 2拜访 3邮件 4微信 5会议 6其他", required = true)
    private Integer followType;

    @ApiModelProperty("跟进方式 1主动跟进 2客户来访")
    private Integer followWay;

    @NotBlank(message = "跟进主题不能为空")
    @ApiModelProperty(value = "跟进主题", required = true)
    private String followTitle;

    @NotBlank(message = "跟进内容不能为空")
    @ApiModelProperty(value = "跟进内容", required = true)
    private String followContent;

    @ApiModelProperty("跟进结果 1无反馈 2意向明确 3需要考虑 4拒绝 5成交")
    private Integer followResult;

    @ApiModelProperty("跟进时间")
    private LocalDateTime followTime;

    @ApiModelProperty("跟进人ID")
    private Long followBy;

    @ApiModelProperty("跟进人姓名")
    private String followName;

    @ApiModelProperty("联系人")
    private String contactPerson;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("下次跟进时间")
    private LocalDate nextFollowTime;

    @ApiModelProperty("下次跟进内容")
    private String nextFollowContent;

    @ApiModelProperty("客户需求")
    private String customerDemand;

    @ApiModelProperty("附件ID")
    private Long attachmentId;

    @ApiModelProperty("附件路径")
    private String attachmentPath;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
