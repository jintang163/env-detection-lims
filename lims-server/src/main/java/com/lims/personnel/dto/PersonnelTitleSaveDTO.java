package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Validated
@ApiModel("职称保存DTO")
public class PersonnelTitleSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("职称名称")
    @NotBlank(message = "职称名称不能为空")
    private String titleName;

    @ApiModelProperty("职称级别")
    private String titleLevel;

    @ApiModelProperty("取得日期")
    private LocalDate acquireDate;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("授予机构")
    private String grantingAuthority;

    @ApiModelProperty("备注")
    private String remark;
}
