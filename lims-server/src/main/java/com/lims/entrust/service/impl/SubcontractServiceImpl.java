package com.lims.entrust.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.entrust.dto.SubcontractSaveDTO;
import com.lims.entrust.entity.EntEntrust;
import com.lims.entrust.entity.EntEntrustItem;
import com.lims.entrust.entity.EntSubcontract;
import com.lims.entrust.mapper.EntEntrustItemMapper;
import com.lims.entrust.mapper.EntEntrustMapper;
import com.lims.entrust.mapper.EntSubcontractMapper;
import com.lims.entrust.service.SubcontractService;
import com.lims.entrust.vo.SubcontractVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubcontractServiceImpl implements SubcontractService {

    public static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(0, "待发送");
        STATUS_MAP.put(1, "已发送");
        STATUS_MAP.put(2, "检测中");
        STATUS_MAP.put(3, "已完成");
        STATUS_MAP.put(4, "已取消");
    }

    @Autowired
    private EntSubcontractMapper subcontractMapper;

    @Autowired
    private EntEntrustMapper entrustMapper;

    @Autowired
    private EntEntrustItemMapper entrustItemMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    @Override
    public PageResult<SubcontractVO> page(Long entrustId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<EntSubcontract> wrapper = new LambdaQueryWrapper<>();
        if (entrustId != null) {
            wrapper.eq(EntSubcontract::getEntrustId, entrustId);
        }
        wrapper.orderByDesc(EntSubcontract::getCreateTime);

        Page<EntSubcontract> page = new Page<>(pageNum, pageSize);
        IPage<EntSubcontract> result = subcontractMapper.selectPage(page, wrapper);

        List<SubcontractVO> voList = result.getRecords().stream().map(s -> {
            SubcontractVO vo = BeanUtil.copyProperties(s, SubcontractVO.class);
            vo.setStatusName(STATUS_MAP.get(s.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        PageResult<SubcontractVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
        pageResult.setList(voList);
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SubcontractSaveDTO dto) {
        EntEntrust entrust = entrustMapper.selectById(dto.getEntrustId());
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getStatus() < 2) {
            throw new BizException("只能对已受理的委托单进行分包");
        }

        EntSubcontract subcontract = BeanUtil.copyProperties(dto, EntSubcontract.class);
        subcontract.setSubcontractNo(codeGenerator.generateSubcontractNo());
        subcontract.setStatus(0);
        subcontract.setSubcontractDate(LocalDateTime.now());
        if (dto.getSubcontractAmount() == null) {
            subcontract.setSubcontractAmount(BigDecimal.ZERO);
        }

        subcontractMapper.insert(subcontract);

        if (dto.getItemIds() != null && !dto.getItemIds().isEmpty()) {
            LambdaQueryWrapper<EntEntrustItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.in(EntEntrustItem::getId, dto.getItemIds());
            itemWrapper.eq(EntEntrustItem::getEntrustId, dto.getEntrustId());
            List<EntEntrustItem> items = entrustItemMapper.selectList(itemWrapper);

            for (EntEntrustItem item : items) {
                item.setSubcontractFlag(1);
                item.setSubcontractId(subcontract.getId());
                entrustItemMapper.updateById(item);
            }
        }

        entrust.setSubcontractFlag(1);
        BigDecimal currentSubcontract = entrust.getSubcontractAmount() == null ? BigDecimal.ZERO : entrust.getSubcontractAmount();
        entrust.setSubcontractAmount(currentSubcontract.add(subcontract.getSubcontractAmount()));
        updateEntrustActualAmount(entrust);
        entrustMapper.updateById(entrust);

        return subcontract.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status, String remark) {
        EntSubcontract subcontract = subcontractMapper.selectById(id);
        if (subcontract == null) {
            throw new BizException("分包记录不存在");
        }

        if (subcontract.getStatus() == 3 || subcontract.getStatus() == 4) {
            throw new BizException("已完成或已取消的分包不能变更状态");
        }

        subcontract.setStatus(status);
        if (status == 3) {
            subcontract.setActualDate(LocalDateTime.now());
        }
        subcontract.setRemark(remark);
        subcontractMapper.updateById(subcontract);
    }

    @Override
    public SubcontractVO getDetail(Long id) {
        EntSubcontract subcontract = subcontractMapper.selectById(id);
        if (subcontract == null) {
            throw new BizException("分包记录不存在");
        }

        SubcontractVO vo = BeanUtil.copyProperties(subcontract, SubcontractVO.class);
        vo.setStatusName(STATUS_MAP.get(subcontract.getStatus()));
        return vo;
    }

    private void updateEntrustActualAmount(EntEntrust entrust) {
        BigDecimal totalAmount = entrust.getTotalAmount() == null ? BigDecimal.ZERO : entrust.getTotalAmount();
        BigDecimal discountAmount = entrust.getDiscountAmount() == null ? BigDecimal.ZERO : entrust.getDiscountAmount();
        BigDecimal urgentFee = entrust.getUrgentFee() == null ? BigDecimal.ZERO : entrust.getUrgentFee();
        BigDecimal subcontractAmount = entrust.getSubcontractAmount() == null ? BigDecimal.ZERO : entrust.getSubcontractAmount();
        BigDecimal adjustAmount = entrust.getAdjustAmount() == null ? BigDecimal.ZERO : entrust.getAdjustAmount();

        BigDecimal actualAmount = totalAmount.subtract(discountAmount)
                .add(urgentFee)
                .add(subcontractAmount)
                .subtract(adjustAmount)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        entrust.setActualAmount(actualAmount);
    }
}
