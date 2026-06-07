package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.StdCurveCalcDTO;
import com.lims.detection.dto.StdCurvePointDTO;
import com.lims.detection.dto.StdCurveQuery;
import com.lims.detection.dto.StdCurveSaveDTO;
import com.lims.detection.entity.StdCurve;
import com.lims.detection.entity.StdCurvePoint;
import com.lims.detection.mapper.StdCurveMapper;
import com.lims.detection.mapper.StdCurvePointMapper;
import com.lims.detection.service.StandardCurveService;
import com.lims.detection.vo.StdCurveCalcResultVO;
import com.lims.detection.vo.StdCurvePointVO;
import com.lims.detection.vo.StdCurveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StandardCurveServiceImpl extends ServiceImpl<StdCurveMapper, StdCurve> implements StandardCurveService {

    @Autowired
    private StdCurveMapper stdCurveMapper;

    @Autowired
    private StdCurvePointMapper stdCurvePointMapper;

    private static final int SCALE = 6;
    private static final BigDecimal THREE_POINT_THREE = new BigDecimal("3.3");
    private static final BigDecimal TEN = new BigDecimal("10");

    @Override
    public PageResult<StdCurveVO> selectPage(StdCurveQuery query) {
        LambdaQueryWrapper<StdCurve> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(StdCurve::getCurveNo, query.getKeyword())
                    .or().like(StdCurve::getCurveName, query.getKeyword())
                    .or().like(StdCurve::getItemName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getItemCode())) {
            wrapper.eq(StdCurve::getItemCode, query.getItemCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(StdCurve::getStatus, query.getStatus());
        }
        if (StrUtil.isNotBlank(query.getFitType())) {
            wrapper.eq(StdCurve::getFitType, query.getFitType());
        }
        wrapper.orderByDesc(StdCurve::getCreateTime);

        Page<StdCurve> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<StdCurve> pageResult = this.page(page, wrapper);

        IPage<StdCurveVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public StdCurveVO getDetail(Long id) {
        StdCurve curve = this.getById(id);
        if (curve == null) {
            throw new BizException("标准曲线不存在");
        }
        StdCurveVO vo = convertToVO(curve);
        vo.setPoints(getCurvePoints(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCurve(StdCurveSaveDTO dto) {
        StdCurveCalcDTO calcDTO = new StdCurveCalcDTO();
        calcDTO.setFitType(dto.getFitType());
        calcDTO.setWeightType(dto.getWeightType());
        calcDTO.setPoints(dto.getCurvePoints());

        StdCurveCalcResultVO calcResult = calculateCurve(calcDTO);

        StdCurve curve = BeanUtil.copyProperties(dto, StdCurve.class);
        curve.setSlope(calcResult.getSlope());
        curve.setIntercept(calcResult.getIntercept());
        curve.setCorrelationCoefficient(calcResult.getCorrelationCoefficient());
        curve.setRSquared(calcResult.getRSquared());
        curve.setLod(calcResult.getLod());
        curve.setLoq(calcResult.getLoq());
        curve.setResidualStd(calcResult.getResidualStd());
        curve.setCurveEquation(calcResult.getCurveEquation());
        curve.setStatus(1);

        if (dto.getCalibrationDate() != null && dto.getValidDays() != null) {
            curve.setExpireDate(dto.getCalibrationDate().plusDays(dto.getValidDays()));
        }

        this.save(curve);

        saveCurvePoints(curve.getId(), dto.getCurvePoints(), calcResult.getPoints());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCurve(StdCurveSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("曲线ID不能为空");
        }
        StdCurve oldCurve = this.getById(dto.getId());
        if (oldCurve == null) {
            throw new BizException("标准曲线不存在");
        }

        StdCurveCalcDTO calcDTO = new StdCurveCalcDTO();
        calcDTO.setFitType(dto.getFitType());
        calcDTO.setWeightType(dto.getWeightType());
        calcDTO.setPoints(dto.getCurvePoints());

        StdCurveCalcResultVO calcResult = calculateCurve(calcDTO);

        StdCurve curve = BeanUtil.copyProperties(dto, StdCurve.class);
        curve.setVersion(oldCurve.getVersion());
        curve.setSlope(calcResult.getSlope());
        curve.setIntercept(calcResult.getIntercept());
        curve.setCorrelationCoefficient(calcResult.getCorrelationCoefficient());
        curve.setRSquared(calcResult.getRSquared());
        curve.setLod(calcResult.getLod());
        curve.setLoq(calcResult.getLoq());
        curve.setResidualStd(calcResult.getResidualStd());
        curve.setCurveEquation(calcResult.getCurveEquation());

        if (dto.getCalibrationDate() != null && dto.getValidDays() != null) {
            curve.setExpireDate(dto.getCalibrationDate().plusDays(dto.getValidDays()));
        }

        this.updateById(curve);

        stdCurvePointMapper.delete(new LambdaQueryWrapper<StdCurvePoint>().eq(StdCurvePoint::getCurveId, dto.getId()));
        saveCurvePoints(dto.getId(), dto.getCurvePoints(), calcResult.getPoints());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCurve(Long id) {
        StdCurve curve = this.getById(id);
        if (curve == null) {
            throw new BizException("标准曲线不存在");
        }
        curve.setStatus(0);
        this.updateById(curve);
    }

    @Override
    public StdCurveCalcResultVO calculateCurve(StdCurveCalcDTO dto) {
        List<StdCurvePointDTO> pointDTOs = dto.getPoints();
        if (pointDTOs == null || pointDTOs.isEmpty()) {
            throw new BizException("曲线点不能为空");
        }

        List<StdCurvePointDTO> validPoints = pointDTOs.stream()
                .filter(p -> p.getIsValid() == null || p.getIsValid())
                .collect(Collectors.toList());

        if (validPoints.size() < 2) {
            throw new BizException("有效曲线点至少需要2个");
        }

        int n = validPoints.size();
        BigDecimal[] x = new BigDecimal[n];
        BigDecimal[] y = new BigDecimal[n];

        for (int i = 0; i < n; i++) {
            StdCurvePointDTO point = validPoints.get(i);
            x[i] = point.getConcentration();
            y[i] = calculateResponseAvg(point);
        }

        BigDecimal sumX = BigDecimal.ZERO;
        BigDecimal sumY = BigDecimal.ZERO;
        for (int i = 0; i < n; i++) {
            sumX = sumX.add(x[i]);
            sumY = sumY.add(y[i]);
        }
        BigDecimal meanX = sumX.divide(new BigDecimal(n), SCALE, RoundingMode.HALF_UP);
        BigDecimal meanY = sumY.divide(new BigDecimal(n), SCALE, RoundingMode.HALF_UP);

        BigDecimal sumXYDiff = BigDecimal.ZERO;
        BigDecimal sumXXDiff = BigDecimal.ZERO;
        BigDecimal sumYYDiff = BigDecimal.ZERO;

        for (int i = 0; i < n; i++) {
            BigDecimal xDiff = x[i].subtract(meanX);
            BigDecimal yDiff = y[i].subtract(meanY);
            sumXYDiff = sumXYDiff.add(xDiff.multiply(yDiff));
            sumXXDiff = sumXXDiff.add(xDiff.multiply(xDiff));
            sumYYDiff = sumYYDiff.add(yDiff.multiply(yDiff));
        }

        BigDecimal slope = sumXYDiff.divide(sumXXDiff, SCALE, RoundingMode.HALF_UP);
        BigDecimal intercept = meanY.subtract(slope.multiply(meanX));

        BigDecimal r = sumXYDiff.divide(
                new BigDecimal(Math.sqrt(sumXXDiff.multiply(sumYYDiff).doubleValue())),
                SCALE, RoundingMode.HALF_UP
        );
        BigDecimal rSquared = r.multiply(r);

        List<StdCurvePointVO> pointVOs = new ArrayList<>();
        BigDecimal sumResidualSquared = BigDecimal.ZERO;
        int validCount = 0;

        for (StdCurvePointDTO pointDTO : pointDTOs) {
            StdCurvePointVO pointVO = BeanUtil.copyProperties(pointDTO, StdCurvePointVO.class);
            pointVO.setIsValid(pointDTO.getIsValid() != null && pointDTO.getIsValid() ? 1 : 0);

            BigDecimal responseAvg = calculateResponseAvg(pointDTO);
            pointVO.setResponseAvg(responseAvg);
            pointVO.setResponseRsd(calculateResponseRsd(pointDTO));

            if (pointDTO.getIsValid() == null || pointDTO.getIsValid()) {
                BigDecimal calculatedValue = slope.multiply(pointDTO.getConcentration()).add(intercept);
                pointVO.setCalculatedValue(calculatedValue);

                BigDecimal residual = responseAvg.subtract(calculatedValue);
                pointVO.setResidual(residual);

                if (calculatedValue.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal relativeError = residual.divide(calculatedValue, SCALE, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                    pointVO.setRelativeError(relativeError);
                }

                sumResidualSquared = sumResidualSquared.add(residual.multiply(residual));
                validCount++;
            }

            pointVOs.add(pointVO);
        }

        BigDecimal residualStd = BigDecimal.ZERO;
        if (validCount > 2) {
            BigDecimal variance = sumResidualSquared.divide(new BigDecimal(validCount - 2), SCALE, RoundingMode.HALF_UP);
            residualStd = new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal lod = BigDecimal.ZERO;
        BigDecimal loq = BigDecimal.ZERO;
        if (slope.compareTo(BigDecimal.ZERO) != 0) {
            lod = THREE_POINT_THREE.multiply(residualStd).divide(slope, SCALE, RoundingMode.HALF_UP);
            loq = TEN.multiply(residualStd).divide(slope, SCALE, RoundingMode.HALF_UP);
        }

        String curveEquation = String.format("y = %sx + %s", slope.toPlainString(), intercept.toPlainString());

        StdCurveCalcResultVO result = new StdCurveCalcResultVO();
        result.setSlope(slope);
        result.setIntercept(intercept);
        result.setCorrelationCoefficient(r);
        result.setRSquared(rSquared);
        result.setResidualStd(residualStd);
        result.setLod(lod);
        result.setLoq(loq);
        result.setCurveEquation(curveEquation);
        result.setPoints(pointVOs);

        return result;
    }

    @Override
    public List<StdCurvePointVO> getCurvePoints(Long curveId) {
        List<StdCurvePoint> points = stdCurvePointMapper.selectList(
                new LambdaQueryWrapper<StdCurvePoint>()
                        .eq(StdCurvePoint::getCurveId, curveId)
                        .orderByAsc(StdCurvePoint::getPointNo)
        );
        return points.stream()
                .map(p -> BeanUtil.copyProperties(p, StdCurvePointVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StdCurveVO> getValidCurves(String itemCode) {
        validateExpire();
        LambdaQueryWrapper<StdCurve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StdCurve::getItemCode, itemCode)
                .eq(StdCurve::getStatus, 1)
                .gt(StdCurve::getExpireDate, LocalDateTime.now())
                .orderByDesc(StdCurve::getCalibrationDate);
        List<StdCurve> curves = this.list(wrapper);
        return curves.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", this.count());
        stats.put("valid", this.count(new LambdaQueryWrapper<StdCurve>().eq(StdCurve::getStatus, 1)));
        stats.put("invalid", this.count(new LambdaQueryWrapper<StdCurve>().eq(StdCurve::getStatus, 0)));

        LocalDateTime now = LocalDateTime.now();
        long expired = this.count(new LambdaQueryWrapper<StdCurve>()
                .eq(StdCurve::getStatus, 1)
                .lt(StdCurve::getExpireDate, now));
        stats.put("expired", expired);

        long expiringSoon = this.count(new LambdaQueryWrapper<StdCurve>()
                .eq(StdCurve::getStatus, 1)
                .ge(StdCurve::getExpireDate, now)
                .lt(StdCurve::getExpireDate, now.plusDays(7)));
        stats.put("expiringSoon", expiringSoon);

        return stats;
    }

    @Override
    public Map<String, Object> verifyCurve(Long id, List<BigDecimal> verifyConcentrations, List<BigDecimal> verifyResponses) {
        if (verifyConcentrations == null || verifyResponses == null || verifyConcentrations.size() != verifyResponses.size()) {
            throw new BizException("验证点浓度和响应值数量不一致");
        }
        if (verifyConcentrations.isEmpty()) {
            throw new BizException("验证点不能为空");
        }

        StdCurve curve = this.getById(id);
        if (curve == null) {
            throw new BizException("标准曲线不存在");
        }

        BigDecimal slope = curve.getSlope();
        BigDecimal intercept = curve.getIntercept();

        List<Map<String, Object>> verifyResults = new ArrayList<>();
        BigDecimal totalRecovery = BigDecimal.ZERO;

        for (int i = 0; i < verifyConcentrations.size(); i++) {
            BigDecimal expectedConcentration = verifyConcentrations.get(i);
            BigDecimal response = verifyResponses.get(i);

            BigDecimal calculatedConcentration = response.subtract(intercept).divide(slope, SCALE, RoundingMode.HALF_UP);
            BigDecimal recovery = calculatedConcentration.divide(expectedConcentration, SCALE, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));

            totalRecovery = totalRecovery.add(recovery);

            Map<String, Object> pointResult = new LinkedHashMap<>();
            pointResult.put("pointNo", i + 1);
            pointResult.put("expectedConcentration", expectedConcentration);
            pointResult.put("response", response);
            pointResult.put("calculatedConcentration", calculatedConcentration);
            pointResult.put("recovery", recovery);
            verifyResults.add(pointResult);
        }

        BigDecimal avgRecovery = totalRecovery.divide(new BigDecimal(verifyConcentrations.size()), SCALE, RoundingMode.HALF_UP);

        Map<String, Object> result = new HashMap<>();
        result.put("curveId", id);
        result.put("curveNo", curve.getCurveNo());
        result.put("avgRecovery", avgRecovery);
        result.put("verifyResults", verifyResults);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validateExpire() {
        LocalDateTime now = LocalDateTime.now();
        List<StdCurve> expiredCurves = this.list(new LambdaQueryWrapper<StdCurve>()
                .eq(StdCurve::getStatus, 1)
                .lt(StdCurve::getExpireDate, now));

        for (StdCurve curve : expiredCurves) {
            curve.setStatus(0);
            this.updateById(curve);
        }
    }

    private BigDecimal calculateResponseAvg(StdCurvePointDTO point) {
        List<BigDecimal> responses = new ArrayList<>();
        if (point.getResponse1() != null) responses.add(point.getResponse1());
        if (point.getResponse2() != null) responses.add(point.getResponse2());
        if (point.getResponse3() != null) responses.add(point.getResponse3());

        if (responses.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal r : responses) {
            sum = sum.add(r);
        }
        return sum.divide(new BigDecimal(responses.size()), SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateResponseRsd(StdCurvePointDTO point) {
        List<BigDecimal> responses = new ArrayList<>();
        if (point.getResponse1() != null) responses.add(point.getResponse1());
        if (point.getResponse2() != null) responses.add(point.getResponse2());
        if (point.getResponse3() != null) responses.add(point.getResponse3());

        if (responses.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal r : responses) {
            sum = sum.add(r);
        }
        BigDecimal mean = sum.divide(new BigDecimal(responses.size()), SCALE, RoundingMode.HALF_UP);

        BigDecimal sumSquaredDiff = BigDecimal.ZERO;
        for (BigDecimal r : responses) {
            BigDecimal diff = r.subtract(mean);
            sumSquaredDiff = sumSquaredDiff.add(diff.multiply(diff));
        }

        BigDecimal variance = sumSquaredDiff.divide(new BigDecimal(responses.size() - 1), SCALE, RoundingMode.HALF_UP);
        BigDecimal sd = new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(SCALE, RoundingMode.HALF_UP);

        if (mean.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return sd.divide(mean, SCALE, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    private void saveCurvePoints(Long curveId, List<StdCurvePointDTO> pointDTOs, List<StdCurvePointVO> pointVOs) {
        for (int i = 0; i < pointDTOs.size(); i++) {
            StdCurvePointDTO dto = pointDTOs.get(i);
            StdCurvePointVO vo = pointVOs.get(i);

            StdCurvePoint point = new StdCurvePoint();
            point.setCurveId(curveId);
            point.setPointNo(dto.getPointNo() != null ? dto.getPointNo() : i + 1);
            point.setConcentration(dto.getConcentration());
            point.setResponse1(dto.getResponse1());
            point.setResponse2(dto.getResponse2());
            point.setResponse3(dto.getResponse3());
            point.setResponseAvg(vo.getResponseAvg());
            point.setResponseRsd(vo.getResponseRsd());
            point.setCalculatedValue(vo.getCalculatedValue());
            point.setResidual(vo.getResidual());
            point.setRelativeError(vo.getRelativeError());
            point.setIsValid(vo.getIsValid());
            point.setRemark(dto.getRemark());

            stdCurvePointMapper.insert(point);
        }
    }

    private StdCurveVO convertToVO(StdCurve curve) {
        return BeanUtil.copyProperties(curve, StdCurveVO.class);
    }
}
