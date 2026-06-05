package com.lims.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
@ApiModel("角色实体")
public class SysRole extends BaseEntity {

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色编码")
    private String roleCode;

    @ApiModelProperty("显示顺序")
    private Integer roleSort;

    @ApiModelProperty("状态 0停用 1正常")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
