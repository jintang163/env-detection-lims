package com.lims.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户信息")
public class UserInfo {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;

    @ApiModelProperty("角色编码列表")
    private List<String> roles;

    @ApiModelProperty("权限列表")
    private List<String> permissions;
}
