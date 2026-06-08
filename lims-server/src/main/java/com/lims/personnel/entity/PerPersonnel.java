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
@TableName("per_personnel")
@ApiModel("人员档案表")
public class PerPersonnel extends BaseEntity {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("员工编号")
    private String employeeNo;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("出生日期")
    private LocalDate birthDate;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("职位")
    private String position;

    @ApiModelProperty("岗位")
    private String post;

    @ApiModelProperty("职称")
    private String title;

    @ApiModelProperty("学历")
    private String education;

    @ApiModelProperty("专业")
    private String major;

    @ApiModelProperty("毕业院校")
    private String graduationSchool;

    @ApiModelProperty("毕业日期")
    private LocalDate graduationDate;

    @ApiModelProperty("参加工作日期")
    private LocalDate workDate;

    @ApiModelProperty("入职日期")
    private LocalDate entryDate;

    @ApiModelProperty("状态 0离职 1在职")
    private Integer status;

    @ApiModelProperty("头像URL")
    private String avatarUrl;

    @ApiModelProperty("住址")
    private String address;

    @ApiModelProperty("紧急联系人")
    private String emergencyContact;

    @ApiModelProperty("紧急联系电话")
    private String emergencyPhone;

    @ApiModelProperty("备注")
    private String remark;
}
