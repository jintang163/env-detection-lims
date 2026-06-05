package com.lims.sampling.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("采样计划保存DTO")
public class SamplingPlanSaveDTO {

    @ApiModelProperty("主键ID（修改时传）")
    private Long id;

    @ApiModelProperty("计划名称")
    @NotBlank(message = "计划名称不能为空")
    private String planName;

    @ApiModelProperty("关联委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("计划采样日期")
    @NotNull(message = "计划采样日期不能为空")
    private LocalDate planDate;

    @ApiModelProperty("采样类型")
    @NotBlank(message = "采样类型不能为空")
    private String samplingType;

    @ApiModelProperty("采样员ID列表")
    private List<Long> samplerIdList;

    @ApiModelProperty("采样员姓名列表")
    private List<String> samplerNameList;

    @ApiModelProperty("设备ID列表")
    private List<Long> equipmentIdList;

    @ApiModelProperty("设备名称列表")
    private List<String> equipmentNameList;

    @ApiModelProperty("容器ID列表")
    private List<Long> containerIdList;

    @ApiModelProperty("容器名称列表")
    private List<String> containerNameList;

    @ApiModelProperty("点位清单")
    private List<SamplingPointSaveDTO> pointList;

    @ApiModelProperty("预计样品数量")
    private Integer sampleCount;

    @ApiModelProperty("备注")
    private String remark;
}
