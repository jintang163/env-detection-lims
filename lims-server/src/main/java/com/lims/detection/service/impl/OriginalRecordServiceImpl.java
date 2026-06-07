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
import com.lims.detection.dto.OriginalRecordQuery;
import com.lims.detection.dto.OriginalRecordSaveDTO;
import com.lims.detection.entity.DetDataAuditLog;
import com.lims.detection.entity.DetDataRecord;
import com.lims.detection.entity.DetOriginalRecord;
import com.lims.detection.entity.DetOriginalRecordVersion;
import com.lims.detection.mapper.DetDataAuditLogMapper;
import com.lims.detection.mapper.DetDataRecordMapper;
import com.lims.detection.mapper.DetOriginalRecordMapper;
import com.lims.detection.service.OriginalRecordService;
import com.lims.detection.vo.OriginalRecordDetailVO;
import com.lims.detection.vo.OriginalRecordVO;
import com.lims.detection.vo.OriginalRecordVersionVO;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OriginalRecordServiceImpl extends ServiceImpl<DetOriginalRecordMapper, DetOriginalRecord> implements OriginalRecordService {

    public static final Integer STATUS_DRAFT = 0;
    public static final Integer STATUS_SUBMITTED = 1;
    public static final Integer STATUS_ARCHIVED = 2;

    public static final String PERMISSION_READ = "READ";
    public static final String PERMISSION_WRITE = "WRITE";

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private DetOriginalRecordVersionMapper versionMapper;

    @Autowired
    private DetDataRecordMapper dataRecordMapper;

    @Autowired
    private DetDataAuditLogMapper auditLogMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已提交";
            case 2: return "已归档";
            default: return "";
        }
    }

    private String getPermissionTypeName(Integer permissionType) {
        if (permissionType == null) return "";
        return permissionType == 1 ? "只读" : "可写";
    }

    private OriginalRecordVO convertToVO(DetOriginalRecord record) {
        OriginalRecordVO vo = BeanUtil.copyProperties(record, OriginalRecordVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setPermissionTypeName(getPermissionTypeName(record.getPermissionType()));
        return vo;
    }

    private void saveAuditLog(DetOriginalRecord record, String operateType, String fieldName,
                              String oldValue, String newValue, Integer tamperAttempt, String blockReason) {
        DetDataAuditLog auditLog = new DetDataAuditLog();
        auditLog.setDataType("original_record");
        auditLog.setDataId(record.getId());
        auditLog.setDataNo(record.getRecordNo());
        auditLog.setOperateType(operateType);
        auditLog.setFieldName(fieldName);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setOperatorId(SecurityUtils.getCurrentUserId());
        auditLog.setOperatorName(SecurityUtils.getCurrentUsername());
        auditLog.setOperateTime(LocalDateTime.now());
        auditLog.setTamperAttempt(tamperAttempt);
        auditLog.setBlockReason(blockReason);
        auditLogMapper.insert(auditLog);
    }

    private void saveVersion(DetOriginalRecord record, String operateType) {
        saveVersion(record, operateType, null);
    }

    private void saveVersion(DetOriginalRecord record, String operateType, String changeReason) {
        DetOriginalRecordVersion version = new DetOriginalRecordVersion();
        version.setOriginalRecordId(record.getId());
        version.setOriginalRecordNo(record.getRecordNo());
        version.setVersion(record.getVersion());
        version.setContent(record.getContent());
        version.setHtmlContent(record.getHtmlContent());
        version.setDataHash(record.getDataHash());
        version.setChangeType(operateType);
        version.setChangeReason(changeReason);
        version.setOperatorId(SecurityUtils.getCurrentUserId());
        version.setOperatorName(SecurityUtils.getCurrentUsername());
        version.setOperateTime(LocalDateTime.now());
        versionMapper.insert(version);
    }

    @Override
    public String calculateContentHash(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("计算内容哈希失败");
        }
    }

    @Override
    public boolean verifyOriginalRecordIntegrity(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }
        if (StrUtil.isBlank(record.getContent()) || StrUtil.isBlank(record.getDataHash())) {
            return false;
        }
        String currentHash = calculateContentHash(record.getContent());
        return currentHash.equals(record.getDataHash());
    }

    @Override
    public boolean checkPermission(Long id, Long userId, String userRole) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }
        Integer status = record.getStatus();
        Long createBy = record.getCreateBy();

        if (STATUS_DRAFT.equals(status)) {
            return userId != null && userId.equals(createBy);
        } else if (STATUS_SUBMITTED.equals(status)) {
            if (userId != null && userId.equals(createBy)) {
                return true;
            }
            if (StrUtil.isNotBlank(userRole) && ("REVIEWER".equals(userRole) || "ADMIN".equals(userRole))) {
                return true;
            }
        } else if (STATUS_ARCHIVED.equals(status)) {
            return true;
        }
        return false;
    }

    @Override
    public PageResult<OriginalRecordVO> selectPage(OriginalRecordQuery query) {
        LambdaQueryWrapper<DetOriginalRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getRecordNo())) {
            wrapper.like(DetOriginalRecord::getRecordNo, query.getRecordNo());
        }
        if (StrUtil.isNotBlank(query.getDataRecordNo())) {
            wrapper.like(DetOriginalRecord::getDataRecordNo, query.getDataRecordNo());
        }
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(DetOriginalRecord::getTaskNo, query.getTaskNo());
        }
        if (StrUtil.isNotBlank(query.getSampleNo())) {
            wrapper.like(DetOriginalRecord::getSampleNo, query.getSampleNo());
        }
        if (StrUtil.isNotBlank(query.getTitle())) {
            wrapper.like(DetOriginalRecord::getTitle, query.getTitle());
        }
        if (StrUtil.isNotBlank(query.getRecordType())) {
            wrapper.eq(DetOriginalRecord::getRecordType, query.getRecordType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DetOriginalRecord::getStatus, query.getStatus());
        }
        if (query.getPermissionType() != null) {
            wrapper.eq(DetOriginalRecord::getPermissionType, query.getPermissionType());
        }
        if (query.getCreateBy() != null) {
            wrapper.eq(DetOriginalRecord::getCreateBy, query.getCreateBy());
        }
        if (query.getCreateTimeStart() != null) {
            wrapper.ge(DetOriginalRecord::getCreateTime, query.getCreateTimeStart().atStartOfDay());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.le(DetOriginalRecord::getCreateTime, query.getCreateTimeEnd().atTime(23, 59, 59));
        }
        wrapper.orderByDesc(DetOriginalRecord::getCreateTime);

        Page<DetOriginalRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<DetOriginalRecord> pageResult = this.page(page, wrapper);

        IPage<OriginalRecordVO> voPage = pageResult.convert(this::convertToVO);

        return PageResult.of(voPage);
    }

    @Override
    public OriginalRecordDetailVO getDetail(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }

        OriginalRecordDetailVO vo = BeanUtil.copyProperties(record, OriginalRecordDetailVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setPermissionTypeName(getPermissionTypeName(record.getPermissionType()));

        try {
            if (StrUtil.isNotBlank(record.getAttachmentUrls())) {
                vo.setAttachmentUrlList(objectMapper.readValue(record.getAttachmentUrls(), List.class));
            }
            if (StrUtil.isNotBlank(record.getAttachmentNames())) {
                vo.setAttachmentNameList(objectMapper.readValue(record.getAttachmentNames(), List.class));
            }
            if (StrUtil.isNotBlank(record.getPermissionUsers())) {
                vo.setPermissionUserIdList(objectMapper.readValue(record.getPermissionUsers(), List.class));
            }
            if (StrUtil.isNotBlank(record.getPermissionRoles())) {
                vo.setPermissionRoleList(objectMapper.readValue(record.getPermissionRoles(), List.class));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetOriginalRecordVersion> versionList = versionMapper.selectList(
                new LambdaQueryWrapper<DetOriginalRecordVersion>()
                        .eq(DetOriginalRecordVersion::getOriginalRecordId, id)
                        .orderByDesc(DetOriginalRecordVersion::getVersion)
        );
        vo.setVersionList(versionList.stream().map(v -> {
            OriginalRecordVersionVO vvo = BeanUtil.copyProperties(v, OriginalRecordVersionVO.class);
            return vvo;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOriginalRecord(OriginalRecordSaveDTO dto) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dto.getDataRecordId());
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }

        DetOriginalRecord record = BeanUtil.copyProperties(dto, DetOriginalRecord.class);
        record.setRecordNo(codeGenerator.generateOriginalRecordNo());
        record.setTaskId(dataRecord.getTaskId());
        record.setTaskNo(dataRecord.getTaskNo());
        record.setSampleId(dataRecord.getSampleId());
        record.setSampleNo(dataRecord.getSampleNo());
        record.setDataRecordNo(dataRecord.getRecordNo());
        record.setStatus(STATUS_DRAFT);
        record.setVersion(1);
        record.setEditCount(0);
        record.setDataHash(calculateContentHash(dto.getContent()));
        record.setLastEditUserId(SecurityUtils.getCurrentUserId());
        record.setLastEditUserName(SecurityUtils.getCurrentUsername());
        record.setLastEditTime(LocalDateTime.now());

        try {
            if (dto.getAttachmentUrlList() != null && !dto.getAttachmentUrlList().isEmpty()) {
                record.setAttachmentUrls(objectMapper.writeValueAsString(dto.getAttachmentUrlList()));
            }
            if (dto.getAttachmentNameList() != null && !dto.getAttachmentNameList().isEmpty()) {
                record.setAttachmentNames(objectMapper.writeValueAsString(dto.getAttachmentNameList()));
            }
            if (dto.getPermissionUserIdList() != null && !dto.getPermissionUserIdList().isEmpty()) {
                record.setPermissionUsers(objectMapper.writeValueAsString(dto.getPermissionUserIdList()));
            }
            if (dto.getPermissionRoleList() != null && !dto.getPermissionRoleList().isEmpty()) {
                record.setPermissionRoles(objectMapper.writeValueAsString(dto.getPermissionRoleList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.save(record);

        saveVersion(record, "创建", dto.getChangeReason());
        saveAuditLog(record, "create", null, null, null, 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOriginalRecord(OriginalRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("原始记录ID不能为空");
        }

        DetOriginalRecord oldRecord = this.getById(dto.getId());
        if (oldRecord == null) {
            throw new BizException("原始记录不存在");
        }

        if (!STATUS_DRAFT.equals(oldRecord.getStatus())) {
            throw new BizException("只能修改草稿状态的原始记录");
        }

        if (dto.getVersion() == null || !dto.getVersion().equals(oldRecord.getVersion())) {
            saveAuditLog(oldRecord, "update", "version", 
                    oldRecord.getVersion().toString(), dto.getVersion() != null ? dto.getVersion().toString() : "null", 
                    0, "版本号不匹配，数据已被他人修改");
            throw new BizException("数据已被他人修改，请刷新后重试");
        }

        String currentHash = calculateContentHash(oldRecord.getContent());
        if (!currentHash.equals(oldRecord.getDataHash())) {
            saveAuditLog(oldRecord, "update", "content", 
                    oldRecord.getDataHash(), currentHash, 
                    1, "内容哈希不匹配，检测到篡改尝试");
            throw new BizException("内容已被篡改，无法更新");
        }

        DetOriginalRecord record = BeanUtil.copyProperties(dto, DetOriginalRecord.class);
        record.setRecordNo(oldRecord.getRecordNo());
        record.setTaskId(oldRecord.getTaskId());
        record.setTaskNo(oldRecord.getTaskNo());
        record.setSampleId(oldRecord.getSampleId());
        record.setSampleNo(oldRecord.getSampleNo());
        record.setDataRecordId(oldRecord.getDataRecordId());
        record.setDataRecordNo(oldRecord.getDataRecordNo());
        record.setVersion(oldRecord.getVersion() + 1);
        record.setEditCount(oldRecord.getEditCount() + 1);
        record.setDataHash(calculateContentHash(dto.getContent()));
        record.setLastEditUserId(SecurityUtils.getCurrentUserId());
        record.setLastEditUserName(SecurityUtils.getCurrentUsername());
        record.setLastEditTime(LocalDateTime.now());
        record.setCreateBy(oldRecord.getCreateBy());
        record.setCreateTime(oldRecord.getCreateTime());

        try {
            if (dto.getAttachmentUrlList() != null && !dto.getAttachmentUrlList().isEmpty()) {
                record.setAttachmentUrls(objectMapper.writeValueAsString(dto.getAttachmentUrlList()));
            }
            if (dto.getAttachmentNameList() != null && !dto.getAttachmentNameList().isEmpty()) {
                record.setAttachmentNames(objectMapper.writeValueAsString(dto.getAttachmentNameList()));
            }
            if (dto.getPermissionUserIdList() != null && !dto.getPermissionUserIdList().isEmpty()) {
                record.setPermissionUsers(objectMapper.writeValueAsString(dto.getPermissionUserIdList()));
            }
            if (dto.getPermissionRoleList() != null && !dto.getPermissionRoleList().isEmpty()) {
                record.setPermissionRoles(objectMapper.writeValueAsString(dto.getPermissionRoleList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.updateById(record);

        saveVersion(record, "修改", dto.getChangeReason());
        saveAuditLog(record, "update", "content", oldRecord.getContent(), record.getContent(), 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOriginalRecord(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }
        if (!STATUS_DRAFT.equals(record.getStatus())) {
            throw new BizException("只能提交草稿状态的原始记录");
        }

        String currentHash = calculateContentHash(record.getContent());
        if (!currentHash.equals(record.getDataHash())) {
            saveAuditLog(record, "submit", "content", 
                    record.getDataHash(), currentHash, 
                    1, "内容哈希不匹配，检测到篡改尝试");
            throw new BizException("内容已被篡改，无法提交");
        }

        Integer oldStatus = record.getStatus();
        record.setStatus(STATUS_SUBMITTED);
        record.setLastEditUserId(SecurityUtils.getCurrentUserId());
        record.setLastEditUserName(SecurityUtils.getCurrentUsername());
        record.setLastEditTime(LocalDateTime.now());

        this.updateById(record);

        saveVersion(record, "提交", null);
        saveAuditLog(record, "submit", "status", oldStatus.toString(), STATUS_SUBMITTED.toString(), 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveOriginalRecord(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }
        if (!STATUS_SUBMITTED.equals(record.getStatus())) {
            throw new BizException("只能归档已提交状态的原始记录");
        }

        String currentHash = calculateContentHash(record.getContent());
        if (!currentHash.equals(record.getDataHash())) {
            saveAuditLog(record, "archive", "content", 
                    record.getDataHash(), currentHash, 
                    1, "内容哈希不匹配，检测到篡改尝试");
            throw new BizException("内容已被篡改，无法归档");
        }

        Integer oldStatus = record.getStatus();
        record.setStatus(STATUS_ARCHIVED);
        record.setLastEditUserId(SecurityUtils.getCurrentUserId());
        record.setLastEditUserName(SecurityUtils.getCurrentUsername());
        record.setLastEditTime(LocalDateTime.now());

        this.updateById(record);

        saveVersion(record, "归档", null);
        saveAuditLog(record, "archive", "status", oldStatus.toString(), STATUS_ARCHIVED.toString(), 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOriginalRecord(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }
        if (!STATUS_DRAFT.equals(record.getStatus())) {
            throw new BizException("只能删除草稿状态的原始记录");
        }

        this.removeById(id);
        saveAuditLog(record, "delete", null, null, null, 0, null);
    }

    @Override
    public String previewAsHtml(Long id) {
        DetOriginalRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("原始记录不存在");
        }

        if (StrUtil.isNotBlank(record.getHtmlContent())) {
            return record.getHtmlContent();
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>").append(record.getTitle() != null ? record.getTitle() : "原始记录预览").append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }\n");
        html.append(".header { border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px; }\n");
        html.append(".title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }\n");
        html.append(".info { font-size: 14px; color: #666; margin-bottom: 5px; }\n");
        html.append(".content { margin-top: 20px; line-height: 1.6; }\n");
        html.append(".footer { margin-top: 30px; border-top: 1px solid #ccc; padding-top: 10px; font-size: 12px; color: #999; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"header\">\n");
        html.append("<div class=\"title\">").append(record.getTitle() != null ? escapeHtml(record.getTitle()) : "原始记录").append("</div>\n");
        html.append("<div class=\"info\">记录编号: ").append(escapeHtml(record.getRecordNo())).append("</div>\n");
        html.append("<div class=\"info\">样品编号: ").append(escapeHtml(record.getSampleNo())).append("</div>\n");
        html.append("<div class=\"info\">任务编号: ").append(escapeHtml(record.getTaskNo())).append("</div>\n");
        html.append("<div class=\"info\">记录类型: ").append(escapeHtml(record.getRecordType())).append("</div>\n");
        html.append("<div class=\"info\">状态: ").append(getStatusName(record.getStatus())).append("</div>\n");
        html.append("</div>\n");
        html.append("<div class=\"content\">\n");
        if (StrUtil.isNotBlank(record.getContent())) {
            html.append("<pre>").append(escapeHtml(record.getContent())).append("</pre>\n");
        } else {
            html.append("<p>暂无内容</p>\n");
        }
        html.append("</div>\n");
        html.append("<div class=\"footer\">\n");
        html.append("<div>创建人: ").append(record.getCreateBy()).append("</div>\n");
        html.append("<div>创建时间: ").append(record.getCreateTime()).append("</div>\n");
        html.append("<div>最后编辑: ").append(record.getLastEditUserName()).append(" at ").append(record.getLastEditTime()).append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private String escapeHtml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
