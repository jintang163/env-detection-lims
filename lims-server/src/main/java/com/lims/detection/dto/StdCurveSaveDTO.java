package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("标准曲线保存")
public class StdCurveSaveDTO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("曲线名称")
    private String curveName;

    @ApiModelProperty("项目ID")
    private Long itemId;

    @ApiModelProperty("项目编码")
    private String itemCode;

    @ApiModelProperty("项目名称")
    private String itemName;

    @ApiModelProperty("方法ID")
    private Long methodId;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("仪器ID")
    private Long instrumentId;

    @ApiModelProperty("仪器名称")
    private String instrumentName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("拟合类型")
    private String fitType;

    @ApiModelProperty("权重类型")
    private String weightType;

    @ApiModelProperty("校准日期")
    private LocalDateTime calibrationDate;

    @ApiModelProperty("有效天数")
    private Integer validDays;

    @ApiModelProperty("到期日期")
    private LocalDateTime expireDate;

    @ApiModelProperty("配制人")
    private String preparedBy;

    @ApiModelProperty("复核人")
    private String verifiedBy;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("曲线点列表")
    private List<StdCurvePointDTO> curvePoints;
}
