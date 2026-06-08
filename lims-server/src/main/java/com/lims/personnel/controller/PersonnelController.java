package com.lims.personnel.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.personnel.dto.*;
import com.lims.personnel.service.PersonnelService;
import com.lims.personnel.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "人员档案管理")
@RestController
@RequestMapping("/personnel")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    @ApiOperation("分页查询人员列表")
    @GetMapping("/page")
    public Result<PageResult<PersonnelVO>> selectPage(PersonnelQuery query) {
        return Result.success(personnelService.selectPage(query));
    }

    @ApiOperation("获取人员详情")
    @GetMapping("/{id}")
    public Result<PersonnelDetailVO> getDetail(@PathVariable @ApiParam("人员ID") Long id) {
        return Result.success(personnelService.getDetail(id));
    }

    @ApiOperation("新增人员")
    @PostMapping
    public Result<Void> savePersonnel(@RequestBody @Validated PersonnelSaveDTO dto) {
        personnelService.savePersonnel(dto);
        return Result.success();
    }

    @ApiOperation("修改人员")
    @PutMapping
    public Result<Void> updatePersonnel(@RequestBody @Validated PersonnelSaveDTO dto) {
        personnelService.updatePersonnel(dto);
        return Result.success();
    }

    @ApiOperation("删除人员")
    @DeleteMapping("/{id}")
    public Result<Void> deletePersonnel(@PathVariable @ApiParam("人员ID") Long id) {
        personnelService.deletePersonnel(id);
        return Result.success();
    }

    @ApiOperation("获取人员统计")
    @GetMapping("/stats")
    public Result<PersonnelStatsVO> getStats() {
        return Result.success(personnelService.getStats());
    }

    @ApiOperation("校验人员资质")
    @GetMapping("/{id}/checkQualification")
    public Result<QualificationCheckResultVO> checkQualification(
            @PathVariable @ApiParam("人员ID") Long id,
            @RequestParam @ApiParam("检测项目ID列表") List<Long> testItemIds) {
        return Result.success(personnelService.checkQualification(id, testItemIds));
    }

    @ApiOperation("获取有资质的人员列表")
    @GetMapping("/qualified")
    public Result<List<PersonnelVO>> getQualifiedPersonnel(
            @RequestParam @ApiParam("检测项目ID列表") List<Long> testItemIds) {
        return Result.success(personnelService.getQualifiedPersonnel(testItemIds));
    }

    @ApiOperation("生成员工编号")
    @GetMapping("/generateEmployeeNo")
    public Result<String> generateEmployeeNo() {
        return Result.success(personnelService.generateEmployeeNo());
    }

    @ApiOperation("获取人员学历经历列表")
    @GetMapping("/{personnelId}/education")
    public Result<List<PersonnelEducationVO>> getEducationList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getEducationList(personnelId));
    }

    @ApiOperation("新增人员学历经历")
    @PostMapping("/education")
    public Result<Void> saveEducation(@RequestBody @Validated PersonnelEducationSaveDTO dto) {
        personnelService.saveEducation(dto);
        return Result.success();
    }

    @ApiOperation("修改人员学历经历")
    @PutMapping("/education")
    public Result<Void> updateEducation(@RequestBody @Validated PersonnelEducationSaveDTO dto) {
        personnelService.updateEducation(dto);
        return Result.success();
    }

    @ApiOperation("删除人员学历经历")
    @DeleteMapping("/education/{id}")
    public Result<Void> deleteEducation(@PathVariable Long id) {
        personnelService.deleteEducation(id);
        return Result.success();
    }

    @ApiOperation("获取人员职称列表")
    @GetMapping("/{personnelId}/title")
    public Result<List<PersonnelTitleVO>> getTitleList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getTitleList(personnelId));
    }

    @ApiOperation("新增人员职称")
    @PostMapping("/title")
    public Result<Void> saveTitle(@RequestBody @Validated PersonnelTitleSaveDTO dto) {
        personnelService.saveTitle(dto);
        return Result.success();
    }

    @ApiOperation("修改人员职称")
    @PutMapping("/title")
    public Result<Void> updateTitle(@RequestBody @Validated PersonnelTitleSaveDTO dto) {
        personnelService.updateTitle(dto);
        return Result.success();
    }

    @ApiOperation("删除人员职称")
    @DeleteMapping("/title/{id}")
    public Result<Void> deleteTitle(@PathVariable Long id) {
        personnelService.deleteTitle(id);
        return Result.success();
    }

    @ApiOperation("获取人员授权项目列表")
    @GetMapping("/{personnelId}/authorization")
    public Result<List<PersonnelAuthorizationVO>> getAuthorizationList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getAuthorizationList(personnelId));
    }

    @ApiOperation("新增人员授权项目")
    @PostMapping("/authorization")
    public Result<Void> saveAuthorization(@RequestBody @Validated PersonnelAuthorizationSaveDTO dto) {
        personnelService.saveAuthorization(dto);
        return Result.success();
    }

    @ApiOperation("修改人员授权项目")
    @PutMapping("/authorization")
    public Result<Void> updateAuthorization(@RequestBody @Validated PersonnelAuthorizationSaveDTO dto) {
        personnelService.updateAuthorization(dto);
        return Result.success();
    }

    @ApiOperation("删除人员授权项目")
    @DeleteMapping("/authorization/{id}")
    public Result<Void> deleteAuthorization(@PathVariable Long id) {
        personnelService.deleteAuthorization(id);
        return Result.success();
    }

    @ApiOperation("启用人员授权项目")
    @PostMapping("/authorization/enable/{id}")
    public Result<Void> enableAuthorization(@PathVariable Long id) {
        personnelService.enableAuthorization(id);
        return Result.success();
    }

    @ApiOperation("停用人员授权项目")
    @PostMapping("/authorization/disable/{id}")
    public Result<Void> disableAuthorization(@PathVariable Long id) {
        personnelService.disableAuthorization(id);
        return Result.success();
    }

    @ApiOperation("获取人员证书列表")
    @GetMapping("/{personnelId}/certificate")
    public Result<List<PersonnelCertificateVO>> getCertificateList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getCertificateList(personnelId));
    }

    @ApiOperation("新增人员证书")
    @PostMapping("/certificate")
    public Result<Void> saveCertificate(@RequestBody @Validated PersonnelCertificateSaveDTO dto) {
        personnelService.saveCertificate(dto);
        return Result.success();
    }

    @ApiOperation("修改人员证书")
    @PutMapping("/certificate")
    public Result<Void> updateCertificate(@RequestBody @Validated PersonnelCertificateSaveDTO dto) {
        personnelService.updateCertificate(dto);
        return Result.success();
    }

    @ApiOperation("删除人员证书")
    @DeleteMapping("/certificate/{id}")
    public Result<Void> deleteCertificate(@PathVariable Long id) {
        personnelService.deleteCertificate(id);
        return Result.success();
    }

    @ApiOperation("获取人员培训记录列表")
    @GetMapping("/{personnelId}/trainingRecord")
    public Result<List<TrainingRecordVO>> getTrainingRecordList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getTrainingRecordList(personnelId));
    }

    @ApiOperation("新增人员培训记录")
    @PostMapping("/trainingRecord")
    public Result<Void> saveTrainingRecord(@RequestBody @Validated TrainingRecordSaveDTO dto) {
        personnelService.saveTrainingRecord(dto);
        return Result.success();
    }

    @ApiOperation("修改人员培训记录")
    @PutMapping("/trainingRecord")
    public Result<Void> updateTrainingRecord(@RequestBody @Validated TrainingRecordSaveDTO dto) {
        personnelService.updateTrainingRecord(dto);
        return Result.success();
    }

    @ApiOperation("删除人员培训记录")
    @DeleteMapping("/trainingRecord/{id}")
    public Result<Void> deleteTrainingRecord(@PathVariable Long id) {
        personnelService.deleteTrainingRecord(id);
        return Result.success();
    }

    @ApiOperation("获取人员考核记录列表")
    @GetMapping("/{personnelId}/assessment")
    public Result<List<AssessmentRecordVO>> getAssessmentList(@PathVariable Long personnelId) {
        return Result.success(personnelService.getAssessmentList(personnelId));
    }

    @ApiOperation("新增人员考核记录")
    @PostMapping("/assessment")
    public Result<Void> saveAssessment(@RequestBody @Validated AssessmentRecordSaveDTO dto) {
        personnelService.saveAssessment(dto);
        return Result.success();
    }

    @ApiOperation("修改人员考核记录")
    @PutMapping("/assessment")
    public Result<Void> updateAssessment(@RequestBody @Validated AssessmentRecordSaveDTO dto) {
        personnelService.updateAssessment(dto);
        return Result.success();
    }

    @ApiOperation("删除人员考核记录")
    @DeleteMapping("/assessment/{id}")
    public Result<Void> deleteAssessment(@PathVariable Long id) {
        personnelService.deleteAssessment(id);
        return Result.success();
    }
}
