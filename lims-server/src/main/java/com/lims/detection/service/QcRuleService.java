package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcRuleQuery;
import com.lims.detection.dto.QcRuleSaveDTO;
import com.lims.detection.entity.QcRule;
import com.lims.detection.vo.QcRuleStatsVO;
import com.lims.detection.vo.QcRuleVO;
import io.swagger.annotations.Api;

import java.util.List;

@Api(tags = "质控规则服务")
public interface QcRuleService extends IService<QcRule> {

    PageResult<QcRuleVO> selectPage(QcRuleQuery query);

    QcRuleStatsVO getStats();

    QcRuleVO getDetail(Long id);

    void save(QcRuleSaveDTO dto);

    void update(QcRuleSaveDTO dto);

    void delete(Long id);

    void toggle(Long id, Integer enabled);

    List<QcRuleVO> getEnabledRules();
}
