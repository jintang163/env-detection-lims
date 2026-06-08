package com.lims.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.equipment.dto.EquipmentQuery;
import com.lims.equipment.dto.EquipmentSaveDTO;
import com.lims.equipment.entity.EqEquipment;
import com.lims.equipment.vo.EquipmentDetailVO;
import com.lims.equipment.vo.EquipmentVO;

import java.util.List;

public interface EquipmentService extends IService<EqEquipment> {

    PageResult<EquipmentVO> selectPage(EquipmentQuery query);

    void addEquipment(EquipmentSaveDTO dto);

    void updateEquipment(EquipmentSaveDTO dto);

    void deleteEquipment(Long id);

    EquipmentDetailVO getEquipmentDetail(Long id);

    List<EquipmentVO> getAvailableEquipmentList();

    void updateEquipmentStatus(Long id, Integer status);

    java.util.Map<String, Object> getStats();
}
