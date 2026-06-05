package com.lims.common.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页查询参数")
public class PageQuery {

    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;

    @ApiModelProperty("排序字段")
    private String orderBy;

    @ApiModelProperty("排序方式 asc/desc")
    private String orderType = "desc";
}
