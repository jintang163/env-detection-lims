package com.lims.detection.service;

import com.lims.detection.dto.QcChartDataQuery;
import com.lims.detection.entity.QcData;
import com.lims.detection.entity.QcRule;
import com.lims.detection.vo.QcAnalyzeResultVO;
import com.lims.detection.vo.QcChartDataVO;
import com.lims.detection.vo.QcViolatedRuleVO;
import io.swagger.annotations.Api;

import java.math.BigDecimal;
import java.util.List;

@Api(tags = "质控分析核心服务")
public interface QualityControlService {

    QcAnalyzeResultVO analyze(QcChartDataQuery query);

    QcChartDataVO getChartData(QcChartDataQuery query);

    byte[] exportReport(QcChartDataQuery query);

    List<QcViolatedRuleVO> checkRules(List<QcData> dataList, List<QcRule> rules, BigDecimal mean, BigDecimal sd);
}
