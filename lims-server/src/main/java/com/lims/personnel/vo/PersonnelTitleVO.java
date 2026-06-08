package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("职称VO")
public class PersonnelTitleVO {

    @ApiModelProperty("ID")
    private Long id;

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
