package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Validated
@ApiModel("学历经历保存DTO")
public class PersonnelEducationSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("学历")
    @NotNull(message = "学历不能为空")
    private String education;

    @ApiModelProperty("学位")
    private String degree;

    @ApiModelProperty("专业")
    private String major;

    @ApiModelProperty("毕业院校")
    private String graduationSchool;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("是否全日制 0否 1是")
    private Integer isFullTime;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("备注")
    private String remark;
}
