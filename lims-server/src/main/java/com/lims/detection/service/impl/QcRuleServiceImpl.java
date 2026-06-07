package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcRuleQuery;
import com.lims.detection.dto.QcRuleSaveDTO;
import com.lims.detection.entity.QcRule;
import com.lims.detection.mapper.QcRuleMapper;
import com.lims.detection.service.QcRuleService;
import com.lims.detection.vo.QcRuleStatsVO;
import com.lims.detection.vo.QcRuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QcRuleServiceImpl extends ServiceImpl<QcRuleMapper, QcRule> implements QcRuleService {

    @Autowired
    private QcRuleMapper qcRuleMapper;

    @Override
    public PageResult<QcRuleVO> selectPage(QcRuleQuery query) {
        LambdaQueryWrapper<QcRule> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(QcRule::getRuleCode, query.getKeyword())
                    .or().like(QcRule::getRuleName, query.getKeyword()));
        }
        if (query.getRuleType() != null) {
            wrapper.eq(QcRule::getRuleType, query.getRuleType());
        }
        wrapper.orderByDesc(QcRule::getCreateTime);

        Page<QcRule> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QcRule> pageResult = this.page(page, wrapper);

        IPage<QcRuleVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public QcRuleStatsVO getStats() {
        QcRuleStatsVO stats = new QcRuleStatsVO();
        stats.setTotal(this.count());
        stats.setEnabled(this.count(new LambdaQueryWrapper<QcRule>().eq(QcRule::getEnabled, 1)));
        stats.setWarning(this.count(new LambdaQueryWrapper<QcRule>().eq(QcRule::getRuleType, 1)));
        stats.setReject(this.count(new LambdaQueryWrapper<QcRule>().eq(QcRule::getRuleType, 2)));
        return stats;
    }

    @Override
    public QcRuleVO getDetail(Long id) {
        QcRule rule = this.getById(id);
        if (rule == null) {
            throw new BizException("质控规则不存在");
        }
        return convertToVO(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(QcRuleSaveDTO dto) {
        QcRule rule = BeanUtil.copyProperties(dto, QcRule.class);
        rule.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : 1);
        this.save(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QcRuleSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("规则ID不能为空");
        }
        QcRule oldRule = this.getById(dto.getId());
        if (oldRule == null) {
            throw new BizException("质控规则不存在");
        }
        QcRule rule = BeanUtil.copyProperties(dto, QcRule.class);
        rule.setVersion(oldRule.getVersion());
        this.updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        QcRule rule = this.getById(id);
        if (rule == null) {
            throw new BizException("质控规则不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggle(Long id, Integer enabled) {
        QcRule rule = this.getById(id);
        if (rule == null) {
            throw new BizException("质控规则不存在");
        }
        rule.setEnabled(enabled);
        this.updateById(rule);
    }

    @Override
    public List<QcRuleVO> getEnabledRules() {
        List<QcRule> rules = this.list(new LambdaQueryWrapper<QcRule>()
                .eq(QcRule::getEnabled, 1)
                .orderByAsc(QcRule::getRuleCode));
        return rules.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private String getRuleTypeName(Integer ruleType) {
        if (ruleType == null) return "";
        switch (ruleType) {
            case 1: return "正态分布规则";
            case 2: return "非参数规则";
            case 3: return "自定义规则";
            default: return "";
        }
    }

    private String getEnabledName(Integer enabled) {
        if (enabled == null) return "";
        return enabled == 1 ? "是" : "否";
    }

    private QcRuleVO convertToVO(QcRule rule) {
        QcRuleVO vo = BeanUtil.copyProperties(rule, QcRuleVO.class);
        vo.setRuleTypeName(getRuleTypeName(rule.getRuleType()));
        vo.setEnabledName(getEnabledName(rule.getEnabled()));
        return vo;
    }
}
