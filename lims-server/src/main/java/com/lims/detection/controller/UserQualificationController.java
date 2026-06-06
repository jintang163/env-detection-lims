package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.UserQualificationSaveDTO;
import com.lims.detection.service.UserQualificationService;
import com.lims.detection.vo.UserQualificationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "人员资质管理")
@RestController
@RequestMapping("/detection/userQualification")
public class UserQualificationController {

    @Autowired
    private UserQualificationService userQualificationService;

    @ApiOperation("分页查询人员资质")
    @GetMapping("/page")
    public Result<PageResult<UserQualificationVO>> selectPage(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(userQualificationService.selectPage(userId, pageNum, pageSize));
    }

    @ApiOperation("获取用户的资质列表")
    @GetMapping("/byUser/{userId}")
    public Result<List<UserQualificationVO>> getByUserId(@PathVariable Long userId) {
        return Result.success(userQualificationService.getByUserId(userId));
    }

    @ApiOperation("新增人员资质")
    @PostMapping
    public Result<Void> saveQualification(@RequestBody @Validated UserQualificationSaveDTO dto) {
        userQualificationService.saveQualification(dto);
        return Result.success();
    }

    @ApiOperation("修改人员资质")
    @PutMapping
    public Result<Void> updateQualification(@RequestBody @Validated UserQualificationSaveDTO dto) {
        userQualificationService.updateQualification(dto);
        return Result.success();
    }

    @ApiOperation("删除人员资质")
    @DeleteMapping("/{id}")
    public Result<Void> deleteQualification(@PathVariable Long id) {
        userQualificationService.deleteQualification(id);
        return Result.success();
    }

    @ApiOperation("启用人员资质")
    @PostMapping("/enable/{id}")
    public Result<Void> enableQualification(@PathVariable Long id) {
        userQualificationService.enableQualification(id);
        return Result.success();
    }

    @ApiOperation("停用人员资质")
    @PostMapping("/disable/{id}")
    public Result<Void> disableQualification(@PathVariable Long id) {
        userQualificationService.disableQualification(id);
        return Result.success();
    }
}
