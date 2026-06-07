package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.detection.entity.DetFormField;
import com.lims.detection.mapper.DetFormFieldMapper;
import com.lims.detection.service.FormFieldService;
import com.lims.detection.vo.FormFieldVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormFieldServiceImpl extends ServiceImpl<DetFormFieldMapper, DetFormField> implements FormFieldService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FormFieldVO> getFormFieldsByMethodId(Long methodId) {
        if (methodId == null) {
            throw new BizException("方法ID不能为空");
        }
        List<DetFormField> list = this.list(
                new LambdaQueryWrapper<DetFormField>()
                        .eq(DetFormField::getMethodId, methodId)
                        .eq(DetFormField::getStatus, 1)
                        .orderByAsc(DetFormField::getSortOrder)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<FormFieldVO> getFormFieldsByMethodCode(String methodCode) {
        if (StrUtil.isBlank(methodCode)) {
            throw new BizException("方法编码不能为空");
        }
        List<DetFormField> list = this.list(
                new LambdaQueryWrapper<DetFormField>()
                        .eq(DetFormField::getMethodCode, methodCode)
                        .eq(DetFormField::getStatus, 1)
                        .orderByAsc(DetFormField::getSortOrder)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFormField(DetFormField formField) {
        if (formField == null) {
            throw new BizException("表单字段不能为空");
        }
        if (formField.getMethodId() == null && StrUtil.isBlank(formField.getMethodCode())) {
            throw new BizException("方法ID和方法编码不能同时为空");
        }
        if (StrUtil.isBlank(formField.getFieldCode())) {
            throw new BizException("字段编码不能为空");
        }
        if (StrUtil.isBlank(formField.getFieldName())) {
            throw new BizException("字段名称不能为空");
        }
        long count = this.count(
                new LambdaQueryWrapper<DetFormField>()
                        .eq(DetFormField::getFieldCode, formField.getFieldCode())
                        .and(wrapper -> wrapper.eq(DetFormField::getMethodId, formField.getMethodId())
                                .or()
                                .eq(DetFormField::getMethodCode, formField.getMethodCode()))
        );
        if (count > 0) {
            throw new BizException("字段编码已存在");
        }
        if (formField.getStatus() == null) {
            formField.setStatus(1);
        }
        if (formField.getRequired() == null) {
            formField.setRequired(0);
        }
        if (formField.getSortOrder() == null) {
            formField.setSortOrder(0);
        }
        this.save(formField);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFormField(DetFormField formField) {
        if (formField == null || formField.getId() == null) {
            throw new BizException("表单字段ID不能为空");
        }
        DetFormField oldField = this.getById(formField.getId());
        if (oldField == null) {
            throw new BizException("表单字段不存在");
        }
        if (StrUtil.isNotBlank(formField.getFieldCode()) && !formField.getFieldCode().equals(oldField.getFieldCode())) {
            long count = this.count(
                    new LambdaQueryWrapper<DetFormField>()
                            .eq(DetFormField::getFieldCode, formField.getFieldCode())
                            .and(wrapper -> wrapper.eq(DetFormField::getMethodId, formField.getMethodId() != null ? formField.getMethodId() : oldField.getMethodId())
                                    .or()
                                    .eq(DetFormField::getMethodCode, StrUtil.isNotBlank(formField.getMethodCode()) ? formField.getMethodCode() : oldField.getMethodCode()))
            );
            if (count > 0) {
                throw new BizException("字段编码已存在");
            }
        }
        this.updateById(formField);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormField(Long id) {
        if (id == null) {
            throw new BizException("表单字段ID不能为空");
        }
        DetFormField field = this.getById(id);
        if (field == null) {
            throw new BizException("表单字段不存在");
        }
        this.removeById(id);
    }

    private FormFieldVO convertToVO(DetFormField field) {
        FormFieldVO vo = BeanUtil.copyProperties(field, FormFieldVO.class);
        if (StrUtil.isNotBlank(field.getOptions())) {
            try {
                vo.setOptions(objectMapper.readValue(field.getOptions(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setOptions(Collections.emptyList());
            }
        } else {
            vo.setOptions(Collections.emptyList());
        }
        return vo;
    }
}
