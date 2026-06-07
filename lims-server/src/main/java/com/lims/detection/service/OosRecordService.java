package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.OosCloseDTO;
import com.lims.detection.dto.OosInvestigationDTO;
import com.lims.detection.dto.OosRecordQuery;
import com.lims.detection.dto.OosReviewDTO;
import com.lims.detection.dto.OosRecordSaveDTO;
import com.lims.detection.entity.DetOosRecord;
import com.lims.detection.vo.OosRecordDetailVO;
import com.lims.detection.vo.OosRecordVO;

import java.util.List;

public interface OosRecordService extends IService<DetOosRecord> {

    PageResult<OosRecordVO> selectPage(OosRecordQuery query);

    OosRecordDetailVO getDetail(Long id);

    void createOosRecord(Long dataRecordId, Long dataItemId);

    void saveOosRecord(OosRecordSaveDTO dto);

    void startInvestigation(OosInvestigationDTO dto);

    void completeInvestigation(OosInvestigationDTO dto);

    void reviewOos(OosReviewDTO dto);

    void closeOos(OosCloseDTO dto);

    void assignInvestigator(Long oosId, Long investigatorUserId, String investigatorUserName);

    List<OosRecordVO> getPendingInvestigationList();

    List<OosRecordVO> getPendingReviewList();

    long getPendingInvestigationCount();

    long getPendingReviewCount();

    long getOpenOosCount();

    void addProcessLog(Long oosId, String processNode, String processContent, List<String> attachmentUrlList);
}
