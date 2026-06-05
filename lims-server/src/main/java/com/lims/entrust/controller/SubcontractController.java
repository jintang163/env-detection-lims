package com.lims.entrust.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.entrust.dto.SubcontractSaveDTO;
import com.lims.entrust.service.SubcontractService;
import com.lims.entrust.vo.SubcontractVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "分包管理")
@RestController
@RequestMapping("/api/subcontract")
public class SubcontractController {

    @Autowired
    private SubcontractService subcontractService;

    @GetMapping("/page")
    @ApiOperation("分页查询分包列表")
    public Result<PageResult<SubcontractVO>> page(
            @RequestParam(required = false) Long entrustId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(subcontractService.page(entrustId, pageNum, pageSize));
    }

    @PostMapping
    @ApiOperation("创建分包")
    public Result<Long> create(@Valid @RequestBody SubcontractSaveDTO dto) {
        return Result.success(subcontractService.create(dto));
    }

    @PutMapping("/status")
    @ApiOperation("更新分包状态")
    public Result<Void> updateStatus(
            @RequestParam Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        subcontractService.updateStatus(id, status, remark);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取分包详情")
    public Result<SubcontractVO> getDetail(@PathVariable Long id) {
        return Result.success(subcontractService.getDetail(id));
    }
}
