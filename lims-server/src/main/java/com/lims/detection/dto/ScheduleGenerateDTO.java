package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("智能排程生成DTO")
public class ScheduleGenerateDTO {

    @ApiModelProperty("排程日期")
    private LocalDate scheduleDate;

    @ApiModelProperty("待排程任务ID列表（不传则自动获取待分配任务）")
    private List<Long> taskIdList;

    @ApiModelProperty("排程策略 1均衡分配 2效率优先 3优先级优先")
    private Integer strategy = 1;

    @ApiModelProperty("是否考虑设备占用 0否 1是")
    private Integer considerEquipment = 1;

    @ApiModelProperty("是否考虑人员资质 0否 1是")
    private Integer considerQualification = 1;

    @ApiModelProperty("生成方案数量")
    private Integer planCount = 3;
}
