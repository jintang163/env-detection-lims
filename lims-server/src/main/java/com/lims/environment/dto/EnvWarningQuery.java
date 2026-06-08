package com.lims.environment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("预警查询DTO")
public class EnvWarningQuery extends PageQuery {

    @ApiModelProperty("监控点")
    private String monitorPoint;

    @ApiModelProperty("监控类型")
    private Integer monitorType;

    @ApiModelProperty("预警开始时间")
    private LocalDateTime warnTimeStart;

    @ApiModelProperty("预警结束时间")
    private LocalDateTime warnTimeEnd;

    @ApiModelProperty("预警级别")
    private Integer warnLevel;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("处理人ID")
    private Long handlerId;
}
