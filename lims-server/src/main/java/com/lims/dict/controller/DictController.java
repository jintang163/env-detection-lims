package com.lims.dict.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.common.result.Result;
import com.lims.dict.entity.DictTestItem;
import com.lims.dict.entity.DictTestStandard;
import com.lims.dict.mapper.DictTestItemMapper;
import com.lims.dict.mapper.DictTestStandardMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "字典管理")
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictTestItemMapper dictTestItemMapper;

    @Autowired
    private DictTestStandardMapper dictTestStandardMapper;

    @GetMapping("/testItem/list")
    @ApiOperation("获取检测项目列表")
    public Result<List<DictTestItem>> getTestItemList(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<DictTestItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictTestItem::getStatus, 1);
        if (category != null) {
            wrapper.eq(DictTestItem::getItemCategory, category);
        }
        if (keyword != null) {
            wrapper.and(w -> w.like(DictTestItem::getItemName, keyword)
                    .or().like(DictTestItem::getItemCode, keyword));
        }
        wrapper.orderByAsc(DictTestItem::getItemCode);
        return Result.success(dictTestItemMapper.selectList(wrapper));
    }

    @GetMapping("/testItem/{id}")
    @ApiOperation("获取检测项目详情")
    public Result<DictTestItem> getTestItem(@PathVariable Long id) {
        return Result.success(dictTestItemMapper.selectById(id));
    }

    @GetMapping("/testStandard/list")
    @ApiOperation("获取检测标准列表")
    public Result<List<DictTestStandard>> getTestStandardList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<DictTestStandard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictTestStandard::getValid, 1);
        if (type != null) {
            wrapper.eq(DictTestStandard::getStandardType, type);
        }
        if (keyword != null) {
            wrapper.and(w -> w.like(DictTestStandard::getStandardName, keyword)
                    .or().like(DictTestStandard::getStandardNo, keyword));
        }
        wrapper.orderByDesc(DictTestStandard::getImplementDate);
        return Result.success(dictTestStandardMapper.selectList(wrapper));
    }

    @GetMapping("/testStandard/byItem/{itemId}")
    @ApiOperation("根据检测项目获取关联标准")
    public Result<List<DictTestStandard>> getStandardByItemId(@PathVariable Long itemId) {
        return Result.success(dictTestStandardMapper.selectByItemId(itemId));
    }
}
