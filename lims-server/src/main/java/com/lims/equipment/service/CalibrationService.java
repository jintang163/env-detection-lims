package com.lims.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.CalibrationPlanQuery;
import com.lims.equipment.dto.CalibrationPlanSaveDTO;
import com.lims.equipment.dto.CalibrationRecordQuery;
import com.lims.equipment.dto.CalibrationRecordSaveDTO;
import com.lims.equipment.entity.EqCalibrationPlan;
import com.lims.equipment.entity.EqCalibrationRecord;
import com.lims.equipment.vo.CalibrationPlanVO;
import com.lims.equipment.vo.CalibrationRecordVO;

import java.util.List;

public interface CalibrationService extends IService<EqCalibrationPlan> {

    PageResult<CalibrationPlanVO> selectPlanPage(CalibrationPlanQuery query);

    void addPlan(CalibrationPlanSaveDTO dto);

    void updatePlan(CalibrationPlanSaveDTO dto);

    void deletePlan(Long id);

    CalibrationPlanVO getPlanDetail(Long id);

    PageResult<CalibrationRecordVO> selectRecordPage(CalibrationRecordQuery query);

    void addRecord(CalibrationRecordSaveDTO dto);

    void updateRecord(CalibrationRecordSaveDTO dto);

    void deleteRecord(Long id);

    CalibrationRecordVO getRecordDetail(Long id);

    List<CalibrationPlanVO> getUpcomingCalibrations();

    void checkAndUpdatePlanStatus();

    List<CalibrationRecordVO> getRecordsByEquipmentId(Long equipmentId);
}
