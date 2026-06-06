package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.StandardMethodQuery;
import com.lims.detection.dto.StandardMethodSaveDTO;
import com.lims.detection.service.StandardMethodService;
import com.lims.detection.vo.StandardMethodDetailVO;
import com.lims.detection.vo.StandardMethodVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "标准方法库管理")
@RestController
@RequestMapping("/detection/standardMethod")
public class StandardMethodController {

    @Autowired
    private StandardMethodService standardMethodService;

    @ApiOperation("分页查询标准方法")
    @GetMapping("/page")
    public Result<PageResult<StandardMethodVO>> selectPage(StandardMethodQuery query) {
        return Result.success(standardMethodService.selectPage(query));
    }

    @ApiOperation("获取标准方法详情")
    @GetMapping("/{id}")
    public Result<StandardMethodDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(standardMethodService.getDetail(id));
    }

    @ApiOperation("新增标准方法")
    @PostMapping
    public Result<Void> saveMethod(@RequestBody @Validated StandardMethodSaveDTO dto) {
        standardMethodService.saveMethod(dto);
        return Result.success();
    }

    @ApiOperation("修改标准方法")
    @PutMapping
    public Result<Void> updateMethod(@RequestBody @Validated StandardMethodSaveDTO dto) {
        standardMethodService.updateMethod(dto);
        return Result.success();
    }

    @ApiOperation("删除标准方法")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMethod(@PathVariable Long id) {
        standardMethodService.deleteMethod(id);
        return Result.success();
    }

    @ApiOperation("启用标准方法")
    @PostMapping("/enable/{id}")
    public Result<Void> enableMethod(@PathVariable Long id) {
        standardMethodService.enableMethod(id);
        return Result.success();
    }

    @ApiOperation("停用标准方法")
    @PostMapping("/disable/{id}")
    public Result<Void> disableMethod(@PathVariable Long id) {
        standardMethodService.disableMethod(id);
        return Result.success();
    }

    @ApiOperation("设为当前版本")
    @PostMapping("/setCurrentVersion/{id}")
    public Result<Void> setCurrentVersion(@PathVariable Long id) {
        standardMethodService.setCurrentVersion(id);
        return Result.success();
    }

    @ApiOperation("获取所有当前版本方法列表")
    @GetMapping("/currentVersionList")
    public Result<List<StandardMethodVO>> getCurrentVersionList() {
        return Result.success(standardMethodService.getCurrentVersionList());
    }

    @ApiOperation("获取同一方法代码的所有历史版本")
    @GetMapping("/methodVersions/{methodCode}")
    public Result<List<StandardMethodVO>> getMethodVersions(@PathVariable String methodCode) {
        return Result.success(standardMethodService.getMethodVersions(methodCode));
    }
}
