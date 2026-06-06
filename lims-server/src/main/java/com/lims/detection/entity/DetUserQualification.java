package com.lims.detection.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("det_user_qualification")
@ApiModel("人员资质表")
public class DetUserQualification extends BaseEntity {

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

    @ApiModelProperty("可检测项目ID列表(JSON格式)")
    private String testItemIds;

    @ApiModelProperty("可检测项目名称列表(JSON格式)")
    private String testItemNames;

    @ApiModelProperty("状态 0无效 1有效")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;
}
