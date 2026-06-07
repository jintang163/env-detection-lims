package com.lims.detection.controller;

import com.lims.common.result.Result;
import com.lims.detection.entity.DetFormField;
import com.lims.detection.service.FormFieldService;
import com.lims.detection.vo.FormFieldVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "动态表单字段")
@RestController
@RequestMapping("/detection/formField")
public class FormFieldController {

    @Autowired
    private FormFieldService formFieldService;

    @ApiOperation("根据方法ID获取表单字段")
    @GetMapping("/method/{methodId}")
    public Result<List<FormFieldVO>> getFormFieldsByMethodId(@PathVariable Long methodId) {
        return Result.success(formFieldService.getFormFieldsByMethodId(methodId));
    }

    @ApiOperation("根据方法编码获取表单字段")
    @GetMapping("/methodCode/{methodCode}")
    public Result<List<FormFieldVO>> getFormFieldsByMethodCode(@PathVariable String methodCode) {
        return Result.success(formFieldService.getFormFieldsByMethodCode(methodCode));
    }

    @ApiOperation("新增表单字段")
    @PostMapping
    public Result<Void> saveFormField(@RequestBody DetFormField formField) {
        formFieldService.saveFormField(formField);
        return Result.success();
    }

    @ApiOperation("更新表单字段")
    @PutMapping
    public Result<Void> updateFormField(@RequestBody DetFormField formField) {
        formFieldService.updateFormField(formField);
        return Result.success();
    }

    @ApiOperation("删除表单字段")
    @DeleteMapping("/{id}")
    public Result<Void> deleteFormField(@PathVariable Long id) {
        formFieldService.deleteFormField(id);
        return Result.success();
    }
}
