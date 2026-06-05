package com.lims.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_test_standard")
@ApiModel("检测标准表")
public class DictTestStandard extends BaseEntity {

    @ApiModelProperty("标准编号")
    private String standardNo;

    @ApiModelProperty("标准名称")
    private String standardName;

    @ApiModelProperty("标准类型")
    private String standardType;

    @ApiModelProperty("发布日期")
    private LocalDate publishDate;

    @ApiModelProperty("实施日期")
    private LocalDate implementDate;

    @ApiModelProperty("是否有效 0无效 1有效")
    private Integer valid;

    @ApiModelProperty("标准说明")
    private String description;

    @ApiModelProperty("标准文件地址")
    private String fileUrl;
}
