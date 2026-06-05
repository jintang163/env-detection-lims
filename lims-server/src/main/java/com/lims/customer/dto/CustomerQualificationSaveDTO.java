package com.lims.customer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("客户资质文件新增/修改参数")
public class CustomerQualificationSaveDTO {

    @ApiModelProperty("主键ID")
    private Long id;

    @NotNull(message = "客户ID不能为空")
    @ApiModelProperty(value = "客户ID", required = true)
    private Long customerId;

    @NotNull(message = "资质类型不能为空")
    @ApiModelProperty(value = "资质类型 1营业执照 2资质证书 3认证证书 4其他", required = true)
    private Integer qualType;

    @NotBlank(message = "资质名称不能为空")
    @ApiModelProperty(value = "资质名称", required = true)
    private String qualName;

    @ApiModelProperty("资质编号")
    private String qualNo;

    @ApiModelProperty("发证机构")
    private String issuingAuthority;

    @ApiModelProperty("发证日期")
    private LocalDate issueDate;

    @ApiModelProperty("有效期至")
    private LocalDate validTo;

    @ApiModelProperty("是否长期有效 0否 1是")
    private Integer isLongTerm;

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
