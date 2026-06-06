package com.lims.detection.service.impl;

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
import com.lims.detection.dto.UserQualificationSaveDTO;
import com.lims.detection.entity.DetUserQualification;
import com.lims.detection.mapper.DetUserQualificationMapper;
import com.lims.detection.service.UserQualificationService;
import com.lims.detection.vo.UserQualificationVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserQualificationServiceImpl extends ServiceImpl<DetUserQualificationMapper, DetUserQualification> implements UserQualificationService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return status == 1 ? "有效" : "无效";
    }

    @Override
    public PageResult<UserQualificationVO> selectPage(Long userId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DetUserQualification> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(DetUserQualification::getUserId, userId);
        }
        wrapper.orderByDesc(DetUserQualification::getCreateTime);

        Page<DetUserQualification> page = new Page<>(pageNum, pageSize);
        IPage<DetUserQualification> pageResult = this.page(page, wrapper);

        IPage<UserQualificationVO> voPage = pageResult.convert(qual -> {
            UserQualificationVO vo = BeanUtil.copyProperties(qual, UserQualificationVO.class);
            vo.setStatusName(getStatusName(qual.getStatus()));
            try {
                if (StrUtil.isNotBlank(qual.getTestItemIds())) {
                    vo.setTestItemIdList(objectMapper.readValue(qual.getTestItemIds(), List.class));
                }
                if (StrUtil.isNotBlank(qual.getTestItemNames())) {
                    vo.setTestItemNameList(objectMapper.readValue(qual.getTestItemNames(), List.class));
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析JSON数据失败");
            }
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public List<UserQualificationVO> getByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        List<DetUserQualification> list = this.list(
                new LambdaQueryWrapper<DetUserQualification>()
                        .eq(DetUserQualification::getUserId, userId)
                        .eq(DetUserQualification::getStatus, 1)
                        .orderByDesc(DetUserQualification::getCreateTime)
        );
        return list.stream().map(qual -> {
            UserQualificationVO vo = BeanUtil.copyProperties(qual, UserQualificationVO.class);
            vo.setStatusName(getStatusName(qual.getStatus()));
            try {
                if (StrUtil.isNotBlank(qual.getTestItemIds())) {
                    vo.setTestItemIdList(objectMapper.readValue(qual.getTestItemIds(), List.class));
                }
                if (StrUtil.isNotBlank(qual.getTestItemNames())) {
                    vo.setTestItemNameList(objectMapper.readValue(qual.getTestItemNames(), List.class));
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析JSON数据失败");
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveQualification(UserQualificationSaveDTO dto) {
        DetUserQualification qual = BeanUtil.copyProperties(dto, DetUserQualification.class);
        if (qual.getStatus() == null) {
            qual.setStatus(1);
        }

        try {
            if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                qual.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
            }
            if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                qual.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.save(qual);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualification(UserQualificationSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("资质ID不能为空");
        }

        DetUserQualification oldQual = this.getById(dto.getId());
        if (oldQual == null) {
            throw new BizException("人员资质不存在");
        }

        DetUserQualification qual = BeanUtil.copyProperties(dto, DetUserQualification.class);
        qual.setUserId(oldQual.getUserId());

        try {
            if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                qual.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
            }
            if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                qual.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.updateById(qual);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualification(Long id) {
        DetUserQualification qual = this.getById(id);
        if (qual == null) {
            throw new BizException("人员资质不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableQualification(Long id) {
        DetUserQualification qual = this.getById(id);
        if (qual == null) {
            throw new BizException("人员资质不存在");
        }
        qual.setStatus(1);
        this.updateById(qual);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableQualification(Long id) {
        DetUserQualification qual = this.getById(id);
        if (qual == null) {
            throw new BizException("人员资质不存在");
        }
        qual.setStatus(0);
        this.updateById(qual);
    }
}
