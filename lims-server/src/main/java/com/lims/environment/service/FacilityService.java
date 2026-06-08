package com.lims.environment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvFacilityQuery;
import com.lims.environment.dto.EnvFacilitySaveDTO;
import com.lims.environment.entity.EnvFacility;
import com.lims.environment.vo.EnvFacilityStatsVO;
import com.lims.environment.vo.EnvFacilityVO;

import java.util.List;

public interface FacilityService extends IService<EnvFacility> {

    PageResult<EnvFacilityVO> selectPage(EnvFacilityQuery query);

    void addFacility(EnvFacilitySaveDTO dto);

    void updateFacility(EnvFacilitySaveDTO dto);

    void deleteFacility(Long id);

    EnvFacilityVO getFacilityDetail(Long id);

    List<EnvFacilityVO> getFacilityList();

    void updateFacilityStatus(Long id, Integer status);

    EnvFacilityStatsVO getStats();
}
