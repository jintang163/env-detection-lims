package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcSamplePrepareQuery;
import com.lims.detection.dto.QcSamplePrepareSaveDTO;
import com.lims.detection.entity.QcSamplePrepare;
import com.lims.detection.vo.QcSamplePrepareVO;
import io.swagger.annotations.Api;

@Api(tags = "配制记录服务")
public interface QcSamplePrepareService extends IService<QcSamplePrepare> {

    PageResult<QcSamplePrepareVO> selectPage(QcSamplePrepareQuery query);

    QcSamplePrepareVO getDetail(Long id);

    void save(QcSamplePrepareSaveDTO dto);
}
