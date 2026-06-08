package com.lims.personnel.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Validated
@ApiModel("培训签到DTO")
public class TrainingSignInDTO {

    @ApiModelProperty("培训人员ID")
    @NotNull(message = "培训人员ID不能为空")
    private Long participantId;

    @ApiModelProperty("签到时间")
    private LocalDateTime signInTime;

    @ApiModelProperty("签到状态：0未签到 1已签到 2迟到 3早退 4缺勤")
    @NotNull(message = "签到状态不能为空")
    private Integer signInStatus;
}
