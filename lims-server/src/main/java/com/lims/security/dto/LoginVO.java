package com.lims.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("登录返回信息")
public class LoginVO {

    @ApiModelProperty("JWT令牌")
    private String token;

    @ApiModelProperty("用户信息")
    private UserInfo userInfo;
}
