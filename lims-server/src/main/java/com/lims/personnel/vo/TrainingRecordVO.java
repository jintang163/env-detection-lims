package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("培训记录VO")
public class TrainingRecordVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("人员ID")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("培训类型")
    private String trainingType;

    @ApiModelProperty("培训名称")
    private String trainingName;

    @ApiModelProperty("培训内容")
    private String trainingContent;

    @ApiModelProperty("培训日期")
    private LocalDate trainingDate;

    @ApiModelProperty("培训时长(小时)")
    private BigDecimal trainingHours;

    @ApiModelProperty("培训机构")
    private String trainingOrganization;

    @ApiModelProperty("是否获得证书：0否 1是")
    private Integer certificateFlag;

    @ApiModelProperty("是否获得证书名称")
    private String certificateFlagName;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("证书到期日期")
    private LocalDate certificateExpiryDate;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
