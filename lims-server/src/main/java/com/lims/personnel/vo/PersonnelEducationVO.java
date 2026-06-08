package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("学历经历VO")
public class PersonnelEducationVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("学历")
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

    @ApiModelProperty("是否全日制名称")
    private String isFullTimeName;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
