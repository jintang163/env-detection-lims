package com.lims.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lims.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_item_standard")
@ApiModel("项目标准关联表")
public class DictItemStandard extends BaseEntity {

    @ApiModelProperty("检测项目ID")
    private Long itemId;

    @ApiModelProperty("检测标准ID")
    private Long standardId;

    @ApiModelProperty("标准内容")
    private String standardContent;

    @ApiModelProperty("限值要求")
    private String limitValue;
}
