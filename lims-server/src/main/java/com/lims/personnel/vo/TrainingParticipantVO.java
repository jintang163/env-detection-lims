package com.lims.personnel.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("培训人员VO")
public class TrainingParticipantVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("培训计划ID")
    private Long trainingPlanId;

    @ApiModelProperty("人员ID")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("签到时间")
    private LocalDateTime signInTime;

    @ApiModelProperty("签到状态：0未签到 1已签到 2迟到 3早退 4缺勤")
    private Integer signInStatus;

    @ApiModelProperty("签到状态名称")
    private String signInStatusName;

    @ApiModelProperty("评估分数")
    private BigDecimal evaluationScore;

    @ApiModelProperty("评估结果：0不合格 1合格 2优秀")
    private Integer evaluationResult;

    @ApiModelProperty("评估结果名称")
    private String evaluationResultName;

    @ApiModelProperty("是否颁发证书：0否 1是")
    private Integer certificateFlag;

    @ApiModelProperty("是否颁发证书名称")
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
