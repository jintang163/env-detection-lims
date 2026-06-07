package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcSampleQuery;
import com.lims.detection.dto.QcSampleSaveDTO;
import com.lims.detection.entity.QcSample;
import com.lims.detection.mapper.QcSampleMapper;
import com.lims.detection.service.QcSampleService;
import com.lims.detection.vo.QcSampleStatsVO;
import com.lims.detection.vo.QcSampleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QcSampleServiceImpl extends ServiceImpl<QcSampleMapper, QcSample> implements QcSampleService {

    @Autowired
    private QcSampleMapper qcSampleMapper;

    private static final int EXPIRE_STATUS_VALID = 0;
    private static final int EXPIRE_STATUS_WARNING = 1;
    private static final int EXPIRE_STATUS_EXPIRED = 2;

    @Override
    public PageResult<QcSampleVO> selectPage(QcSampleQuery query) {
        LambdaQueryWrapper<QcSample> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(QcSample::getCreateTime);

        Page<QcSample> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QcSample> pageResult = this.page(page, wrapper);

        IPage<QcSampleVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    private LambdaQueryWrapper<QcSample> buildQueryWrapper(QcSampleQuery query) {
        LambdaQueryWrapper<QcSample> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(QcSample::getSampleNo, query.getKeyword())
                    .or().like(QcSample::getSampleName, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getSampleType())) {
            wrapper.eq(QcSample::getSampleType, query.getSampleType());
        }
        LocalDate today = LocalDate.now();
        if (query.getExpireStatus() != null) {
            switch (query.getExpireStatus()) {
                case EXPIRE_STATUS_VALID:
                    wrapper.gt(QcSample::getExpireDate, today);
                    break;
                case EXPIRE_STATUS_WARNING:
                    wrapper.le(QcSample::getExpireDate, today.plusDays(30))
                            .gt(QcSample::getExpireDate, today);
                    break;
                case EXPIRE_STATUS_EXPIRED:
                    wrapper.le(QcSample::getExpireDate, today);
                    break;
                default:
                    break;
            }
        }
        return wrapper;
    }

    @Override
    public QcSampleStatsVO getStats() {
        LocalDate today = LocalDate.now();
        QcSampleStatsVO stats = new QcSampleStatsVO();
        stats.setTotal(this.count());
        stats.setValid(this.count(new LambdaQueryWrapper<QcSample>().gt(QcSample::getExpireDate, today)));
        stats.setWarning(this.count(new LambdaQueryWrapper<QcSample>()
                .le(QcSample::getExpireDate, today.plusDays(30))
                .gt(QcSample::getExpireDate, today)));
        stats.setExpired(this.count(new LambdaQueryWrapper<QcSample>().le(QcSample::getExpireDate, today)));
        return stats;
    }

    @Override
    public QcSampleVO getDetail(Long id) {
        QcSample sample = this.getById(id);
        if (sample == null) {
            throw new BizException("质控样品不存在");
        }
        return convertToVO(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(QcSampleSaveDTO dto) {
        QcSample sample = BeanUtil.copyProperties(dto, QcSample.class);
        this.save(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QcSampleSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("样品ID不能为空");
        }
        QcSample oldSample = this.getById(dto.getId());
        if (oldSample == null) {
            throw new BizException("质控样品不存在");
        }
        QcSample sample = BeanUtil.copyProperties(dto, QcSample.class);
        sample.setVersion(oldSample.getVersion());
        this.updateById(sample);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        QcSample sample = this.getById(id);
        if (sample == null) {
            throw new BizException("质控样品不存在");
        }
        this.removeById(id);
    }

    @Override
    public List<QcSampleVO> getValidSamples() {
        LocalDate today = LocalDate.now();
        List<QcSample> samples = this.list(new LambdaQueryWrapper<QcSample>()
                .gt(QcSample::getExpireDate, today)
                .orderByDesc(QcSample::getCreateTime));
        return samples.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private Integer getExpireStatus(LocalDate expireDate) {
        if (expireDate == null) {
            return EXPIRE_STATUS_VALID;
        }
        LocalDate today = LocalDate.now();
        if (expireDate.isBefore(today) || expireDate.isEqual(today)) {
            return EXPIRE_STATUS_EXPIRED;
        } else if (expireDate.isBefore(today.plusDays(30)) || expireDate.isEqual(today.plusDays(30))) {
            return EXPIRE_STATUS_WARNING;
        }
        return EXPIRE_STATUS_VALID;
    }

    private String getSampleTypeName(String sampleType) {
        if (sampleType == null) return "";
        switch (sampleType) {
            case "1": return "标准物质";
            case "2": return "质控样";
            case "3": return "空白样";
            case "4": return "平行样";
            default: return "";
        }
    }

    private String getExpireStatusName(Integer expireStatus) {
        if (expireStatus == null) return "";
        switch (expireStatus) {
            case EXPIRE_STATUS_VALID: return "未过期";
            case EXPIRE_STATUS_WARNING: return "即将过期";
            case EXPIRE_STATUS_EXPIRED: return "已过期";
            default: return "";
        }
    }

    private QcSampleVO convertToVO(QcSample sample) {
        QcSampleVO vo = BeanUtil.copyProperties(sample, QcSampleVO.class);
        vo.setSampleTypeName(getSampleTypeName(sample.getSampleType()));
        Integer expireStatus = getExpireStatus(sample.getExpireDate());
        vo.setExpireStatus(expireStatus);
        vo.setExpireStatusName(getExpireStatusName(expireStatus));
        return vo;
    }
}
