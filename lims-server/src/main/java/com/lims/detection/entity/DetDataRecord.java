package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_data_record")
@ApiModel("检测数据表")
public class DetDataRecord extends BaseEntity {

    @ApiModelProperty("记录编号")
    private String recordNo;

    @ApiModelProperty("检测任务ID")
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

    @ApiModelProperty("使用设备ID列表(JSON格式)")
    private String equipmentIds;

    @ApiModelProperty("使用设备名称列表(JSON格式)")
    private String equipmentNames;

    @ApiModelProperty("标准物质ID列表(JSON格式)")
    private String standardSubstanceIds;

    @ApiModelProperty("标准物质名称列表(JSON格式)")
    private String standardSubstanceNames;

    @ApiModelProperty("检测日期")
    private LocalDate testDate;

    @ApiModelProperty("检测时间")
    private LocalTime testTime;

    @ApiModelProperty("检测环境条件(JSON格式: 温度、湿度、气压等)")
    private String testEnv;

    @ApiModelProperty("数据来源 1手动录入 2仪器导入 3文件导入")
    private Integer dataSource;

    @ApiModelProperty("导入文件地址")
    private String importFileUrl;

    @ApiModelProperty("导入文件名称")
    private String importFileName;

    @ApiModelProperty("录入人ID")
    private Long entryUserId;

    @ApiModelProperty("录入人姓名")
    private String entryUserName;

    @ApiModelProperty("录入时间")
    private LocalDateTime entryTime;

    @ApiModelProperty("数据哈希值（防篡改）")
    private String dataHash;

    @ApiModelProperty("一级审核人ID")
    private Long firstReviewUserId;

    @ApiModelProperty("一级审核人姓名")
    private String firstReviewUserName;

    @ApiModelProperty("一级审核时间")
    private LocalDateTime firstReviewTime;

    @ApiModelProperty("一级审核意见")
    private String firstReviewOpinion;

    @ApiModelProperty("一级审核结果 1通过 2驳回")
    private Integer firstReviewResult;

    @ApiModelProperty("二级审核人ID")
    private Long secondReviewUserId;

    @ApiModelProperty("二级审核人姓名")
    private String secondReviewUserName;

    @ApiModelProperty("二级审核时间")
    private LocalDateTime secondReviewTime;

    @ApiModelProperty("二级审核意见")
    private String secondReviewOpinion;

    @ApiModelProperty("二级审核结果 1通过 2驳回")
    private Integer secondReviewResult;

    @ApiModelProperty("审核状态 0待审核 1一级审核中 2一级审核通过 3一级审核驳回 4二级审核中 5二级审核通过 6二级审核驳回")
    private Integer reviewStatus;

    @ApiModelProperty("是否有OOS 0否 1是")
    private Integer hasOos;

    @ApiModelProperty("OOS数量")
    private Integer oosCount;

    @ApiModelProperty("状态 0草稿 1已录入 2审核中 3已完成 4已退回")
    private Integer status;

    @Version
    @ApiModelProperty("版本号（乐观锁）")
    private Integer version;

    @ApiModelProperty("备注")
    private String remark;
}
