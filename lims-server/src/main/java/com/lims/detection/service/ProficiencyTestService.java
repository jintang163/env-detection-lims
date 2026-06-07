package com.lims.detection.service;

import com.lims.common.page.PageResult;
import com.lims.detection.dto.PtPlanQuery;
import com.lims.detection.dto.PtPlanSaveDTO;
import com.lims.detection.dto.PtResultReportDTO;
import com.lims.detection.dto.PtResultSaveDTO;
import com.lims.detection.vo.PtPlanVO;
import com.lims.detection.vo.PtResultVO;
import com.lims.detection.vo.PtYoudenDataVO;
import io.swagger.annotations.Api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Api(tags = "能力验证服务")
public interface ProficiencyTestService {

    PageResult<PtPlanVO> selectPage(PtPlanQuery query);

    PtPlanVO getDetail(Long id);

    void save(PtPlanSaveDTO dto);

    void update(PtPlanSaveDTO dto);

    void delete(Long id);

    void register(Long id);

    void receiveSample(Long id);

    void saveResult(PtResultSaveDTO dto);

    void reportResult(PtResultReportDTO dto);

    List<PtResultVO> getZScoreAnalysis(Long id);

    List<PtYoudenDataVO> getYoudenData(Long planId);

    Map<String, Object> getStats();

    BigDecimal calculateZScore(BigDecimal detectedValue, BigDecimal assignedValue, BigDecimal targetSd);

    String getEvaluation(BigDecimal zScore);
}
