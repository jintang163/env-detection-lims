package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.common.exception.BizException;
import com.lims.detection.dto.QcChartDataQuery;
import com.lims.detection.entity.QcData;
import com.lims.detection.entity.QcRule;
import com.lims.detection.mapper.QcDataMapper;
import com.lims.detection.mapper.QcRuleMapper;
import com.lims.detection.service.QualityControlService;
import com.lims.detection.vo.QcAnalyzeResultVO;
import com.lims.detection.vo.QcChartDataVO;
import com.lims.detection.vo.QcDataVO;
import com.lims.detection.vo.QcViolatedRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QualityControlServiceImpl implements QualityControlService {

    @Autowired
    private QcDataMapper qcDataMapper;

    @Autowired
    private QcRuleMapper qcRuleMapper;

    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal TEN = new BigDecimal("10");
    private static final int SCALE = 4;

    private static final String RULE_1_2S = "1_2s";
    private static final String RULE_1_3S = "1_3s";
    private static final String RULE_2_2S = "2_2s";
    private static final String RULE_R_4S = "R_4s";
    private static final String RULE_4_1S = "4_1s";
    private static final String RULE_10_X = "10_x";

    private static final int STATUS_IN_CONTROL = 0;
    private static final int STATUS_WARNING = 1;
    private static final int STATUS_OUT_CONTROL = 2;

    private static final int TYPE_WARNING = 1;
    private static final int TYPE_REJECT = 2;

    @Override
    public QcAnalyzeResultVO analyze(QcChartDataQuery query) {
        List<QcData> dataList = getQcDataList(query);
        if (dataList.isEmpty()) {
            return buildEmptyAnalyzeResult();
        }

        BigDecimal mean = calculateMean(dataList);
        BigDecimal sd = calculateSD(dataList, mean);

        List<QcRule> enabledRules = getEnabledRules();

        List<QcDataVO> violationData = analyzeViolationData(dataList, enabledRules, mean, sd);

        int total = violationData.size();
        int outControl = (int) violationData.stream().filter(d -> STATUS_OUT_CONTROL == d.getStatus()).count();
        int warning = (int) violationData.stream().filter(d -> STATUS_WARNING == d.getStatus()).count();
        int inControl = total - outControl - warning;

        String overallStatus;
        String overallStatusName;
        if (outControl > 0) {
            overallStatus = "out_control";
            overallStatusName = "失控";
        } else if (warning > 0) {
            overallStatus = "warning";
            overallStatusName = "警告";
        } else {
            overallStatus = "in_control";
            overallStatusName = "在控";
        }

        QcAnalyzeResultVO result = new QcAnalyzeResultVO();
        result.setTotal(total);
        result.setInControl(inControl);
        result.setWarning(warning);
        result.setOutControl(outControl);
        result.setMean(mean);
        result.setSd(sd);
        result.setMeanValue(mean);
        result.setSdValue(sd);
        result.setOverallStatus(overallStatus);
        result.setOverallStatusName(overallStatusName);
        result.setViolationData(violationData);

        return result;
    }

    @Override
    public QcChartDataVO getChartData(QcChartDataQuery query) {
        List<QcData> dataList = getQcDataList(query);
        if (dataList.isEmpty()) {
            return buildEmptyChartData();
        }

        dataList.sort(Comparator.comparing(QcData::getMeasureDate, Comparator.nullsLast(Comparator.naturalOrder())));

        BigDecimal mean = calculateMean(dataList);
        BigDecimal sd = calculateSD(dataList, mean);

        List<String> xData = new ArrayList<>();
        List<BigDecimal> yData = new ArrayList<>();
        List<BigDecimal> zScores = new ArrayList<>();
        List<BigDecimal> cusumPosList = new ArrayList<>();
        List<BigDecimal> cusumNegList = new ArrayList<>();

        BigDecimal cusumPos = BigDecimal.ZERO;
        BigDecimal cusumNeg = BigDecimal.ZERO;
        BigDecimal k = sd.multiply(new BigDecimal("0.5"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");

        for (QcData data : dataList) {
            BigDecimal measuredValue = data.getMeasuredValue();
            if (measuredValue == null) continue;

            xData.add(data.getMeasureDate() != null ? data.getMeasureDate().format(formatter) : "");
            yData.add(measuredValue);

            BigDecimal zScore = calculateZScore(measuredValue, mean, sd);
            zScores.add(zScore);

            BigDecimal deviation = measuredValue.subtract(mean);
            cusumPos = calculateCusum(cusumPos, deviation, k, true);
            cusumNeg = calculateCusum(cusumNeg, deviation, k, false);
            cusumPosList.add(cusumPos);
            cusumNegList.add(cusumNeg);
        }

        List<QcRule> enabledRules = getEnabledRules();
        List<QcDataVO> violationData = analyzeViolationData(dataList, enabledRules, mean, sd);

        QcChartDataVO chartData = new QcChartDataVO();
        chartData.setXData(xData);
        chartData.setYData(yData);
        chartData.setMeanValue(mean);
        chartData.setSdValue(sd);
        chartData.setZScores(zScores);
        chartData.setCusumPos(cusumPosList);
        chartData.setCusumNeg(cusumNegList);
        chartData.setViolationData(violationData);

        return chartData;
    }

    @Override
    public byte[] exportReport(QcChartDataQuery query) {
        throw new BizException("报表导出功能待实现");
    }

    @Override
    public List<QcViolatedRuleVO> checkRules(List<QcData> dataList, List<QcRule> rules, BigDecimal mean, BigDecimal sd) {
        if (dataList == null || dataList.isEmpty() || rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        dataList.sort(Comparator.comparing(QcData::getMeasureDate, Comparator.nullsLast(Comparator.naturalOrder())));

        List<QcViolatedRuleVO> violatedRules = new ArrayList<>();
        Map<String, QcRule> ruleMap = rules.stream().collect(Collectors.toMap(QcRule::getRuleCode, r -> r));

        for (int i = 0; i < dataList.size(); i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) continue;

            BigDecimal zScore = calculateZScore(data.getMeasuredValue(), mean, sd);

            for (QcRule rule : rules) {
                if (checkSingleRule(dataList, i, rule, zScore, mean, sd)) {
                    QcViolatedRuleVO vo = buildViolatedRuleVO(rule);
                    if (!containsRule(violatedRules, vo.getRuleCode())) {
                        violatedRules.add(vo);
                    }
                }
            }
        }

        return violatedRules;
    }

    private List<QcData> getQcDataList(QcChartDataQuery query) {
        LambdaQueryWrapper<QcData> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getProject())) {
            wrapper.eq(QcData::getProject, query.getProject());
        }
        if (query.getSampleId() != null) {
            wrapper.eq(QcData::getSampleId, query.getSampleId());
        }
        if (query.getStartDate() != null) {
            wrapper.ge(QcData::getMeasureDate, query.getStartDate().atStartOfDay());
        }
        if (query.getEndDate() != null) {
            wrapper.le(QcData::getMeasureDate, query.getEndDate().atTime(23, 59, 59));
        }
        wrapper.orderByAsc(QcData::getMeasureDate);

        return qcDataMapper.selectList(wrapper);
    }

    private List<QcRule> getEnabledRules() {
        return qcRuleMapper.selectList(
                new LambdaQueryWrapper<QcRule>()
                        .eq(QcRule::getEnabled, 1)
                        .orderByAsc(QcRule::getRuleCode)
        );
    }

    private BigDecimal calculateMean(List<QcData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (QcData data : dataList) {
            if (data.getMeasuredValue() != null) {
                sum = sum.add(data.getMeasuredValue());
                count++;
            }
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(new BigDecimal(count), SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateSD(List<QcData> dataList, BigDecimal mean) {
        if (dataList == null || dataList.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal sumSquaredDiff = BigDecimal.ZERO;
        int count = 0;

        for (QcData data : dataList) {
            if (data.getMeasuredValue() != null) {
                BigDecimal diff = data.getMeasuredValue().subtract(mean);
                sumSquaredDiff = sumSquaredDiff.add(diff.multiply(diff));
                count++;
            }
        }

        if (count < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal variance = sumSquaredDiff.divide(new BigDecimal(count - 1), SCALE, RoundingMode.HALF_UP);
        return new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateZScore(BigDecimal value, BigDecimal mean, BigDecimal sd) {
        if (sd == null || sd.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return value.subtract(mean).divide(sd, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCusum(BigDecimal currentCusum, BigDecimal deviation, BigDecimal k, boolean isPositive) {
        BigDecimal value;
        if (isPositive) {
            value = currentCusum.add(deviation).subtract(k);
        } else {
            value = currentCusum.subtract(deviation).subtract(k);
        }
        return value.max(BigDecimal.ZERO);
    }

    private boolean checkSingleRule(List<QcData> dataList, int currentIndex, QcRule rule, BigDecimal currentZScore, BigDecimal mean, BigDecimal sd) {
        String ruleCode = rule.getRuleCode();
        if (ruleCode == null) return false;

        BigDecimal sdMultiple = rule.getSdMultiple() != null ? rule.getSdMultiple() : BigDecimal.ZERO;
        Integer consecutivePoints = rule.getConsecutivePoints() != null ? rule.getConsecutivePoints() : 1;

        switch (ruleCode) {
            case RULE_1_2S:
                return check1_2S(currentZScore, sdMultiple);
            case RULE_1_3S:
                return check1_3S(currentZScore, sdMultiple);
            case RULE_2_2S:
                return check2_2S(dataList, currentIndex, mean, sd, sdMultiple, consecutivePoints);
            case RULE_R_4S:
                return checkR_4S(dataList, currentIndex, sd, sdMultiple);
            case RULE_4_1S:
                return check4_1S(dataList, currentIndex, mean, sd, sdMultiple, consecutivePoints);
            case RULE_10_X:
                return check10_x(dataList, currentIndex, mean, consecutivePoints);
            default:
                return checkCustomRule(dataList, currentIndex, rule, mean, sd);
        }
    }

    private boolean check1_2S(BigDecimal zScore, BigDecimal sdMultiple) {
        BigDecimal threshold = sdMultiple.compareTo(BigDecimal.ZERO) > 0 ? sdMultiple : TWO;
        return zScore.abs().compareTo(threshold) > 0;
    }

    private boolean check1_3S(BigDecimal zScore, BigDecimal sdMultiple) {
        BigDecimal threshold = sdMultiple.compareTo(BigDecimal.ZERO) > 0 ? sdMultiple : new BigDecimal("3");
        return zScore.abs().compareTo(threshold) > 0;
    }

    private boolean check2_2S(List<QcData> dataList, int currentIndex, BigDecimal mean, BigDecimal sd, BigDecimal sdMultiple, Integer consecutivePoints) {
        int points = consecutivePoints != null && consecutivePoints > 0 ? consecutivePoints : 2;
        BigDecimal threshold = sdMultiple.compareTo(BigDecimal.ZERO) > 0 ? sdMultiple : TWO;

        if (currentIndex < points - 1) return false;

        boolean allAbove = true;
        boolean allBelow = true;

        for (int i = currentIndex - points + 1; i <= currentIndex; i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) return false;

            BigDecimal z = calculateZScore(data.getMeasuredValue(), mean, sd);
            if (z.compareTo(threshold) <= 0) allAbove = false;
            if (z.compareTo(threshold.negate()) >= 0) allBelow = false;
        }

        return allAbove || allBelow;
    }

    private boolean checkR_4S(List<QcData> dataList, int currentIndex, BigDecimal sd, BigDecimal sdMultiple) {
        if (currentIndex < 1) return false;

        BigDecimal threshold = sdMultiple.compareTo(BigDecimal.ZERO) > 0 ? sdMultiple : FOUR;
        BigDecimal range = threshold.multiply(sd);

        BigDecimal minVal = null;
        BigDecimal maxVal = null;

        for (int i = currentIndex - 1; i <= currentIndex; i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) return false;

            if (minVal == null || data.getMeasuredValue().compareTo(minVal) < 0) {
                minVal = data.getMeasuredValue();
            }
            if (maxVal == null || data.getMeasuredValue().compareTo(maxVal) > 0) {
                maxVal = data.getMeasuredValue();
            }
        }

        return maxVal.subtract(minVal).compareTo(range) > 0;
    }

    private boolean check4_1S(List<QcData> dataList, int currentIndex, BigDecimal mean, BigDecimal sd, BigDecimal sdMultiple, Integer consecutivePoints) {
        int points = consecutivePoints != null && consecutivePoints > 0 ? consecutivePoints : 4;
        BigDecimal threshold = sdMultiple.compareTo(BigDecimal.ZERO) > 0 ? sdMultiple : BigDecimal.ONE;

        if (currentIndex < points - 1) return false;

        boolean allAbove = true;
        boolean allBelow = true;

        for (int i = currentIndex - points + 1; i <= currentIndex; i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) return false;

            BigDecimal z = calculateZScore(data.getMeasuredValue(), mean, sd);
            if (z.compareTo(threshold) <= 0) allAbove = false;
            if (z.compareTo(threshold.negate()) >= 0) allBelow = false;
        }

        return allAbove || allBelow;
    }

    private boolean check10_x(List<QcData> dataList, int currentIndex, BigDecimal mean, Integer consecutivePoints) {
        int points = consecutivePoints != null && consecutivePoints > 0 ? consecutivePoints : 10;

        if (currentIndex < points - 1) return false;

        boolean allAbove = true;
        boolean allBelow = true;

        for (int i = currentIndex - points + 1; i <= currentIndex; i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) return false;

            if (data.getMeasuredValue().compareTo(mean) <= 0) allAbove = false;
            if (data.getMeasuredValue().compareTo(mean) >= 0) allBelow = false;
        }

        return allAbove || allBelow;
    }

    private boolean checkCustomRule(List<QcData> dataList, int currentIndex, QcRule rule, BigDecimal mean, BigDecimal sd) {
        if (dataList.get(currentIndex).getMeasuredValue() == null) return false;

        BigDecimal zScore = calculateZScore(dataList.get(currentIndex).getMeasuredValue(), mean, sd);
        BigDecimal sdMultiple = rule.getSdMultiple() != null ? rule.getSdMultiple() : BigDecimal.ZERO;

        if (sdMultiple.compareTo(BigDecimal.ZERO) > 0) {
            return zScore.abs().compareTo(sdMultiple) > 0;
        }

        return false;
    }

    private List<QcDataVO> analyzeViolationData(List<QcData> dataList, List<QcRule> rules, BigDecimal mean, BigDecimal sd) {
        List<QcDataVO> result = new ArrayList<>();
        dataList.sort(Comparator.comparing(QcData::getMeasureDate, Comparator.nullsLast(Comparator.naturalOrder())));

        for (int i = 0; i < dataList.size(); i++) {
            QcData data = dataList.get(i);
            if (data.getMeasuredValue() == null) continue;

            List<QcViolatedRuleVO> violatedRules = new ArrayList<>();
            BigDecimal zScore = calculateZScore(data.getMeasuredValue(), mean, sd);

            for (QcRule rule : rules) {
                if (checkSingleRule(dataList, i, rule, zScore, mean, sd)) {
                    violatedRules.add(buildViolatedRuleVO(rule));
                }
            }

            QcDataVO vo = BeanUtil.copyProperties(data, QcDataVO.class);
            vo.setTestValue(data.getMeasuredValue());
            vo.setMeasuredValue(data.getMeasuredValue());
            vo.setMean(mean);
            vo.setSd(sd);
            vo.setZScore(zScore);
            vo.setSeqNo(i + 1);
            vo.setUnit(data.getUnit());

            boolean hasReject = violatedRules.stream().anyMatch(r -> TYPE_REJECT == r.getType());
            boolean hasWarning = violatedRules.stream().anyMatch(r -> TYPE_WARNING == r.getType());

            if (hasReject) {
                vo.setStatus(STATUS_OUT_CONTROL);
                vo.setStatusName("失控");
            } else if (hasWarning) {
                vo.setStatus(STATUS_WARNING);
                vo.setStatusName("警告");
            } else {
                vo.setStatus(STATUS_IN_CONTROL);
                vo.setStatusName("在控");
            }

            vo.setViolatedRules(violatedRules);
            result.add(vo);
        }

        return result;
    }

    private QcViolatedRuleVO buildViolatedRuleVO(QcRule rule) {
        QcViolatedRuleVO vo = new QcViolatedRuleVO();
        vo.setRuleCode(rule.getRuleCode());
        vo.setRuleName(rule.getRuleName());
        vo.setDescription(rule.getDescription());
        vo.setFormula(rule.getFormula());

        int type = determineRuleType(rule.getRuleCode());
        vo.setType(type);
        vo.setTypeName(type == TYPE_REJECT ? "失控" : "警告");

        return vo;
    }

    private int determineRuleType(String ruleCode) {
        if (ruleCode == null) return TYPE_WARNING;
        switch (ruleCode) {
            case RULE_1_3S:
            case RULE_2_2S:
            case RULE_R_4S:
            case RULE_4_1S:
            case RULE_10_X:
                return TYPE_REJECT;
            case RULE_1_2S:
            default:
                return TYPE_WARNING;
        }
    }

    private boolean containsRule(List<QcViolatedRuleVO> rules, String ruleCode) {
        return rules.stream().anyMatch(r -> ruleCode.equals(r.getRuleCode()));
    }

    private QcAnalyzeResultVO buildEmptyAnalyzeResult() {
        QcAnalyzeResultVO result = new QcAnalyzeResultVO();
        result.setTotal(0);
        result.setInControl(0);
        result.setWarning(0);
        result.setOutControl(0);
        result.setMean(BigDecimal.ZERO);
        result.setSd(BigDecimal.ZERO);
        result.setMeanValue(BigDecimal.ZERO);
        result.setSdValue(BigDecimal.ZERO);
        result.setOverallStatus("in_control");
        result.setOverallStatusName("在控");
        result.setViolationData(Collections.emptyList());
        return result;
    }

    private QcChartDataVO buildEmptyChartData() {
        QcChartDataVO chartData = new QcChartDataVO();
        chartData.setXData(Collections.emptyList());
        chartData.setYData(Collections.emptyList());
        chartData.setMeanValue(BigDecimal.ZERO);
        chartData.setSdValue(BigDecimal.ZERO);
        chartData.setZScores(Collections.emptyList());
        chartData.setCusumPos(Collections.emptyList());
        chartData.setCusumNeg(Collections.emptyList());
        chartData.setViolationData(Collections.emptyList());
        return chartData;
    }
}
