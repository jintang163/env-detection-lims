package com.lims.quotation.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.quotation.dto.*;
import com.lims.quotation.service.QuotationService;
import com.lims.quotation.vo.QuotationDetailVO;
import com.lims.quotation.vo.QuotationPrintVO;
import com.lims.quotation.vo.QuotationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "报价单管理")
@RestController
@RequestMapping("/quotation")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @GetMapping("/page")
    @ApiOperation("分页查询报价单列表")
    public Result<PageResult<QuotationVO>> page(QuotationQuery query) {
        return Result.success(quotationService.page(query));
    }

    @PostMapping
    @ApiOperation("新增报价单")
    public Result<Long> save(@Valid @RequestBody QuotationSaveDTO dto) {
        return Result.success(quotationService.save(dto));
    }

    @PutMapping
    @ApiOperation("修改报价单")
    public Result<Void> update(@Valid @RequestBody QuotationSaveDTO dto) {
        quotationService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除报价单")
    public Result<Void> delete(@PathVariable Long id) {
        quotationService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取报价单详情")
    public Result<QuotationDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(quotationService.getDetail(id));
    }

    @PostMapping("/submit/{id}")
    @ApiOperation("提交审批")
    public Result<Void> submitApproval(@PathVariable Long id) {
        quotationService.submitApproval(id);
        return Result.success();
    }

    @PostMapping("/approval")
    @ApiOperation("报价单审批")
    public Result<Void> approval(@Valid @RequestBody QuotationApprovalDTO dto) {
        quotationService.approval(dto);
        return Result.success();
    }

    @PostMapping("/confirm/{id}")
    @ApiOperation("客户确认")
    public Result<Void> customerConfirm(@PathVariable Long id, @RequestParam String confirmPerson) {
        quotationService.customerConfirm(id, confirmPerson);
        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    @ApiOperation("报价单作废")
    public Result<Void> cancel(@PathVariable Long id) {
        quotationService.cancel(id);
        return Result.success();
    }

    @PostMapping("/convert")
    @ApiOperation("报价单转委托单")
    public Result<Long> convertToEntrust(@Valid @RequestBody ConvertToEntrustDTO dto) {
        return Result.success(quotationService.convertToEntrust(dto));
    }

    @GetMapping("/print/{id}")
    @ApiOperation("获取报价单打印数据")
    public Result<QuotationPrintVO> getPrintData(@PathVariable Long id) {
        return Result.success(quotationService.getPrintData(id));
    }
}
