package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.StdCurveCalcDTO;
import com.lims.detection.dto.StdCurveQuery;
import com.lims.detection.dto.StdCurveSaveDTO;
import com.lims.detection.entity.StdCurve;
import com.lims.detection.vo.StdCurveCalcResultVO;
import com.lims.detection.vo.StdCurvePointVO;
import com.lims.detection.vo.StdCurveVO;
import io.swagger.annotations.Api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "标准曲线服务")
public interface StandardCurveService extends IService<StdCurve> {

    PageResult<StdCurveVO> selectPage(StdCurveQuery query);

    StdCurveVO getDetail(Long id);

    void saveCurve(StdCurveSaveDTO dto);

    void updateCurve(StdCurveSaveDTO dto);

    void deleteCurve(Long id);

    StdCurveCalcResultVO calculateCurve(StdCurveCalcDTO dto);

    List<StdCurvePointVO> getCurvePoints(Long curveId);

    List<StdCurveVO> getValidCurves(String itemCode);

    Map<String, Object> getStats();

    Map<String, Object> verifyCurve(Long id, List<BigDecimal> verifyConcentrations, List<BigDecimal> verifyResponses);

    void validateExpire();
}
