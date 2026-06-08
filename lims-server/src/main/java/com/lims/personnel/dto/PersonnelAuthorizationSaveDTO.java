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
@ApiModel("授权项目保存DTO")
public class PersonnelAuthorizationSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("人员档案ID")
    @NotNull(message = "人员档案ID不能为空")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("授权类型")
    @NotBlank(message = "授权类型不能为空")
    private String authorizationType;

    @ApiModelProperty("项目ID")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("标准ID")
    private Long standardId;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("标准名称")
    private String standardName;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("授权日期")
    private LocalDate authorizeDate;

    @ApiModelProperty("有效期至")
    private LocalDate expiryDate;

    @ApiModelProperty("状态 0无效 1有效")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
