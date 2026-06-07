package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.StabilitySchemeQuery;
import com.lims.detection.dto.StabilitySchemeSaveDTO;
import com.lims.detection.dto.StabilityTestResultDTO;
import com.lims.detection.entity.StabilityScheme;
import com.lims.detection.vo.StabilitySchemeVO;
import com.lims.detection.vo.StabilityTrendVO;
import io.swagger.annotations.Api;

import java.util.List;
import java.util.Map;

@Api(tags = "稳定性考察服务")
public interface StabilityService extends IService<StabilityScheme> {

    PageResult<StabilitySchemeVO> selectPage(StabilitySchemeQuery query);

    StabilitySchemeVO getDetail(Long id);

    void createScheme(StabilitySchemeSaveDTO dto);

    void updateScheme(StabilitySchemeSaveDTO dto);

    void deleteScheme(Long id);

    void startScheme(Long id);

    void recordResult(Long pointId, StabilityTestResultDTO dto);

    List<StabilityTrendVO> getTrendData(Long schemeId);

    Integer estimateShelfLife(Long schemeId);

    byte[] generateReport(Long schemeId);

    Map<String, Object> getStats();
}
