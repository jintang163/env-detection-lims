package com.lims.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvMonitorDataQuery;
import com.lims.environment.dto.EnvMonitorDataSaveDTO;
import com.lims.environment.dto.EnvMonitorThresholdSaveDTO;
import com.lims.environment.dto.EnvWarningHandleDTO;
import com.lims.environment.dto.EnvWarningQuery;
import com.lims.environment.entity.EnvMonitorData;
import com.lims.environment.vo.EnvMonitorDataVO;
import com.lims.environment.vo.EnvMonitorStatsVO;
import com.lims.environment.vo.EnvMonitorThresholdVO;
import com.lims.environment.vo.EnvWarningRecordVO;

import java.time.LocalDateTime;
import java.util.List;

public interface EnvironmentMonitorService extends IService<EnvMonitorData> {

    PageResult<EnvMonitorDataVO> selectDataPage(EnvMonitorDataQuery query);

    void addMonitorData(EnvMonitorDataSaveDTO dto);

    void updateMonitorData(EnvMonitorDataSaveDTO dto);

    void deleteMonitorData(Long id);

    EnvMonitorDataVO getMonitorDataDetail(Long id);

    PageResult<EnvMonitorThresholdVO> selectThresholdPage();

    void addThreshold(EnvMonitorThresholdSaveDTO dto);

    void updateThreshold(EnvMonitorThresholdSaveDTO dto);

    void deleteThreshold(Long id);

    void toggleThreshold(Long id, Integer isEnabled);

    PageResult<EnvWarningRecordVO> selectWarningPage(EnvWarningQuery query);

    void handleWarning(EnvWarningHandleDTO dto);

    void ignoreWarning(Long id);

    List<EnvMonitorDataVO> getRealtimeData(String monitorPoint);

    EnvMonitorStatsVO getMonitorStats();

    List<EnvMonitorDataVO> getHistoryData(String monitorPoint, Integer monitorType, LocalDateTime startTime, LocalDateTime endTime);
}
