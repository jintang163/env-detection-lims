package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcSampleQuery;
import com.lims.detection.dto.QcSampleSaveDTO;
import com.lims.detection.entity.QcSample;
import com.lims.detection.vo.QcSampleStatsVO;
import com.lims.detection.vo.QcSampleVO;
import io.swagger.annotations.Api;

import java.util.List;

@Api(tags = "质控样品服务")
public interface QcSampleService extends IService<QcSample> {

    PageResult<QcSampleVO> selectPage(QcSampleQuery query);

    QcSampleStatsVO getStats();

    QcSampleVO getDetail(Long id);

    void save(QcSampleSaveDTO dto);

    void update(QcSampleSaveDTO dto);

    void delete(Long id);

    List<QcSampleVO> getValidSamples();
}
