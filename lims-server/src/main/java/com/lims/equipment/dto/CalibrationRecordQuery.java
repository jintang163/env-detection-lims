package com.lims.equipment.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("校准记录查询DTO")
public class CalibrationRecordQuery extends PageQuery {

    @ApiModelProperty("设备ID")
    private Long equipmentId;

    @ApiModelProperty("设备编号")
    private String equipmentNo;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("校准类型")
    private Integer calibrationType;

    @ApiModelProperty("校准结果")
    private Integer result;

    @ApiModelProperty("校准单位")
    private String calibrationUnit;

    @ApiModelProperty("证书编号")
    private String certificateNo;

    @ApiModelProperty("校准开始日期")
    private LocalDate calibrationDateStart;

    @ApiModelProperty("校准结束日期")
    private LocalDate calibrationDateEnd;
}
