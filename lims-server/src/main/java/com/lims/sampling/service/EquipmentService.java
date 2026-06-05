package com.lims.sampling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.sampling.dto.EquipmentQuery;
import com.lims.sampling.dto.EquipmentSaveDTO;
import com.lims.sampling.entity.SmpEquipment;
import com.lims.sampling.vo.EquipmentVO;

import java.util.List;

public interface EquipmentService extends IService<SmpEquipment> {

    PageResult<EquipmentVO> selectPage(EquipmentQuery query);

    List<EquipmentVO> getAvailableEquipments(String equipmentType);

    void addEquipment(EquipmentSaveDTO dto);

    void updateEquipment(EquipmentSaveDTO dto);

    void deleteEquipment(Long id);

    SmpEquipment getEquipmentDetail(Long id);

    void updateBorrowStatus(Long equipmentId, Integer borrowStatus, Long borrowerId, String borrowerName);
}
