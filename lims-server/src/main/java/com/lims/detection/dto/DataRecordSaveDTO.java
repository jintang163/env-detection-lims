package com.lims.detection.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@ApiModel("检测数据保存DTO")
public class DataRecordSaveDTO {

    @ApiModelProperty("记录ID（更新时传）")
    private Long id;

    @ApiModelProperty("检测任务ID")
    @NotNull(message = "检测任务ID不能为空")
    private Long taskId;

    @ApiModelProperty("任务编号")
    private String taskNo;

    @ApiModelProperty("样品ID")
    private Long sampleId;

    @ApiModelProperty("样品编号")
    private String sampleNo;

    @ApiModelProperty("样品名称")
    private String sampleName;

    @ApiModelProperty("委托单ID")
    private Long entrustId;

    @ApiModelProperty("委托单编号")
    private String entrustNo;

    @ApiModelProperty("标准方法ID")
    private Long methodId;

    @ApiModelProperty("标准方法编号")
    private String methodCode;

    @ApiModelProperty("标准方法名称")
    private String methodName;

    @ApiModelProperty("使用设备ID列表")
    private List<Long> equipmentIdList;

    @ApiModelProperty("使用设备名称列表")
    private List<String> equipmentNameList;

    @ApiModelProperty("标准物质ID列表")
    private List<Long> standardSubstanceIdList;

    @ApiModelProperty("标准物质名称列表")
    private List<String> standardSubstanceNameList;

    @ApiModelProperty("检测日期")
    private LocalDate testDate;

    @ApiModelProperty("检测时间")
    private LocalTime testTime;

    @ApiModelProperty("检测环境条件(JSON格式)")
    private String testEnv;

    @ApiModelProperty("数据来源 1手动录入 2仪器导入 3文件导入")
    private Integer dataSource;

    @ApiModelProperty("导入文件地址")
    private String importFileUrl;

    @ApiModelProperty("导入文件名称")
    private String importFileName;

    @ApiModelProperty("检测数据明细")
    @Valid
    private List<DataRecordItemSaveDTO> itemList;

    @ApiModelProperty("备注")
    private String remark;
}
