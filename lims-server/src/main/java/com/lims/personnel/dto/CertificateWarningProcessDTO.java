package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
@ApiModel("证书预警处理DTO")
public class CertificateWarningProcessDTO {

    @ApiModelProperty("预警ID")
    @NotNull(message = "预警ID不能为空")
    private Long warningId;

    @ApiModelProperty("处理结果")
    @NotBlank(message = "处理结果不能为空")
    private String processResult;
}
