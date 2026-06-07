package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.detection.entity.DetFormField;
import com.lims.detection.vo.FormFieldVO;

import java.util.List;

public interface FormFieldService extends IService<DetFormField> {

    List<FormFieldVO> getFormFieldsByMethodId(Long methodId);

    List<FormFieldVO> getFormFieldsByMethodCode(String methodCode);

    void saveFormField(DetFormField formField);

    void updateFormField(DetFormField formField);

    void deleteFormField(Long id);
}
