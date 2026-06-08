package com.lims.environment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvFacilityQuery;
import com.lims.environment.dto.EnvFacilitySaveDTO;
import com.lims.environment.entity.EnvFacility;
import com.lims.environment.entity.EnvFacilityMaintenance;
import com.lims.environment.entity.EnvFacilityMaintenancePlan;
import com.lims.environment.entity.EnvRoom;
import com.lims.environment.mapper.EnvFacilityMapper;
import com.lims.environment.mapper.EnvFacilityMaintenanceMapper;
import com.lims.environment.mapper.EnvFacilityMaintenancePlanMapper;
import com.lims.environment.mapper.EnvRoomMapper;
import com.lims.environment.service.FacilityService;
import com.lims.environment.vo.EnvFacilityMaintenancePlanVO;
import com.lims.environment.vo.EnvFacilityMaintenanceVO;
import com.lims.environment.vo.EnvFacilityStatsVO;
import com.lims.environment.vo.EnvFacilityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl extends ServiceImpl<EnvFacilityMapper, EnvFacility> implements FacilityService {

    @Autowired
    private EnvFacilityMapper envFacilityMapper;

    @Autowired
    private EnvFacilityMaintenanceMapper envFacilityMaintenanceMapper;

    @Autowired
    private EnvFacilityMaintenancePlanMapper envFacilityMaintenancePlanMapper;

    @Autowired
    private EnvRoomMapper envRoomMapper;

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_MAINTENANCE = 2;
    private static final int STATUS_FAULT = 3;

    private static final int ROOM_STATUS_NORMAL = 1;
    private static final int ROOM_STATUS_MAINTENANCE = 2;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "停用";
            case 1: return "正常";
            case 2: return "维护中";
            case 3: return "故障";
            default: return "";
        }
    }

    private String getFacilityTypeName(Integer facilityType) {
        if (facilityType == null) return "";
        switch (facilityType) {
            case 1: return "通风橱";
            case 2: return "超净台";
            case 3: return "生物安全柜";
            case 4: return "空调系统";
            case 5: return "纯水系统";
            case 6: return "供气系统";
            case 7: return "排风系统";
            case 8: return "其他";
            default: return "";
        }
    }

    @Override
    public PageResult<EnvFacilityVO> selectPage(EnvFacilityQuery query) {
        LambdaQueryWrapper<EnvFacility> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getFacilityNo())) {
            wrapper.like(EnvFacility::getFacilityNo, query.getFacilityNo());
        }
        if (StrUtil.isNotBlank(query.getFacilityName())) {
            wrapper.like(EnvFacility::getFacilityName, query.getFacilityName());
        }
        if (query.getFacilityType() != null) {
            wrapper.eq(EnvFacility::getFacilityType, query.getFacilityType());
        }
        if (query.getRoomId() != null) {
            wrapper.eq(EnvFacility::getRoomId, query.getRoomId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EnvFacility::getStatus, query.getStatus());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(EnvFacility::getDeptId, query.getDeptId());
        }
        if (query.getManagerId() != null) {
            wrapper.eq(EnvFacility::getManagerId, query.getManagerId());
        }
        wrapper.orderByDesc(EnvFacility::getCreateTime);

        IPage<EnvFacility> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvFacilityVO> voList = page.getRecords().stream().map(entity -> {
            EnvFacilityVO vo = BeanUtil.copyProperties(entity, EnvFacilityVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            vo.setFacilityTypeName(getFacilityTypeName(entity.getFacilityType()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFacility(EnvFacilitySaveDTO dto) {
        LambdaQueryWrapper<EnvFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvFacility::getFacilityNo, dto.getFacilityNo());
        if (this.count(wrapper) > 0) {
            throw new BizException("设施编号已存在");
        }
        EnvFacility facility = BeanUtil.copyProperties(dto, EnvFacility.class);
        this.save(facility);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFacility(EnvFacilitySaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("设施ID不能为空");
        }
        EnvFacility existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("设施不存在");
        }
        if (!existing.getFacilityNo().equals(dto.getFacilityNo())) {
            LambdaQueryWrapper<EnvFacility> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EnvFacility::getFacilityNo, dto.getFacilityNo());
            wrapper.ne(EnvFacility::getId, dto.getId());
            if (this.count(wrapper) > 0) {
                throw new BizException("设施编号已存在");
            }
        }
        EnvFacility facility = BeanUtil.copyProperties(dto, EnvFacility.class);
        this.updateById(facility);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFacility(Long id) {
        EnvFacility facility = this.getById(id);
        if (facility == null) {
            throw new BizException("设施不存在");
        }
        this.removeById(id);
    }

    @Override
    public EnvFacilityVO getFacilityDetail(Long id) {
        EnvFacility facility = this.getById(id);
        if (facility == null) {
            throw new BizException("设施不存在");
        }
        EnvFacilityVO vo = BeanUtil.copyProperties(facility, EnvFacilityVO.class);
        vo.setStatusName(getStatusName(facility.getStatus()));
        vo.setFacilityTypeName(getFacilityTypeName(facility.getFacilityType()));

        LambdaQueryWrapper<EnvFacilityMaintenance> maintenanceWrapper = new LambdaQueryWrapper<>();
        maintenanceWrapper.eq(EnvFacilityMaintenance::getFacilityId, id);
        maintenanceWrapper.orderByDesc(EnvFacilityMaintenance::getMaintenanceDate);
        List<EnvFacilityMaintenance> maintenanceList = envFacilityMaintenanceMapper.selectList(maintenanceWrapper);
        List<EnvFacilityMaintenanceVO> maintenanceVOList = maintenanceList.stream()
                .map(entity -> BeanUtil.copyProperties(entity, EnvFacilityMaintenanceVO.class))
                .collect(Collectors.toList());
        vo.setMaintenanceRecords(maintenanceVOList);

        LambdaQueryWrapper<EnvFacilityMaintenancePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(EnvFacilityMaintenancePlan::getFacilityId, id);
        planWrapper.orderByDesc(EnvFacilityMaintenancePlan::getCreateTime);
        List<EnvFacilityMaintenancePlan> planList = envFacilityMaintenancePlanMapper.selectList(planWrapper);
        List<EnvFacilityMaintenancePlanVO> planVOList = planList.stream()
                .map(entity -> BeanUtil.copyProperties(entity, EnvFacilityMaintenancePlanVO.class))
                .collect(Collectors.toList());
        vo.setMaintenancePlans(planVOList);

        return vo;
    }

    @Override
    public List<EnvFacilityVO> getFacilityList() {
        LambdaQueryWrapper<EnvFacility> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvFacility::getStatus, STATUS_NORMAL);
        wrapper.orderByAsc(EnvFacility::getFacilityNo);
        List<EnvFacility> list = this.list(wrapper);
        return list.stream().map(entity -> {
            EnvFacilityVO vo = BeanUtil.copyProperties(entity, EnvFacilityVO.class);
            vo.setStatusName(getStatusName(entity.getStatus()));
            vo.setFacilityTypeName(getFacilityTypeName(entity.getFacilityType()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFacilityStatus(Long id, Integer status) {
        EnvFacility facility = this.getById(id);
        if (facility == null) {
            throw new BizException("设施不存在");
        }
        facility.setStatus(status);
        this.updateById(facility);
    }

    @Override
    public EnvFacilityStatsVO getStats() {
        EnvFacilityStatsVO stats = new EnvFacilityStatsVO();
        LambdaQueryWrapper<EnvFacility> facilityWrapper;
        LambdaQueryWrapper<EnvRoom> roomWrapper;

        facilityWrapper = new LambdaQueryWrapper<>();
        stats.setTotalFacilities(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(EnvFacility::getStatus, STATUS_NORMAL);
        stats.setNormalFacilities(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(EnvFacility::getStatus, STATUS_MAINTENANCE);
        stats.setMaintenanceFacilities(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(EnvFacility::getStatus, STATUS_FAULT);
        stats.setFaultFacilities(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.eq(EnvFacility::getStatus, STATUS_DISABLED);
        stats.setDisabledFacilities(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.le(EnvFacility::getNextMaintenanceDate, LocalDate.now().plusDays(30));
        facilityWrapper.ge(EnvFacility::getNextMaintenanceDate, LocalDate.now());
        stats.setDueMaintenance(Math.toIntExact(this.count(facilityWrapper)));

        facilityWrapper = new LambdaQueryWrapper<>();
        facilityWrapper.lt(EnvFacility::getNextMaintenanceDate, LocalDate.now());
        stats.setOverdueMaintenance(Math.toIntExact(this.count(facilityWrapper)));

        roomWrapper = new LambdaQueryWrapper<>();
        stats.setTotalRooms(Math.toIntExact(envRoomMapper.selectCount(roomWrapper)));

        roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(EnvRoom::getStatus, ROOM_STATUS_NORMAL);
        stats.setNormalRooms(Math.toIntExact(envRoomMapper.selectCount(roomWrapper)));

        roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(EnvRoom::getStatus, ROOM_STATUS_MAINTENANCE);
        stats.setMaintenanceRooms(Math.toIntExact(envRoomMapper.selectCount(roomWrapper)));

        return stats;
    }
}
