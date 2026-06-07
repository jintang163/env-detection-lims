package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcSamplePrepareQuery;
import com.lims.detection.dto.QcSamplePrepareSaveDTO;
import com.lims.detection.entity.QcSamplePrepare;
import com.lims.detection.mapper.QcSamplePrepareMapper;
import com.lims.detection.service.QcSamplePrepareService;
import com.lims.detection.vo.QcSamplePrepareVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QcSamplePrepareServiceImpl extends ServiceImpl<QcSamplePrepareMapper, QcSamplePrepare> implements QcSamplePrepareService {

    @Autowired
    private QcSamplePrepareMapper qcSamplePrepareMapper;

    @Override
    public PageResult<QcSamplePrepareVO> selectPage(QcSamplePrepareQuery query) {
        LambdaQueryWrapper<QcSamplePrepare> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(QcSamplePrepare::getPrepareNo, query.getKeyword())
                    .or().like(QcSamplePrepare::getSampleName, query.getKeyword()));
        }
        wrapper.orderByDesc(QcSamplePrepare::getCreateTime);

        Page<QcSamplePrepare> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QcSamplePrepare> pageResult = this.page(page, wrapper);

        IPage<QcSamplePrepareVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public QcSamplePrepareVO getDetail(Long id) {
        QcSamplePrepare prepare = this.getById(id);
        if (prepare == null) {
            throw new BizException("配制记录不存在");
        }
        return convertToVO(prepare);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(QcSamplePrepareSaveDTO dto) {
        QcSamplePrepare prepare = BeanUtil.copyProperties(dto, QcSamplePrepare.class);
        if (dto.getId() != null) {
            QcSamplePrepare oldPrepare = this.getById(dto.getId());
            if (oldPrepare == null) {
                throw new BizException("配制记录不存在");
            }
            prepare.setVersion(oldPrepare.getVersion());
            this.updateById(prepare);
        } else {
            this.save(prepare);
        }
    }

    private String getSampleTypeName(String sampleType) {
        if (sampleType == null) return "";
        switch (sampleType) {
            case "1": return "标准物质";
            case "2": return "质控样";
            case "3": return "加标样";
            default: return "";
        }
    }

    private QcSamplePrepareVO convertToVO(QcSamplePrepare prepare) {
        QcSamplePrepareVO vo = BeanUtil.copyProperties(prepare, QcSamplePrepareVO.class);
        vo.setSampleTypeName(getSampleTypeName(prepare.getSampleType()));
        return vo;
    }
}
