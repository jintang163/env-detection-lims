package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Validated
@ApiModel("证书保存DTO")
public class PersonnelCertificateSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("人员档案ID")
    @NotNull(message = "人员档案ID不能为空")
    private Long personnelId;

    @ApiModelProperty("证书类型")
    @NotBlank(message = "证书类型不能为空")
    private String certificateType;

    @ApiModelProperty("证书名称")
    @NotBlank(message = "证书名称不能为空")
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
