package com.lims.detection.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
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
import com.lims.detection.dto.CapaProcessDTO;
import com.lims.detection.dto.CapaQuery;
import com.lims.detection.dto.CapaSaveDTO;
import com.lims.detection.dto.CapaVerificationDTO;
import com.lims.detection.entity.CapaProcessLog;
import com.lims.detection.entity.CapaRecord;
import com.lims.detection.mapper.CapaProcessLogMapper;
import com.lims.detection.mapper.CapaRecordMapper;
import com.lims.detection.service.CapaService;
import com.lims.detection.vo.CapaProcessLogVO;
import com.lims.detection.vo.CapaRecordVO;
import com.lims.detection.vo.CapaStatsVO;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CapaServiceImpl extends ServiceImpl<CapaRecordMapper, CapaRecord> implements CapaService {

    @Autowired
    private CapaProcessLogMapper processLogMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final AtomicInteger capaSeq = new AtomicInteger(1);

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PENDING_APPROVAL = 1;
    private static final int STATUS_IN_PROGRESS = 2;
    private static final int STATUS_PENDING_VERIFICATION = 3;
    private static final int STATUS_VERIFIED = 4;
    private static final int STATUS_CLOSED = 5;
    private static final int STATUS_REJECTED = 6;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "待审批";
            case 2: return "执行中";
            case 3: return "待验证";
            case 4: return "已验证";
            case 5: return "已关闭";
            case 6: return "已驳回";
            default: return "";
        }
    }

    private Integer parseStatus(String statusStr) {
        if (StrUtil.isBlank(statusStr)) return null;
        try {
            return Integer.parseInt(statusStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String generateCapaNo() {
        String year = DateUtil.format(DateUtil.date(), "yyyy");
        int sequence = capaSeq.getAndIncrement();
        if (sequence > 999) {
            capaSeq.set(1);
            sequence = 1;
        }
        return String.format("CAPA-%s-%03d", year, sequence);
    }

    @Override
    public PageResult<CapaRecordVO> selectPage(CapaQuery query) {
        LambdaQueryWrapper<CapaRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(CapaRecord::getTitle, query.getKeyword())
                    .or().like(CapaRecord::getSourceNo, query.getKeyword()));
        }
        if (StrUtil.isNotBlank(query.getCapaType())) {
            wrapper.eq(CapaRecord::getCapaType, query.getCapaType());
        }
        if (StrUtil.isNotBlank(query.getSourceType())) {
            wrapper.eq(CapaRecord::getSourceType, query.getSourceType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(CapaRecord::getStatus, String.valueOf(query.getStatus()));
        }
        if (StrUtil.isNotBlank(query.getSeverityLevel())) {
            wrapper.eq(CapaRecord::getSeverityLevel, query.getSeverityLevel());
        }
        if (StrUtil.isNotBlank(query.getPriority())) {
            wrapper.eq(CapaRecord::getPriority, query.getPriority());
        }
        wrapper.orderByDesc(CapaRecord::getCreateTime);

        Page<CapaRecord> page = new Page<>(1, 10);
        try {
            if (query.getClass().getMethod("getPageNum") != null && query.getClass().getMethod("getPageSize") != null) {
                Integer pageNum = (Integer) query.getClass().getMethod("getPageNum").invoke(query);
                Integer pageSize = (Integer) query.getClass().getMethod("getPageSize").invoke(query);
                if (pageNum != null && pageSize != null) {
                    page = new Page<>(pageNum, pageSize);
                }
            }
        } catch (Exception ignored) {
        }

        IPage<CapaRecord> pageResult = this.page(page, wrapper);
        IPage<CapaRecordVO> voPage = pageResult.convert(this::convertToVO);
        return PageResult.of(voPage);
    }

    @Override
    public CapaRecordVO getDetail(Long id) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        CapaRecordVO vo = convertToVO(record);
        vo.setProcessLogs(getProcessLogs(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCapa(CapaSaveDTO dto) {
        CapaRecord record = new CapaRecord();
        BeanUtil.copyProperties(dto, record);
        record.setCapaNo(generateCapaNo());
        record.setStatus(String.valueOf(STATUS_DRAFT));
        this.save(record);
        addProcessLog(record.getId(), "创建", getStatusName(STATUS_DRAFT), "创建CAPA记录", SecurityUtils.getCurrentUsername());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCapa(CapaSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("CAPA ID不能为空");
        }
        CapaRecord record = this.getById(dto.getId());
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer status = parseStatus(record.getStatus());
        if (status != null && status != STATUS_DRAFT && status != STATUS_REJECTED) {
            throw new BizException("CAPA非草稿或已驳回状态，不能编辑");
        }
        BeanUtil.copyProperties(dto, record);
        this.updateById(record);
        addProcessLog(record.getId(), "更新", getStatusName(status), "更新CAPA记录", SecurityUtils.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCapa(Long id) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer status = parseStatus(record.getStatus());
        if (status != null && status != STATUS_DRAFT && status != STATUS_REJECTED) {
            throw new BizException("CAPA非草稿或已驳回状态，不能删除");
        }
        this.removeById(id);
        processLogMapper.delete(new LambdaQueryWrapper<CapaProcessLog>().eq(CapaProcessLog::getCapaId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || (oldStatus != STATUS_DRAFT && oldStatus != STATUS_REJECTED)) {
            throw new BizException("CAPA状态不正确，不能提交审批");
        }
        record.setStatus(String.valueOf(STATUS_PENDING_APPROVAL));
        this.updateById(record);
        addProcessLog(id, "提交审批", getStatusName(STATUS_PENDING_APPROVAL),
                "提交审批，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_PENDING_APPROVAL) + "】",
                SecurityUtils.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, CapaProcessDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_PENDING_APPROVAL) {
            throw new BizException("CAPA状态不正确，不能审批");
        }
        record.setStatus(String.valueOf(STATUS_IN_PROGRESS));
        record.setApprover(SecurityUtils.getCurrentUserId());
        record.setApproverName(SecurityUtils.getCurrentUsername());
        this.updateById(record);

        String content = StrUtil.isNotBlank(dto.getOperationContent()) ? dto.getOperationContent() : "审批通过";
        addProcessLogWithAttachments(id, "审批通过", getStatusName(STATUS_IN_PROGRESS),
                content + "，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_IN_PROGRESS) + "】",
                SecurityUtils.getCurrentUsername(), dto.getAttachments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long id, CapaProcessDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_PENDING_APPROVAL) {
            throw new BizException("CAPA状态不正确，不能驳回");
        }
        record.setStatus(String.valueOf(STATUS_REJECTED));
        record.setApprover(SecurityUtils.getCurrentUserId());
        record.setApproverName(SecurityUtils.getCurrentUsername());
        this.updateById(record);

        String content = StrUtil.isNotBlank(dto.getOperationContent()) ? dto.getOperationContent() : "审批驳回";
        addProcessLogWithAttachments(id, "审批驳回", getStatusName(STATUS_REJECTED),
                content + "，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_REJECTED) + "】",
                SecurityUtils.getCurrentUsername(), dto.getAttachments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeComplete(Long id, CapaProcessDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_IN_PROGRESS) {
            throw new BizException("CAPA状态不正确，不能执行完成");
        }
        record.setStatus(String.valueOf(STATUS_PENDING_VERIFICATION));
        record.setActualCompleteDate(LocalDateTime.now());
        this.updateById(record);

        String content = StrUtil.isNotBlank(dto.getOperationContent()) ? dto.getOperationContent() : "执行完成";
        addProcessLogWithAttachments(id, "执行完成", getStatusName(STATUS_PENDING_VERIFICATION),
                content + "，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_PENDING_VERIFICATION) + "】",
                SecurityUtils.getCurrentUsername(), dto.getAttachments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyPass(Long id, CapaVerificationDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_PENDING_VERIFICATION) {
            throw new BizException("CAPA状态不正确，不能验证");
        }
        record.setStatus(String.valueOf(STATUS_VERIFIED));
        record.setVerificationDate(LocalDateTime.now());
        record.setVerificationResult(dto.getVerificationResult());
        record.setVerificationEvidence(dto.getVerificationEvidence());
        this.updateById(record);

        addProcessLog(id, "验证通过", getStatusName(STATUS_VERIFIED),
                "验证通过，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_VERIFIED) + "】，验证结果：" + dto.getVerificationResult(),
                SecurityUtils.getCurrentUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void verifyFail(Long id, CapaProcessDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_PENDING_VERIFICATION) {
            throw new BizException("CAPA状态不正确，不能验证不通过");
        }
        record.setStatus(String.valueOf(STATUS_IN_PROGRESS));
        this.updateById(record);

        String content = StrUtil.isNotBlank(dto.getOperationContent()) ? dto.getOperationContent() : "验证不通过";
        addProcessLogWithAttachments(id, "验证不通过", getStatusName(STATUS_IN_PROGRESS),
                content + "，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_IN_PROGRESS) + "】，需重新执行",
                SecurityUtils.getCurrentUsername(), dto.getAttachments());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeCapa(Long id, CapaProcessDTO dto) {
        CapaRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("CAPA记录不存在");
        }
        Integer oldStatus = parseStatus(record.getStatus());
        if (oldStatus == null || oldStatus != STATUS_VERIFIED) {
            throw new BizException("CAPA状态不正确，不能关闭");
        }
        record.setStatus(String.valueOf(STATUS_CLOSED));
        record.setCloseDate(LocalDateTime.now());
        record.setEffectivenessReview(dto.getOperationContent());
        this.updateById(record);

        String content = StrUtil.isNotBlank(dto.getOperationContent()) ? dto.getOperationContent() : "关闭CAPA";
        addProcessLogWithAttachments(id, "关闭", getStatusName(STATUS_CLOSED),
                content + "，状态从【" + getStatusName(oldStatus) + "】变更为【" + getStatusName(STATUS_CLOSED) + "】",
                SecurityUtils.getCurrentUsername(), dto.getAttachments());
    }

    @Override
    public List<CapaProcessLogVO> getProcessLogs(Long capaId) {
        List<CapaProcessLog> logList = processLogMapper.selectList(
                new LambdaQueryWrapper<CapaProcessLog>()
                        .eq(CapaProcessLog::getCapaId, capaId)
                        .orderByDesc(CapaProcessLog::getOperationTime)
        );
        return logList.stream().map(this::convertLogToVO).collect(Collectors.toList());
    }

    @Override
    public CapaStatsVO getStats() {
        CapaStatsVO stats = new CapaStatsVO();
        stats.setTotal(this.count().intValue());
        stats.setPendingApproval(this.count(new LambdaQueryWrapper<CapaRecord>()
                .eq(CapaRecord::getStatus, String.valueOf(STATUS_PENDING_APPROVAL))).intValue());
        stats.setInProgress(this.count(new LambdaQueryWrapper<CapaRecord>()
                .eq(CapaRecord::getStatus, String.valueOf(STATUS_IN_PROGRESS))).intValue());
        stats.setPendingVerification(this.count(new LambdaQueryWrapper<CapaRecord>()
                .eq(CapaRecord::getStatus, String.valueOf(STATUS_PENDING_VERIFICATION))).intValue());
        stats.setClosed(this.count(new LambdaQueryWrapper<CapaRecord>()
                .eq(CapaRecord::getStatus, String.valueOf(STATUS_CLOSED))).intValue());

        List<CapaRecord> allRecords = this.list();
        int overdueCount = 0;
        LocalDateTime now = LocalDateTime.now();
        for (CapaRecord record : allRecords) {
            Integer status = parseStatus(record.getStatus());
            if (status != null && status != STATUS_CLOSED && record.getPlanCompleteDate() != null) {
                if (record.getPlanCompleteDate().isBefore(now)) {
                    overdueCount++;
                }
            }
        }
        stats.setOverdue(overdueCount);
        return stats;
    }

    @Override
    public List<CapaRecordVO> getOverdueCapa() {
        LocalDateTime now = LocalDateTime.now();
        List<CapaRecord> records = this.list(new LambdaQueryWrapper<CapaRecord>()
                .ne(CapaRecord::getStatus, String.valueOf(STATUS_CLOSED))
                .lt(CapaRecord::getPlanCompleteDate, now)
                .orderByAsc(CapaRecord::getPlanCompleteDate));
        return records.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProcessLog(Long capaId, String operationType, String operationStatus, String content, String operatorName) {
        CapaProcessLog log = new CapaProcessLog();
        log.setCapaId(capaId);
        log.setOperationType(operationType);
        log.setOperationStatus(operationStatus);
        log.setOperationContent(content);
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(operatorName != null ? operatorName : SecurityUtils.getCurrentUsername());
        log.setOperationTime(LocalDateTime.now());
        processLogMapper.insert(log);
    }

    private void addProcessLogWithAttachments(Long capaId, String operationType, String operationStatus,
                                              String content, String operatorName, List<String> attachments) {
        CapaProcessLog log = new CapaProcessLog();
        log.setCapaId(capaId);
        log.setOperationType(operationType);
        log.setOperationStatus(operationStatus);
        log.setOperationContent(content);
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(operatorName != null ? operatorName : SecurityUtils.getCurrentUsername());
        log.setOperationTime(LocalDateTime.now());

        if (attachments != null && !attachments.isEmpty()) {
            try {
                log.setAttachments(objectMapper.writeValueAsString(attachments));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化附件列表失败");
            }
        }
        processLogMapper.insert(log);
    }

    private CapaRecordVO convertToVO(CapaRecord record) {
        CapaRecordVO vo = BeanUtil.copyProperties(record, CapaRecordVO.class);
        Integer status = parseStatus(record.getStatus());
        if (status != null) {
            vo.setStatus(getStatusName(status));
        }
        return vo;
    }

    private CapaProcessLogVO convertLogToVO(CapaProcessLog log) {
        CapaProcessLogVO vo = BeanUtil.copyProperties(log, CapaProcessLogVO.class);
        if (StrUtil.isNotBlank(log.getAttachments())) {
            try {
                vo.setAttachments(objectMapper.readValue(log.getAttachments(), String.class));
            } catch (JsonProcessingException e) {
                vo.setAttachments(log.getAttachments());
            }
        }
        return vo;
    }
}
