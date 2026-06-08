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
@TableName("per_personnel_title")
@ApiModel("人员职称表")
public class PerPersonnelTitle extends BaseEntity {

    @ApiModelProperty("人员档案ID")
    private Long personnelId;

    @ApiModelProperty("职称名称")
    private String titleName;

    @ApiModelProperty("职称级别")
    private String titleLevel;

    @ApiModelProperty("取得日期")
    private LocalDate acquireDate;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("授予机构")
    private String grantingAuthority;

    @ApiModelProperty("备注")
    private String remark;
}
