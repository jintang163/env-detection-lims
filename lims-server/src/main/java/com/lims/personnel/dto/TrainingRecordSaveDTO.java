package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Validated
@ApiModel("培训记录保存DTO")
public class TrainingRecordSaveDTO {

    @ApiModelProperty("ID（更新时传）")
    private Long id;

    @ApiModelProperty("人员ID")
    @NotNull(message = "人员ID不能为空")
    private Long personnelId;

    @ApiModelProperty("人员姓名")
    private String personnelName;

    @ApiModelProperty("培训类型")
    @NotBlank(message = "培训类型不能为空")
    private String trainingType;

    @ApiModelProperty("培训名称")
    @NotBlank(message = "培训名称不能为空")
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

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("证书到期日期")
    private LocalDate certificateExpiryDate;

    @ApiModelProperty("备注")
    private String remark;
}
