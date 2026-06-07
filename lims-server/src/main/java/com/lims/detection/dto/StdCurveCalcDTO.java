package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("标准曲线计算")
public class StdCurveCalcDTO {

    @ApiModelProperty("拟合类型")
    private String fitType;

    @ApiModelProperty("权重类型")
    private String weightType;

    @ApiModelProperty("曲线点列表")
    private List<StdCurvePointDTO> points;
}
