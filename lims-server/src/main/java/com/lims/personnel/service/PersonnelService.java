package com.lims.personnel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.personnel.dto.*;
import com.lims.personnel.entity.PerPersonnel;
import com.lims.personnel.vo.*;

import java.util.List;

public interface PersonnelService extends IService<PerPersonnel> {

    PageResult<PersonnelVO> selectPage(PersonnelQuery query);

    PersonnelDetailVO getDetail(Long id);

    void savePersonnel(PersonnelSaveDTO dto);

    void updatePersonnel(PersonnelSaveDTO dto);

    void deletePersonnel(Long id);

    PersonnelStatsVO getStats();

    QualificationCheckResultVO checkQualification(Long personnelId, List<Long> testItemIds);

    List<PersonnelVO> getQualifiedPersonnel(List<Long> testItemIds);

    String generateEmployeeNo();

    List<PersonnelEducationVO> getEducationList(Long personnelId);

    void saveEducation(PersonnelEducationSaveDTO dto);

    void updateEducation(PersonnelEducationSaveDTO dto);

    void deleteEducation(Long id);

    List<PersonnelTitleVO> getTitleList(Long personnelId);

    void saveTitle(PersonnelTitleSaveDTO dto);

    void updateTitle(PersonnelTitleSaveDTO dto);

    void deleteTitle(Long id);

    List<PersonnelAuthorizationVO> getAuthorizationList(Long personnelId);

    void saveAuthorization(PersonnelAuthorizationSaveDTO dto);

    void updateAuthorization(PersonnelAuthorizationSaveDTO dto);

    void deleteAuthorization(Long id);

    void enableAuthorization(Long id);

    void disableAuthorization(Long id);

    List<PersonnelCertificateVO> getCertificateList(Long personnelId);

    void saveCertificate(PersonnelCertificateSaveDTO dto);

    void updateCertificate(PersonnelCertificateSaveDTO dto);

    void deleteCertificate(Long id);

    List<TrainingRecordVO> getTrainingRecordList(Long personnelId);

    void saveTrainingRecord(TrainingRecordSaveDTO dto);

    void updateTrainingRecord(TrainingRecordSaveDTO dto);

    void deleteTrainingRecord(Long id);

    List<AssessmentRecordVO> getAssessmentList(Long personnelId);

    void saveAssessment(AssessmentRecordSaveDTO dto);

    void updateAssessment(AssessmentRecordSaveDTO dto);

    void deleteAssessment(Long id);
}
