package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("标准方法列表VO")
public class StandardMethodVO {

    @ApiModelProperty("标准方法ID")
    private Long id;

    @ApiModelProperty("方法编号")
    private String methodCode;

    @ApiModelProperty("方法名称")
    private String methodName;

    @ApiModelProperty("标准类型 1国家标准 2行业标准 3企业标准")
    private String standardType;

    @ApiModelProperty("标准类型名称")
    private String standardTypeName;

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("发布日期")
    private LocalDate publishDate;

    @ApiModelProperty("实施日期")
    private LocalDate implementDate;

    @ApiModelProperty("适用范围")
    private String applicableScope;

    @ApiModelProperty("检出限")
    private BigDecimal detectionLimit;

    @ApiModelProperty("定量限")
    private BigDecimal quantitationLimit;

    @ApiModelProperty("检测周期(天)")
    private Integer detectionCycle;

    @ApiModelProperty("是否当前版本 0否 1是")
    private Integer isCurrent;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
