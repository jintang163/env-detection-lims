package com.lims.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.MaintenanceRecordQuery;
import com.lims.equipment.dto.MaintenanceRecordSaveDTO;
import com.lims.equipment.dto.RepairConfirmDTO;
import com.lims.equipment.dto.RepairHandleDTO;
import com.lims.equipment.dto.RepairRequestQuery;
import com.lims.equipment.dto.RepairRequestSaveDTO;
import com.lims.equipment.entity.EqMaintenanceRecord;
import com.lims.equipment.entity.EqRepairRequest;
import com.lims.equipment.vo.MaintenanceRecordVO;
import com.lims.equipment.vo.RepairRequestVO;

import java.util.List;

public interface MaintenanceService extends IService<EqMaintenanceRecord> {

    PageResult<MaintenanceRecordVO> selectMaintenancePage(MaintenanceRecordQuery query);

    void addMaintenanceRecord(MaintenanceRecordSaveDTO dto);

    void updateMaintenanceRecord(MaintenanceRecordSaveDTO dto);

    void deleteMaintenanceRecord(Long id);

    MaintenanceRecordVO getMaintenanceRecordDetail(Long id);

    List<MaintenanceRecordVO> getMaintenanceByEquipmentId(Long equipmentId);

    PageResult<RepairRequestVO> selectRepairPage(RepairRequestQuery query);

    void submitRepairRequest(RepairRequestSaveDTO dto);

    void updateRepairRequest(RepairRequestSaveDTO dto);

    void deleteRepairRequest(Long id);

    RepairRequestVO getRepairRequestDetail(Long id);

    void handleRepair(RepairHandleDTO dto);

    void confirmRepair(RepairConfirmDTO dto);

    void rejectRepair(Long id, String reason);

    List<RepairRequestVO> getRepairByEquipmentId(Long equipmentId);
}
