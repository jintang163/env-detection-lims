package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.detection.dto.DataReviewSaveDTO;
import com.lims.detection.entity.DetDataRecord;
import com.lims.detection.entity.DetDataReview;
import com.lims.detection.mapper.DetDataRecordMapper;
import com.lims.detection.mapper.DetDataReviewMapper;
import com.lims.detection.service.DataReviewService;
import com.lims.detection.vo.DataReviewVO;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataReviewServiceImpl extends ServiceImpl<DetDataReviewMapper, DetDataReview> implements DataReviewService {

    @Autowired
    private DetDataRecordMapper dataRecordMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PENDING_FIRST_REVIEW = 1;
    private static final int STATUS_FIRST_REVIEW_PASSED = 2;
    private static final int STATUS_PENDING_SECOND_REVIEW = 3;
    private static final int STATUS_SECOND_REVIEW_PASSED = 4;
    private static final int STATUS_REJECTED = 5;
    private static final int STATUS_COMPLETED = 6;

    private static final int REVIEW_LEVEL_FIRST = 1;
    private static final int REVIEW_LEVEL_SECOND = 2;

    private static final int REVIEW_RESULT_PASS = 1;
    private static final int REVIEW_RESULT_REJECT = 2;

    private String getReviewLevelName(Integer reviewLevel) {
        if (reviewLevel == null) return "";
        switch (reviewLevel) {
            case 1: return "一级审核";
            case 2: return "二级审核";
            default: return "";
        }
    }

    private String getReviewResultName(Integer reviewResult) {
        if (reviewResult == null) return "";
        switch (reviewResult) {
            case 1: return "通过";
            case 2: return "驳回";
            default: return "";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "待一级审核";
            case 2: return "一级审核通过";
            case 3: return "待二级审核";
            case 4: return "二级审核通过";
            case 5: return "已驳回";
            case 6: return "已完成";
            default: return "";
        }
    }

    @Override
    public List<DataReviewVO> getReviewListByDataRecordId(Long dataRecordId) {
        List<DetDataReview> list = this.list(
                new LambdaQueryWrapper<DetDataReview>()
                        .eq(DetDataReview::getDataRecordId, dataRecordId)
                        .orderByDesc(DetDataReview::getReviewTime, DetDataReview::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void firstReview(DataReviewSaveDTO dto) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dto.getDataRecordId());
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }
        if (dataRecord.getStatus() != STATUS_PENDING_FIRST_REVIEW) {
            throw new BizException("数据记录状态不正确，当前状态：" + getStatusName(dataRecord.getStatus()) + "，不能进行一级审核");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        DetDataReview review = BeanUtil.copyProperties(dto, DetDataReview.class);
        review.setDataRecordNo(dataRecord.getRecordNo());
        review.setReviewLevel(REVIEW_LEVEL_FIRST);
        review.setReviewerId(currentUserId);
        review.setReviewerName(currentUserName);
        review.setReviewTime(LocalDateTime.now());
        review.setIsModified(dto.getCorrectedItemList() != null && !dto.getCorrectedItemList().isEmpty() ? 1 : 0);

        if (dto.getCorrectedItemList() != null && !dto.getCorrectedItemList().isEmpty()) {
            try {
                review.setCorrectedData(objectMapper.writeValueAsString(dto.getCorrectedItemList()));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化修正数据失败");
            }
        }

        this.save(review);

        dataRecord.setFirstReviewUserId(currentUserId);
        dataRecord.setFirstReviewUserName(currentUserName);
        dataRecord.setFirstReviewTime(LocalDateTime.now());
        dataRecord.setFirstReviewOpinion(dto.getReviewOpinion());
        dataRecord.setFirstReviewResult(dto.getReviewResult());

        if (dto.getReviewResult() == REVIEW_RESULT_PASS) {
            dataRecord.setStatus(STATUS_FIRST_REVIEW_PASSED);
        } else {
            dataRecord.setStatus(STATUS_REJECTED);
            dataRecord.setRemark(dto.getRejectReason());
        }

        dataRecordMapper.updateById(dataRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void secondReview(DataReviewSaveDTO dto) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dto.getDataRecordId());
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }
        if (dataRecord.getStatus() != STATUS_PENDING_SECOND_REVIEW) {
            throw new BizException("数据记录状态不正确，当前状态：" + getStatusName(dataRecord.getStatus()) + "，不能进行二级审核");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        DetDataReview review = BeanUtil.copyProperties(dto, DetDataReview.class);
        review.setDataRecordNo(dataRecord.getRecordNo());
        review.setReviewLevel(REVIEW_LEVEL_SECOND);
        review.setReviewerId(currentUserId);
        review.setReviewerName(currentUserName);
        review.setReviewTime(LocalDateTime.now());
        review.setIsModified(dto.getCorrectedItemList() != null && !dto.getCorrectedItemList().isEmpty() ? 1 : 0);

        if (dto.getCorrectedItemList() != null && !dto.getCorrectedItemList().isEmpty()) {
            try {
                review.setCorrectedData(objectMapper.writeValueAsString(dto.getCorrectedItemList()));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化修正数据失败");
            }
        }

        this.save(review);

        dataRecord.setSecondReviewUserId(currentUserId);
        dataRecord.setSecondReviewUserName(currentUserName);
        dataRecord.setSecondReviewTime(LocalDateTime.now());
        dataRecord.setSecondReviewOpinion(dto.getReviewOpinion());
        dataRecord.setSecondReviewResult(dto.getReviewResult());

        if (dto.getReviewResult() == REVIEW_RESULT_PASS) {
            dataRecord.setStatus(STATUS_COMPLETED);
        } else {
            dataRecord.setStatus(STATUS_REJECTED);
            dataRecord.setRemark(dto.getRejectReason());
        }

        dataRecordMapper.updateById(dataRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectData(Long dataRecordId, Integer reviewLevel, String rejectReason) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dataRecordId);
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();

        DetDataReview review = new DetDataReview();
        review.setDataRecordId(dataRecordId);
        review.setDataRecordNo(dataRecord.getRecordNo());
        review.setReviewLevel(reviewLevel);
        review.setReviewerId(currentUserId);
        review.setReviewerName(currentUserName);
        review.setReviewTime(LocalDateTime.now());
        review.setReviewResult(REVIEW_RESULT_REJECT);
        review.setRejectReason(rejectReason);
        review.setIsModified(0);
        this.save(review);

        if (reviewLevel == REVIEW_LEVEL_FIRST) {
            dataRecord.setFirstReviewUserId(currentUserId);
            dataRecord.setFirstReviewUserName(currentUserName);
            dataRecord.setFirstReviewTime(LocalDateTime.now());
            dataRecord.setFirstReviewResult(REVIEW_RESULT_REJECT);
            dataRecord.setFirstReviewOpinion(rejectReason);
        } else if (reviewLevel == REVIEW_LEVEL_SECOND) {
            dataRecord.setSecondReviewUserId(currentUserId);
            dataRecord.setSecondReviewUserName(currentUserName);
            dataRecord.setSecondReviewTime(LocalDateTime.now());
            dataRecord.setSecondReviewResult(REVIEW_RESULT_REJECT);
            dataRecord.setSecondReviewOpinion(rejectReason);
        }

        dataRecord.setStatus(STATUS_REJECTED);
        dataRecord.setRemark(rejectReason);
        dataRecordMapper.updateById(dataRecord);
    }

    @Override
    public List<DataReviewVO> getMyReviewTasks(Long userId) {
        List<DetDataRecord> dataRecordList = dataRecordMapper.selectList(
                new LambdaQueryWrapper<DetDataRecord>()
                        .in(DetDataRecord::getStatus, STATUS_PENDING_FIRST_REVIEW, STATUS_PENDING_SECOND_REVIEW)
                        .orderByDesc(DetDataRecord::getCreateTime)
        );

        return dataRecordList.stream().map(record -> {
            DataReviewVO vo = new DataReviewVO();
            vo.setDataRecordId(record.getId());
            vo.setDataRecordNo(record.getRecordNo());
            vo.setReviewLevel(record.getStatus() == STATUS_PENDING_FIRST_REVIEW ? REVIEW_LEVEL_FIRST : REVIEW_LEVEL_SECOND);
            vo.setReviewLevelName(getReviewLevelName(vo.getReviewLevel()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public long getPendingReviewCount(Long userId, Integer reviewLevel) {
        LambdaQueryWrapper<DetDataRecord> wrapper = new LambdaQueryWrapper<>();
        if (reviewLevel == null) {
            wrapper.in(DetDataRecord::getStatus, STATUS_PENDING_FIRST_REVIEW, STATUS_PENDING_SECOND_REVIEW);
        } else if (reviewLevel == REVIEW_LEVEL_FIRST) {
            wrapper.eq(DetDataRecord::getStatus, STATUS_PENDING_FIRST_REVIEW);
        } else if (reviewLevel == REVIEW_LEVEL_SECOND) {
            wrapper.eq(DetDataRecord::getStatus, STATUS_PENDING_SECOND_REVIEW);
        }
        return dataRecordMapper.selectCount(wrapper);
    }

    private DataReviewVO convertToVO(DetDataReview review) {
        DataReviewVO vo = BeanUtil.copyProperties(review, DataReviewVO.class);
        vo.setReviewLevelName(getReviewLevelName(review.getReviewLevel()));
        vo.setReviewResultName(getReviewResultName(review.getReviewResult()));
        return vo;
    }
}
