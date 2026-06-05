package com.lims.contract.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.contract.dto.ContractApprovalDTO;
import com.lims.contract.dto.ContractChangeSaveDTO;
import com.lims.contract.dto.ContractPerformanceSaveDTO;
import com.lims.contract.dto.ContractQuery;
import com.lims.contract.dto.ContractSaveDTO;
import com.lims.contract.entity.ConContractPerformance;
import com.lims.contract.service.ContractService;
import com.lims.contract.vo.ContractDetailVO;
import com.lims.contract.vo.ContractVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "合同管理")
@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @ApiOperation("分页查询合同台账")
    @GetMapping("/page")
    public Result<PageResult<ContractVO>> selectPage(ContractQuery query) {
        return Result.success(contractService.selectPage(query));
    }

    @ApiOperation("新增合同")
    @PostMapping
    public Result<Void> addContract(@RequestBody @Validated ContractSaveDTO dto) {
        contractService.addContract(dto);
        return Result.success();
    }

    @ApiOperation("修改合同")
    @PutMapping
    public Result<Void> updateContract(@RequestBody @Validated ContractSaveDTO dto) {
        contractService.updateContract(dto);
        return Result.success();
    }

    @ApiOperation("删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return Result.success();
    }

    @ApiOperation("获取合同详情")
    @GetMapping("/{id}")
    public Result<ContractDetailVO> getContractDetail(@PathVariable Long id) {
        return Result.success(contractService.getContractDetail(id));
    }

    @ApiOperation("提交审批")
    @PostMapping("/submit/{id}")
    public Result<Void> submitApproval(@PathVariable Long id) {
        contractService.submitApproval(id);
        return Result.success();
    }

    @ApiOperation("合同审批")
    @PostMapping("/approval")
    public Result<Void> approvalContract(@RequestBody @Validated ContractApprovalDTO dto) {
        contractService.approvalContract(dto);
        return Result.success();
    }

    @ApiOperation("合同变更申请")
    @PostMapping("/change/apply")
    public Result<Void> applyChange(@RequestBody @Validated ContractChangeSaveDTO dto) {
        contractService.applyChange(dto);
        return Result.success();
    }

    @ApiOperation("变更审批")
    @PostMapping("/change/approval")
    public Result<Void> approvalChange(@RequestBody @Validated ContractApprovalDTO dto) {
        contractService.approvalChange(dto);
        return Result.success();
    }

    @ApiOperation("新增履约记录")
    @PostMapping("/performance")
    public Result<Void> addPerformance(@RequestBody @Validated ContractPerformanceSaveDTO dto) {
        contractService.addPerformance(dto);
        return Result.success();
    }

    @ApiOperation("修改履约记录")
    @PutMapping("/performance")
    public Result<Void> updatePerformance(@RequestBody @Validated ContractPerformanceSaveDTO dto) {
        contractService.updatePerformance(dto);
        return Result.success();
    }

    @ApiOperation("删除履约记录")
    @DeleteMapping("/performance/{id}")
    public Result<Void> deletePerformance(@PathVariable Long id) {
        contractService.deletePerformance(id);
        return Result.success();
    }

    @ApiOperation("获取履约记录详情")
    @GetMapping("/performance/{id}")
    public Result<ConContractPerformance> getPerformance(@PathVariable Long id) {
        return Result.success(contractService.getPerformance(id));
    }

    @ApiOperation("更新履约进度")
    @PutMapping("/performance/progress/{id}")
    public Result<Void> updatePerformanceProgress(@PathVariable Long id, @RequestParam Integer progress) {
        contractService.updatePerformanceProgress(id, progress);
        return Result.success();
    }

    @ApiOperation("合同终止")
    @PostMapping("/terminate/{id}")
    public Result<Void> terminateContract(@PathVariable Long id, @RequestParam String reason) {
        contractService.terminateContract(id, reason);
        return Result.success();
    }
}
