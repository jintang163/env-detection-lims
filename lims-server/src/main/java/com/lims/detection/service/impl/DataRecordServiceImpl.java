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
import com.lims.detection.dto.DataRecordItemSaveDTO;
import com.lims.detection.dto.DataRecordQuery;
import com.lims.detection.dto.DataRecordSaveDTO;
import com.lims.detection.entity.*;
import com.lims.detection.mapper.*;
import com.lims.detection.service.DataRecordService;
import com.lims.detection.service.DataTraceService;
import com.lims.detection.service.FormFieldService;
import com.lims.detection.service.OosRecordService;
import com.lims.detection.vo.*;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataRecordServiceImpl extends ServiceImpl<DetDataRecordMapper, DetDataRecord> implements DataRecordService {

    @Autowired
    private DetDataRecordItemMapper dataRecordItemMapper;

    @Autowired
    private DetFormFieldMapper formFieldMapper;

    @Autowired
    private DetDataAuditLogMapper dataAuditLogMapper;

    @Autowired
    private DetDetectionTaskMapper detectionTaskMapper;

    @Autowired
    private DetDataReviewMapper dataReviewMapper;

    @Autowired
    private DetOosRecordMapper oosRecordMapper;

    @Autowired
    private OosRecordService oosRecordService;

    @Autowired
    private DataTraceService dataTraceService;

    @Autowired
    private FormFieldService formFieldService;

    @Autowired
    private CodeGenerator codeGenerator;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_PENDING_FIRST_REVIEW = 1;
    private static final int STATUS_FIRST_REVIEW_PASSED = 2;
    private static final int STATUS_PENDING_SECOND_REVIEW = 3;
    private static final int STATUS_SECOND_REVIEW_PASSED = 4;
    private static final int STATUS_REJECTED = 5;
    private static final int STATUS_COMPLETED = 6;

    private static final int REVIEW_STATUS_PENDING = 0;
    private static final int REVIEW_STATUS_FIRST_REVIEWING = 1;
    private static final int REVIEW_STATUS_FIRST_PASSED = 2;
    private static final int REVIEW_STATUS_FIRST_REJECTED = 3;
    private static final int REVIEW_STATUS_SECOND_REVIEWING = 4;
    private static final int REVIEW_STATUS_SECOND_PASSED = 5;
    private static final int REVIEW_STATUS_SECOND_REJECTED = 6;

    private static final int DATA_SOURCE_MANUAL = 1;
    private static final int DATA_SOURCE_INSTRUMENT = 2;
    private static final int DATA_SOURCE_FILE = 3;

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

    private String getReviewStatusName(Integer reviewStatus) {
        if (reviewStatus == null) return "";
        switch (reviewStatus) {
            case 0: return "待审核";
            case 1: return "一级审核中";
            case 2: return "一级审核通过";
            case 3: return "一级审核驳回";
            case 4: return "二级审核中";
            case 5: return "二级审核通过";
            case 6: return "二级审核驳回";
            default: return "";
        }
    }

    private String getDataSourceName(Integer dataSource) {
        if (dataSource == null) return "";
        switch (dataSource) {
            case 1: return "手动录入";
            case 2: return "仪器导入";
            case 3: return "文件导入";
            default: return "";
        }
    }

    @Override
    public PageResult<DataRecordVO> selectPage(DataRecordQuery query) {
        LambdaQueryWrapper<DetDataRecord> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getRecordNo())) {
            wrapper.like(DetDataRecord::getRecordNo, query.getRecordNo());
        }
        if (StrUtil.isNotBlank(query.getTaskNo())) {
            wrapper.like(DetDataRecord::getTaskNo, query.getTaskNo());
        }
        if (StrUtil.isNotBlank(query.getSampleNo())) {
            wrapper.like(DetDataRecord::getSampleNo, query.getSampleNo());
        }
        if (StrUtil.isNotBlank(query.getSampleName())) {
            wrapper.like(DetDataRecord::getSampleName, query.getSampleName());
        }
        if (StrUtil.isNotBlank(query.getEntrustNo())) {
            wrapper.like(DetDataRecord::getEntrustNo, query.getEntrustNo());
        }
        if (query.getMethodId() != null) {
            wrapper.eq(DetDataRecord::getMethodId, query.getMethodId());
        }
        if (StrUtil.isNotBlank(query.getMethodName())) {
            wrapper.like(DetDataRecord::getMethodName, query.getMethodName());
        }
        if (query.getEntryUserId() != null) {
            wrapper.eq(DetDataRecord::getEntryUserId, query.getEntryUserId());
        }
        if (StrUtil.isNotBlank(query.getEntryUserName())) {
            wrapper.like(DetDataRecord::getEntryUserName, query.getEntryUserName());
        }
        if (query.getReviewStatus() != null) {
            wrapper.eq(DetDataRecord::getReviewStatus, query.getReviewStatus());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DetDataRecord::getStatus, query.getStatus());
        }
        if (query.getHasOos() != null) {
            wrapper.eq(DetDataRecord::getHasOos, query.getHasOos());
        }
        if (query.getDataSource() != null) {
            wrapper.eq(DetDataRecord::getDataSource, query.getDataSource());
        }
        if (query.getTestDateStart() != null) {
            wrapper.ge(DetDataRecord::getTestDate, query.getTestDateStart());
        }
        if (query.getTestDateEnd() != null) {
            wrapper.le(DetDataRecord::getTestDate, query.getTestDateEnd());
        }
        if (query.getEntryTimeStart() != null) {
            wrapper.ge(DetDataRecord::getEntryTime, query.getEntryTimeStart().atStartOfDay());
        }
        if (query.getEntryTimeEnd() != null) {
            wrapper.le(DetDataRecord::getEntryTime, query.getEntryTimeEnd().atTime(23, 59, 59));
        }
        wrapper.orderByDesc(DetDataRecord::getCreateTime);

        Page<DetDataRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<DetDataRecord> pageResult = this.page(page, wrapper);

        IPage<DataRecordVO> voPage = pageResult.convert(record -> convertToVO(record));

        return PageResult.of(voPage);
    }

    @Override
    public DataRecordDetailVO getDetail(Long id) {
        DetDataRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("数据记录不存在");
        }

        DataRecordDetailVO vo = BeanUtil.copyProperties(record, DataRecordDetailVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setReviewStatusName(getReviewStatusName(record.getReviewStatus()));
        vo.setDataSourceName(getDataSourceName(record.getDataSource()));

        try {
            if (StrUtil.isNotBlank(record.getEquipmentIds())) {
                vo.setEquipmentIdList(objectMapper.readValue(record.getEquipmentIds(), List.class));
            }
            if (StrUtil.isNotBlank(record.getEquipmentNames())) {
                vo.setEquipmentNameList(objectMapper.readValue(record.getEquipmentNames(), List.class));
            }
            if (StrUtil.isNotBlank(record.getStandardSubstanceIds())) {
                vo.setStandardSubstanceIdList(objectMapper.readValue(record.getStandardSubstanceIds(), List.class));
            }
            if (StrUtil.isNotBlank(record.getStandardSubstanceNames())) {
                vo.setStandardSubstanceNameList(objectMapper.readValue(record.getStandardSubstanceNames(), List.class));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetDataRecordItem> itemList = dataRecordItemMapper.selectList(
                new LambdaQueryWrapper<DetDataRecordItem>()
                        .eq(DetDataRecordItem::getRecordId, id)
                        .orderByAsc(DetDataRecordItem::getSortOrder)
        );
        vo.setItemList(itemList.stream().map(item -> {
            DataRecordItemVO itemVO = BeanUtil.copyProperties(item, DataRecordItemVO.class);
            return itemVO;
        }).collect(Collectors.toList()));

        List<DetDataReview> reviewList = dataReviewMapper.selectList(
                new LambdaQueryWrapper<DetDataReview>()
                        .eq(DetDataReview::getDataRecordId, id)
                        .orderByDesc(DetDataReview::getReviewTime)
        );
        vo.setReviewList(reviewList.stream().map(review -> {
            DataReviewVO reviewVO = BeanUtil.copyProperties(review, DataReviewVO.class);
            reviewVO.setReviewLevelName(review.getReviewLevel() == 1 ? "一级审核" : "二级审核");
            reviewVO.setReviewResultName(review.getReviewResult() == 1 ? "通过" : "驳回");
            return reviewVO;
        }).collect(Collectors.toList()));

        List<DetOosRecord> oosList = oosRecordMapper.selectList(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getDataRecordId, id)
                        .orderByDesc(DetOosRecord::getCreateTime)
        );
        vo.setOosList(oosList.stream().map(oos -> {
            OosRecordVO oosVO = BeanUtil.copyProperties(oos, OosRecordVO.class);
            oosVO.setOosTypeName("OOS".equals(oos.getOosType()) ? "超标" : "趋势异常");
            oosVO.setOosLevelName(oos.getOosLevel() == 1 ? "一般" : oos.getOosLevel() == 2 ? "严重" : "重大");
            oosVO.setStatusName(getOosStatusName(oos.getStatus()));
            oosVO.setIsClosedName(oos.getIsClosed() == 1 ? "是" : "否");
            return oosVO;
        }).collect(Collectors.toList()));

        return vo;
    }

    private String getOosStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待调查";
            case 1: return "调查中";
            case 2: return "待审核";
            case 3: return "已完成";
            case 4: return "已关闭";
            default: return "";
        }
    }

    @Override
    public List<FormFieldVO> generateDynamicForm(Long methodId) {
        return formFieldService.getFormFieldsByMethodId(methodId);
    }

    @Override
    public DataValidationResultVO validateData(DataRecordSaveDTO dto) {
        DataValidationResultVO result = new DataValidationResultVO();

        if (dto.getItemList() == null || dto.getItemList().isEmpty()) {
            result.addError("检测数据明细不能为空");
            return result;
        }

        Map<Long, DetFormField> formFieldMap = new HashMap<>();
        if (dto.getMethodId() != null) {
            List<DetFormField> formFields = formFieldMapper.selectList(
                    new LambdaQueryWrapper<DetFormField>()
                            .eq(DetFormField::getMethodId, dto.getMethodId())
                            .eq(DetFormField::getStatus, 1)
            );
            for (DetFormField field : formFields) {
                formFieldMap.put(field.getId(), field);
            }
        }

        for (DataRecordItemSaveDTO item : dto.getItemList()) {
            String fieldName = item.getFieldName() != null ? item.getFieldName() : "字段";

            DetFormField field = formFieldMap.get(item.getFieldId());
            if (field == null) {
                field = new DetFormField();
                field.setRequired(0);
                field.setMinValue(item.getMinValue());
                field.setMaxValue(item.getMaxValue());
                field.setPrecisionScale(null);
                field.setDetectionLimit(item.getDetectionLimit());
                field.setQuantitationLimit(item.getQuantitationLimit());
            }

            if (field.getRequired() != null && field.getRequired() == 1) {
                if (StrUtil.isBlank(item.getResultValue()) && (item.getIsNd() == null || item.getIsNd() != 1)) {
                    result.addError(fieldName + "为必填项");
                }
            }

            if (item.getResultNumeric() != null) {
                if (field.getMinValue() != null && item.getResultNumeric().compareTo(field.getMinValue()) < 0) {
                    result.addError(fieldName + "数值不能小于最小值 " + field.getMinValue());
                }
                if (field.getMaxValue() != null && item.getResultNumeric().compareTo(field.getMaxValue()) > 0) {
                    result.addError(fieldName + "数值不能大于最大值 " + field.getMaxValue());
                }

                if (field.getPrecisionScale() != null && field.getPrecisionScale() >= 0) {
                    String[] parts = item.getResultNumeric().toPlainString().split("\\.");
                    if (parts.length > 1 && parts[1].length() > field.getPrecisionScale()) {
                        result.addError(fieldName + "小数位数不能超过" + field.getPrecisionScale() + "位");
                    }
                }

                if (field.getDetectionLimit() != null && item.getResultNumeric().compareTo(field.getDetectionLimit()) < 0) {
                    result.addWarning(fieldName + "检测结果低于检出限 " + field.getDetectionLimit());
                }
                if (field.getQuantitationLimit() != null && item.getResultNumeric().compareTo(field.getQuantitationLimit()) < 0) {
                    result.addWarning(fieldName + "检测结果低于定量限 " + field.getQuantitationLimit());
                }

                if (StrUtil.isNotBlank(item.getLimitValue())) {
                    boolean isOos = checkOos(item.getResultNumeric(), item.getLimitValue());
                    if (isOos) {
                        result.addOosInfo(fieldName + "检测结果 " + item.getResultNumeric() + " 超出标准限值 " + item.getLimitValue());
                    }
                }
            }
        }

        return result;
    }

    private boolean checkOos(BigDecimal result, String limitValue) {
        if (result == null || StrUtil.isBlank(limitValue)) {
            return false;
        }

        try {
            limitValue = limitValue.trim();

            if (limitValue.startsWith("≤")) {
                BigDecimal limit = new BigDecimal(limitValue.substring(1).trim());
                return result.compareTo(limit) > 0;
            } else if (limitValue.startsWith("<")) {
                BigDecimal limit = new BigDecimal(limitValue.substring(1).trim());
                return result.compareTo(limit) >= 0;
            } else if (limitValue.startsWith("≥")) {
                BigDecimal limit = new BigDecimal(limitValue.substring(1).trim());
                return result.compareTo(limit) < 0;
            } else if (limitValue.startsWith(">")) {
                BigDecimal limit = new BigDecimal(limitValue.substring(1).trim());
                return result.compareTo(limit) <= 0;
            } else if (limitValue.contains("~") || limitValue.contains("-")) {
                String separator = limitValue.contains("~") ? "~" : "-";
                String[] parts = limitValue.split(separator);
                if (parts.length == 2) {
                    BigDecimal min = new BigDecimal(parts[0].trim());
                    BigDecimal max = new BigDecimal(parts[1].trim());
                    return result.compareTo(min) < 0 || result.compareTo(max) > 0;
                }
            } else {
                BigDecimal limit = new BigDecimal(limitValue);
                return result.compareTo(limit) > 0;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDataRecord(DataRecordSaveDTO dto) {
        DataValidationResultVO validationResult = validateData(dto);
        if (!validationResult.getValid()) {
            throw new BizException("数据校验失败: " + String.join("; ", validationResult.getErrors()));
        }

        DetDetectionTask task = detectionTaskMapper.selectById(dto.getTaskId());
        if (task == null) {
            throw new BizException("检测任务不存在");
        }

        DetDataRecord record = BeanUtil.copyProperties(dto, DetDataRecord.class);
        record.setRecordNo(codeGenerator.generateDataRecordNo());
        record.setTaskNo(task.getTaskNo());
        record.setStatus(STATUS_DRAFT);
        record.setReviewStatus(REVIEW_STATUS_PENDING);
        record.setEntryUserId(SecurityUtils.getCurrentUserId());
        record.setEntryUserName(SecurityUtils.getCurrentUsername());
        record.setEntryTime(LocalDateTime.now());
        record.setVersion(1);
        record.setHasOos(0);
        record.setOosCount(0);

        if (record.getDataSource() == null) {
            record.setDataSource(DATA_SOURCE_MANUAL);
        }

        try {
            if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                record.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
            }
            if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                record.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
            }
            if (dto.getStandardSubstanceIdList() != null && !dto.getStandardSubstanceIdList().isEmpty()) {
                record.setStandardSubstanceIds(objectMapper.writeValueAsString(dto.getStandardSubstanceIdList()));
            }
            if (dto.getStandardSubstanceNameList() != null && !dto.getStandardSubstanceNameList().isEmpty()) {
                record.setStandardSubstanceNames(objectMapper.writeValueAsString(dto.getStandardSubstanceNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        String dataHash = calculateDataHash(dto);
        record.setDataHash(dataHash);

        this.save(record);

        int oosCount = 0;
        if (dto.getItemList() != null && !dto.getItemList().isEmpty()) {
            for (DataRecordItemSaveDTO itemDTO : dto.getItemList()) {
                DetDataRecordItem item = BeanUtil.copyProperties(itemDTO, DetDataRecordItem.class);
                item.setRecordId(record.getId());
                item.setRecordNo(record.getRecordNo());

                if (itemDTO.getResultNumeric() != null && StrUtil.isNotBlank(itemDTO.getLimitValue())) {
                    boolean isOos = checkOos(itemDTO.getResultNumeric(), itemDTO.getLimitValue());
                    item.setIsOos(isOos ? 1 : 0);
                    if (isOos) {
                        item.setOosType("OOS");
                        oosCount++;
                    }
                } else {
                    item.setIsOos(0);
                }

                dataRecordItemMapper.insert(item);

                if (item.getIsOos() != null && item.getIsOos() == 1) {
                    oosRecordService.createOosRecord(record.getId(), item.getId());
                }
            }
        }

        if (oosCount > 0) {
            record.setHasOos(1);
            record.setOosCount(oosCount);
            this.updateById(record);
        }

        dataTraceService.buildTraceForDataRecord(record.getId());

        saveAuditLog("data_record", record.getId(), record.getRecordNo(),
                "create", null, null, null, false, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDataRecord(DataRecordSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("记录ID不能为空");
        }

        DetDataRecord oldRecord = this.getById(dto.getId());
        if (oldRecord == null) {
            throw new BizException("数据记录不存在");
        }

        if (oldRecord.getStatus() != STATUS_DRAFT && oldRecord.getStatus() != STATUS_REJECTED) {
            throw new BizException("只能编辑草稿或已驳回状态的记录");
        }

        if (dto.getVersion() == null || !dto.getVersion().equals(oldRecord.getVersion())) {
            saveAuditLog("data_record", oldRecord.getId(), oldRecord.getRecordNo(),
                    "update", null, null, null, false, "版本号不匹配，操作被拦截");
            throw new BizException("数据已被修改，请刷新后重试");
        }

        String currentHash = calculateDataHashFromRecord(oldRecord.getId());
        if (StrUtil.isNotBlank(oldRecord.getDataHash()) && !oldRecord.getDataHash().equals(currentHash)) {
            saveAuditLog("data_record", oldRecord.getId(), oldRecord.getRecordNo(),
                    "update", null, null, null, true, "数据哈希校验失败，疑似篡改");
            throw new BizException("数据完整性校验失败，数据可能已被篡改");
        }

        DataValidationResultVO validationResult = validateData(dto);
        if (!validationResult.getValid()) {
            throw new BizException("数据校验失败: " + String.join("; ", validationResult.getErrors()));
        }

        DetDataRecord record = BeanUtil.copyProperties(dto, DetDataRecord.class);
        record.setRecordNo(oldRecord.getRecordNo());
        record.setTaskNo(oldRecord.getTaskNo());
        record.setStatus(oldRecord.getStatus());
        record.setReviewStatus(oldRecord.getReviewStatus());
        record.setEntryUserId(oldRecord.getEntryUserId());
        record.setEntryUserName(oldRecord.getEntryUserName());
        record.setEntryTime(oldRecord.getEntryTime());
        record.setVersion(oldRecord.getVersion() + 1);

        try {
            if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                record.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
            }
            if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                record.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
            }
            if (dto.getStandardSubstanceIdList() != null && !dto.getStandardSubstanceIdList().isEmpty()) {
                record.setStandardSubstanceIds(objectMapper.writeValueAsString(dto.getStandardSubstanceIdList()));
            }
            if (dto.getStandardSubstanceNameList() != null && !dto.getStandardSubstanceNameList().isEmpty()) {
                record.setStandardSubstanceNames(objectMapper.writeValueAsString(dto.getStandardSubstanceNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        String newDataHash = calculateDataHash(dto);
        record.setDataHash(newDataHash);

        dataRecordItemMapper.delete(
                new LambdaQueryWrapper<DetDataRecordItem>()
                        .eq(DetDataRecordItem::getRecordId, dto.getId())
        );

        oosRecordMapper.delete(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getDataRecordId, dto.getId())
        );

        int oosCount = 0;
        if (dto.getItemList() != null && !dto.getItemList().isEmpty()) {
            for (DataRecordItemSaveDTO itemDTO : dto.getItemList()) {
                DetDataRecordItem item = BeanUtil.copyProperties(itemDTO, DetDataRecordItem.class);
                item.setRecordId(record.getId());
                item.setRecordNo(record.getRecordNo());
                item.setId(null);

                if (itemDTO.getResultNumeric() != null && StrUtil.isNotBlank(itemDTO.getLimitValue())) {
                    boolean isOos = checkOos(itemDTO.getResultNumeric(), itemDTO.getLimitValue());
                    item.setIsOos(isOos ? 1 : 0);
                    if (isOos) {
                        item.setOosType("OOS");
                        oosCount++;
                    }
                } else {
                    item.setIsOos(0);
                }

                dataRecordItemMapper.insert(item);

                if (item.getIsOos() != null && item.getIsOos() == 1) {
                    oosRecordService.createOosRecord(record.getId(), item.getId());
                }
            }
        }

        record.setHasOos(oosCount > 0 ? 1 : 0);
        record.setOosCount(oosCount);

        this.updateById(record);

        saveAuditLog("data_record", record.getId(), record.getRecordNo(),
                "update", null, null, null, false, null);
    }

    private String calculateDataHashFromRecord(Long recordId) {
        List<DetDataRecordItem> itemList = dataRecordItemMapper.selectList(
                new LambdaQueryWrapper<DetDataRecordItem>()
                        .eq(DetDataRecordItem::getRecordId, recordId)
                        .orderByAsc(DetDataRecordItem::getSortOrder, DetDataRecordItem::getFieldId)
        );

        StringBuilder sb = new StringBuilder();
        for (DetDataRecordItem item : itemList) {
            sb.append(item.getFieldId()).append("|")
                    .append(item.getFieldCode()).append("|")
                    .append(item.getResultValue()).append("|")
                    .append(item.getResultNumeric() != null ? item.getResultNumeric().toPlainString() : "").append("|")
                    .append(item.getIsNd() != null ? item.getIsNd() : "").append(";");
        }

        return sha256(sb.toString());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitDataRecord(Long id) {
        DetDataRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("数据记录不存在");
        }

        if (record.getStatus() != STATUS_DRAFT && record.getStatus() != STATUS_REJECTED) {
            throw new BizException("只能提交草稿或已驳回状态的记录");
        }

        boolean integrityValid = verifyDataIntegrity(id);
        if (!integrityValid) {
            saveAuditLog("data_record", record.getId(), record.getRecordNo(),
                    "submit", null, null, null, true, "数据完整性校验失败，提交被拦截");
            throw new BizException("数据完整性校验失败，数据可能已被篡改");
        }

        Integer oldStatus = record.getStatus();
        record.setStatus(STATUS_PENDING_FIRST_REVIEW);
        record.setReviewStatus(REVIEW_STATUS_FIRST_REVIEWING);
        this.updateById(record);

        saveAuditLog("data_record", record.getId(), record.getRecordNo(),
                "submit", "status", String.valueOf(oldStatus), String.valueOf(STATUS_PENDING_FIRST_REVIEW),
                false, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataRecord(Long id) {
        DetDataRecord record = this.getById(id);
        if (record == null) {
            throw new BizException("数据记录不存在");
        }

        if (record.getStatus() != STATUS_DRAFT) {
            throw new BizException("只能删除草稿状态的记录");
        }

        dataRecordItemMapper.delete(
                new LambdaQueryWrapper<DetDataRecordItem>()
                        .eq(DetDataRecordItem::getRecordId, id)
        );

        dataAuditLogMapper.delete(
                new LambdaQueryWrapper<DetDataAuditLog>()
                        .eq(DetDataAuditLog::getDataType, "data_record")
                        .eq(DetDataAuditLog::getDataId, id)
        );

        oosRecordMapper.delete(
                new LambdaQueryWrapper<DetOosRecord>()
                        .eq(DetOosRecord::getDataRecordId, id)
        );

        this.removeById(id);

        saveAuditLog("data_record", id, record.getRecordNo(),
                "delete", null, null, null, false, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importInstrumentData(Long taskId, String fileUrl, String fileName) {
        DetDetectionTask task = detectionTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }

        DetDataRecord record = new DetDataRecord();
        record.setRecordNo(codeGenerator.generateDataRecordNo());
        record.setTaskId(taskId);
        record.setTaskNo(task.getTaskNo());
        record.setSampleId(task.getSampleId());
        record.setSampleNo(task.getSampleNo());
        record.setSampleName(task.getSampleName());
        record.setEntrustId(task.getEntrustId());
        record.setEntrustNo(task.getEntrustNo());
        record.setMethodId(task.getMethodId());
        record.setMethodCode(task.getMethodCode());
        record.setMethodName(task.getMethodName());
        record.setStatus(STATUS_DRAFT);
        record.setReviewStatus(REVIEW_STATUS_PENDING);
        record.setDataSource(DATA_SOURCE_INSTRUMENT);
        record.setImportFileUrl(fileUrl);
        record.setImportFileName(fileName);
        record.setEntryUserId(SecurityUtils.getCurrentUserId());
        record.setEntryUserName(SecurityUtils.getCurrentUsername());
        record.setEntryTime(LocalDateTime.now());
        record.setVersion(1);
        record.setHasOos(0);
        record.setOosCount(0);
        record.setTestDate(LocalDateTime.now().toLocalDate());
        record.setTestTime(LocalDateTime.now().toLocalTime());

        this.save(record);

        dataTraceService.buildTraceForDataRecord(record.getId());

        saveAuditLog("data_record", record.getId(), record.getRecordNo(),
                "import", null, null, null, false, "仪器文件导入: " + fileName);
    }

    @Override
    public void saveAuditLog(String dataType, Long dataId, String dataNo, String operateType,
                             String fieldName, String oldValue, String newValue, boolean tamperAttempt, String blockReason) {
        DetDataAuditLog log = new DetDataAuditLog();
        log.setDataType(dataType);
        log.setDataId(dataId);
        log.setDataNo(dataNo);
        log.setOperateType(operateType);
        log.setFieldName(fieldName);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setOperateTime(LocalDateTime.now());
        log.setTamperAttempt(tamperAttempt ? 1 : 0);
        log.setBlockReason(blockReason);
        dataAuditLogMapper.insert(log);
    }

    @Override
    public boolean verifyDataIntegrity(Long id) {
        DetDataRecord record = this.getById(id);
        if (record == null) {
            return false;
        }

        if (StrUtil.isBlank(record.getDataHash())) {
            return true;
        }

        String currentHash = calculateDataHashFromRecord(id);
        return record.getDataHash().equals(currentHash);
    }

    @Override
    public String calculateDataHash(DataRecordSaveDTO dto) {
        if (dto.getItemList() == null || dto.getItemList().isEmpty()) {
            return sha256("");
        }

        List<DataRecordItemSaveDTO> sortedItems = dto.getItemList().stream()
                .sorted(Comparator.comparing(DataRecordItemSaveDTO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(DataRecordItemSaveDTO::getFieldId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (DataRecordItemSaveDTO item : sortedItems) {
            sb.append(item.getFieldId() != null ? item.getFieldId() : "").append("|")
                    .append(item.getFieldCode() != null ? item.getFieldCode() : "").append("|")
                    .append(item.getResultValue() != null ? item.getResultValue() : "").append("|")
                    .append(item.getResultNumeric() != null ? item.getResultNumeric().toPlainString() : "").append("|")
                    .append(item.getIsNd() != null ? item.getIsNd() : "").append(";");
        }

        return sha256(sb.toString());
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException("哈希算法不支持");
        }
    }

    private DataRecordVO convertToVO(DetDataRecord record) {
        DataRecordVO vo = BeanUtil.copyProperties(record, DataRecordVO.class);
        vo.setStatusName(getStatusName(record.getStatus()));
        vo.setReviewStatusName(getReviewStatusName(record.getReviewStatus()));
        vo.setDataSourceName(getDataSourceName(record.getDataSource()));
        return vo;
    }
}
