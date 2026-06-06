package com.lims.detection.dto;

import com.lims.common.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("标准方法查询DTO")
public class StandardMethodQuery extends PageQuery {

    @ApiModelProperty("方法编号")
    private String methodCode;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("标准类型 1国家标准 2行业标准 3企业标准")
    private String standardType;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("是否当前版本 0否 1是")
    private Integer isCurrent;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;
}
