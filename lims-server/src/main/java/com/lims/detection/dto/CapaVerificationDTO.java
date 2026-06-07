package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("CAPA验证DTO")
public class CapaVerificationDTO {

    @ApiModelProperty("CAPA ID")
    private Long id;

    @ApiModelProperty("验证结果")
    private String verificationResult;

    @ApiModelProperty("验证证据")
    private String verificationEvidence;
}
