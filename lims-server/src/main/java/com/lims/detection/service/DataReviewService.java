package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.detection.dto.DataReviewSaveDTO;
import com.lims.detection.entity.DetDataReview;
import com.lims.detection.vo.DataReviewVO;

import java.util.List;

public interface DataReviewService extends IService<DetDataReview> {

    List<DataReviewVO> getReviewListByDataRecordId(Long dataRecordId);

    void firstReview(DataReviewSaveDTO dto);

    void secondReview(DataReviewSaveDTO dto);

    void rejectData(Long dataRecordId, Integer reviewLevel, String rejectReason);

    List<DataReviewVO> getMyReviewTasks(Long userId);

    long getPendingReviewCount(Long userId, Integer reviewLevel);
}
