package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("人员统计VO")
public class PersonnelStatsVO {

    @ApiModelProperty("总人数")
    private Integer totalCount;

    @ApiModelProperty("在职人数")
    private Integer onJobCount;

    @ApiModelProperty("离职人数")
    private Integer offJobCount;

    @ApiModelProperty("休假人数")
    private Integer vacationCount;

    @ApiModelProperty("即将到期证书数")
    private Integer expiringCertificatesCount;

    @ApiModelProperty("已过期证书数")
    private Integer expiredCertificatesCount;
}
