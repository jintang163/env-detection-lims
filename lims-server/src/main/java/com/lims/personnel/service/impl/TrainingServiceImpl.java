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
import com.lims.personnel.dto.CertificateWarningProcessDTO;
import com.lims.personnel.dto.TrainingEvaluationSaveDTO;
import com.lims.personnel.dto.TrainingParticipantSaveDTO;
import com.lims.personnel.dto.TrainingPlanSaveDTO;
import com.lims.personnel.dto.TrainingSignInDTO;
import com.lims.personnel.entity.PerCertificateWarning;
import com.lims.personnel.entity.PerPersonnel;
import com.lims.personnel.entity.PerPersonnelCertificate;
import com.lims.personnel.entity.PerTrainingEvaluation;
import com.lims.personnel.entity.PerTrainingParticipant;
import com.lims.personnel.entity.PerTrainingPlan;
import com.lims.personnel.entity.PerTrainingRecord;
import com.lims.personnel.mapper.PerCertificateWarningMapper;
import com.lims.personnel.mapper.PerPersonnelCertificateMapper;
import com.lims.personnel.mapper.PerPersonnelMapper;
import com.lims.personnel.mapper.PerTrainingEvaluationMapper;
import com.lims.personnel.mapper.PerTrainingParticipantMapper;
import com.lims.personnel.mapper.PerTrainingPlanMapper;
import com.lims.personnel.mapper.PerTrainingRecordMapper;
import com.lims.personnel.service.TrainingService;
import com.lims.personnel.vo.CertificateWarningVO;
import com.lims.personnel.vo.TrainingEvaluationVO;
import com.lims.personnel.vo.TrainingParticipantVO;
import com.lims.personnel.vo.TrainingPlanVO;
import com.lims.personnel.vo.TrainingStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl extends ServiceImpl<PerTrainingPlanMapper, PerTrainingPlan> implements TrainingService {

    @Autowired
    private PerTrainingParticipantMapper participantMapper;

    @Autowired
    private PerTrainingEvaluationMapper evaluationMapper;

    @Autowired
    private PerTrainingRecordMapper trainingRecordMapper;

    @Autowired
    private PerPersonnelCertificateMapper certificateMapper;

    @Autowired
    private PerCertificateWarningMapper warningMapper;

    @Autowired
    private PerPersonnelMapper personnelMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PUBLISHED = 1;
    private static final int STATUS_ONGOING = 2;
    private static final int STATUS_COMPLETED = 3;
    private static final int STATUS_CANCELLED = 4;

    private static final int SIGN_IN_NOT = 0;
    private static final int SIGN_IN_YES = 1;
    private static final int SIGN_IN_LATE = 2;
    private static final int SIGN_IN_EARLY = 3;
    private static final int SIGN_IN_ABSENT = 4;

    private static final int EVAL_RESULT_FAIL = 0;
    private static final int EVAL_RESULT_PASS = 1;
    private static final int EVAL_RESULT_EXCELLENT = 2;

    private static final int WARNING_TYPE_SOON = 1;
    private static final int WARNING_TYPE_EXPIRED = 2;

    private static final int WARNING_STATUS_UNPROCESSED = 0;
    private static final int WARNING_STATUS_PROCESSED = 1;
    private static final int WARNING_STATUS_IGNORED = 2;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发布";
            case 2: return "进行中";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "";
        }
    }

    private String getSignInStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "未签到";
            case 1: return "已签到";
            case 2: return "迟到";
            case 3: return "早退";
            case 4: return "缺勤";
            default: return "";
        }
    }

    private String getEvalResultName(Integer result) {
        if (result == null) return "";
        switch (result) {
            case 0: return "不合格";
            case 1: return "合格";
            case 2: return "优秀";
            default: return "";
        }
    }

    private String getCertificateFlagName(Integer flag) {
        if (flag == null) return "";
        return flag == 1 ? "是" : "否";
    }

    private String getWarningTypeName(Integer type) {
        if (type == null) return "";
        return type == 1 ? "即将到期" : "已过期";
    }

    private String getWarningStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "未处理";
            case 1: return "已处理";
            case 2: return "已忽略";
            default: return "";
        }
    }

    @Override
    public PageResult<TrainingPlanVO> planPage(String planName, String trainingType, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PerTrainingPlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(planName)) {
            wrapper.like(PerTrainingPlan::getPlanName, planName);
        }
        if (StrUtil.isNotBlank(trainingType)) {
            wrapper.eq(PerTrainingPlan::getTrainingType, trainingType);
        }
        if (status != null) {
            wrapper.eq(PerTrainingPlan::getStatus, status);
        }
        wrapper.orderByDesc(PerTrainingPlan::getCreateTime);

        IPage<PerTrainingPlan> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        List<TrainingPlanVO> voList = page.getRecords().stream().map(entity -> {
            TrainingPlanVO vo = BeanUtil.copyProperties(entity, TrainingPlanVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public TrainingPlanVO planGet(Long id) {
        PerTrainingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        TrainingPlanVO vo = BeanUtil.copyProperties(plan, TrainingPlanVO.class);
        vo.setStatusName(getStatusName(plan.getStatus()));

        LambdaQueryWrapper<PerTrainingParticipant> participantWrapper = new LambdaQueryWrapper<>();
        participantWrapper.eq(PerTrainingParticipant::getTrainingPlanId, id);
        participantWrapper.orderByAsc(PerTrainingParticipant::getCreateTime);
        List<PerTrainingParticipant> participants = participantMapper.selectList(participantWrapper);
        List<TrainingParticipantVO> participantVOList = participants.stream().map(p -> {
            TrainingParticipantVO pvo = BeanUtil.copyProperties(p, TrainingParticipantVO.class);
            pvo.setSignInStatusName(getSignInStatusName(p.getSignInStatus()));
            pvo.setEvaluationResultName(getEvalResultName(p.getEvaluationResult()));
            pvo.setCertificateFlagName(getCertificateFlagName(p.getCertificateFlag()));
            return pvo;
        }).collect(Collectors.toList());
        vo.setParticipantList(participantVOList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planSave(TrainingPlanSaveDTO dto) {
        String planNo = codeGenerator.generateTrainingPlanNo();
        PerTrainingPlan plan = BeanUtil.copyProperties(dto, PerTrainingPlan.class);
        plan.setPlanNo(planNo);
        plan.setStatus(STATUS_DRAFT);
        this.save(plan);

        if (dto.getParticipantList() != null && !dto.getParticipantList().isEmpty()) {
            for (TrainingParticipantSaveDTO participantDTO : dto.getParticipantList()) {
                PerTrainingParticipant participant = BeanUtil.copyProperties(participantDTO, PerTrainingParticipant.class);
                participant.setTrainingPlanId(plan.getId());
                participant.setSignInStatus(SIGN_IN_NOT);
                participantMapper.insert(participant);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planUpdate(TrainingPlanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("培训计划ID不能为空");
        }
        PerTrainingPlan existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("培训计划不存在");
        }
        if (existing.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可修改");
        }
        PerTrainingPlan plan = BeanUtil.copyProperties(dto, PerTrainingPlan.class);
        this.updateById(plan);

        LambdaQueryWrapper<PerTrainingParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerTrainingParticipant::getTrainingPlanId, dto.getId());
        participantMapper.delete(wrapper);

        if (dto.getParticipantList() != null && !dto.getParticipantList().isEmpty()) {
            for (TrainingParticipantSaveDTO participantDTO : dto.getParticipantList()) {
                PerTrainingParticipant participant = BeanUtil.copyProperties(participantDTO, PerTrainingParticipant.class);
                participant.setTrainingPlanId(dto.getId());
                participant.setSignInStatus(SIGN_IN_NOT);
                participantMapper.insert(participant);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planDelete(Long id) {
        PerTrainingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可删除");
        }
        this.removeById(id);

        LambdaQueryWrapper<PerTrainingParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerTrainingParticipant::getTrainingPlanId, id);
        participantMapper.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planPublish(Long id) {
        PerTrainingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的计划可发布");
        }
        plan.setStatus(STATUS_PUBLISHED);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planStart(Long id) {
        PerTrainingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_PUBLISHED) {
            throw new BizException("仅已发布状态的计划可开始");
        }
        plan.setStatus(STATUS_ONGOING);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void planComplete(Long id) {
        PerTrainingPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_ONGOING) {
            throw new BizException("仅进行中状态的计划可完成");
        }
        plan.setStatus(STATUS_COMPLETED);
        this.updateById(plan);

        LambdaQueryWrapper<PerTrainingParticipant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerTrainingParticipant::getTrainingPlanId, id);
        wrapper.eq(PerTrainingParticipant::getSignInStatus, SIGN_IN_YES);
        wrapper.in(PerTrainingParticipant::getEvaluationResult, EVAL_RESULT_PASS, EVAL_RESULT_EXCELLENT);
        List<PerTrainingParticipant> qualifiedParticipants = participantMapper.selectList(wrapper);

        for (PerTrainingParticipant participant : qualifiedParticipants) {
            PerTrainingRecord record = new PerTrainingRecord();
            record.setPersonnelId(participant.getPersonnelId());
            record.setPersonnelName(participant.getPersonnelName());
            record.setTrainingType(plan.getTrainingType());
            record.setTrainingName(plan.getPlanName());
            record.setTrainingContent(plan.getTrainingContent());
            record.setTrainingDate(plan.getEndDate());
            record.setTrainingHours(plan.getTrainingHours());
            record.setTrainingOrganization(plan.getTrainer());
            record.setCertificateFlag(participant.getCertificateFlag());
            record.setCertificateNo(participant.getCertificateNo());
            record.setCertificateExpiryDate(participant.getCertificateExpiryDate());
            record.setRemark(participant.getRemark());
            trainingRecordMapper.insert(record);
        }
    }

    @Override
    public PageResult<TrainingParticipantVO> participantPage(Long trainingPlanId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PerTrainingParticipant> wrapper = new LambdaQueryWrapper<>();
        if (trainingPlanId != null) {
            wrapper.eq(PerTrainingParticipant::getTrainingPlanId, trainingPlanId);
        }
        wrapper.orderByAsc(PerTrainingParticipant::getCreateTime);

        IPage<PerTrainingParticipant> page = participantMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<TrainingParticipantVO> voList = page.getRecords().stream().map(entity -> {
            TrainingParticipantVO vo = BeanUtil.copyProperties(entity, TrainingParticipantVO.class);
            vo.setSignInStatusName(getSignInStatusName(entity.getSignInStatus()));
            vo.setEvaluationResultName(getEvalResultName(entity.getEvaluationResult()));
            vo.setCertificateFlagName(getCertificateFlagName(entity.getCertificateFlag()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participantAdd(List<TrainingParticipantSaveDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BizException("培训人员列表不能为空");
        }
        for (TrainingParticipantSaveDTO dto : dtoList) {
            if (dto.getTrainingPlanId() == null) {
                throw new BizException("培训计划ID不能为空");
            }
            PerTrainingPlan plan = this.getById(dto.getTrainingPlanId());
            if (plan == null) {
                throw new BizException("培训计划不存在");
            }
            if (plan.getStatus() == STATUS_COMPLETED || plan.getStatus() == STATUS_CANCELLED) {
                throw new BizException("已完成或已取消的培训计划不能添加人员");
            }
            LambdaQueryWrapper<PerTrainingParticipant> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(PerTrainingParticipant::getTrainingPlanId, dto.getTrainingPlanId());
            checkWrapper.eq(PerTrainingParticipant::getPersonnelId, dto.getPersonnelId());
            if (participantMapper.selectCount(checkWrapper) > 0) {
                throw new BizException("人员已在培训计划中：" + dto.getPersonnelName());
            }
            PerTrainingParticipant participant = BeanUtil.copyProperties(dto, PerTrainingParticipant.class);
            participant.setSignInStatus(SIGN_IN_NOT);
            participantMapper.insert(participant);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participantSignIn(TrainingSignInDTO dto) {
        PerTrainingParticipant participant = participantMapper.selectById(dto.getParticipantId());
        if (participant == null) {
            throw new BizException("培训人员不存在");
        }
        PerTrainingPlan plan = this.getById(participant.getTrainingPlanId());
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_ONGOING) {
            throw new BizException("仅进行中的培训计划可签到");
        }
        participant.setSignInTime(dto.getSignInTime() != null ? dto.getSignInTime() : LocalDateTime.now());
        participant.setSignInStatus(dto.getSignInStatus());
        participantMapper.updateById(participant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participantUpdate(TrainingParticipantSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("培训人员ID不能为空");
        }
        PerTrainingParticipant existing = participantMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("培训人员不存在");
        }
        PerTrainingParticipant participant = BeanUtil.copyProperties(dto, PerTrainingParticipant.class);
        participantMapper.updateById(participant);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void participantDelete(Long id) {
        PerTrainingParticipant participant = participantMapper.selectById(id);
        if (participant == null) {
            throw new BizException("培训人员不存在");
        }
        PerTrainingPlan plan = this.getById(participant.getTrainingPlanId());
        if (plan != null && (plan.getStatus() == STATUS_COMPLETED || plan.getStatus() == STATUS_CANCELLED)) {
            throw new BizException("已完成或已取消的培训计划不能删除人员");
        }
        participantMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluationAdd(TrainingEvaluationSaveDTO dto) {
        PerTrainingPlan plan = this.getById(dto.getTrainingPlanId());
        if (plan == null) {
            throw new BizException("培训计划不存在");
        }
        if (plan.getStatus() != STATUS_ONGOING && plan.getStatus() != STATUS_COMPLETED) {
            throw new BizException("仅进行中或已完成的培训计划可添加评估");
        }
        PerTrainingEvaluation evaluation = BeanUtil.copyProperties(dto, PerTrainingEvaluation.class);
        evaluation.setEvaluateTime(LocalDateTime.now());
        evaluationMapper.insert(evaluation);
    }

    @Override
    public List<TrainingEvaluationVO> evaluationByPlan(Long trainingPlanId) {
        LambdaQueryWrapper<PerTrainingEvaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PerTrainingEvaluation::getTrainingPlanId, trainingPlanId);
        wrapper.orderByDesc(PerTrainingEvaluation::getCreateTime);
        List<PerTrainingEvaluation> list = evaluationMapper.selectList(wrapper);
        return list.stream().map(entity -> BeanUtil.copyProperties(entity, TrainingEvaluationVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TrainingStatisticsVO getTrainingStatistics() {
        TrainingStatisticsVO vo = new TrainingStatisticsVO();

        LambdaQueryWrapper<PerTrainingPlan> planWrapper;

        planWrapper = new LambdaQueryWrapper<>();
        vo.setTotalPlans(Math.toIntExact(this.count(planWrapper)));

        planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(PerTrainingPlan::getStatus, STATUS_COMPLETED);
        vo.setCompletedPlans(Math.toIntExact(this.count(planWrapper)));

        planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(PerTrainingPlan::getStatus, STATUS_ONGOING);
        vo.setOngoingPlans(Math.toIntExact(this.count(planWrapper)));

        planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(PerTrainingPlan::getStatus, STATUS_COMPLETED);
        List<PerTrainingPlan> completedPlans = this.list(planWrapper);
        BigDecimal totalHours = BigDecimal.ZERO;
        for (PerTrainingPlan plan : completedPlans) {
            if (plan.getTrainingHours() != null) {
                totalHours = totalHours.add(plan.getTrainingHours());
            }
        }
        vo.setTotalTrainingHours(totalHours);

        LambdaQueryWrapper<PerTrainingParticipant> participantWrapper = new LambdaQueryWrapper<>();
        vo.setTotalParticipants(Math.toIntExact(participantMapper.selectCount(participantWrapper)));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkCertificateExpiry() {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<PerPersonnelCertificate> certWrapper = new LambdaQueryWrapper<>();
        certWrapper.eq(PerPersonnelCertificate::getStatus, 1);
        certWrapper.eq(PerPersonnelCertificate::getIsRemind, 1);
        List<PerPersonnelCertificate> certificates = certificateMapper.selectList(certWrapper);

        for (PerPersonnelCertificate cert : certificates) {
            if (cert.getExpiryDate() == null || cert.getRemindDays() == null) {
                continue;
            }

            long daysUntilExpiry = ChronoUnit.DAYS.between(today, cert.getExpiryDate());
            int warningType;
            int warningDays;

            if (daysUntilExpiry < 0) {
                warningType = WARNING_TYPE_EXPIRED;
                warningDays = (int) Math.abs(daysUntilExpiry);
            } else if (daysUntilExpiry <= cert.getRemindDays()) {
                warningType = WARNING_TYPE_SOON;
                warningDays = (int) daysUntilExpiry;
            } else {
                continue;
            }

            LambdaQueryWrapper<PerCertificateWarning> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(PerCertificateWarning::getCertificateId, cert.getId());
            checkWrapper.eq(PerCertificateWarning::getWarningType, warningType);
            checkWrapper.eq(PerCertificateWarning::getWarningStatus, WARNING_STATUS_UNPROCESSED);
            if (warningMapper.selectCount(checkWrapper) > 0) {
                continue;
            }

            PerCertificateWarning warning = new PerCertificateWarning();
            warning.setPersonnelId(cert.getPersonnelId());
            warning.setCertificateId(cert.getId());
            warning.setCertificateType(cert.getCertificateType());
            warning.setCertificateName(cert.getCertificateName());
            warning.setCertificateNo(cert.getCertificateNo());
            warning.setExpiryDate(cert.getExpiryDate());
            warning.setWarningType(warningType);
            warning.setWarningDays(warningDays);
            warning.setWarningStatus(WARNING_STATUS_UNPROCESSED);

            PerPersonnel personnel = personnelMapper.selectById(cert.getPersonnelId());
            if (personnel != null) {
                warning.setPersonnelName(personnel.getName());
            }

            warningMapper.insert(warning);
        }
    }

    @Override
    public PageResult<CertificateWarningVO> warningPage(Integer warningStatus, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<PerCertificateWarning> wrapper = new LambdaQueryWrapper<>();
        if (warningStatus != null) {
            wrapper.eq(PerCertificateWarning::getWarningStatus, warningStatus);
        }
        wrapper.orderByDesc(PerCertificateWarning::getCreateTime);

        IPage<PerCertificateWarning> page = warningMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<CertificateWarningVO> voList = page.getRecords().stream().map(entity -> {
            CertificateWarningVO vo = BeanUtil.copyProperties(entity, CertificateWarningVO.class);
            vo.setWarningTypeName(getWarningTypeName(entity.getWarningType()));
            vo.setWarningStatusName(getWarningStatusName(entity.getWarningStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void warningProcess(CertificateWarningProcessDTO dto) {
        PerCertificateWarning warning = warningMapper.selectById(dto.getWarningId());
        if (warning == null) {
            throw new BizException("预警记录不存在");
        }
        if (warning.getWarningStatus() != WARNING_STATUS_UNPROCESSED) {
            throw new BizException("仅未处理的预警可处理");
        }
        warning.setWarningStatus(WARNING_STATUS_PROCESSED);
        warning.setProcessResult(dto.getProcessResult());
        warning.setProcessTime(LocalDateTime.now());
        warningMapper.updateById(warning);
    }
}
