package com.lims.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.EquipmentUsageQuery;
import com.lims.equipment.dto.EquipmentUsageSaveDTO;
import com.lims.equipment.entity.EqEquipmentUsage;
import com.lims.equipment.vo.EquipmentUsageVO;

import java.util.List;

public interface EquipmentUsageService extends IService<EqEquipmentUsage> {

    PageResult<EquipmentUsageVO> selectPage(EquipmentUsageQuery query);

    void addUsage(EquipmentUsageSaveDTO dto);

    void updateUsage(EquipmentUsageSaveDTO dto);

    void deleteUsage(Long id);

    EquipmentUsageVO getUsageDetail(Long id);

    void startUsage(EquipmentUsageSaveDTO dto);

    void endUsage(Long id, Integer runningStatus, String anomalyDescription);

    List<EquipmentUsageVO> getUsageByEquipmentId(Long equipmentId);

    List<EquipmentUsageVO> getUsageByTaskId(Long taskId);
}
