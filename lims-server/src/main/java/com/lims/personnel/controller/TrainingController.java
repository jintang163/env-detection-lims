package com.lims.personnel.controller;

import com.lims.common.page.PageResult;
import com.lims.common.result.Result;
import com.lims.personnel.dto.CertificateWarningProcessDTO;
import com.lims.personnel.dto.TrainingEvaluationSaveDTO;
import com.lims.personnel.dto.TrainingParticipantSaveDTO;
import com.lims.personnel.dto.TrainingPlanSaveDTO;
import com.lims.personnel.dto.TrainingSignInDTO;
import com.lims.personnel.service.TrainingService;
import com.lims.personnel.vo.CertificateWarningVO;
import com.lims.personnel.vo.TrainingEvaluationVO;
import com.lims.personnel.vo.TrainingParticipantVO;
import com.lims.personnel.vo.TrainingPlanVO;
import com.lims.personnel.vo.TrainingStatisticsVO;
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

import java.util.List;

@Api(tags = "培训管理")
@RestController
@RequestMapping("/training")
public class TrainingController {

    @Autowired
    private TrainingService trainingService;

    @ApiOperation("培训计划分页")
    @GetMapping("/plan/page")
    public Result<PageResult<TrainingPlanVO>> planPage(
            @RequestParam(required = false) String planName,
            @RequestParam(required = false) String trainingType,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(trainingService.planPage(planName, trainingType, status, pageNum, pageSize));
    }

    @ApiOperation("培训计划详情")
    @GetMapping("/plan/{id}")
    public Result<TrainingPlanVO> planGet(@PathVariable Long id) {
        return Result.success(trainingService.planGet(id));
    }

    @ApiOperation("新增培训计划")
    @PostMapping("/plan")
    public Result<Void> planSave(@RequestBody @Validated TrainingPlanSaveDTO dto) {
        trainingService.planSave(dto);
        return Result.success();
    }

    @ApiOperation("修改培训计划")
    @PutMapping("/plan")
    public Result<Void> planUpdate(@RequestBody @Validated TrainingPlanSaveDTO dto) {
        trainingService.planUpdate(dto);
        return Result.success();
    }

    @ApiOperation("删除培训计划")
    @DeleteMapping("/plan/{id}")
    public Result<Void> planDelete(@PathVariable Long id) {
        trainingService.planDelete(id);
        return Result.success();
    }

    @ApiOperation("发布培训计划")
    @PostMapping("/plan/publish/{id}")
    public Result<Void> planPublish(@PathVariable Long id) {
        trainingService.planPublish(id);
        return Result.success();
    }

    @ApiOperation("开始培训")
    @PostMapping("/plan/start/{id}")
    public Result<Void> planStart(@PathVariable Long id) {
        trainingService.planStart(id);
        return Result.success();
    }

    @ApiOperation("完成培训")
    @PostMapping("/plan/complete/{id}")
    public Result<Void> planComplete(@PathVariable Long id) {
        trainingService.planComplete(id);
        return Result.success();
    }

    @ApiOperation("培训人员分页")
    @GetMapping("/participant/page")
    public Result<PageResult<TrainingParticipantVO>> participantPage(
            @RequestParam(required = false) Long trainingPlanId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(trainingService.participantPage(trainingPlanId, pageNum, pageSize));
    }

    @ApiOperation("添加培训人员")
    @PostMapping("/participant")
    public Result<Void> participantAdd(@RequestBody @Validated List<TrainingParticipantSaveDTO> dtoList) {
        trainingService.participantAdd(dtoList);
        return Result.success();
    }

    @ApiOperation("培训签到")
    @PostMapping("/participant/signIn")
    public Result<Void> participantSignIn(@RequestBody @Validated TrainingSignInDTO dto) {
        trainingService.participantSignIn(dto);
        return Result.success();
    }

    @ApiOperation("更新培训人员")
    @PutMapping("/participant")
    public Result<Void> participantUpdate(@RequestBody @Validated TrainingParticipantSaveDTO dto) {
        trainingService.participantUpdate(dto);
        return Result.success();
    }

    @ApiOperation("删除培训人员")
    @DeleteMapping("/participant/{id}")
    public Result<Void> participantDelete(@PathVariable Long id) {
        trainingService.participantDelete(id);
        return Result.success();
    }

    @ApiOperation("添加培训评估")
    @PostMapping("/evaluation")
    public Result<Void> evaluationAdd(@RequestBody @Validated TrainingEvaluationSaveDTO dto) {
        trainingService.evaluationAdd(dto);
        return Result.success();
    }

    @ApiOperation("培训评估列表")
    @GetMapping("/evaluation/plan/{planId}")
    public Result<List<TrainingEvaluationVO>> evaluationByPlan(@PathVariable Long planId) {
        return Result.success(trainingService.evaluationByPlan(planId));
    }

    @ApiOperation("培训统计")
    @GetMapping("/statistics")
    public Result<TrainingStatisticsVO> getTrainingStatistics() {
        return Result.success(trainingService.getTrainingStatistics());
    }

    @ApiOperation("检查证书到期")
    @PostMapping("/certificate/checkExpiry")
    public Result<Void> checkCertificateExpiry() {
        trainingService.checkCertificateExpiry();
        return Result.success();
    }

    @ApiOperation("证书预警分页")
    @GetMapping("/warning/page")
    public Result<PageResult<CertificateWarningVO>> warningPage(
            @RequestParam(required = false) Integer warningStatus,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(trainingService.warningPage(warningStatus, pageNum, pageSize));
    }

    @ApiOperation("处理证书预警")
    @PostMapping("/warning/process")
    public Result<Void> warningProcess(@RequestBody @Validated CertificateWarningProcessDTO dto) {
        trainingService.warningProcess(dto);
        return Result.success();
    }
}
