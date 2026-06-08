package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Validated
@ApiModel("人员档案保存DTO")
public class PersonnelSaveDTO {

    @ApiModelProperty("人员ID（更新时传）")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("员工编号")
    @NotBlank(message = "员工编号不能为空")
    private String employeeNo;

    @ApiModelProperty("姓名")
    @NotBlank(message = "姓名不能为空")
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
    @NotNull(message = "部门ID不能为空")
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

    @ApiModelProperty("学历经历列表")
    private List<PersonnelEducationSaveDTO> educationList;

    @ApiModelProperty("职称列表")
    private List<PersonnelTitleSaveDTO> titleList;

    @ApiModelProperty("授权项目列表")
    private List<PersonnelAuthorizationSaveDTO> authorizationList;

    @ApiModelProperty("证书列表")
    private List<PersonnelCertificateSaveDTO> certificateList;
}
