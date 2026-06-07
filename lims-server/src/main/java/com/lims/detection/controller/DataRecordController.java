package com.lims.detection.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.detection.dto.DataRecordQuery;
import com.lims.detection.dto.DataRecordSaveDTO;
import com.lims.detection.service.DataRecordService;
import com.lims.detection.vo.DataRecordDetailVO;
import com.lims.detection.vo.DataRecordVO;
import com.lims.detection.vo.DataValidationResultVO;
import com.lims.detection.vo.FormFieldVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "检测数据录入")
@RestController
@RequestMapping("/detection/dataRecord")
public class DataRecordController {

    @Autowired
    private DataRecordService dataRecordService;

    @ApiOperation("分页查询检测数据")
    @GetMapping("/page")
    public Result<PageResult<DataRecordVO>> selectPage(DataRecordQuery query) {
        return Result.success(dataRecordService.selectPage(query));
    }

    @ApiOperation("获取详情")
    @GetMapping("/{id}")
    public Result<DataRecordDetailVO> getDetail(@PathVariable Long id) {
        return Result.success(dataRecordService.getDetail(id));
    }

    @ApiOperation("生成动态表单")
    @GetMapping("/form/{methodId}")
    public Result<List<FormFieldVO>> generateDynamicForm(@PathVariable Long methodId) {
        return Result.success(dataRecordService.generateDynamicForm(methodId));
    }

    @ApiOperation("实时校验数据")
    @PostMapping("/validate")
    public Result<DataValidationResultVO> validateData(@RequestBody DataRecordSaveDTO dto) {
        return Result.success(dataRecordService.validateData(dto));
    }

    @ApiOperation("保存数据记录")
    @PostMapping
    public Result<Void> saveDataRecord(@RequestBody @Validated DataRecordSaveDTO dto) {
        dataRecordService.saveDataRecord(dto);
        return Result.success();
    }

    @ApiOperation("更新数据记录")
    @PutMapping
    public Result<Void> updateDataRecord(@RequestBody @Validated DataRecordSaveDTO dto) {
        dataRecordService.updateDataRecord(dto);
        return Result.success();
    }

    @ApiOperation("提交审核")
    @PostMapping("/submit/{id}")
    public Result<Void> submitDataRecord(@PathVariable Long id) {
        dataRecordService.submitDataRecord(id);
        return Result.success();
    }

    @ApiOperation("删除数据记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteDataRecord(@PathVariable Long id) {
        dataRecordService.deleteDataRecord(id);
        return Result.success();
    }

    @ApiOperation("仪器文件导入")
    @PostMapping("/import")
    public Result<Void> importInstrumentData(@RequestParam Long taskId, @RequestParam String fileUrl, @RequestParam String fileName) {
        dataRecordService.importInstrumentData(taskId, fileUrl, fileName);
        return Result.success();
    }

    @ApiOperation("验证数据完整性")
    @GetMapping("/verify/{id}")
    public Result<Boolean> verifyDataIntegrity(@PathVariable Long id) {
        return Result.success(dataRecordService.verifyDataIntegrity(id));
    }

    @ApiOperation("获取数据哈希值")
    @GetMapping("/hash/{id}")
    public Result<String> getDataHash(@PathVariable Long id) {
        return Result.success(dataRecordService.getById(id).getDataHash());
    }
}
