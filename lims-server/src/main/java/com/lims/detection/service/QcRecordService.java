package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.QcRecordQuery;
import com.lims.detection.entity.QcRecord;
import com.lims.detection.vo.QcRecordVO;
import io.swagger.annotations.Api;

import java.math.BigDecimal;

@Api(tags = "质控记录服务")
public interface QcRecordService extends IService<QcRecord> {

    PageResult<QcRecordVO> selectPage(QcRecordQuery query);

    QcRecordVO getDetail(Long id);

    void execute(Long id, BigDecimal measuredValue, String operator);
}
