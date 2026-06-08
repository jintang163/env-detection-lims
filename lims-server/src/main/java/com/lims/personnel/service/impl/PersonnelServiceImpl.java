package com.lims.personnel.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.personnel.dto.*;
import com.lims.personnel.entity.*;
import com.lims.personnel.mapper.*;
import com.lims.personnel.service.PersonnelService;
import com.lims.personnel.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonnelServiceImpl extends ServiceImpl<PerPersonnelMapper, PerPersonnel> implements PersonnelService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private PerPersonnelEducationMapper educationMapper;

    @Autowired
    private PerPersonnelTitleMapper titleMapper;

    @Autowired
    private PerPersonnelAuthorizationMapper authorizationMapper;

    @Autowired
    private PerPersonnelCertificateMapper certificateMapper;

    @Autowired
    private PerTrainingRecordMapper trainingRecordMapper;

    @Autowired
    private PerAssessmentRecordMapper assessmentRecordMapper;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return status == 1 ? "在职" : "离职";
    }

    private String getIsFullTimeName(Integer isFullTime) {
        if (isFullTime == null) return "";
        return isFullTime == 1 ? "是" : "否";
    }

    private String getAuthorizationStatusName(Integer status) {
        if (status == null) return "";
        return status == 1 ? "有效" : "无效";
    }

    private String getCertificateStatusName(Integer status) {
        if (status == null) return "";
        return status == 1 ? "有效" : "无效";
    }

    @Override
    public PageResult<PersonnelVO> selectPage(PersonnelQuery query) {
        LambdaQueryWrapper<PerPersonnel> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getName())) {
            wrapper.like(PerPersonnel::getName, query.getName());
        }
        if (StrUtil.isNotBlank(query.getEmployeeNo())) {
            wrapper.like(PerPersonnel::getEmployeeNo, query.getEmployeeNo());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(PerPersonnel::getDeptId, query.getDeptId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(PerPersonnel::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(PerPersonnel::getCreateTime);

        Integer pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        Integer pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        Page<PerPersonnel> page = new Page<>(pageNum, pageSize);
        IPage<PerPersonnel> pageResult = this.page(page, wrapper);

        IPage<PersonnelVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public PersonnelDetailVO getDetail(Long id) {
        PerPersonnel personnel = this.getById(id);
        if (personnel == null) {
            throw new BizException("人员档案不存在");
        }

        PersonnelDetailVO vo = BeanUtil.copyProperties(personnel, PersonnelDetailVO.class);
        vo.setStatusName(getStatusName(personnel.getStatus()));

        List<PerPersonnelEducation> educationList = educationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelEducation>()
                        .eq(PerPersonnelEducation::getPersonnelId, id)
                        .orderByAsc(PerPersonnelEducation::getStartDate)
        );
        vo.setEducationList(educationList.stream().map(edu -> {
            PersonnelEducationVO eduVO = BeanUtil.copyProperties(edu, PersonnelEducationVO.class);
            eduVO.setIsFullTimeName(getIsFullTimeName(edu.getIsFullTime()));
            return eduVO;
        }).collect(Collectors.toList()));

        List<PerPersonnelTitle> titleList = titleMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelTitle>()
                        .eq(PerPersonnelTitle::getPersonnelId, id)
                        .orderByDesc(PerPersonnelTitle::getAcquireDate)
        );
        vo.setTitleList(titleList.stream().map(title ->
                BeanUtil.copyProperties(title, PersonnelTitleVO.class)
        ).collect(Collectors.toList()));

        List<PerPersonnelAuthorization> authorizationList = authorizationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelAuthorization>()
                        .eq(PerPersonnelAuthorization::getPersonnelId, id)
                        .orderByDesc(PerPersonnelAuthorization::getAuthorizeDate)
        );
        vo.setAuthorizationList(authorizationList.stream().map(auth -> {
            PersonnelAuthorizationVO authVO = BeanUtil.copyProperties(auth, PersonnelAuthorizationVO.class);
            authVO.setStatusName(getAuthorizationStatusName(auth.getStatus()));
            return authVO;
        }).collect(Collectors.toList()));

        List<PerPersonnelCertificate> certificateList = certificateMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelCertificate>()
                        .eq(PerPersonnelCertificate::getPersonnelId, id)
                        .orderByDesc(PerPersonnelCertificate::getIssueDate)
        );
        vo.setCertificateList(certificateList.stream().map(cert -> {
            PersonnelCertificateVO certVO = BeanUtil.copyProperties(cert, PersonnelCertificateVO.class);
            certVO.setStatusName(getCertificateStatusName(cert.getStatus()));
            return certVO;
        }).collect(Collectors.toList()));

        List<PerTrainingRecord> trainingRecordList = trainingRecordMapper.selectList(
                new LambdaQueryWrapper<PerTrainingRecord>()
                        .eq(PerTrainingRecord::getPersonnelId, id)
                        .orderByDesc(PerTrainingRecord::getTrainingDate)
        );
        vo.setTrainingRecordList(trainingRecordList.stream().map(record ->
                BeanUtil.copyProperties(record, TrainingRecordVO.class)
        ).collect(Collectors.toList()));

        List<PerAssessmentRecord> assessmentRecordList = assessmentRecordMapper.selectList(
                new LambdaQueryWrapper<PerAssessmentRecord>()
                        .eq(PerAssessmentRecord::getPersonnelId, id)
                        .orderByDesc(PerAssessmentRecord::getAssessmentDate)
        );
        vo.setAssessmentRecordList(assessmentRecordList.stream().map(record ->
                BeanUtil.copyProperties(record, AssessmentRecordVO.class)
        ).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePersonnel(PersonnelSaveDTO dto) {
        checkEmployeeNoUnique(dto.getEmployeeNo(), null);

        PerPersonnel personnel = BeanUtil.copyProperties(dto, PerPersonnel.class);
        if (personnel.getStatus() == null) {
            personnel.setStatus(1);
        }
        this.save(personnel);

        savePersonnelRelations(personnel.getId(), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePersonnel(PersonnelSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("人员ID不能为空");
        }

        PerPersonnel oldPersonnel = this.getById(dto.getId());
        if (oldPersonnel == null) {
            throw new BizException("人员档案不存在");
        }

        checkEmployeeNoUnique(dto.getEmployeeNo(), dto.getId());

        PerPersonnel personnel = BeanUtil.copyProperties(dto, PerPersonnel.class);
        this.updateById(personnel);

        deletePersonnelRelations(dto.getId());
        savePersonnelRelations(dto.getId(), dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePersonnel(Long id) {
        PerPersonnel personnel = this.getById(id);
        if (personnel == null) {
            throw new BizException("人员档案不存在");
        }

        long authCount = authorizationMapper.selectCount(
                new LambdaQueryWrapper<PerPersonnelAuthorization>()
                        .eq(PerPersonnelAuthorization::getPersonnelId, id)
        );
        if (authCount > 0) {
            throw new BizException("该人员存在授权项目，不能删除");
        }

        long certCount = certificateMapper.selectCount(
                new LambdaQueryWrapper<PerPersonnelCertificate>()
                        .eq(PerPersonnelCertificate::getPersonnelId, id)
        );
        if (certCount > 0) {
            throw new BizException("该人员存在证书信息，不能删除");
        }

        long trainingCount = trainingRecordMapper.selectCount(
                new LambdaQueryWrapper<PerTrainingRecord>()
                        .eq(PerTrainingRecord::getPersonnelId, id)
        );
        if (trainingCount > 0) {
            throw new BizException("该人员存在培训记录，不能删除");
        }

        long assessmentCount = assessmentRecordMapper.selectCount(
                new LambdaQueryWrapper<PerAssessmentRecord>()
                        .eq(PerAssessmentRecord::getPersonnelId, id)
        );
        if (assessmentCount > 0) {
            throw new BizException("该人员存在考核记录，不能删除");
        }

        deletePersonnelRelations(id);
        this.removeById(id);
    }

    @Override
    public PersonnelStatsVO getStats() {
        PersonnelStatsVO stats = new PersonnelStatsVO();

        stats.setTotalCount(this.count());
        stats.setOnJobCount(this.count(new LambdaQueryWrapper<PerPersonnel>().eq(PerPersonnel::getStatus, 1)));
        stats.setOffJobCount(this.count(new LambdaQueryWrapper<PerPersonnel>().eq(PerPersonnel::getStatus, 0)));
        stats.setVacationCount(0);

        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);

        List<PerPersonnelCertificate> allCertificates = certificateMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelCertificate>().eq(PerPersonnelCertificate::getStatus, 1)
        );

        int expiringCount = 0;
        int expiredCount = 0;
        for (PerPersonnelCertificate cert : allCertificates) {
            if (cert.getExpiryDate() != null) {
                if (cert.getExpiryDate().isBefore(today)) {
                    expiredCount++;
                } else if (cert.getExpiryDate().isBefore(thirtyDaysLater) || cert.getExpiryDate().isEqual(thirtyDaysLater)) {
                    expiringCount++;
                }
            }
        }

        stats.setExpiringCertificatesCount(expiringCount);
        stats.setExpiredCertificatesCount(expiredCount);

        return stats;
    }

    @Override
    public QualificationCheckResultVO checkQualification(Long personnelId, List<Long> testItemIds) {
        if (personnelId == null) {
            throw new BizException("人员ID不能为空");
        }
        if (testItemIds == null || testItemIds.isEmpty()) {
            throw new BizException("检测项目ID列表不能为空");
        }

        PerPersonnel personnel = this.getById(personnelId);
        if (personnel == null) {
            throw new BizException("人员档案不存在");
        }
        if (personnel.getStatus() == null || personnel.getStatus() != 1) {
            throw new BizException("该人员已离职，无法进行资质校验");
        }

        QualificationCheckResultVO result = new QualificationCheckResultVO();
        List<String> qualifiedItems = new ArrayList<>();
        List<String> unqualifiedItems = new ArrayList<>();
        LocalDate today = LocalDate.now();

        List<PerPersonnelAuthorization> authorizations = authorizationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelAuthorization>()
                        .eq(PerPersonnelAuthorization::getPersonnelId, personnelId)
                        .eq(PerPersonnelAuthorization::getStatus, 1)
        );

        Map<Long, PerPersonnelAuthorization> authMap = authorizations.stream()
                .filter(auth -> auth.getItemId() != null)
                .collect(Collectors.toMap(PerPersonnelAuthorization::getItemId, auth -> auth, (a, b) -> a));

        for (Long testItemId : testItemIds) {
            PerPersonnelAuthorization auth = authMap.get(testItemId);
            if (auth == null) {
                unqualifiedItems.add("检测项目ID【" + testItemId + "】：未授权");
            } else if (auth.getExpiryDate() != null && auth.getExpiryDate().isBefore(today)) {
                unqualifiedItems.add("检测项目【" + auth.getItemName() + "】：授权已过期（有效期至：" + auth.getExpiryDate() + "）");
            } else if (auth.getStatus() == null || auth.getStatus() != 1) {
                unqualifiedItems.add("检测项目【" + auth.getItemName() + "】：授权已失效");
            } else {
                qualifiedItems.add(auth.getItemName());
            }
        }

        result.setQualified(unqualifiedItems.isEmpty());
        result.setQualifiedItems(qualifiedItems);
        result.setUnqualifiedItems(unqualifiedItems);

        return result;
    }

    @Override
    public List<PersonnelVO> getQualifiedPersonnel(List<Long> testItemIds) {
        if (testItemIds == null || testItemIds.isEmpty()) {
            throw new BizException("检测项目ID列表不能为空");
        }

        LocalDate today = LocalDate.now();

        List<PerPersonnelAuthorization> validAuthorizations = authorizationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelAuthorization>()
                        .in(PerPersonnelAuthorization::getItemId, testItemIds)
                        .eq(PerPersonnelAuthorization::getStatus, 1)
                        .and(wrapper -> wrapper.isNull(PerPersonnelAuthorization::getExpiryDate)
                                .or()
                                .ge(PerPersonnelAuthorization::getExpiryDate, today))
        );

        Map<Long, List<Long>> personnelItemMap = validAuthorizations.stream()
                .filter(auth -> auth.getPersonnelId() != null && auth.getItemId() != null)
                .collect(Collectors.groupingBy(
                        PerPersonnelAuthorization::getPersonnelId,
                        Collectors.mapping(PerPersonnelAuthorization::getItemId, Collectors.toList())
                ));

        List<Long> qualifiedPersonnelIds = new ArrayList<>();
        for (Map.Entry<Long, List<Long>> entry : personnelItemMap.entrySet()) {
            if (entry.getValue().containsAll(testItemIds)) {
                qualifiedPersonnelIds.add(entry.getKey());
            }
        }

        if (qualifiedPersonnelIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<PerPersonnel> personnelList = this.list(
                new LambdaQueryWrapper<PerPersonnel>()
                        .in(PerPersonnel::getId, qualifiedPersonnelIds)
                        .eq(PerPersonnel::getStatus, 1)
                        .orderByAsc(PerPersonnel::getName)
        );

        return personnelList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public String generateEmployeeNo() {
        return codeGenerator.generateEmployeeNo();
    }

    private void checkEmployeeNoUnique(String employeeNo, Long excludeId) {
        LambdaQueryWrapper<PerPersonnel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerPersonnel::getEmployeeNo, employeeNo);
        if (excludeId != null) {
            wrapper.ne(PerPersonnel::getId, excludeId);
        }
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BizException("员工编号已存在：" + employeeNo);
        }
    }

    private void savePersonnelRelations(Long personnelId, PersonnelSaveDTO dto) {
        if (dto.getEducationList() != null && !dto.getEducationList().isEmpty()) {
            for (PersonnelEducationSaveDTO eduDTO : dto.getEducationList()) {
                PerPersonnelEducation education = BeanUtil.copyProperties(eduDTO, PerPersonnelEducation.class);
                education.setPersonnelId(personnelId);
                educationMapper.insert(education);
            }
        }

        if (dto.getTitleList() != null && !dto.getTitleList().isEmpty()) {
            for (PersonnelTitleSaveDTO titleDTO : dto.getTitleList()) {
                PerPersonnelTitle title = BeanUtil.copyProperties(titleDTO, PerPersonnelTitle.class);
                title.setPersonnelId(personnelId);
                titleMapper.insert(title);
            }
        }

        if (dto.getAuthorizationList() != null && !dto.getAuthorizationList().isEmpty()) {
            for (PersonnelAuthorizationSaveDTO authDTO : dto.getAuthorizationList()) {
                PerPersonnelAuthorization authorization = BeanUtil.copyProperties(authDTO, PerPersonnelAuthorization.class);
                authorization.setPersonnelId(personnelId);
                authorizationMapper.insert(authorization);
            }
        }

        if (dto.getCertificateList() != null && !dto.getCertificateList().isEmpty()) {
            for (PersonnelCertificateSaveDTO certDTO : dto.getCertificateList()) {
                PerPersonnelCertificate certificate = BeanUtil.copyProperties(certDTO, PerPersonnelCertificate.class);
                certificate.setPersonnelId(personnelId);
                certificateMapper.insert(certificate);
            }
        }
    }

    private void deletePersonnelRelations(Long personnelId) {
        educationMapper.delete(new LambdaQueryWrapper<PerPersonnelEducation>()
                .eq(PerPersonnelEducation::getPersonnelId, personnelId));
        titleMapper.delete(new LambdaQueryWrapper<PerPersonnelTitle>()
                .eq(PerPersonnelTitle::getPersonnelId, personnelId));
        authorizationMapper.delete(new LambdaQueryWrapper<PerPersonnelAuthorization>()
                .eq(PerPersonnelAuthorization::getPersonnelId, personnelId));
        certificateMapper.delete(new LambdaQueryWrapper<PerPersonnelCertificate>()
                .eq(PerPersonnelCertificate::getPersonnelId, personnelId));
    }

    private PersonnelVO convertToVO(PerPersonnel personnel) {
        PersonnelVO vo = BeanUtil.copyProperties(personnel, PersonnelVO.class);
        vo.setStatusName(getStatusName(personnel.getStatus()));
        return vo;
    }

    @Override
    public List<PersonnelEducationVO> getEducationList(Long personnelId) {
        List<PerPersonnelEducation> list = educationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelEducation>()
                        .eq(PerPersonnelEducation::getPersonnelId, personnelId)
                        .orderByAsc(PerPersonnelEducation::getStartDate)
        );
        return list.stream().map(edu -> {
            PersonnelEducationVO vo = BeanUtil.copyProperties(edu, PersonnelEducationVO.class);
            vo.setIsFullTimeName(getIsFullTimeName(edu.getIsFullTime()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveEducation(PersonnelEducationSaveDTO dto) {
        PerPersonnelEducation education = BeanUtil.copyProperties(dto, PerPersonnelEducation.class);
        educationMapper.insert(education);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEducation(PersonnelEducationSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("学历经历ID不能为空");
        }
        PerPersonnelEducation education = BeanUtil.copyProperties(dto, PerPersonnelEducation.class);
        educationMapper.updateById(education);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEducation(Long id) {
        educationMapper.deleteById(id);
    }

    @Override
    public List<PersonnelTitleVO> getTitleList(Long personnelId) {
        List<PerPersonnelTitle> list = titleMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelTitle>()
                        .eq(PerPersonnelTitle::getPersonnelId, personnelId)
                        .orderByDesc(PerPersonnelTitle::getAcquireDate)
        );
        return list.stream().map(title ->
                BeanUtil.copyProperties(title, PersonnelTitleVO.class)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTitle(PersonnelTitleSaveDTO dto) {
        PerPersonnelTitle title = BeanUtil.copyProperties(dto, PerPersonnelTitle.class);
        titleMapper.insert(title);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTitle(PersonnelTitleSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("职称ID不能为空");
        }
        PerPersonnelTitle title = BeanUtil.copyProperties(dto, PerPersonnelTitle.class);
        titleMapper.updateById(title);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTitle(Long id) {
        titleMapper.deleteById(id);
    }

    @Override
    public List<PersonnelAuthorizationVO> getAuthorizationList(Long personnelId) {
        List<PerPersonnelAuthorization> list = authorizationMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelAuthorization>()
                        .eq(PerPersonnelAuthorization::getPersonnelId, personnelId)
                        .orderByDesc(PerPersonnelAuthorization::getAuthorizeDate)
        );
        return list.stream().map(auth -> {
            PersonnelAuthorizationVO vo = BeanUtil.copyProperties(auth, PersonnelAuthorizationVO.class);
            vo.setStatusName(getAuthorizationStatusName(auth.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAuthorization(PersonnelAuthorizationSaveDTO dto) {
        PerPersonnelAuthorization authorization = BeanUtil.copyProperties(dto, PerPersonnelAuthorization.class);
        if (authorization.getStatus() == null) {
            authorization.setStatus(1);
        }
        authorizationMapper.insert(authorization);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAuthorization(PersonnelAuthorizationSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("授权项目ID不能为空");
        }
        PerPersonnelAuthorization authorization = BeanUtil.copyProperties(dto, PerPersonnelAuthorization.class);
        authorizationMapper.updateById(authorization);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthorization(Long id) {
        authorizationMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableAuthorization(Long id) {
        PerPersonnelAuthorization authorization = authorizationMapper.selectById(id);
        if (authorization == null) {
            throw new BizException("授权项目不存在");
        }
        authorization.setStatus(1);
        authorizationMapper.updateById(authorization);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableAuthorization(Long id) {
        PerPersonnelAuthorization authorization = authorizationMapper.selectById(id);
        if (authorization == null) {
            throw new BizException("授权项目不存在");
        }
        authorization.setStatus(0);
        authorizationMapper.updateById(authorization);
    }

    @Override
    public List<PersonnelCertificateVO> getCertificateList(Long personnelId) {
        List<PerPersonnelCertificate> list = certificateMapper.selectList(
                new LambdaQueryWrapper<PerPersonnelCertificate>()
                        .eq(PerPersonnelCertificate::getPersonnelId, personnelId)
                        .orderByDesc(PerPersonnelCertificate::getIssueDate)
        );
        LocalDate today = LocalDate.now();
        return list.stream().map(cert -> {
            PersonnelCertificateVO vo = BeanUtil.copyProperties(cert, PersonnelCertificateVO.class);
            vo.setStatusName(getCertificateStatusName(cert.getStatus()));
            if (cert.getExpiryDate() != null) {
                if (cert.getExpiryDate().isBefore(today)) {
                    vo.setExpiryStatus("已过期");
                } else if (java.time.temporal.ChronoUnit.DAYS.between(today, cert.getExpiryDate()) <= 30) {
                    vo.setExpiryStatus("即将到期");
                } else {
                    vo.setExpiryStatus("正常");
                }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCertificate(PersonnelCertificateSaveDTO dto) {
        PerPersonnelCertificate certificate = BeanUtil.copyProperties(dto, PerPersonnelCertificate.class);
        if (certificate.getStatus() == null) {
            certificate.setStatus(1);
        }
        if (certificate.getIsRemind() == null) {
            certificate.setIsRemind(1);
        }
        if (certificate.getRemindDays() == null) {
            certificate.setRemindDays(30);
        }
        certificateMapper.insert(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCertificate(PersonnelCertificateSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("证书ID不能为空");
        }
        PerPersonnelCertificate certificate = BeanUtil.copyProperties(dto, PerPersonnelCertificate.class);
        certificateMapper.updateById(certificate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCertificate(Long id) {
        certificateMapper.deleteById(id);
    }

    @Override
    public List<TrainingRecordVO> getTrainingRecordList(Long personnelId) {
        List<PerTrainingRecord> list = trainingRecordMapper.selectList(
                new LambdaQueryWrapper<PerTrainingRecord>()
                        .eq(PerTrainingRecord::getPersonnelId, personnelId)
                        .orderByDesc(PerTrainingRecord::getTrainingDate)
        );
        return list.stream().map(record ->
                BeanUtil.copyProperties(record, TrainingRecordVO.class)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTrainingRecord(TrainingRecordSaveDTO dto) {
        PerTrainingRecord record = BeanUtil.copyProperties(dto, PerTrainingRecord.class);
        trainingRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTrainingRecord(TrainingRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("培训记录ID不能为空");
        }
        PerTrainingRecord record = BeanUtil.copyProperties(dto, PerTrainingRecord.class);
        trainingRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTrainingRecord(Long id) {
        trainingRecordMapper.deleteById(id);
    }

    @Override
    public List<AssessmentRecordVO> getAssessmentList(Long personnelId) {
        List<PerAssessmentRecord> list = assessmentRecordMapper.selectList(
                new LambdaQueryWrapper<PerAssessmentRecord>()
                        .eq(PerAssessmentRecord::getPersonnelId, personnelId)
                        .orderByDesc(PerAssessmentRecord::getAssessmentDate)
        );
        return list.stream().map(record ->
                BeanUtil.copyProperties(record, AssessmentRecordVO.class)
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAssessment(AssessmentRecordSaveDTO dto) {
        PerAssessmentRecord record = BeanUtil.copyProperties(dto, PerAssessmentRecord.class);
        assessmentRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssessment(AssessmentRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("考核记录ID不能为空");
        }
        PerAssessmentRecord record = BeanUtil.copyProperties(dto, PerAssessmentRecord.class);
        assessmentRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAssessment(Long id) {
        assessmentRecordMapper.deleteById(id);
    }
}
