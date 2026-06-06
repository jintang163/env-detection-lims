package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.ScheduleGenerateDTO;
import com.lims.detection.entity.DetSchedulePlan;
import com.lims.detection.vo.GanttChartVO;
import com.lims.detection.vo.SchedulePlanVO;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService extends IService<DetSchedulePlan> {

    List<SchedulePlanVO> generateSchedulePlans(ScheduleGenerateDTO dto);

    PageResult<SchedulePlanVO> selectPage(Integer pageNum, Integer pageSize);

    SchedulePlanVO getDetail(Long id);

    void applySchedulePlan(Long id);

    GanttChartVO getGanttChart(String resourceType, LocalDate startDate, LocalDate endDate);
}
