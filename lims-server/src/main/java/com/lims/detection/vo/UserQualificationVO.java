package com.lims.detection.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("人员资质VO")
public class UserQualificationVO {

    @ApiModelProperty("资质ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户姓名")
    private String userName;

    @ApiModelProperty("资质类型")
    private String qualificationType;

    @ApiModelProperty("资质名称")
    private String qualificationName;

    @ApiModelProperty("资质证书编号")
    private String qualificationNo;

    @ApiModelProperty("发证日期")
    private LocalDate issueDate;

    @ApiModelProperty("有效期至")
    private LocalDate expiryDate;

    @ApiModelProperty("发证机构")
    private String issuer;

    @ApiModelProperty("可检测项目ID列表")
    private List<Long> testItemIdList;

    @ApiModelProperty("可检测项目名称列表")
    private List<String> testItemNameList;

    @ApiModelProperty("状态 0无效 1有效")
    private Integer status;

    @ApiModelProperty("状态名称")
    private String statusName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
