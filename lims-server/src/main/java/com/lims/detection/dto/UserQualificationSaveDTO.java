package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("人员资质保存DTO")
public class UserQualificationSaveDTO {

    @ApiModelProperty("资质ID（更新时传）")
    private Long id;

    @ApiModelProperty("用户ID")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("资质类型")
    @NotBlank(message = "资质类型不能为空")
    private String qualificationType;

    @ApiModelProperty("资质名称")
    @NotBlank(message = "资质名称不能为空")
    private String qualificationName;

    @ApiModelProperty("资质证书编号")
    private String qualificationNo;

    @ApiModelProperty("发证日期")
    private LocalDate issueDate;

    @ApiModelProperty("有效期至")
    private LocalDate expiryDate;

    @ApiModelProperty("发证机构")
    private String issuer;

    @ApiModelProperty("可检测项目ID列表")
    private List<Long> testItemIdList;

    @ApiModelProperty("可检测项目名称列表")
    private List<String> testItemNameList;

    @ApiModelProperty("状态 0无效 1有效")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
