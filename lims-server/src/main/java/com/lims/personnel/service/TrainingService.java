package com.lims.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.personnel.dto.CertificateWarningProcessDTO;
import com.lims.personnel.dto.TrainingEvaluationSaveDTO;
import com.lims.personnel.dto.TrainingParticipantSaveDTO;
import com.lims.personnel.dto.TrainingPlanSaveDTO;
import com.lims.personnel.dto.TrainingSignInDTO;
import com.lims.personnel.entity.PerTrainingPlan;
import com.lims.personnel.vo.CertificateWarningVO;
import com.lims.personnel.vo.TrainingEvaluationVO;
import com.lims.personnel.vo.TrainingParticipantVO;
import com.lims.personnel.vo.TrainingPlanVO;
import com.lims.personnel.vo.TrainingStatisticsVO;

import java.util.List;

public interface TrainingService extends IService<PerTrainingPlan> {

    PageResult<TrainingPlanVO> planPage(String planName, String trainingType, Integer status, Integer pageNum, Integer pageSize);

    TrainingPlanVO planGet(Long id);

    void planSave(TrainingPlanSaveDTO dto);

    void planUpdate(TrainingPlanSaveDTO dto);

    void planDelete(Long id);

    void planPublish(Long id);

    void planStart(Long id);

    void planComplete(Long id);

    PageResult<TrainingParticipantVO> participantPage(Long trainingPlanId, Integer pageNum, Integer pageSize);

    void participantAdd(List<TrainingParticipantSaveDTO> dtoList);

    void participantSignIn(TrainingSignInDTO dto);

    void participantUpdate(TrainingParticipantSaveDTO dto);

    void participantDelete(Long id);

    void evaluationAdd(TrainingEvaluationSaveDTO dto);

    List<TrainingEvaluationVO> evaluationByPlan(Long trainingPlanId);

    TrainingStatisticsVO getTrainingStatistics();

    void checkCertificateExpiry();

    PageResult<CertificateWarningVO> warningPage(Integer warningStatus, Integer pageNum, Integer pageSize);

    void warningProcess(CertificateWarningProcessDTO dto);
}
