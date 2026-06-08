package com.lims.environment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel("预警处理DTO")
public class EnvWarningHandleDTO {

    @ApiModelProperty("主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @ApiModelProperty("状态")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @ApiModelProperty("处理人ID")
    private Long handlerId;

    @ApiModelProperty("处理人名称")
    private String handlerName;

    @ApiModelProperty("处理时间")
    private LocalDateTime handleTime;

    @ApiModelProperty("处理结果")
    @NotBlank(message = "处理结果不能为空")
    private String handleResult;

    @ApiModelProperty("备注")
    private String remark;
}
