package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.DetectionTaskQuery;
import com.lims.detection.dto.DetectionTaskSaveDTO;
import com.lims.detection.dto.TaskAssignDTO;
import com.lims.detection.dto.TaskReviewDTO;
import com.lims.detection.entity.DetDetectionTask;
import com.lims.detection.vo.AssignmentRecommendationVO;
import com.lims.detection.vo.DetectionTaskDetailVO;
import com.lims.detection.vo.DetectionTaskVO;
import com.lims.detection.vo.TaskBoardStatsVO;

import java.util.List;

public interface DetectionTaskService extends IService<DetDetectionTask> {

    PageResult<DetectionTaskVO> selectPage(DetectionTaskQuery query);

    DetectionTaskDetailVO getDetail(Long id);

    void saveTask(DetectionTaskSaveDTO dto);

    void updateTask(DetectionTaskSaveDTO dto);

    void deleteTask(Long id);

    void assignTasks(TaskAssignDTO dto);

    void acceptTask(Long taskId);

    void startTask(Long taskId);

    void submitDataEntry(Long taskId);

    void submitReview(Long taskId);

    void reviewTask(TaskReviewDTO dto);

    void pauseTask(Long taskId, String reason);

    void resumeTask(Long taskId);

    void terminateTask(Long taskId, String reason);

    void grabTask(Long taskId, Long userId, String userName);

    List<DetectionTaskVO> getMyTasks(Long userId);

    List<DetectionTaskVO> getGrabOrderPool();

    List<AssignmentRecommendationVO> getAssignmentRecommendations(Long taskId);

    TaskBoardStatsVO getBoardStats();

    List<DetectionTaskVO> getTasksByStatus(Integer status);
}
