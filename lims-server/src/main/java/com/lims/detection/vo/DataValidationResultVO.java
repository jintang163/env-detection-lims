package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("数据校验结果VO")
public class DataValidationResultVO {

    @ApiModelProperty("是否校验通过")
    private Boolean valid = true;

    @ApiModelProperty("错误信息列表")
    private List<String> errors = new ArrayList<>();

    @ApiModelProperty("警告信息列表")
    private List<String> warnings = new ArrayList<>();

    @ApiModelProperty("OOS信息列表")
    private List<String> oosInfos = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public void addOosInfo(String oosInfo) {
        this.oosInfos.add(oosInfo);
    }
}
