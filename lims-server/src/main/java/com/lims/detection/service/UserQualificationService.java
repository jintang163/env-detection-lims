package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.UserQualificationSaveDTO;
import com.lims.detection.entity.DetUserQualification;
import com.lims.detection.vo.UserQualificationVO;

import java.util.List;

public interface UserQualificationService extends IService<DetUserQualification> {

    PageResult<UserQualificationVO> selectPage(Long userId, Integer pageNum, Integer pageSize);

    List<UserQualificationVO> getByUserId(Long userId);

    void saveQualification(UserQualificationSaveDTO dto);

    void updateQualification(UserQualificationSaveDTO dto);

    void deleteQualification(Long id);

    void enableQualification(Long id);

    void disableQualification(Long id);
}
