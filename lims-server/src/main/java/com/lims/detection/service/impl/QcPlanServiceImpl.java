package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcPlanQuery;
import com.lims.detection.dto.QcPlanSaveDTO;
import com.lims.detection.dto.QcSampleConfigDTO;
import com.lims.detection.entity.QcPlan;
import com.lims.detection.mapper.QcPlanMapper;
import com.lims.detection.service.QcPlanService;
import com.lims.detection.vo.QcPlanStatsVO;
import com.lims.detection.vo.QcPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QcPlanServiceImpl extends ServiceImpl<QcPlanMapper, QcPlan> implements QcPlanService {

    @Autowired
    private QcPlanMapper qcPlanMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_PAUSED = 2;
    private static final int STATUS_ENDED = 3;

    @Override
    public PageResult<QcPlanVO> selectPage(QcPlanQuery query) {
        LambdaQueryWrapper<QcPlan> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(QcPlan::getPlanNo, query.getKeyword())
                    .or().like(QcPlan::getPlanName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getCycleType())) {
            wrapper.eq(QcPlan::getCycleType, query.getCycleType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(QcPlan::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(QcPlan::getCreateTime);

        Page<QcPlan> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QcPlan> pageResult = this.page(page, wrapper);

        IPage<QcPlanVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public QcPlanStatsVO getStats() {
        QcPlanStatsVO stats = new QcPlanStatsVO();
        stats.setTotal(this.count());
        stats.setActive(this.count(new LambdaQueryWrapper<QcPlan>().eq(QcPlan::getStatus, STATUS_ACTIVE)));
        stats.setToday(0);
        stats.setMissed(0);
        return stats;
    }

    @Override
    public QcPlanVO getDetail(Long id) {
        QcPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("质控计划不存在");
        }
        return convertToVO(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(QcPlanSaveDTO dto) {
        QcPlan plan = convertToEntity(dto);
        plan.setStatus(dto.getStatus() != null ? dto.getStatus() : STATUS_DRAFT);
        this.save(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QcPlanSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("计划ID不能为空");
        }
        QcPlan oldPlan = this.getById(dto.getId());
        if (oldPlan == null) {
            throw new BizException("质控计划不存在");
        }
        QcPlan plan = convertToEntity(dto);
        plan.setVersion(oldPlan.getVersion());
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        QcPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("质控计划不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pause(Long id) {
        QcPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("质控计划不存在");
        }
        if (plan.getStatus() != STATUS_ACTIVE) {
            throw new BizException("只有启用状态的计划才能暂停");
        }
        plan.setStatus(STATUS_PAUSED);
        this.updateById(plan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resume(Long id) {
        QcPlan plan = this.getById(id);
        if (plan == null) {
            throw new BizException("质控计划不存在");
        }
        if (plan.getStatus() != STATUS_PAUSED) {
            throw new BizException("只有暂停状态的计划才能启用");
        }
        plan.setStatus(STATUS_ACTIVE);
        this.updateById(plan);
    }

    @Override
    public List<QcPlanVO> getActivePlans() {
        List<QcPlan> plans = this.list(new LambdaQueryWrapper<QcPlan>()
                .eq(QcPlan::getStatus, STATUS_ACTIVE)
                .orderByDesc(QcPlan::getCreateTime));
        return plans.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private QcPlan convertToEntity(QcPlanSaveDTO dto) {
        QcPlan plan = BeanUtil.copyProperties(dto, QcPlan.class);
        try {
            if (dto.getQcSamples() != null && !dto.getQcSamples().isEmpty()) {
                plan.setQcSamples(objectMapper.writeValueAsString(dto.getQcSamples()));
            }
            if (dto.getReminderType() != null && !dto.getReminderType().isEmpty()) {
                plan.setReminderType(objectMapper.writeValueAsString(dto.getReminderType()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }
        return plan;
    }

    private String getCycleTypeName(String cycleType) {
        if (cycleType == null) return "";
        switch (cycleType) {
            case "1": return "每日";
            case "2": return "每周";
            case "3": return "每月";
            case "4": return "每批次";
            default: return "";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case STATUS_DRAFT: return "草稿";
            case STATUS_ACTIVE: return "启用";
            case STATUS_PAUSED: return "暂停";
            case STATUS_ENDED: return "结束";
            default: return "";
        }
    }

    private QcPlanVO convertToVO(QcPlan plan) {
        QcPlanVO vo = BeanUtil.copyProperties(plan, QcPlanVO.class);
        vo.setCycleTypeName(getCycleTypeName(plan.getCycleType()));
        vo.setStatusName(getStatusName(plan.getStatus()));

        try {
            if (StrUtil.isNotBlank(plan.getQcSamples())) {
                vo.setQcSamples(objectMapper.readValue(plan.getQcSamples(), new TypeReference<List<QcSampleConfigDTO>>() {}));
            }
            if (StrUtil.isNotBlank(plan.getReminderType())) {
                vo.setReminderType(objectMapper.readValue(plan.getReminderType(), new TypeReference<List<String>>() {}));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        return vo;
    }
}
