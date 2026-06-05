package com.lims.sampling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.sampling.dto.EquipmentBorrowQuery;
import com.lims.sampling.dto.EquipmentBorrowSaveDTO;
import com.lims.sampling.entity.SmpEquipmentBorrow;
import com.lims.sampling.vo.EquipmentBorrowVO;

public interface EquipmentBorrowService extends IService<SmpEquipmentBorrow> {

    PageResult<EquipmentBorrowVO> selectPage(EquipmentBorrowQuery query);

    void borrowEquipment(EquipmentBorrowSaveDTO dto);

    void returnEquipment(Long id, String returnCheck, Integer returnStatus, String damageDesc);

    SmpEquipmentBorrow getBorrowDetail(Long id);
}
