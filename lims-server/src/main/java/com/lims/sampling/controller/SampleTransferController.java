package com.lims.sampling.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.sampling.dto.SampleTransferQuery;
import com.lims.sampling.dto.SampleTransferSaveDTO;
import com.lims.sampling.entity.SmpSampleTransfer;
import com.lims.sampling.service.SampleTransferService;
import com.lims.sampling.vo.SampleTransferVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "样品交接管理")
@RestController
@RequestMapping("/sampling/transfer")
public class SampleTransferController {

    @Autowired
    private SampleTransferService sampleTransferService;

    @ApiOperation("分页查询样品交接记录")
    @GetMapping("/page")
    public Result<PageResult<SampleTransferVO>> selectPage(SampleTransferQuery query) {
        return Result.success(sampleTransferService.selectPage(query));
    }

    @ApiOperation("创建样品交接单")
    @PostMapping
    public Result<Void> createTransfer(@RequestBody @Validated SampleTransferSaveDTO dto) {
        sampleTransferService.createTransfer(dto);
        return Result.success();
    }

    @ApiOperation("确认样品交接")
    @PostMapping("/confirm/{id}")
    public Result<Void> confirmTransfer(@PathVariable Long id) {
        sampleTransferService.confirmTransfer(id);
        return Result.success();
    }

    @ApiOperation("驳回样品交接")
    @PostMapping("/reject/{id}")
    public Result<Void> rejectTransfer(@PathVariable Long id, @RequestParam String reason) {
        sampleTransferService.rejectTransfer(id, reason);
        return Result.success();
    }

    @ApiOperation("获取交接单详情")
    @GetMapping("/{id}")
    public Result<SmpSampleTransfer> getTransferDetail(@PathVariable Long id) {
        return Result.success(sampleTransferService.getTransferDetail(id));
    }
}
