package com.lims.environment.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.environment.dto.EnvMonitorDataQuery;
import com.lims.environment.dto.EnvMonitorDataSaveDTO;
import com.lims.environment.dto.EnvMonitorThresholdSaveDTO;
import com.lims.environment.dto.EnvWarningHandleDTO;
import com.lims.environment.dto.EnvWarningQuery;
import com.lims.environment.entity.EnvMonitorData;
import com.lims.environment.entity.EnvMonitorThreshold;
import com.lims.environment.entity.EnvRoom;
import com.lims.environment.entity.EnvWarningRecord;
import com.lims.environment.mapper.EnvMonitorDataMapper;
import com.lims.environment.mapper.EnvMonitorThresholdMapper;
import com.lims.environment.mapper.EnvRoomMapper;
import com.lims.environment.mapper.EnvWarningRecordMapper;
import com.lims.environment.service.EnvironmentMonitorService;
import com.lims.environment.vo.EnvMonitorDataVO;
import com.lims.environment.vo.EnvMonitorStatsVO;
import com.lims.environment.vo.EnvMonitorThresholdVO;
import com.lims.environment.vo.EnvWarningRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvironmentMonitorServiceImpl extends ServiceImpl<EnvMonitorDataMapper, EnvMonitorData> implements EnvironmentMonitorService {

    @Autowired
    private EnvMonitorThresholdMapper thresholdMapper;

    @Autowired
    private EnvWarningRecordMapper warningRecordMapper;

    @Autowired
    private EnvRoomMapper roomMapper;

    private static final int MONITOR_TYPE_TEMPERATURE = 1;
    private static final int MONITOR_TYPE_HUMIDITY = 2;
    private static final int MONITOR_TYPE_PRESSURE = 3;
    private static final int MONITOR_TYPE_NOISE = 4;

    private static final int COLLECT_METHOD_MANUAL = 1;
    private static final int COLLECT_METHOD_ONLINE = 2;

    private static final int WARN_LEVEL_GENERAL = 1;
    private static final int WARN_LEVEL_IMPORTANT = 2;
    private static final int WARN_LEVEL_URGENT = 3;

    private static final int WARNING_STATUS_PENDING = 0;
    private static final int WARNING_STATUS_PROCESSING = 1;
    private static final int WARNING_STATUS_PROCESSED = 2;
    private static final int WARNING_STATUS_IGNORED = 3;

    private String getMonitorTypeName(Integer type) {
        if (type == null) return "";
        switch (type) {
            case 1: return "温度";
            case 2: return "湿度";
            case 3: return "压差";
            case 4: return "噪声";
            default: return "";
        }
    }

    private String getCollectMethodName(Integer method) {
        if (method == null) return "";
        switch (method) {
            case 1: return "人工录入";
            case 2: return "在线采集";
            default: return "";
        }
    }

    private String getWarnLevelName(Integer level) {
        if (level == null) return "";
        switch (level) {
            case 1: return "一般";
            case 2: return "重要";
            case 3: return "紧急";
            default: return "";
        }
    }

    private String getWarningStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已处理";
            case 3: return "已忽略";
            default: return "";
        }
    }

    @Override
    public PageResult<EnvMonitorDataVO> selectDataPage(EnvMonitorDataQuery query) {
        LambdaQueryWrapper<EnvMonitorData> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getMonitorPoint())) {
            wrapper.eq(EnvMonitorData::getMonitorPoint, query.getMonitorPoint());
        }
        if (query.getMonitorType() != null) {
            wrapper.eq(EnvMonitorData::getMonitorType, query.getMonitorType());
        }
        if (query.getCollectTimeStart() != null) {
            wrapper.ge(EnvMonitorData::getCollectTime, query.getCollectTimeStart());
        }
        if (query.getCollectTimeEnd() != null) {
            wrapper.le(EnvMonitorData::getCollectTime, query.getCollectTimeEnd());
        }
        if (query.getIsWarning() != null) {
            wrapper.eq(EnvMonitorData::getIsWarning, query.getIsWarning());
        }
        if (query.getWarnLevel() != null) {
            wrapper.eq(EnvMonitorData::getWarnLevel, query.getWarnLevel());
        }
        if (query.getCollectorId() != null) {
            wrapper.eq(EnvMonitorData::getCollectorId, query.getCollectorId());
        }
        wrapper.orderByDesc(EnvMonitorData::getCollectTime);

        IPage<EnvMonitorData> page = this.page(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvMonitorDataVO> voList = page.getRecords().stream().map(entity -> {
            EnvMonitorDataVO vo = BeanUtil.copyProperties(entity, EnvMonitorDataVO.class);
            vo.setMonitorTypeName(getMonitorTypeName(entity.getMonitorType()));
            vo.setCollectMethodName(getCollectMethodName(entity.getCollectMethod()));
            vo.setWarnLevelName(getWarnLevelName(entity.getWarnLevel()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMonitorData(EnvMonitorDataSaveDTO dto) {
        EnvMonitorData data = BeanUtil.copyProperties(dto, EnvMonitorData.class);
        checkThresholdAndGenerateWarning(data);
        this.save(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMonitorData(EnvMonitorDataSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("监控数据ID不能为空");
        }
        EnvMonitorData existing = this.getById(dto.getId());
        if (existing == null) {
            throw new BizException("监控数据不存在");
        }
        LambdaQueryWrapper<EnvWarningRecord> warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorDataId, dto.getId());
        warningRecordMapper.delete(warningWrapper);

        EnvMonitorData data = BeanUtil.copyProperties(dto, EnvMonitorData.class);
        this.updateById(data);
        checkThresholdAndGenerateWarning(data);
        this.updateById(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorData(Long id) {
        EnvMonitorData data = this.getById(id);
        if (data == null) {
            throw new BizException("监控数据不存在");
        }
        LambdaQueryWrapper<EnvWarningRecord> warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorDataId, id);
        warningRecordMapper.delete(warningWrapper);
        this.removeById(id);
    }

    @Override
    public EnvMonitorDataVO getMonitorDataDetail(Long id) {
        EnvMonitorData data = this.getById(id);
        if (data == null) {
            throw new BizException("监控数据不存在");
        }
        EnvMonitorDataVO vo = BeanUtil.copyProperties(data, EnvMonitorDataVO.class);
        vo.setMonitorTypeName(getMonitorTypeName(data.getMonitorType()));
        vo.setCollectMethodName(getCollectMethodName(data.getCollectMethod()));
        vo.setWarnLevelName(getWarnLevelName(data.getWarnLevel()));
        return vo;
    }

    @Override
    public PageResult<EnvMonitorThresholdVO> selectThresholdPage() {
        LambdaQueryWrapper<EnvMonitorThreshold> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EnvMonitorThreshold::getMonitorType);
        wrapper.orderByAsc(EnvMonitorThreshold::getMonitorPoint);

        List<EnvMonitorThreshold> list = thresholdMapper.selectList(wrapper);
        List<EnvMonitorThresholdVO> voList = list.stream().map(entity -> {
            EnvMonitorThresholdVO vo = BeanUtil.copyProperties(entity, EnvMonitorThresholdVO.class);
            vo.setMonitorTypeName(getMonitorTypeName(entity.getMonitorType()));
            vo.setWarnLevelName(getWarnLevelName(entity.getWarnLevel()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, (long) voList.size(), 1, voList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addThreshold(EnvMonitorThresholdSaveDTO dto) {
        LambdaQueryWrapper<EnvMonitorThreshold> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnvMonitorThreshold::getMonitorType, dto.getMonitorType());
        wrapper.eq(EnvMonitorThreshold::getMonitorPoint, dto.getMonitorPoint());
        if (thresholdMapper.selectCount(wrapper) > 0) {
            throw new BizException("该监控点该类型的阈值配置已存在");
        }
        EnvMonitorThreshold threshold = BeanUtil.copyProperties(dto, EnvMonitorThreshold.class);
        thresholdMapper.insert(threshold);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateThreshold(EnvMonitorThresholdSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("阈值配置ID不能为空");
        }
        EnvMonitorThreshold existing = thresholdMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("阈值配置不存在");
        }
        if (!existing.getMonitorType().equals(dto.getMonitorType()) || !existing.getMonitorPoint().equals(dto.getMonitorPoint())) {
            LambdaQueryWrapper<EnvMonitorThreshold> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EnvMonitorThreshold::getMonitorType, dto.getMonitorType());
            wrapper.eq(EnvMonitorThreshold::getMonitorPoint, dto.getMonitorPoint());
            wrapper.ne(EnvMonitorThreshold::getId, dto.getId());
            if (thresholdMapper.selectCount(wrapper) > 0) {
                throw new BizException("该监控点该类型的阈值配置已存在");
            }
        }
        EnvMonitorThreshold threshold = BeanUtil.copyProperties(dto, EnvMonitorThreshold.class);
        thresholdMapper.updateById(threshold);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteThreshold(Long id) {
        EnvMonitorThreshold threshold = thresholdMapper.selectById(id);
        if (threshold == null) {
            throw new BizException("阈值配置不存在");
        }
        thresholdMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleThreshold(Long id, Integer isEnabled) {
        EnvMonitorThreshold threshold = thresholdMapper.selectById(id);
        if (threshold == null) {
            throw new BizException("阈值配置不存在");
        }
        threshold.setIsEnabled(isEnabled);
        thresholdMapper.updateById(threshold);
    }

    @Override
    public PageResult<EnvWarningRecordVO> selectWarningPage(EnvWarningQuery query) {
        LambdaQueryWrapper<EnvWarningRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getMonitorPoint())) {
            wrapper.eq(EnvWarningRecord::getMonitorPoint, query.getMonitorPoint());
        }
        if (query.getMonitorType() != null) {
            wrapper.eq(EnvWarningRecord::getMonitorType, query.getMonitorType());
        }
        if (query.getWarnTimeStart() != null) {
            wrapper.ge(EnvWarningRecord::getWarnTime, query.getWarnTimeStart());
        }
        if (query.getWarnTimeEnd() != null) {
            wrapper.le(EnvWarningRecord::getWarnTime, query.getWarnTimeEnd());
        }
        if (query.getWarnLevel() != null) {
            wrapper.eq(EnvWarningRecord::getWarnLevel, query.getWarnLevel());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EnvWarningRecord::getStatus, query.getStatus());
        }
        if (query.getHandlerId() != null) {
            wrapper.eq(EnvWarningRecord::getHandlerId, query.getHandlerId());
        }
        wrapper.orderByDesc(EnvWarningRecord::getWarnTime);

        IPage<EnvWarningRecord> page = warningRecordMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<EnvWarningRecordVO> voList = page.getRecords().stream().map(entity -> {
            EnvWarningRecordVO vo = BeanUtil.copyProperties(entity, EnvWarningRecordVO.class);
            vo.setMonitorTypeName(getMonitorTypeName(entity.getMonitorType()));
            vo.setWarnLevelName(getWarnLevelName(entity.getWarnLevel()));
            vo.setStatusName(getWarningStatusName(entity.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWarning(EnvWarningHandleDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("预警记录ID不能为空");
        }
        EnvWarningRecord record = warningRecordMapper.selectById(dto.getId());
        if (record == null) {
            throw new BizException("预警记录不存在");
        }
        if (record.getStatus() == WARNING_STATUS_IGNORED) {
            throw new BizException("已忽略的预警不能处理");
        }
        if (dto.getStatus() != WARNING_STATUS_PROCESSING && dto.getStatus() != WARNING_STATUS_PROCESSED) {
            throw new BizException("无效的处理状态");
        }
        record.setStatus(dto.getStatus());
        record.setHandlerId(dto.getHandlerId());
        record.setHandlerName(dto.getHandlerName());
        record.setHandleTime(dto.getHandleTime() != null ? dto.getHandleTime() : LocalDateTime.now());
        record.setHandleResult(dto.getHandleResult());
        record.setRemark(dto.getRemark());
        warningRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ignoreWarning(Long id) {
        EnvWarningRecord record = warningRecordMapper.selectById(id);
        if (record == null) {
            throw new BizException("预警记录不存在");
        }
        record.setStatus(WARNING_STATUS_IGNORED);
        warningRecordMapper.updateById(record);
    }

    @Override
    public List<EnvMonitorDataVO> getRealtimeData(String monitorPoint) {
        List<EnvMonitorDataVO> result = new ArrayList<>();
        int[] types = {MONITOR_TYPE_TEMPERATURE, MONITOR_TYPE_HUMIDITY, MONITOR_TYPE_PRESSURE, MONITOR_TYPE_NOISE};
        for (int type : types) {
            LambdaQueryWrapper<EnvMonitorData> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EnvMonitorData::getMonitorPoint, monitorPoint);
            wrapper.eq(EnvMonitorData::getMonitorType, type);
            wrapper.orderByDesc(EnvMonitorData::getCollectTime);
            wrapper.last("LIMIT 1");
            EnvMonitorData data = this.getOne(wrapper);
            if (data != null) {
                EnvMonitorDataVO vo = BeanUtil.copyProperties(data, EnvMonitorDataVO.class);
                vo.setMonitorTypeName(getMonitorTypeName(data.getMonitorType()));
                vo.setCollectMethodName(getCollectMethodName(data.getCollectMethod()));
                vo.setWarnLevelName(getWarnLevelName(data.getWarnLevel()));
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public EnvMonitorStatsVO getMonitorStats() {
        EnvMonitorStatsVO stats = new EnvMonitorStatsVO();
        LambdaQueryWrapper<EnvMonitorData> dataWrapper;
        LambdaQueryWrapper<EnvWarningRecord> warningWrapper;
        LambdaQueryWrapper<EnvRoom> roomWrapper = new LambdaQueryWrapper<>();

        stats.setTotalPoints(roomMapper.selectCount(roomWrapper).intValue());

        dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(EnvMonitorData::getIsWarning, 0);
        dataWrapper.groupBy(EnvMonitorData::getMonitorPoint);
        List<EnvMonitorData> normalList = this.list(dataWrapper);
        stats.setNormalPoints(normalList.size());

        dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(EnvMonitorData::getIsWarning, 1);
        dataWrapper.groupBy(EnvMonitorData::getMonitorPoint);
        List<EnvMonitorData> warningList = this.list(dataWrapper);
        stats.setWarningPoints(warningList.size());

        dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.ge(EnvMonitorData::getCollectTime, LocalDate.now().atStartOfDay());
        dataWrapper.lt(EnvMonitorData::getCollectTime, LocalDate.now().plusDays(1).atStartOfDay());
        stats.setTodayRecords(this.count(dataWrapper).intValue());

        warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getStatus, WARNING_STATUS_PENDING);
        stats.setPendingWarnings(warningRecordMapper.selectCount(warningWrapper).intValue());

        warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorType, MONITOR_TYPE_TEMPERATURE);
        stats.setTemperatureWarning(warningRecordMapper.selectCount(warningWrapper).intValue());

        warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorType, MONITOR_TYPE_HUMIDITY);
        stats.setHumidityWarning(warningRecordMapper.selectCount(warningWrapper).intValue());

        warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorType, MONITOR_TYPE_PRESSURE);
        stats.setPressureWarning(warningRecordMapper.selectCount(warningWrapper).intValue());

        warningWrapper = new LambdaQueryWrapper<>();
        warningWrapper.eq(EnvWarningRecord::getMonitorType, MONITOR_TYPE_NOISE);
        stats.setNoiseWarning(warningRecordMapper.selectCount(warningWrapper).intValue());

        return stats;
    }

    @Override
    public List<EnvMonitorDataVO> getHistoryData(String monitorPoint, Integer monitorType, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<EnvMonitorData> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(monitorPoint)) {
            wrapper.eq(EnvMonitorData::getMonitorPoint, monitorPoint);
        }
        if (monitorType != null) {
            wrapper.eq(EnvMonitorData::getMonitorType, monitorType);
        }
        if (startTime != null) {
            wrapper.ge(EnvMonitorData::getCollectTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(EnvMonitorData::getCollectTime, endTime);
        }
        wrapper.orderByAsc(EnvMonitorData::getCollectTime);

        List<EnvMonitorData> list = this.list(wrapper);
        return list.stream().map(entity -> {
            EnvMonitorDataVO vo = BeanUtil.copyProperties(entity, EnvMonitorDataVO.class);
            vo.setMonitorTypeName(getMonitorTypeName(entity.getMonitorType()));
            vo.setCollectMethodName(getCollectMethodName(entity.getCollectMethod()));
            vo.setWarnLevelName(getWarnLevelName(entity.getWarnLevel()));
            return vo;
        }).collect(Collectors.toList());
    }

    private void checkThresholdAndGenerateWarning(EnvMonitorData data) {
        LambdaQueryWrapper<EnvMonitorThreshold> thresholdWrapper = new LambdaQueryWrapper<>();
        thresholdWrapper.eq(EnvMonitorThreshold::getMonitorPoint, data.getMonitorPoint());
        thresholdWrapper.eq(EnvMonitorThreshold::getMonitorType, data.getMonitorType());
        thresholdWrapper.eq(EnvMonitorThreshold::getIsEnabled, 1);
        EnvMonitorThreshold threshold = thresholdMapper.selectOne(thresholdWrapper);

        if (threshold == null) {
            data.setIsWarning(0);
            data.setWarnLevel(null);
            data.setWarnMessage(null);
            return;
        }

        BigDecimal value = data.getMonitorValue();
        BigDecimal minValue = threshold.getMinValue();
        BigDecimal maxValue = threshold.getMaxValue();
        String typeName = getMonitorTypeName(data.getMonitorType());

        boolean isWarning = false;
        String warnMessage = "";

        if (minValue != null && value.compareTo(minValue) < 0) {
            isWarning = true;
            warnMessage = typeName + "低于下限：当前值" + value + threshold.getUnit() + "，下限" + minValue + threshold.getUnit();
        } else if (maxValue != null && value.compareTo(maxValue) > 0) {
            isWarning = true;
            warnMessage = typeName + "超出上限：当前值" + value + threshold.getUnit() + "，上限" + maxValue + threshold.getUnit();
        }

        if (isWarning) {
            data.setIsWarning(1);
            data.setWarnLevel(threshold.getWarnLevel());
            data.setWarnMessage(warnMessage);

            EnvWarningRecord warningRecord = new EnvWarningRecord();
            warningRecord.setMonitorDataId(data.getId());
            warningRecord.setMonitorPoint(data.getMonitorPoint());
            warningRecord.setMonitorPointName(data.getMonitorPointName());
            warningRecord.setMonitorType(data.getMonitorType());
            warningRecord.setMonitorValue(data.getMonitorValue());
            warningRecord.setThresholdMin(threshold.getMinValue());
            warningRecord.setThresholdMax(threshold.getMaxValue());
            warningRecord.setWarnLevel(threshold.getWarnLevel());
            warningRecord.setWarnMessage(warnMessage);
            warningRecord.setWarnTime(data.getCollectTime() != null ? data.getCollectTime() : LocalDateTime.now());
            warningRecord.setStatus(WARNING_STATUS_PENDING);
            warningRecordMapper.insert(warningRecord);
        } else {
            data.setIsWarning(0);
            data.setWarnLevel(null);
            data.setWarnMessage(null);
        }
    }
}
