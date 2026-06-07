package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcRecordQuery;
import com.lims.detection.entity.QcPlan;
import com.lims.detection.entity.QcRecord;
import com.lims.detection.entity.QcSample;
import com.lims.detection.mapper.QcPlanMapper;
import com.lims.detection.mapper.QcRecordMapper;
import com.lims.detection.mapper.QcSampleMapper;
import com.lims.detection.service.QcRecordService;
import com.lims.detection.vo.QcRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QcRecordServiceImpl extends ServiceImpl<QcRecordMapper, QcRecord> implements QcRecordService {

    @Autowired
    private QcRecordMapper qcRecordMapper;

    @Autowired
    private QcPlanMapper qcPlanMapper;

    @Autowired
    private QcSampleMapper qcSampleMapper;

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_EXECUTED = 1;
    private static final int STATUS_REVIEWED = 2;

    @Override
    public PageResult<QcRecordVO> selectPage(QcRecordQuery query) {
        LambdaQueryWrapper<QcRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(QcRecord::getRecordNo, query.getKeyword())
                    .or().like(QcRecord::getQcSampleName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getStatus())) {
            wrapper.eq(QcRecord::getStatus, query.getStatus());
        }
        if (query.getPlanId() != null) {
            wrapper.eq(QcRecord::getPlanId, query.getPlanId());
        }
        wrapper.orderByDesc(QcRecord::getCreateTime);

        Page<QcRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QcRecord> pageResult = this.page(page, wrapper);

        IPage<QcRecordVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public QcRecordVO getDetail(Long id) {
        QcRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("质控记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id, BigDecimal measuredValue, String operator) {
        QcRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("质控记录不存在");
        }
        if (record.getStatus() != STATUS_DRAFT) {
            throw new BizException("只能执行草稿状态的记录");
        }

        record.setMeasuredValue(measuredValue);
        record.setOperator(operator);
        record.setExecuteDate(LocalDateTime.now());
        record.setStatus(STATUS_EXECUTED);

        this.updateById(record);
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case STATUS_DRAFT: return "草稿";
            case STATUS_EXECUTED: return "已执行";
            case STATUS_REVIEWED: return "已审核";
            default: return "";
        }
    }

    private QcRecordVO convertToVO(QcRecord record) {
        QcRecordVO vo = BeanUtil.copyProperties(record, QcRecordVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setSampleId(record.getQcSampleId());
        vo.setSampleName(record.getQcSampleName());
        vo.setSampleType(record.getQcSampleType());

        if (record.getPlanId() != null) {
            QcPlan plan = qcPlanMapper.selectById(record.getPlanId());
            if (plan != null) {
                vo.setPlanName(plan.getPlanName());
                vo.setProjectName(plan.getProjectName());
                vo.setMethodName(plan.getMethodName());
            }
        }

        if (record.getQcSampleId() != null) {
            QcSample sample = qcSampleMapper.selectById(record.getQcSampleId());
            if (sample != null) {
                vo.setConcentration(sample.getConcentration());
                vo.setUnit(sample.getUnit());
            }
        }

        return vo;
    }
}
