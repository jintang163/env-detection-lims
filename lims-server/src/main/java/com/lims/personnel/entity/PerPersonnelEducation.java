package com.lims.personnel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("per_personnel_education")
@ApiModel("人员学历经历表")
public class PerPersonnelEducation extends BaseEntity {

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

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("备注")
    private String remark;
}
