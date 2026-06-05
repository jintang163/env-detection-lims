package com.lims.sampling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.sampling.dto.EquipmentBorrowQuery;
import com.lims.sampling.dto.EquipmentBorrowSaveDTO;
import com.lims.sampling.entity.SmpEquipment;
import com.lims.sampling.entity.SmpEquipmentBorrow;
import com.lims.sampling.mapper.SmpEquipmentBorrowMapper;
import com.lims.sampling.mapper.SmpEquipmentMapper;
import com.lims.sampling.service.EquipmentBorrowService;
import com.lims.sampling.service.EquipmentService;
import com.lims.sampling.vo.EquipmentBorrowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EquipmentBorrowServiceImpl extends ServiceImpl<SmpEquipmentBorrowMapper, SmpEquipmentBorrow> implements EquipmentBorrowService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private SmpEquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentService equipmentService;

    private static final int BORROW_STATUS_NOT_RETURNED = 0;
    private static final int BORROW_STATUS_RETURNED = 1;
    private static final int BORROW_STATUS_OVERDUE = 2;
    private static final int BORROW_STATUS_DAMAGED = 3;
    private static final int BORROW_STATUS_LOST = 4;

    private static final int EQUIPMENT_BORROW_STATUS_AVAILABLE = 0;
    private static final int EQUIPMENT_BORROW_STATUS_BORROWED = 1;

    private String getReturnStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "未归还";
            case 1: return "已归还";
            case 2: return "逾期";
            case 3: return "损坏";
            case 4: return "丢失";
            default: return "";
        }
    }

    @Override
    public PageResult<EquipmentBorrowVO> selectPage(EquipmentBorrowQuery query) {
        LambdaQueryWrapper<SmpEquipmentBorrow> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getBorrowNo())) {
            wrapper.like(SmpEquipmentBorrow::getBorrowNo, query.getBorrowNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentNo())) {
            wrapper.like(SmpEquipmentBorrow::getEquipmentNo, query.getEquipmentNo());
        }
        if (StrUtil.isNotBlank(query.getEquipmentName())) {
            wrapper.like(SmpEquipmentBorrow::getEquipmentName, query.getEquipmentName());
        }
        if (query.getBorrowerId() != null) {
            wrapper.eq(SmpEquipmentBorrow::getBorrowerId, query.getBorrowerId());
        }
        if (StrUtil.isNotBlank(query.getBorrowerName())) {
            wrapper.like(SmpEquipmentBorrow::getBorrowerName, query.getBorrowerName());
        }
        if (query.getReturnStatus() != null) {
            wrapper.eq(SmpEquipmentBorrow::getReturnStatus, query.getReturnStatus());
        }
        if (query.getBorrowDateStart() != null) {
            wrapper.ge(SmpEquipmentBorrow::getBorrowDate, query.getBorrowDateStart());
        }
        if (query.getBorrowDateEnd() != null) {
            wrapper.le(SmpEquipmentBorrow::getBorrowDate, query.getBorrowDateEnd());
        }
        if (query.getPlanId() != null) {
            wrapper.eq(SmpEquipmentBorrow::getPlanId, query.getPlanId());
        }
        wrapper.orderByDesc(SmpEquipmentBorrow::getCreateTime);

        Page<SmpEquipmentBorrow> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SmpEquipmentBorrow> pageResult = this.page(page, wrapper);

        IPage<EquipmentBorrowVO> voPage = pageResult.convert(borrow -> {
            EquipmentBorrowVO vo = BeanUtil.copyProperties(borrow, EquipmentBorrowVO.class);
            vo.setReturnStatusName(getReturnStatusName(borrow.getReturnStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowEquipment(EquipmentBorrowSaveDTO dto) {
        SmpEquipment equipment = equipmentMapper.selectById(dto.getEquipmentId());
        if (equipment == null) {
            throw new BizException("设备不存在");
        }
        if (equipment.getBorrowStatus() == EQUIPMENT_BORROW_STATUS_BORROWED) {
            throw new BizException("设备已被领用");
        }

        SmpEquipmentBorrow borrow = BeanUtil.copyProperties(dto, SmpEquipmentBorrow.class);
        borrow.setBorrowNo(codeGenerator.generateBorrowNo());
        borrow.setEquipmentNo(equipment.getEquipmentNo());
        borrow.setEquipmentName(equipment.getEquipmentName());
        borrow.setSpecification(equipment.getSpecification());
        borrow.setReturnStatus(BORROW_STATUS_NOT_RETURNED);

        this.save(borrow);

        equipmentService.updateBorrowStatus(
                dto.getEquipmentId(),
                EQUIPMENT_BORROW_STATUS_BORROWED,
                dto.getBorrowerId(),
                dto.getBorrowerName()
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnEquipment(Long id, String returnCheck, Integer returnStatus, String damageDesc) {
        SmpEquipmentBorrow borrow = this.getById(id);
        if (borrow == null) {
            throw new BizException("领用记录不存在");
        }
        if (borrow.getReturnStatus() != BORROW_STATUS_NOT_RETURNED) {
            throw new BizException("设备已归还");
        }

        borrow.setActualReturnDate(LocalDate.now());
        borrow.setReturnCheck(returnCheck);
        borrow.setDamageDesc(damageDesc);

        if (returnStatus != null) {
            borrow.setReturnStatus(returnStatus);
        } else {
            if (borrow.getExpectedReturnDate() != null && LocalDate.now().isAfter(borrow.getExpectedReturnDate())) {
                borrow.setReturnStatus(BORROW_STATUS_OVERDUE);
            } else {
                borrow.setReturnStatus(BORROW_STATUS_RETURNED);
            }
        }

        this.updateById(borrow);

        if (borrow.getReturnStatus() == BORROW_STATUS_RETURNED || borrow.getReturnStatus() == BORROW_STATUS_OVERDUE) {
            equipmentService.updateBorrowStatus(
                    borrow.getEquipmentId(),
                    EQUIPMENT_BORROW_STATUS_AVAILABLE,
                    null,
                    null
            );
        }
    }

    @Override
    public SmpEquipmentBorrow getBorrowDetail(Long id) {
        SmpEquipmentBorrow borrow = this.getById(id);
        if (borrow == null) {
            throw new BizException("领用记录不存在");
        }
        return borrow;
    }
}
