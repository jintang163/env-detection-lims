package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcPlanQuery;
import com.lims.detection.dto.QcPlanSaveDTO;
import com.lims.detection.entity.QcPlan;
import com.lims.detection.vo.QcPlanStatsVO;
import com.lims.detection.vo.QcPlanVO;
import io.swagger.annotations.Api;

import java.util.List;

@Api(tags = "质控计划服务")
public interface QcPlanService extends IService<QcPlan> {

    PageResult<QcPlanVO> selectPage(QcPlanQuery query);

    QcPlanStatsVO getStats();

    QcPlanVO getDetail(Long id);

    void save(QcPlanSaveDTO dto);

    void update(QcPlanSaveDTO dto);

    void delete(Long id);

    void pause(Long id);

    void resume(Long id);

    List<QcPlanVO> getActivePlans();
}
