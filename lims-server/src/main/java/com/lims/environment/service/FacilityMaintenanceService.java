package com.lims.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvFacilityMaintenancePlanQuery;
import com.lims.environment.dto.EnvFacilityMaintenancePlanSaveDTO;
import com.lims.environment.dto.EnvFacilityMaintenanceQuery;
import com.lims.environment.dto.EnvFacilityMaintenanceSaveDTO;
import com.lims.environment.entity.EnvFacilityMaintenance;
import com.lims.environment.vo.EnvFacilityMaintenancePlanVO;
import com.lims.environment.vo.EnvFacilityMaintenanceVO;

import java.util.List;

public interface FacilityMaintenanceService extends IService<EnvFacilityMaintenance> {

    PageResult<EnvFacilityMaintenanceVO> selectRecordPage(EnvFacilityMaintenanceQuery query);

    void addMaintenanceRecord(EnvFacilityMaintenanceSaveDTO dto);

    void updateMaintenanceRecord(EnvFacilityMaintenanceSaveDTO dto);

    void deleteMaintenanceRecord(Long id);

    EnvFacilityMaintenanceVO getMaintenanceRecordDetail(Long id);

    List<EnvFacilityMaintenanceVO> getRecordByFacility(Long facilityId);

    PageResult<EnvFacilityMaintenancePlanVO> selectPlanPage(EnvFacilityMaintenancePlanQuery query);

    void addMaintenancePlan(EnvFacilityMaintenancePlanSaveDTO dto);

    void updateMaintenancePlan(EnvFacilityMaintenancePlanSaveDTO dto);

    void deleteMaintenancePlan(Long id);

    List<EnvFacilityMaintenancePlanVO> getPlanByFacility(Long facilityId);

    void completeMaintenancePlan(Long planId, EnvFacilityMaintenanceSaveDTO recordDTO);

    void checkMaintenancePlanStatus();
}
