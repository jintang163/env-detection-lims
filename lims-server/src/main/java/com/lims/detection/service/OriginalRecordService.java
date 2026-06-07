package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.OriginalRecordQuery;
import com.lims.detection.dto.OriginalRecordSaveDTO;
import com.lims.detection.entity.DetOriginalRecord;
import com.lims.detection.vo.OriginalRecordDetailVO;
import com.lims.detection.vo.OriginalRecordVO;

public interface OriginalRecordService extends IService<DetOriginalRecord> {

    PageResult<OriginalRecordVO> selectPage(OriginalRecordQuery query);

    OriginalRecordDetailVO getDetail(Long id);

    void saveOriginalRecord(OriginalRecordSaveDTO dto);

    void updateOriginalRecord(OriginalRecordSaveDTO dto);

    void submitOriginalRecord(Long id);

    void archiveOriginalRecord(Long id);

    void deleteOriginalRecord(Long id);

    String previewAsHtml(Long id);

    boolean checkPermission(Long id, Long userId, String userRole);

    boolean verifyOriginalRecordIntegrity(Long id);

    String calculateContentHash(String content);
}
