package com.lims.sampling.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.sampling.dto.SampleTransferQuery;
import com.lims.sampling.dto.SampleTransferSaveDTO;
import com.lims.sampling.entity.SmpSampleRecord;
import com.lims.sampling.entity.SmpSampleTransfer;
import com.lims.sampling.mapper.SmpSampleRecordMapper;
import com.lims.sampling.mapper.SmpSampleTransferMapper;
import com.lims.sampling.service.SampleTransferService;
import com.lims.sampling.vo.SampleTransferVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SampleTransferServiceImpl extends ServiceImpl<SmpSampleTransferMapper, SmpSampleTransfer> implements SampleTransferService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private SmpSampleRecordMapper sampleRecordMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int TRANSFER_TYPE_SAMPLING = 1;
    private static final int TRANSFER_TYPE_DELIVERY = 2;

    private static final int TRANSFER_STATUS_PENDING = 0;
    private static final int TRANSFER_STATUS_CONFIRMED = 1;
    private static final int TRANSFER_STATUS_REJECTED = 2;

    private String getTransferTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "采样交样";
            case 2: return "样品送样";
            default: return "";
        }
    }

    private String getTransferStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待确认";
            case 1: return "已确认";
            case 2: return "已驳回";
            default: return "";
        }
    }

    @Override
    public PageResult<SampleTransferVO> selectPage(SampleTransferQuery query) {
        LambdaQueryWrapper<SmpSampleTransfer> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getTransferNo())) {
            wrapper.like(SmpSampleTransfer::getTransferNo, query.getTransferNo());
        }
        if (StrUtil.isNotBlank(query.getPlanNo())) {
            wrapper.like(SmpSampleTransfer::getPlanNo, query.getPlanNo());
        }
        if (query.getTransferType() != null) {
            wrapper.eq(SmpSampleTransfer::getTransferType, query.getTransferType());
        }
        if (query.getSamplerId() != null) {
            wrapper.eq(SmpSampleTransfer::getSamplerId, query.getSamplerId());
        }
        if (StrUtil.isNotBlank(query.getSamplerName())) {
            wrapper.like(SmpSampleTransfer::getSamplerName, query.getSamplerName());
        }
        if (query.getReceiverId() != null) {
            wrapper.eq(SmpSampleTransfer::getReceiverId, query.getReceiverId());
        }
        if (query.getTransferStatus() != null) {
            wrapper.eq(SmpSampleTransfer::getTransferStatus, query.getTransferStatus());
        }
        if (query.getTransferTimeStart() != null) {
            wrapper.ge(SmpSampleTransfer::getTransferTime, query.getTransferTimeStart());
        }
        if (query.getTransferTimeEnd() != null) {
            wrapper.le(SmpSampleTransfer::getTransferTime, query.getTransferTimeEnd());
        }
        wrapper.orderByDesc(SmpSampleTransfer::getCreateTime);

        Page<SmpSampleTransfer> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<SmpSampleTransfer> pageResult = this.page(page, wrapper);

        IPage<SampleTransferVO> voPage = pageResult.convert(transfer -> {
            SampleTransferVO vo = BeanUtil.copyProperties(transfer, SampleTransferVO.class);
            vo.setTransferTypeName(getTransferTypeName(transfer.getTransferType()));
            vo.setTransferStatusName(getTransferStatusName(transfer.getTransferStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransfer(SampleTransferSaveDTO dto) {
        SmpSampleTransfer transfer = BeanUtil.copyProperties(dto, SmpSampleTransfer.class);
        transfer.setTransferNo(codeGenerator.generateTransferNo());
        transfer.setTransferStatus(TRANSFER_STATUS_PENDING);
        transfer.setTransferTime(LocalDateTime.now());

        if (dto.getSampleIdList() != null && !dto.getSampleIdList().isEmpty()) {
            transfer.setSampleCount(dto.getSampleIdList().size());
            try {
                transfer.setSampleIds(objectMapper.writeValueAsString(dto.getSampleIdList()));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化样品ID列表失败");
            }
        }

        this.save(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmTransfer(Long id) {
        SmpSampleTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BizException("交接单不存在");
        }
        if (transfer.getTransferStatus() != TRANSFER_STATUS_PENDING) {
            throw new BizException("仅待确认状态的交接单可确认");
        }

        transfer.setTransferStatus(TRANSFER_STATUS_CONFIRMED);
        transfer.setConfirmTime(LocalDateTime.now());
        this.updateById(transfer);

        if (StrUtil.isNotBlank(transfer.getSampleIds())) {
            try {
                List<Long> sampleIdList = objectMapper.readValue(
                        transfer.getSampleIds(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class)
                );
                for (Long sampleId : sampleIdList) {
                    SmpSampleRecord sample = sampleRecordMapper.selectById(sampleId);
                    if (sample != null) {
                        sample.setSampleStatus("已交接");
                        sampleRecordMapper.updateById(sample);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析样品ID列表失败");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTransfer(Long id, String reason) {
        SmpSampleTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BizException("交接单不存在");
        }
        if (transfer.getTransferStatus() != TRANSFER_STATUS_PENDING) {
            throw new BizException("仅待确认状态的交接单可驳回");
        }

        transfer.setTransferStatus(TRANSFER_STATUS_REJECTED);
        transfer.setRejectReason(reason);
        transfer.setConfirmTime(LocalDateTime.now());
        this.updateById(transfer);
    }

    @Override
    public SmpSampleTransfer getTransferDetail(Long id) {
        SmpSampleTransfer transfer = this.getById(id);
        if (transfer == null) {
            throw new BizException("交接单不存在");
        }
        return transfer;
    }
}
