package com.lims.customer.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.customer.dto.*;
import com.lims.customer.entity.CusCustomerCredit;
import com.lims.customer.entity.CusCustomerFollow;
import com.lims.customer.entity.CusCustomerQualification;
import com.lims.customer.service.CustomerService;
import com.lims.customer.vo.CustomerDetailVO;
import com.lims.customer.vo.CustomerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "客户管理")
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/page")
    @ApiOperation("分页查询客户列表")
    public Result<PageResult<CustomerVO>> page(CustomerQuery query) {
        return Result.success(customerService.page(query));
    }

    @GetMapping("/{id}")
    @ApiOperation("获取客户详情")
    public Result<CustomerDetailVO> getDetail(@ApiParam("客户ID") @PathVariable Long id) {
        return Result.success(customerService.getDetail(id));
    }

    @PostMapping
    @ApiOperation("新增客户")
    public Result<Void> save(@Valid @RequestBody CustomerSaveDTO dto) {
        customerService.save(dto);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改客户")
    public Result<Void> update(@Valid @RequestBody CustomerSaveDTO dto) {
        customerService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除客户")
    public Result<Void> delete(@ApiParam("客户ID") @PathVariable Long id) {
        customerService.delete(id);
        return Result.success();
    }

    @PostMapping("/moveToPublic/{id}")
    @ApiOperation("移入公海")
    public Result<Void> moveToPublic(@ApiParam("客户ID") @PathVariable Long id,
                                     @ApiParam("公海原因") @RequestParam(required = false) String reason) {
        customerService.moveToPublic(id, reason);
        return Result.success();
    }

    @PostMapping("/removeFromPublic/{id}")
    @ApiOperation("移出公海")
    public Result<Void> removeFromPublic(@ApiParam("客户ID") @PathVariable Long id,
                                         @ApiParam("归属业务员ID") @RequestParam Long ownerId,
                                         @ApiParam("归属业务员姓名") @RequestParam String ownerName) {
        customerService.removeFromPublic(id, ownerId, ownerName);
        return Result.success();
    }

    @PostMapping("/adjustLevel/{id}")
    @ApiOperation("客户分级调整")
    public Result<Void> adjustLevel(@ApiParam("客户ID") @PathVariable Long id,
                                    @ApiParam("客户等级 1VIP 2重要 3普通 4潜在") @RequestParam Integer level) {
        customerService.adjustLevel(id, level);
        return Result.success();
    }

    @PostMapping("/adjustCredit")
    @ApiOperation("客户信用调整")
    public Result<Void> adjustCredit(@Valid @RequestBody CustomerCreditSaveDTO dto) {
        customerService.adjustCredit(dto);
        return Result.success();
    }

    @PostMapping("/qualification")
    @ApiOperation("新增客户资质")
    public Result<Void> saveQualification(@Valid @RequestBody CustomerQualificationSaveDTO dto) {
        customerService.saveQualification(dto);
        return Result.success();
    }

    @PutMapping("/qualification")
    @ApiOperation("修改客户资质")
    public Result<Void> updateQualification(@Valid @RequestBody CustomerQualificationSaveDTO dto) {
        customerService.updateQualification(dto);
        return Result.success();
    }

    @DeleteMapping("/qualification/{id}")
    @ApiOperation("删除客户资质")
    public Result<Void> deleteQualification(@ApiParam("资质ID") @PathVariable Long id) {
        customerService.deleteQualification(id);
        return Result.success();
    }

    @GetMapping("/qualification/list/{customerId}")
    @ApiOperation("获取客户资质列表")
    public Result<List<CusCustomerQualification>> getQualificationList(@ApiParam("客户ID") @PathVariable Long customerId) {
        return Result.success(customerService.getQualificationList(customerId));
    }

    @PostMapping("/follow")
    @ApiOperation("新增客户跟进记录")
    public Result<Void> saveFollow(@Valid @RequestBody CustomerFollowSaveDTO dto) {
        customerService.saveFollow(dto);
        return Result.success();
    }

    @PutMapping("/follow")
    @ApiOperation("修改客户跟进记录")
    public Result<Void> updateFollow(@Valid @RequestBody CustomerFollowSaveDTO dto) {
        customerService.updateFollow(dto);
        return Result.success();
    }

    @DeleteMapping("/follow/{id}")
    @ApiOperation("删除客户跟进记录")
    public Result<Void> deleteFollow(@ApiParam("跟进记录ID") @PathVariable Long id) {
        customerService.deleteFollow(id);
        return Result.success();
    }

    @GetMapping("/follow/list/{customerId}")
    @ApiOperation("获取客户跟进记录列表")
    public Result<List<CusCustomerFollow>> getFollowList(@ApiParam("客户ID") @PathVariable Long customerId) {
        return Result.success(customerService.getFollowList(customerId));
    }

    @GetMapping("/credit/list/{customerId}")
    @ApiOperation("获取客户信用记录列表")
    public Result<List<CusCustomerCredit>> getCreditList(@ApiParam("客户ID") @PathVariable Long customerId) {
        return Result.success(customerService.getCreditList(customerId));
    }

    @PostMapping("/claim/{id}")
    @ApiOperation("领取公海客户")
    public Result<Void> claimPublicCustomer(@ApiParam("客户ID") @PathVariable Long id) {
        customerService.claimPublicCustomer(id);
        return Result.success();
    }

    @PostMapping("/assign/{id}")
    @ApiOperation("分配客户")
    public Result<Void> assignCustomer(@ApiParam("客户ID") @PathVariable Long id,
                                       @ApiParam("归属业务员ID") @RequestParam Long ownerId,
                                       @ApiParam("归属业务员姓名") @RequestParam String ownerName) {
        customerService.assignCustomer(id, ownerId, ownerName);
        return Result.success();
    }
}
