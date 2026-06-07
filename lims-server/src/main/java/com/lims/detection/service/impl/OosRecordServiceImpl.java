package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.detection.dto.OosCloseDTO;
import com.lims.detection.dto.OosInvestigationDTO;
import com.lims.detection.dto.OosRecordQuery;
import com.lims.detection.dto.OosRecordSaveDTO;
import com.lims.detection.dto.OosReviewDTO;
import com.lims.detection.entity.DetDataRecord;
import com.lims.detection.entity.DetDataRecordItem;
import com.lims.detection.entity.DetFormField;
import com.lims.detection.entity.DetOosProcessLog;
import com.lims.detection.entity.DetOosRecord;
import com.lims.detection.mapper.DetDataRecordItemMapper;
import com.lims.detection.mapper.DetDataRecordMapper;
import com.lims.detection.mapper.DetFormFieldMapper;
import com.lims.detection.mapper.DetOosProcessLogMapper;
import com.lims.detection.mapper.DetOosRecordMapper;
import com.lims.detection.service.OosRecordService;
import com.lims.detection.vo.OosProcessLogVO;
import com.lims.detection.vo.OosRecordDetailVO;
import com.lims.detection.vo.OosRecordVO;
import com.lims.detection.vo.WebSocketMessageVO;
import com.lims.detection.websocket.DetectionTaskWebSocketHandler;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OosRecordServiceImpl extends ServiceImpl<DetOosRecordMapper, DetOosRecord> implements OosRecordService {

    @Autowired
    private DetOosProcessLogMapper processLogMapper;

    @Autowired
    private DetDataRecordMapper dataRecordMapper;

    @Autowired
    private DetDataRecordItemMapper dataRecordItemMapper;

    @Autowired
    private DetFormFieldMapper formFieldMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private DetectionTaskWebSocketHandler webSocketHandler;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_OPEN = 0;
    private static final int STATUS_INVESTIGATING = 1;
    private static final int STATUS_INVESTIGATED = 2;
    private static final int STATUS_REVIEWING = 3;
    private static final int STATUS_CLOSED = 4;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待调查";
            case 1: return "调查中";
            case 2: return "调查完成";
            case 3: return "审核中";
            case 4: return "已关闭";
            default: return "";
        }
    }

    private String getOosLevelName(Integer level) {
        if (level == null) return "";
        switch (level) {
            case 1: return "一般";
            case 2: return "严重";
            case 3: return "重大";
            default: return "";
        }
    }

    private String getOosTypeName(String type) {
        if (StrUtil.isBlank(type)) return "";
        if ("OOS".equals(type)) return "超标";
        if ("OOT".equals(type)) return "超趋势";
        return type;
    }

    private String getReviewResultName(Integer result) {
        if (result == null) return "";
        if (result == 1) return "通过";
        if (result == 2) return "驳回";
        return "";
    }

    private String getIsClosedName(Integer isClosed) {
        if (isClosed == null) return "";
        return isClosed == 1 ? "是" : "否";
    }

    private String getProcessNodeName(String node) {
        if (StrUtil.isBlank(node)) return "";
        switch (node) {
            case "发现": return "发现";
            case "分配": return "分配";
            case "调查": return "调查";
            case "审核": return "审核";
            case "关闭": return "关闭";
            default: return node;
        }
    }

    @Override
    public PageResult<OosRecordVO> selectPage(OosRecordQuery query) {
        LambdaQueryWrapper<DetOosRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getOosNo())) {
            wrapper.like(DetOosRecord::getOosNo, query.getOosNo());
        }
        if (StrUtil.isNotBlank(query.getDataRecordNo())) {
            wrapper.like(DetOosRecord::getDataRecordNo, query.getDataRecordNo());
        }
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(DetOosRecord::getTaskNo, query.getTaskNo());
        }
        if (StrUtil.isNotBlank(query.getSampleNo())) {
            wrapper.like(DetOosRecord::getSampleNo, query.getSampleNo());
        }
        if (StrUtil.isNotBlank(query.getItemName())) {
            wrapper.like(DetOosRecord::getItemName, query.getItemName());
        }
        if (StrUtil.isNotBlank(query.getOosType())) {
            wrapper.eq(DetOosRecord::getOosType, query.getOosType());
        }
        if (query.getOosLevel() != null) {
            wrapper.eq(DetOosRecord::getOosLevel, query.getOosLevel());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DetOosRecord::getStatus, query.getStatus());
        }
        if (query.getIsClosed() != null) {
            wrapper.eq(DetOosRecord::getIsClosed, query.getIsClosed());
        }
        if (query.getDiscoveryUserId() != null) {
            wrapper.eq(DetOosRecord::getDiscoveryUserId, query.getDiscoveryUserId());
        }
        if (query.getInvestigatorUserId() != null) {
            wrapper.eq(DetOosRecord::getInvestigatorUserId, query.getInvestigatorUserId());
        }
        if (query.getDiscoveryTimeStart() != null) {
            wrapper.ge(DetOosRecord::getDiscoveryTime, query.getDiscoveryTimeStart().atStartOfDay());
        }
        if (query.getDiscoveryTimeEnd() != null) {
            wrapper.le(DetOosRecord::getDiscoveryTime, query.getDiscoveryTimeEnd().atTime(23, 59, 59));
        }
        wrapper.orderByDesc(DetOosRecord::getCreateTime);

        Page<DetOosRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<DetOosRecord> pageResult = this.page(page, wrapper);

        IPage<OosRecordVO> voPage = pageResult.convert(record -> convertToVO(record));

        return PageResult.of(voPage);
    }

    @Override
    public OosRecordDetailVO getDetail(Long id) {
        DetOosRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }

        OosRecordDetailVO vo = BeanUtil.copyProperties(record, OosRecordDetailVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setOosLevelName(getOosLevelName(record.getOosLevel()));
        vo.setOosTypeName(getOosTypeName(record.getOosType()));
        vo.setReviewResultName(getReviewResultName(record.getReviewResult()));
        vo.setIsClosedName(getIsClosedName(record.getIsClosed()));

        List<DetOosProcessLog> processLogList = processLogMapper.selectList(
                new LambdaQueryWrapper<DetOosProcessLog>()
                        .eq(DetOosProcessLog::getOosId, id)
                        .orderByDesc(DetOosProcessLog::getOperateTime)
        );
        vo.setProcessLogList(processLogList.stream()
                .map(this::convertProcessLogToVO)
                .collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOosRecord(Long dataRecordId, Long dataItemId) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dataRecordId);
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }

        DetDataRecordItem dataItem = dataRecordItemMapper.selectById(dataItemId);
        if (dataItem == null) {
            throw new BizException("检测数据明细不存在");
        }

        Long existingCount = this.count(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getDataRecordId, dataRecordId)
                        .eq(DetOosRecord::getDataItemId, dataItemId)
                        .ne(DetOosRecord::getStatus, STATUS_CLOSED)
        );
        if (existingCount > 0) {
            throw new BizException("该数据项已存在未关闭的OOS记录");
        }

        DetOosRecord oosRecord = new DetOosRecord();
        oosRecord.setOosNo(codeGenerator.generateOosRecordNo());
        oosRecord.setDataRecordId(dataRecordId);
        oosRecord.setDataRecordNo(dataRecord.getRecordNo());
        oosRecord.setDataItemId(dataItemId);
        oosRecord.setTaskId(dataRecord.getTaskId());
        oosRecord.setTaskNo(dataRecord.getTaskNo());
        oosRecord.setSampleId(dataRecord.getSampleId());
        oosRecord.setSampleNo(dataRecord.getSampleNo());
        oosRecord.setItemId(dataItem.getItemId());
        oosRecord.setItemCode(dataItem.getItemCode());
        oosRecord.setItemName(dataItem.getItemName());
        oosRecord.setUnit(dataItem.getUnit());
        oosRecord.setResultValue(dataItem.getResultNumeric());
        oosRecord.setLimitValue(dataItem.getLimitValue());
        oosRecord.setLimitMin(dataItem.getMinValue());
        oosRecord.setLimitMax(dataItem.getMaxValue());
        oosRecord.setOosType(dataItem.getOosType());
        oosRecord.setOosLevel(1);
        oosRecord.setDiscoveryTime(LocalDateTime.now());
        oosRecord.setDiscoveryUserId(SecurityUtils.getCurrentUserId());
        oosRecord.setDiscoveryUserName(SecurityUtils.getCurrentUsername());
        oosRecord.setStatus(STATUS_OPEN);
        oosRecord.setIsClosed(0);

        if (dataItem.getResultNumeric() != null && dataItem.getMaxValue() != null
                && dataItem.getMaxValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal deviation = dataItem.getResultNumeric()
                    .subtract(dataItem.getMaxValue())
                    .divide(dataItem.getMaxValue(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            oosRecord.setDeviationRate(deviation);
        }

        this.save(oosRecord);

        addProcessLog(oosRecord.getId(), "发现", "检测结果超标，自动创建OOS记录", null);

        WebSocketMessageVO wsMessage = new WebSocketMessageVO();
        wsMessage.setType("OOS_CREATED");
        wsMessage.setTitle("新的OOS超标预警");
        wsMessage.setContent("样品【" + dataRecord.getSampleNo() + "】的【" + dataItem.getItemName() + "】检测结果超标，请及时处理");
        wsMessage.setData(convertToVO(oosRecord));
        wsMessage.setSender(SecurityUtils.getCurrentUsername());
        webSocketHandler.broadcast(wsMessage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOosRecord(OosRecordSaveDTO dto) {
        DetOosRecord record;
        if (dto.getId() != null) {
            record = this.getById(dto.getId());
            if (record == null) {
                throw new BizException("OOS记录不存在");
            }
            if (record.getStatus() > STATUS_OPEN) {
                throw new BizException("OOS记录已启动调查，不能编辑基本信息");
            }
        } else {
            record = new DetOosRecord();
            record.setOosNo(codeGenerator.generateOosRecordNo());
            record.setStatus(STATUS_OPEN);
            record.setIsClosed(0);
            record.setDiscoveryTime(LocalDateTime.now());
            record.setDiscoveryUserId(SecurityUtils.getCurrentUserId());
            record.setDiscoveryUserName(SecurityUtils.getCurrentUsername());
        }

        BeanUtil.copyProperties(dto, record);

        if (dto.getId() == null && dto.getDataRecordId() != null) {
            DetDataRecord dataRecord = dataRecordMapper.selectById(dto.getDataRecordId());
            if (dataRecord != null) {
                record.setDataRecordNo(dataRecord.getRecordNo());
                record.setTaskId(dataRecord.getTaskId());
                record.setTaskNo(dataRecord.getTaskNo());
                record.setSampleId(dataRecord.getSampleId());
                record.setSampleNo(dataRecord.getSampleNo());
            }
        }

        if (dto.getId() == null && dto.getDataItemId() != null) {
            DetDataRecordItem dataItem = dataRecordItemMapper.selectById(dto.getDataItemId());
            if (dataItem != null) {
                record.setItemId(dataItem.getItemId());
                record.setItemCode(dataItem.getItemCode());
                record.setItemName(dataItem.getItemName());
                record.setUnit(dataItem.getUnit());
                record.setResultValue(dataItem.getResultNumeric());
                record.setLimitValue(dataItem.getLimitValue());
                record.setLimitMin(dataItem.getMinValue());
                record.setLimitMax(dataItem.getMaxValue());
                record.setOosType(dataItem.getOosType());
            }
        }

        if (dto.getId() == null) {
            this.save(record);
            addProcessLog(record.getId(), "发现", "手动创建OOS记录", null);
        } else {
            this.updateById(record);
            addProcessLog(record.getId(), "发现", "更新OOS记录基本信息", null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startInvestigation(OosInvestigationDTO dto) {
        DetOosRecord record = this.getById(dto.getOosId());
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }
        if (record.getStatus() != STATUS_OPEN) {
            throw new BizException("OOS记录状态不正确，不能启动调查");
        }

        Integer oldStatus = record.getStatus();
        record.setStatus(STATUS_INVESTIGATING);
        record.setInvestigatorUserId(dto.getInvestigatorUserId());
        record.setInvestigatorUserName(dto.getInvestigatorUserName());
        record.setInvestigationStartTime(LocalDateTime.now());
        this.updateById(record);

        addProcessLog(record.getId(), "分配",
                "启动调查，分配给: " + dto.getInvestigatorUserName(),
                dto.getAttachmentUrlList());

        sendOosStatusChangeMessage(record, oldStatus, STATUS_INVESTIGATING);

        if (dto.getInvestigatorUserId() != null) {
            WebSocketMessageVO wsMessage = new WebSocketMessageVO();
            wsMessage.setType("OOS_ASSIGNED");
            wsMessage.setTitle("OOS调查任务分配");
            wsMessage.setContent("您被分配为OOS【" + record.getOosNo() + "】的调查人员");
            wsMessage.setData(convertToVO(record));
            wsMessage.setSender(SecurityUtils.getCurrentUsername());
            webSocketHandler.sendToUser(dto.getInvestigatorUserId(), wsMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeInvestigation(OosInvestigationDTO dto) {
        DetOosRecord record = this.getById(dto.getOosId());
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }
        if (record.getStatus() != STATUS_INVESTIGATING) {
            throw new BizException("OOS记录状态不正确，不能完成调查");
        }

        Integer oldStatus = record.getStatus();
        record.setInvestigationResult(dto.getInvestigationResult());
        record.setCauseAnalysis(dto.getCauseAnalysis());
        record.setHandlingMeasure(dto.getHandlingMeasure());
        record.setCorrectiveAction(dto.getCorrectiveAction());
        record.setPreventiveAction(dto.getPreventiveAction());
        record.setInvestigationEndTime(dto.getInvestigationEndTime() != null
                ? dto.getInvestigationEndTime() : LocalDateTime.now());
        record.setStatus(STATUS_INVESTIGATED);
        this.updateById(record);

        addProcessLog(record.getId(), "调查",
                "完成调查，原因分析: " + dto.getCauseAnalysis(),
                dto.getAttachmentUrlList());

        sendOosStatusChangeMessage(record, oldStatus, STATUS_INVESTIGATED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewOos(OosReviewDTO dto) {
        DetOosRecord record = this.getById(dto.getOosId());
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }
        if (record.getStatus() != STATUS_INVESTIGATED) {
            throw new BizException("OOS记录状态不正确，不能审核");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUserName = SecurityUtils.getCurrentUsername();
        Integer oldStatus = record.getStatus();

        record.setReviewUserId(currentUserId);
        record.setReviewUserName(currentUserName);
        record.setReviewOpinion(dto.getReviewOpinion());
        record.setReviewResult(dto.getReviewResult());
        record.setReviewTime(LocalDateTime.now());

        if (dto.getReviewResult() == 1) {
            record.setStatus(STATUS_REVIEWING);
            addProcessLog(record.getId(), "审核",
                    "审核通过: " + dto.getReviewOpinion(), null);
        } else {
            record.setStatus(STATUS_INVESTIGATING);
            addProcessLog(record.getId(), "审核",
                    "审核驳回: " + dto.getReviewOpinion(), null);
        }

        this.updateById(record);

        sendOosStatusChangeMessage(record, oldStatus, record.getStatus());

        if (record.getInvestigatorUserId() != null) {
            WebSocketMessageVO wsMessage = new WebSocketMessageVO();
            wsMessage.setData(convertToVO(record));
            wsMessage.setSender(currentUserName);
            if (dto.getReviewResult() == 1) {
                wsMessage.setType("OOS_REVIEW_PASSED");
                wsMessage.setTitle("OOS审核通过");
                wsMessage.setContent("OOS【" + record.getOosNo() + "】审核通过");
            } else {
                wsMessage.setType("OOS_REVIEW_REJECTED");
                wsMessage.setTitle("OOS审核驳回");
                wsMessage.setContent("OOS【" + record.getOosNo() + "】审核被驳回: " + dto.getReviewOpinion());
            }
            webSocketHandler.sendToUser(record.getInvestigatorUserId(), wsMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOos(OosCloseDTO dto) {
        DetOosRecord record = this.getById(dto.getOosId());
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }
        if (record.getStatus() != STATUS_REVIEWING) {
            throw new BizException("OOS记录状态不正确，不能关闭");
        }

        Integer oldStatus = record.getStatus();
        record.setCloseUserId(SecurityUtils.getCurrentUserId());
        record.setCloseUserName(SecurityUtils.getCurrentUsername());
        record.setCloseTime(LocalDateTime.now());
        record.setCloseOpinion(dto.getCloseOpinion());
        record.setStatus(STATUS_CLOSED);
        record.setIsClosed(1);
        this.updateById(record);

        addProcessLog(record.getId(), "关闭",
                "关闭OOS，关闭意见: " + dto.getCloseOpinion(), null);

        sendOosStatusChangeMessage(record, oldStatus, STATUS_CLOSED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignInvestigator(Long oosId, Long investigatorUserId, String investigatorUserName) {
        DetOosRecord record = this.getById(oosId);
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }
        if (record.getStatus() == STATUS_CLOSED) {
            throw new BizException("OOS记录已关闭，不能分配调查人员");
        }

        record.setInvestigatorUserId(investigatorUserId);
        record.setInvestigatorUserName(investigatorUserName);
        this.updateById(record);

        addProcessLog(oosId, "分配",
                "重新分配调查人员: " + investigatorUserName, null);

        if (investigatorUserId != null) {
            WebSocketMessageVO wsMessage = new WebSocketMessageVO();
            wsMessage.setType("OOS_ASSIGNED");
            wsMessage.setTitle("OOS调查任务分配");
            wsMessage.setContent("您被分配为OOS【" + record.getOosNo() + "】的调查人员");
            wsMessage.setData(convertToVO(record));
            wsMessage.setSender(SecurityUtils.getCurrentUsername());
            webSocketHandler.sendToUser(investigatorUserId, wsMessage);
        }
    }

    @Override
    public List<OosRecordVO> getPendingInvestigationList() {
        List<DetOosRecord> list = this.list(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getStatus, STATUS_OPEN)
                        .or()
                        .eq(DetOosRecord::getStatus, STATUS_INVESTIGATING)
                        .orderByAsc(DetOosRecord::getOosLevel, DetOosRecord::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<OosRecordVO> getPendingReviewList() {
        List<DetOosRecord> list = this.list(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getStatus, STATUS_INVESTIGATED)
                        .or()
                        .eq(DetOosRecord::getStatus, STATUS_REVIEWING)
                        .orderByAsc(DetOosRecord::getOosLevel, DetOosRecord::getCreateTime)
        );
        return list.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public long getPendingInvestigationCount() {
        return this.count(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getStatus, STATUS_OPEN)
                        .or()
                        .eq(DetOosRecord::getStatus, STATUS_INVESTIGATING)
        );
    }

    @Override
    public long getPendingReviewCount() {
        return this.count(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getStatus, STATUS_INVESTIGATED)
                        .or()
                        .eq(DetOosRecord::getStatus, STATUS_REVIEWING)
        );
    }

    @Override
    public long getOpenOosCount() {
        return this.count(
                new LambdaQueryWrapper<DetOosRecord>()
                        .ne(DetOosRecord::getStatus, STATUS_CLOSED)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProcessLog(Long oosId, String processNode, String processContent, List<String> attachmentUrlList) {
        DetOosRecord record = this.getById(oosId);
        if (record == null) {
            throw new BizException("OOS记录不存在");
        }

        DetOosProcessLog log = new DetOosProcessLog();
        log.setOosId(oosId);
        log.setOosNo(record.getOosNo());
        log.setProcessNode(processNode);
        log.setProcessContent(processContent);
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setOperateTime(LocalDateTime.now());

        if (attachmentUrlList != null && !attachmentUrlList.isEmpty()) {
            try {
                log.setAttachmentUrls(objectMapper.writeValueAsString(attachmentUrlList));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化附件地址列表失败");
            }
        }

        processLogMapper.insert(log);
    }

    private OosRecordVO convertToVO(DetOosRecord record) {
        OosRecordVO vo = BeanUtil.copyProperties(record, OosRecordVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setOosLevelName(getOosLevelName(record.getOosLevel()));
        vo.setOosTypeName(getOosTypeName(record.getOosType()));
        vo.setIsClosedName(getIsClosedName(record.getIsClosed()));
        return vo;
    }

    private OosProcessLogVO convertProcessLogToVO(DetOosProcessLog log) {
        OosProcessLogVO vo = BeanUtil.copyProperties(log, OosProcessLogVO.class);
        vo.setProcessNodeName(getProcessNodeName(log.getProcessNode()));

        if (StrUtil.isNotBlank(log.getAttachmentUrls())) {
            try {
                vo.setAttachmentUrlList(objectMapper.readValue(log.getAttachmentUrls(), List.class));
            } catch (JsonProcessingException e) {
                vo.setAttachmentUrlList(Collections.emptyList());
            }
        }

        return vo;
    }

    private void sendOosStatusChangeMessage(DetOosRecord record, Integer beforeStatus, Integer afterStatus) {
        if (beforeStatus != null && !beforeStatus.equals(afterStatus)) {
            WebSocketMessageVO wsMessage = new WebSocketMessageVO();
            wsMessage.setType("OOS_STATUS_CHANGED");
            wsMessage.setTitle("OOS状态变更");
            wsMessage.setContent("OOS【" + record.getOosNo() + "】状态从【"
                    + getStatusName(beforeStatus) + "】变更为【" + getStatusName(afterStatus) + "】");
            wsMessage.setData(convertToVO(record));
            wsMessage.setSender(SecurityUtils.getCurrentUsername());
            webSocketHandler.broadcast(wsMessage);
        }
    }
}
