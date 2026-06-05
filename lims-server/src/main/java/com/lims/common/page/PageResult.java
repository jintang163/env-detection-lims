package com.lims.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页结果")
public class PageResult<T> {

    @ApiModelProperty("当前页数据")
    private List<T> list;

    @ApiModelProperty("总条数")
    private Long total;

    @ApiModelProperty("页码")
    private Long pageNum;

    @ApiModelProperty("每页条数")
    private Long pageSize;

    @ApiModelProperty("总页数")
    private Long pages;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setList(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    public static <T> PageResult<T> of(List<T> list, Long total, Long pageNum, Long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        return result;
    }
}
