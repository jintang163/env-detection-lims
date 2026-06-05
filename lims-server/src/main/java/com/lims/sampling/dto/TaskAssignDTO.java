package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("任务分配DTO")
public class TaskAssignDTO {

    @ApiModelProperty("采样计划ID")
    @NotNull(message = "采样计划ID不能为空")
    private Long planId;

    @ApiModelProperty("任务分配列表")
    @NotEmpty(message = "任务分配列表不能为空")
    private List<TaskAssignItem> taskList;

    @ApiModelProperty("计划采样日期")
    @NotNull(message = "计划采样日期不能为空")
    private LocalDate planSamplingDate;

    @Data
    @ApiModel("任务分配项")
    public static class TaskAssignItem {

        @ApiModelProperty("点位ID")
        @NotNull(message = "点位ID不能为空")
        private Long pointId;

        @ApiModelProperty("采样员ID")
        @NotNull(message = "采样员ID不能为空")
        private Long samplerId;

        @ApiModelProperty("采样员姓名")
        @NotNull(message = "采样员姓名不能为空")
        private String samplerName;
    }
}
