package com.lims.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cus_customer_qualification")
@ApiModel("客户资质文件表")
public class CusCustomerQualification extends BaseEntity {

    @ApiModelProperty("客户ID")
    private Long customerId;

    @ApiModelProperty("资质类型 1营业执照 2资质证书 3认证证书 4其他")
    private Integer qualType;

    @ApiModelProperty("资质名称")
    private String qualName;

    @ApiModelProperty("资质编号")
    private String qualNo;

    @ApiModelProperty("发证机构")
    private String issuingAuthority;

    @ApiModelProperty("发证日期")
    private LocalDate issueDate;

    @ApiModelProperty("有效期至")
    private LocalDate validTo;

    @ApiModelProperty("是否长期有效 0否 1是")
    private Integer isLongTerm;

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("审核状态 0待审核 1已通过 2已驳回")
    private Integer auditStatus;

    @ApiModelProperty("审核意见")
    private String auditOpinion;

    @ApiModelProperty("审核人")
    private Long auditBy;

    @ApiModelProperty("审核时间")
    private LocalDate auditTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0禁用 1启用")
    private Integer status;
}
