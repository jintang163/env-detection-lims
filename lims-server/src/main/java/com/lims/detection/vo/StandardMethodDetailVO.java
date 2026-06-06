package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("标准方法详情VO")
public class StandardMethodDetailVO {

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

    @ApiModelProperty("检测项目ID列表")
    private List<Long> testItemIdList;

    @ApiModelProperty("检测项目名称列表")
    private List<String> testItemNameList;

    @ApiModelProperty("所需设备ID列表")
    private List<Long> equipmentIdList;

    @ApiModelProperty("所需设备名称列表")
    private List<String> equipmentNameList;

    @ApiModelProperty("检出限")
    private BigDecimal detectionLimit;

    @ApiModelProperty("定量限")
    private BigDecimal quantitationLimit;

    @ApiModelProperty("精密度要求(%)")
    private BigDecimal precisionRequirement;

    @ApiModelProperty("准确度要求(%)")
    private BigDecimal accuracyRequirement;

    @ApiModelProperty("检测周期(天)")
    private Integer detectionCycle;

    @ApiModelProperty("操作步骤")
    private String operationSteps;

    @ApiModelProperty("计算公式")
    private String calculationFormula;

    @ApiModelProperty("质量控制要求")
    private String qualityControl;

    @ApiModelProperty("标准文件附件")
    private String fileUrl;

    @ApiModelProperty("是否当前版本 0否 1是")
    private Integer isCurrent;

    @ApiModelProperty("状态 0停用 1启用")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("版本历史列表")
    private List<StandardMethodVersionVO> versionHistory;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
