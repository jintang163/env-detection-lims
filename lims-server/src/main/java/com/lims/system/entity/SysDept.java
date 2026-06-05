package com.lims.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
@ApiModel("部门实体")
public class SysDept extends BaseEntity {

    @ApiModelProperty("父部门ID")
    private Long parentId;

    @ApiModelProperty("祖级列表")
    private String ancestors;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("负责人")
    private String leader;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("部门状态 0停用 1正常")
    private Integer status;
}
