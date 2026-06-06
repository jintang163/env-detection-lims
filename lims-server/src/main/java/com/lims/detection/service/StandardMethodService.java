package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.StandardMethodQuery;
import com.lims.detection.dto.StandardMethodSaveDTO;
import com.lims.detection.entity.DetStandardMethod;
import com.lims.detection.vo.StandardMethodDetailVO;
import com.lims.detection.vo.StandardMethodVO;

import java.util.List;

public interface StandardMethodService extends IService<DetStandardMethod> {

    PageResult<StandardMethodVO> selectPage(StandardMethodQuery query);

    StandardMethodDetailVO getDetail(Long id);

    void saveMethod(StandardMethodSaveDTO dto);

    void updateMethod(StandardMethodSaveDTO dto);

    void deleteMethod(Long id);

    void enableMethod(Long id);

    void disableMethod(Long id);

    void setCurrentVersion(Long id);

    List<StandardMethodVO> getCurrentVersionList();

    List<StandardMethodVO> getMethodVersions(String methodCode);
}
