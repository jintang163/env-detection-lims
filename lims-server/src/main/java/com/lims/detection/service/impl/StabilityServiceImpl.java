package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.StabilitySchemeQuery;
import com.lims.detection.dto.StabilitySchemeSaveDTO;
import com.lims.detection.dto.StabilityTestPointDTO;
import com.lims.detection.dto.StabilityTestResultDTO;
import com.lims.detection.entity.StabilityScheme;
import com.lims.detection.entity.StabilityTestPoint;
import com.lims.detection.mapper.StabilitySchemeMapper;
import com.lims.detection.mapper.StabilityTestPointMapper;
import com.lims.detection.service.StabilityService;
import com.lims.detection.vo.StabilitySchemeVO;
import com.lims.detection.vo.StabilityTestPointVO;
import com.lims.detection.vo.StabilityTrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StabilityServiceImpl extends ServiceImpl<StabilitySchemeMapper, StabilityScheme> implements StabilityService {

    @Autowired
    private StabilitySchemeMapper stabilitySchemeMapper;

    @Autowired
    private StabilityTestPointMapper stabilityTestPointMapper;

    private static final int SCALE = 4;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_IN_PROGRESS = 1;
    private static final int STATUS_COMPLETED = 2;
    private static final int STATUS_CANCELLED = 3;

    private static final int POINT_STATUS_PENDING = 0;
    private static final int POINT_STATUS_TESTING = 1;
    private static final int POINT_STATUS_COMPLETED = 2;

    private static final int ACCEPTABLE_YES = 1;
    private static final int ACCEPTABLE_NO = 0;

    private static final int[] DEFAULT_TEST_DAYS = {0, 7, 14, 30, 60, 90, 180};

    @Override
    public PageResult<StabilitySchemeVO> selectPage(StabilitySchemeQuery query) {
        LambdaQueryWrapper<StabilityScheme> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(StabilityScheme::getSchemeName, query.getKeyword())
                    .or().like(StabilityScheme::getSampleName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getSchemeType())) {
            wrapper.eq(StabilityScheme::getSchemeType, query.getSchemeType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(StabilityScheme::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(StabilityScheme::getCreateTime);

        Page<StabilityScheme> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<StabilityScheme> pageResult = this.page(page, wrapper);

        IPage<StabilitySchemeVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public StabilitySchemeVO getDetail(Long id) {
        StabilityScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }
        return convertToDetailVO(scheme);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createScheme(StabilitySchemeSaveDTO dto) {
        StabilityScheme scheme = BeanUtil.copyProperties(dto, StabilityScheme.class);
        scheme.setStatus(STATUS_DRAFT);
        scheme.setSchemeNo(generateSchemeNo());
        this.save(scheme);

        List<StabilityTestPoint> testPoints = generateTestPoints(scheme.getId(), dto);
        if (!testPoints.isEmpty()) {
            for (StabilityTestPoint point : testPoints) {
                stabilityTestPointMapper.insert(point);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheme(StabilitySchemeSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("方案ID不能为空");
        }
        StabilityScheme oldScheme = this.getById(dto.getId());
        if (oldScheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }
        if (oldScheme.getStatus() != STATUS_DRAFT) {
            throw new BizException("只有草稿状态的方案才能修改");
        }

        StabilityScheme scheme = BeanUtil.copyProperties(dto, StabilityScheme.class);
        scheme.setVersion(oldScheme.getVersion());
        this.updateById(scheme);

        stabilityTestPointMapper.delete(new LambdaQueryWrapper<StabilityTestPoint>()
                .eq(StabilityTestPoint::getSchemeId, dto.getId()));

        List<StabilityTestPoint> testPoints = generateTestPoints(dto.getId(), dto);
        if (!testPoints.isEmpty()) {
            for (StabilityTestPoint point : testPoints) {
                stabilityTestPointMapper.insert(point);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScheme(Long id) {
        StabilityScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }
        if (scheme.getStatus() == STATUS_IN_PROGRESS) {
            throw new BizException("进行中的方案不能删除");
        }

        stabilityTestPointMapper.delete(new LambdaQueryWrapper<StabilityTestPoint>()
                .eq(StabilityTestPoint::getSchemeId, id));

        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startScheme(Long id) {
        StabilityScheme scheme = this.getById(id);
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }
        if (scheme.getStatus() != STATUS_DRAFT) {
            throw new BizException("只有草稿状态的方案才能启动");
        }

        scheme.setStatus(STATUS_IN_PROGRESS);
        scheme.setStartDate(LocalDateTime.now());
        this.updateById(scheme);

        LambdaQueryWrapper<StabilityTestPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StabilityTestPoint::getSchemeId, id);
        List<StabilityTestPoint> points = stabilityTestPointMapper.selectList(wrapper);

        for (StabilityTestPoint point : points) {
            if (point.getPlanDate() == null && scheme.getStartDate() != null) {
                point.setPlanDate(scheme.getStartDate().plusDays(point.getTestDay()));
                stabilityTestPointMapper.updateById(point);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordResult(Long pointId, StabilityTestResultDTO dto) {
        StabilityTestPoint point = stabilityTestPointMapper.selectById(pointId);
        if (point == null) {
            throw new BizException("检测点不存在");
        }

        StabilityScheme scheme = this.getById(point.getSchemeId());
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }

        BigDecimal value1 = dto.getTestValue1();
        BigDecimal value2 = dto.getTestValue2();
        if (value1 == null || value2 == null) {
            throw new BizException("检测值不能为空");
        }

        BigDecimal avg = value1.add(value2).divide(TWO, SCALE, RoundingMode.HALF_UP);

        BigDecimal diff = value1.subtract(value2).abs();
        BigDecimal rsd = BigDecimal.ZERO;
        if (avg.compareTo(BigDecimal.ZERO) != 0) {
            rsd = diff.divide(avg, SCALE, RoundingMode.HALF_UP).multiply(ONE_HUNDRED);
        }

        BigDecimal deviationRate = BigDecimal.ZERO;
        if (scheme.getInitialValue() != null && scheme.getInitialValue().compareTo(BigDecimal.ZERO) != 0) {
            deviationRate = avg.subtract(scheme.getInitialValue())
                    .divide(scheme.getInitialValue(), SCALE, RoundingMode.HALF_UP)
                    .multiply(ONE_HUNDRED);
        }

        int isAcceptable = ACCEPTABLE_YES;
        BigDecimal acceptableDeviation = extractAcceptableDeviation(scheme.getAcceptanceCriterion());
        if (acceptableDeviation != null && deviationRate.abs().compareTo(acceptableDeviation) > 0) {
            isAcceptable = ACCEPTABLE_NO;
        }

        point.setTestValue1(value1);
        point.setTestValue2(value2);
        point.setTestValueAvg(avg);
        point.setRsd(rsd);
        point.setDeviationRate(deviationRate);
        point.setIsAcceptable(isAcceptable);
        point.setActualDate(LocalDateTime.now());
        point.setOperator(dto.getOperator());
        point.setRemark(dto.getRemark());
        point.setStatus(POINT_STATUS_COMPLETED);

        stabilityTestPointMapper.updateById(point);

        checkSchemeCompletion(point.getSchemeId());
    }

    @Override
    public List<StabilityTrendVO> getTrendData(Long schemeId) {
        StabilityScheme scheme = this.getById(schemeId);
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }

        LambdaQueryWrapper<StabilityTestPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StabilityTestPoint::getSchemeId, schemeId)
                .eq(StabilityTestPoint::getStatus, POINT_STATUS_COMPLETED)
                .orderByAsc(StabilityTestPoint::getTestDay);

        List<StabilityTestPoint> points = stabilityTestPointMapper.selectList(wrapper);

        return points.stream().map(point -> {
            StabilityTrendVO vo = new StabilityTrendVO();
            vo.setTestDay(point.getTestDay());
            vo.setTestDate(point.getActualDate());
            vo.setTestValue(point.getTestValueAvg());
            vo.setDeviationRate(point.getDeviationRate());
            vo.setIsAcceptable(point.getIsAcceptable() == ACCEPTABLE_YES);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer estimateShelfLife(Long schemeId) {
        StabilityScheme scheme = this.getById(schemeId);
        if (scheme == null) {
            throw new BizException("稳定性考察方案不存在");
        }

        LambdaQueryWrapper<StabilityTestPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StabilityTestPoint::getSchemeId, schemeId)
                .eq(StabilityTestPoint::getStatus, POINT_STATUS_COMPLETED)
                .orderByAsc(StabilityTestPoint::getTestDay);

        List<StabilityTestPoint> points = stabilityTestPointMapper.selectList(wrapper);
        if (points.isEmpty()) {
            return null;
        }

        BigDecimal acceptableDeviation = extractAcceptableDeviation(scheme.getAcceptanceCriterion());
        if (acceptableDeviation == null) {
            acceptableDeviation = new BigDecimal("5");
        }

        List<StabilityTestPoint> sortedPoints = points.stream()
                .sorted(Comparator.comparingInt(StabilityTestPoint::getTestDay))
                .collect(Collectors.toList());

        for (StabilityTestPoint point : sortedPoints) {
            if (point.getDeviationRate() != null &&
                    point.getDeviationRate().abs().compareTo(acceptableDeviation) > 0) {
                return point.getTestDay();
            }
        }

        if (scheme.getDurationDays() != null) {
            return scheme.getDurationDays();
        }

        return sortedPoints.get(sortedPoints.size() - 1).getTestDay();
    }

    @Override
    public byte[] generateReport(Long schemeId) {
        throw new BizException("考察报告生成功能待实现");
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", this.count());
        stats.put("inProgress", this.count(new LambdaQueryWrapper<StabilityScheme>()
                .eq(StabilityScheme::getStatus, STATUS_IN_PROGRESS)));
        stats.put("completed", this.count(new LambdaQueryWrapper<StabilityScheme>()
                .eq(StabilityScheme::getStatus, STATUS_COMPLETED)));
        stats.put("draft", this.count(new LambdaQueryWrapper<StabilityScheme>()
                .eq(StabilityScheme::getStatus, STATUS_DRAFT)));
        return stats;
    }

    private String generateSchemeNo() {
        return "STB" + System.currentTimeMillis();
    }

    private List<StabilityTestPoint> generateTestPoints(Long schemeId, StabilitySchemeSaveDTO dto) {
        List<StabilityTestPoint> points = new ArrayList<>();

        if (dto.getTestPoints() != null && !dto.getTestPoints().isEmpty()) {
            int pointNo = 1;
            for (StabilityTestPointDTO pointDTO : dto.getTestPoints()) {
                StabilityTestPoint point = new StabilityTestPoint();
                point.setSchemeId(schemeId);
                point.setPointNo(pointNo++);
                point.setTestDay(pointDTO.getTestDay());
                point.setPlanDate(pointDTO.getPlanDate());
                point.setStatus(POINT_STATUS_PENDING);
                points.add(point);
            }
        } else {
            LocalDateTime startDate = dto.getStartDate() != null ? dto.getStartDate() : LocalDateTime.now();
            int pointNo = 1;
            for (int day : DEFAULT_TEST_DAYS) {
                if (dto.getDurationDays() != null && day > dto.getDurationDays()) {
                    continue;
                }
                StabilityTestPoint point = new StabilityTestPoint();
                point.setSchemeId(schemeId);
                point.setPointNo(pointNo++);
                point.setTestDay(day);
                point.setPlanDate(startDate.plusDays(day));
                point.setStatus(POINT_STATUS_PENDING);
                points.add(point);
            }
        }

        return points;
    }

    private BigDecimal extractAcceptableDeviation(String acceptanceCriterion) {
        if (StrUtil.isBlank(acceptanceCriterion)) {
            return null;
        }
        try {
            String numStr = acceptanceCriterion.replaceAll("[^0-9.]", "");
            if (StrUtil.isNotBlank(numStr)) {
                return new BigDecimal(numStr);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void checkSchemeCompletion(Long schemeId) {
        LambdaQueryWrapper<StabilityTestPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StabilityTestPoint::getSchemeId, schemeId);
        Long total = stabilityTestPointMapper.selectCount(wrapper);

        wrapper.eq(StabilityTestPoint::getStatus, POINT_STATUS_COMPLETED);
        Long completed = stabilityTestPointMapper.selectCount(wrapper);

        if (total > 0 && total.equals(completed)) {
            StabilityScheme scheme = this.getById(schemeId);
            if (scheme != null) {
                scheme.setStatus(STATUS_COMPLETED);
                scheme.setEndDate(LocalDateTime.now());
                scheme.setEstimatedShelfLife(estimateShelfLife(schemeId));
                this.updateById(scheme);
            }
        }
    }

    private StabilitySchemeVO convertToVO(StabilityScheme scheme) {
        StabilitySchemeVO vo = BeanUtil.copyProperties(scheme, StabilitySchemeVO.class);
        vo.setStatusName(getStatusName(scheme.getStatus()));
        return vo;
    }

    private StabilitySchemeVO convertToDetailVO(StabilityScheme scheme) {
        StabilitySchemeVO vo = convertToVO(scheme);

        LambdaQueryWrapper<StabilityTestPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StabilityTestPoint::getSchemeId, scheme.getId())
                .orderByAsc(StabilityTestPoint::getPointNo);
        List<StabilityTestPoint> points = stabilityTestPointMapper.selectList(wrapper);

        List<StabilityTestPointVO> pointVOs = points.stream()
                .map(this::convertPointToVO)
                .collect(Collectors.toList());

        vo.setTestPoints(pointVOs);
        return vo;
    }

    private StabilityTestPointVO convertPointToVO(StabilityTestPoint point) {
        StabilityTestPointVO vo = BeanUtil.copyProperties(point, StabilityTestPointVO.class);
        vo.setStatusName(getPointStatusName(point.getStatus()));
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case STATUS_DRAFT: return "草稿";
            case STATUS_IN_PROGRESS: return "进行中";
            case STATUS_COMPLETED: return "已完成";
            case STATUS_CANCELLED: return "已取消";
            default: return "";
        }
    }

    private String getPointStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case POINT_STATUS_PENDING: return "待检测";
            case POINT_STATUS_TESTING: return "检测中";
            case POINT_STATUS_COMPLETED: return "已完成";
            default: return "";
        }
    }
}
