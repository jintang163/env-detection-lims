package com.lims.entrust.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.entrust.dto.*;
import com.lims.entrust.service.EntrustService;
import com.lims.entrust.vo.EntrustDetailVO;
import com.lims.entrust.vo.EntrustVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "委托单管理")
@RestController
@RequestMapping("/api/entrust")
public class EntrustController {

    @Autowired
    private EntrustService entrustService;

    @GetMapping("/page")
    @ApiOperation("分页查询委托单列表")
    public Result<PageResult<EntrustVO>> page(EntrustQuery query) {
        return Result.success(entrustService.page(query));
    }

    @PostMapping
    @ApiOperation("创建委托单")
    public Result<Long> create(@Valid @RequestBody EntrustSaveDTO dto) {
        return Result.success(entrustService.create(dto));
    }

    @PutMapping
    @ApiOperation("修改委托单")
    public Result<Void> update(@Valid @RequestBody EntrustSaveDTO dto) {
        entrustService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除委托单")
    public Result<Void> delete(@PathVariable Long id) {
        entrustService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取委托单详情")
    public Result<EntrustDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(entrustService.getDetail(id));
    }

    @PostMapping("/submit/{id}")
    @ApiOperation("提交受理")
    public Result<Void> submit(@PathVariable Long id) {
        entrustService.submit(id);
        return Result.success();
    }

    @PostMapping("/review")
    @ApiOperation("合同评审")
    public Result<Void> review(@Valid @RequestBody EntrustReviewDTO dto) {
        entrustService.review(dto);
        return Result.success();
    }

    @PostMapping("/changeStatus")
    @ApiOperation("状态流转")
    public Result<Void> changeStatus(@Valid @RequestBody EntrustStatusChangeDTO dto) {
        entrustService.changeStatus(dto);
        return Result.success();
    }

    @PostMapping("/urgent/{id}")
    @ApiOperation("加急处理")
    public Result<Void> urgent(@PathVariable Long id) {
        entrustService.urgent(id);
        return Result.success();
    }

    @PostMapping("/adjust")
    @ApiOperation("调账处理")
    public Result<Void> adjust(@Valid @RequestBody AdjustDTO dto) {
        entrustService.adjust(dto);
        return Result.success();
    }
}
