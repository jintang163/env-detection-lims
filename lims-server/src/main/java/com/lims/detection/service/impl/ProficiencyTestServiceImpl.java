package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.PtPlanQuery;
import com.lims.detection.dto.PtPlanSaveDTO;
import com.lims.detection.dto.PtResultReportDTO;
import com.lims.detection.dto.PtResultSaveDTO;
import com.lims.detection.entity.PtPlan;
import com.lims.detection.entity.PtResult;
import com.lims.detection.entity.PtSample;
import com.lims.detection.mapper.PtPlanMapper;
import com.lims.detection.mapper.PtResultMapper;
import com.lims.detection.mapper.PtSampleMapper;
import com.lims.detection.service.ProficiencyTestService;
import com.lims.detection.vo.PtPlanVO;
import com.lims.detection.vo.PtResultVO;
import com.lims.detection.vo.PtSampleVO;
import com.lims.detection.vo.PtYoudenDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProficiencyTestServiceImpl implements ProficiencyTestService {

    @Autowired
    private PtPlanMapper ptPlanMapper;

    @Autowired
    private PtResultMapper ptResultMapper;

    @Autowired
    private PtSampleMapper ptSampleMapper;

    private static final int SCALE = 4;

    private static final String STATUS_DRAFT = "draft";
    private static final String STATUS_REGISTERED = "registered";
    private static final String STATUS_SAMPLE_RECEIVED = "sample_received";
    private static final String STATUS_TESTING = "testing";
    private static final String STATUS_REPORTED = "reported";
    private static final String STATUS_COMPLETED = "completed";

    private static final String REPORT_STATUS_DRAFT = "draft";
    private static final String REPORT_STATUS_SUBMITTED = "submitted";

    private static final String EVALUATION_SATISFACTORY = "satisfactory";
    private static final String EVALUATION_QUESTIONABLE = "questionable";
    private static final String EVALUATION_UNSATISFACTORY = "unsatisfactory";

    @Override
    public PageResult<PtPlanVO> selectPage(PtPlanQuery query) {
        LambdaQueryWrapper<PtPlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(PtPlan::getPtNo, query.getKeyword())
                    .or().like(PtPlan::getPtName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getPtType())) {
            wrapper.eq(PtPlan::getPtType, query.getPtType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(PtPlan::getStatus, String.valueOf(query.getStatus()));
        }
        wrapper.orderByDesc(PtPlan::getCreateTime);

        Page<PtPlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<PtPlan> pageResult = ptPlanMapper.selectPage(page, wrapper);

        IPage<PtPlanVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public PtPlanVO getDetail(Long id) {
        PtPlan plan = ptPlanMapper.selectById(id);
        if (plan == null) {
            throw new BizException("能力验证计划不存在");
        }
        return convertToVO(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(PtPlanSaveDTO dto) {
        PtPlan plan = BeanUtil.copyProperties(dto, PtPlan.class);
        if (dto.getStatus() != null) {
            plan.setStatus(String.valueOf(dto.getStatus()));
        } else {
            plan.setStatus(STATUS_DRAFT);
        }
        ptPlanMapper.insert(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PtPlanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("计划ID不能为空");
        }
        PtPlan oldPlan = ptPlanMapper.selectById(dto.getId());
        if (oldPlan == null) {
            throw new BizException("能力验证计划不存在");
        }
        PtPlan plan = BeanUtil.copyProperties(dto, PtPlan.class);
        if (dto.getStatus() != null) {
            plan.setStatus(String.valueOf(dto.getStatus()));
        }
        ptPlanMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PtPlan plan = ptPlanMapper.selectById(id);
        if (plan == null) {
            throw new BizException("能力验证计划不存在");
        }
        ptPlanMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(Long id) {
        PtPlan plan = ptPlanMapper.selectById(id);
        if (plan == null) {
            throw new BizException("能力验证计划不存在");
        }
        if (!STATUS_DRAFT.equals(plan.getStatus())) {
            throw new BizException("只有草稿状态的计划才能报名");
        }
        plan.setStatus(STATUS_REGISTERED);
        ptPlanMapper.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveSample(Long id) {
        PtPlan plan = ptPlanMapper.selectById(id);
        if (plan == null) {
            throw new BizException("能力验证计划不存在");
        }
        if (!STATUS_REGISTERED.equals(plan.getStatus())) {
            throw new BizException("只有已报名状态的计划才能接收样品");
        }
        plan.setStatus(STATUS_SAMPLE_RECEIVED);
        ptPlanMapper.updateById(plan);

        List<PtSample> samples = ptSampleMapper.selectList(
                new LambdaQueryWrapper<PtSample>().eq(PtSample::getPtPlanId, id));
        for (PtSample sample : samples) {
            sample.setReceiveDate(LocalDateTime.now());
            sample.setSampleStatus("received");
            ptSampleMapper.updateById(sample);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveResult(PtResultSaveDTO dto) {
        PtResult result;
        if (dto.getId() != null) {
            result = ptResultMapper.selectById(dto.getId());
            if (result == null) {
                throw new BizException("能力验证结果不存在");
            }
            BeanUtil.copyProperties(dto, result);
            ptResultMapper.updateById(result);
        } else {
            result = BeanUtil.copyProperties(dto, PtResult.class);
            result.setReportStatus(REPORT_STATUS_DRAFT);
            ptResultMapper.insert(result);
        }

        if (dto.getDetectedValue() != null) {
            PtSample sample = ptSampleMapper.selectById(dto.getPtSampleId());
            if (sample != null && sample.getAssignedValue() != null && sample.getTargetSd() != null) {
                BigDecimal zScore = calculateZScore(dto.getDetectedValue(), sample.getAssignedValue(), sample.getTargetSd());
                result.setZScore(zScore);
                result.setEvaluation(getEvaluation(zScore));
                ptResultMapper.updateById(result);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportResult(PtResultReportDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("结果ID不能为空");
        }
        PtResult result = ptResultMapper.selectById(dto.getId());
        if (result == null) {
            throw new BizException("能力验证结果不存在");
        }
        BeanUtil.copyProperties(dto, result);
        result.setReportStatus(REPORT_STATUS_SUBMITTED);
        result.setReportDate(LocalDateTime.now());

        if (dto.getDetectedValue() != null) {
            PtSample sample = ptSampleMapper.selectById(result.getPtSampleId());
            if (sample != null && sample.getAssignedValue() != null && sample.getTargetSd() != null) {
                BigDecimal zScore = calculateZScore(dto.getDetectedValue(), sample.getAssignedValue(), sample.getTargetSd());
                result.setZScore(zScore);
                result.setEvaluation(getEvaluation(zScore));
            }
        }

        ptResultMapper.updateById(result);

        PtPlan plan = ptPlanMapper.selectById(result.getPtPlanId());
        if (plan != null) {
            plan.setStatus(STATUS_REPORTED);
            ptPlanMapper.updateById(plan);
        }
    }

    @Override
    public List<PtResultVO> getZScoreAnalysis(Long id) {
        List<PtResult> results = ptResultMapper.selectList(
                new LambdaQueryWrapper<PtResult>().eq(PtResult::getPtPlanId, id));
        List<PtSample> samples = ptSampleMapper.selectList(
                new LambdaQueryWrapper<PtSample>().eq(PtSample::getPtPlanId, id));

        Map<Long, PtSample> sampleMap = samples.stream()
                .collect(Collectors.toMap(PtSample::getId, s -> s));

        return results.stream().map(r -> {
            PtResultVO vo = BeanUtil.copyProperties(r, PtResultVO.class);
            PtSample sample = sampleMap.get(r.getPtSampleId());
            if (sample != null) {
                vo.setSampleName(sample.getSampleName());
                vo.setSampleCode(sample.getSampleCode());
                if (r.getZScore() == null && r.getDetectedValue() != null
                        && sample.getAssignedValue() != null && sample.getTargetSd() != null) {
                    BigDecimal zScore = calculateZScore(r.getDetectedValue(), sample.getAssignedValue(), sample.getTargetSd());
                    vo.setZScore(zScore);
                    vo.setEvaluation(getEvaluation(zScore));
                }
                vo.setEvaluationText(getEvaluationText(vo.getEvaluation()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PtYoudenDataVO> getYoudenData(Long planId) {
        List<PtResult> results = ptResultMapper.selectList(
                new LambdaQueryWrapper<PtResult>().eq(PtResult::getPtPlanId, planId));
        List<PtSample> samples = ptSampleMapper.selectList(
                new LambdaQueryWrapper<PtSample>().eq(PtSample::getPtPlanId, planId));

        Map<Long, PtSample> sampleMap = samples.stream()
                .collect(Collectors.toMap(PtSample::getId, s -> s));

        Map<String, List<PtResult>> sampleCodeMap = results.stream()
                .filter(r -> sampleMap.containsKey(r.getPtSampleId()))
                .collect(Collectors.groupingBy(r -> sampleMap.get(r.getPtSampleId()).getSampleCode()));

        List<PtYoudenDataVO> youdenDataList = new ArrayList<>();

        List<String> sampleCodes = new ArrayList<>(sampleCodeMap.keySet());
        if (sampleCodes.size() >= 2) {
            List<PtResult> sampleAResults = sampleCodeMap.get(sampleCodes.get(0));
            List<PtResult> sampleBResults = sampleCodeMap.get(sampleCodes.get(1));

            Map<Long, PtResult> resultBMap = sampleBResults.stream()
                    .collect(Collectors.toMap(PtResult::getCreateBy, r -> r, (r1, r2) -> r1));

            for (PtResult resultA : sampleAResults) {
                PtResult resultB = resultBMap.get(resultA.getCreateBy());
                if (resultB != null && resultA.getDetectedValue() != null && resultB.getDetectedValue() != null) {
                    PtYoudenDataVO vo = new PtYoudenDataVO();
                    vo.setXValue(resultA.getDetectedValue());
                    vo.setYValue(resultB.getDetectedValue());
                    vo.setLaboratoryCode(String.valueOf(resultA.getCreateBy()));

                    PtSample sampleA = sampleMap.get(resultA.getPtSampleId());
                    PtSample sampleB = sampleMap.get(resultB.getPtSampleId());

                    BigDecimal zScoreA = resultA.getZScore();
                    BigDecimal zScoreB = resultB.getZScore();

                    if (zScoreA == null && sampleA != null && sampleA.getTargetSd() != null) {
                        zScoreA = calculateZScore(resultA.getDetectedValue(), sampleA.getAssignedValue(), sampleA.getTargetSd());
                    }
                    if (zScoreB == null && sampleB != null && sampleB.getTargetSd() != null) {
                        zScoreB = calculateZScore(resultB.getDetectedValue(), sampleB.getAssignedValue(), sampleB.getTargetSd());
                    }

                    if (zScoreA != null && zScoreB != null) {
                        if (zScoreA.abs().compareTo(new BigDecimal("2")) > 0 || zScoreB.abs().compareTo(new BigDecimal("2")) > 0) {
                            vo.setEvaluation(EVALUATION_UNSATISFACTORY);
                        } else {
                            vo.setEvaluation(EVALUATION_SATISFACTORY);
                        }
                    }

                    youdenDataList.add(vo);
                }
            }
        }

        return youdenDataList;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", ptPlanMapper.selectCount(null));
        stats.put("draft", ptPlanMapper.selectCount(new LambdaQueryWrapper<PtPlan>().eq(PtPlan::getStatus, STATUS_DRAFT)));
        stats.put("registered", ptPlanMapper.selectCount(new LambdaQueryWrapper<PtPlan>().eq(PtPlan::getStatus, STATUS_REGISTERED)));
        stats.put("sampleReceived", ptPlanMapper.selectCount(new LambdaQueryWrapper<PtPlan>().eq(PtPlan::getStatus, STATUS_SAMPLE_RECEIVED)));
        stats.put("reported", ptPlanMapper.selectCount(new LambdaQueryWrapper<PtPlan>().eq(PtPlan::getStatus, STATUS_REPORTED)));
        stats.put("completed", ptPlanMapper.selectCount(new LambdaQueryWrapper<PtPlan>().eq(PtPlan::getStatus, STATUS_COMPLETED)));
        return stats;
    }

    @Override
    public BigDecimal calculateZScore(BigDecimal detectedValue, BigDecimal assignedValue, BigDecimal targetSd) {
        if (detectedValue == null || assignedValue == null || targetSd == null) {
            return null;
        }
        if (targetSd.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return detectedValue.subtract(assignedValue).divide(targetSd, SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public String getEvaluation(BigDecimal zScore) {
        if (zScore == null) {
            return null;
        }
        BigDecimal absZ = zScore.abs();
        if (absZ.compareTo(new BigDecimal("2")) <= 0) {
            return EVALUATION_SATISFACTORY;
        } else if (absZ.compareTo(new BigDecimal("3")) <= 0) {
            return EVALUATION_QUESTIONABLE;
        } else {
            return EVALUATION_UNSATISFACTORY;
        }
    }

    private String getEvaluationText(String evaluation) {
        if (evaluation == null) {
            return "";
        }
        switch (evaluation) {
            case EVALUATION_SATISFACTORY:
                return "满意";
            case EVALUATION_QUESTIONABLE:
                return "可疑";
            case EVALUATION_UNSATISFACTORY:
                return "不满意";
            default:
                return "";
        }
    }

    private String getStatusName(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case STATUS_DRAFT:
                return "草稿";
            case STATUS_REGISTERED:
                return "已报名";
            case STATUS_SAMPLE_RECEIVED:
                return "已收样";
            case STATUS_TESTING:
                return "检测中";
            case STATUS_REPORTED:
                return "已上报";
            case STATUS_COMPLETED:
                return "已完成";
            default:
                return "";
        }
    }

    private PtPlanVO convertToVO(PtPlan plan) {
        PtPlanVO vo = BeanUtil.copyProperties(plan, PtPlanVO.class);
        vo.setStatusName(getStatusName(plan.getStatus()));

        List<PtSample> samples = ptSampleMapper.selectList(
                new LambdaQueryWrapper<PtSample>().eq(PtSample::getPtPlanId, plan.getId()));
        List<PtSampleVO> sampleVOS = samples.stream()
                .map(s -> BeanUtil.copyProperties(s, PtSampleVO.class))
                .collect(Collectors.toList());
        vo.setSamples(sampleVOS);

        List<PtResult> results = ptResultMapper.selectList(
                new LambdaQueryWrapper<PtResult>().eq(PtResult::getPtPlanId, plan.getId()));
        Map<Long, PtSample> sampleMap = samples.stream()
                .collect(Collectors.toMap(PtSample::getId, s -> s));
        List<PtResultVO> resultVOS = results.stream().map(r -> {
            PtResultVO resultVO = BeanUtil.copyProperties(r, PtResultVO.class);
            PtSample sample = sampleMap.get(r.getPtSampleId());
            if (sample != null) {
                resultVO.setSampleName(sample.getSampleName());
                resultVO.setSampleCode(sample.getSampleCode());
            }
            resultVO.setEvaluationText(getEvaluationText(r.getEvaluation()));
            return resultVO;
        }).collect(Collectors.toList());
        vo.setResults(resultVOS);

        return vo;
    }
}
