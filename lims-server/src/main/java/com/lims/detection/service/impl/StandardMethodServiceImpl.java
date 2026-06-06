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
import com.lims.detection.dto.StandardMethodQuery;
import com.lims.detection.dto.StandardMethodSaveDTO;
import com.lims.detection.entity.DetStandardMethod;
import com.lims.detection.entity.DetStandardMethodVersion;
import com.lims.detection.mapper.DetStandardMethodMapper;
import com.lims.detection.mapper.DetStandardMethodVersionMapper;
import com.lims.detection.service.StandardMethodService;
import com.lims.detection.vo.StandardMethodDetailVO;
import com.lims.detection.vo.StandardMethodVO;
import com.lims.detection.vo.StandardMethodVersionVO;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StandardMethodServiceImpl extends ServiceImpl<DetStandardMethodMapper, DetStandardMethod> implements StandardMethodService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private DetStandardMethodVersionMapper versionMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String getStandardTypeName(String type) {
        if (type == null) return "";
        switch (type) {
            case "1": return "国家标准";
            case "2": return "行业标准";
            case "3": return "企业标准";
            default: return "";
        }
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return status == 1 ? "启用" : "停用";
    }

    @Override
    public PageResult<StandardMethodVO> selectPage(StandardMethodQuery query) {
        LambdaQueryWrapper<DetStandardMethod> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getMethodCode())) {
            wrapper.like(DetStandardMethod::getMethodCode, query.getMethodCode());
        }
        if (StrUtil.isNotBlank(query.getMethodName())) {
            wrapper.like(DetStandardMethod::getMethodName, query.getMethodName());
        }
        if (StrUtil.isNotBlank(query.getStandardType())) {
            wrapper.eq(DetStandardMethod::getStandardType, query.getStandardType());
        }
        if (StrUtil.isNotBlank(query.getStandardNo())) {
            wrapper.like(DetStandardMethod::getStandardNo, query.getStandardNo());
        }
        if (StrUtil.isNotBlank(query.getVersion())) {
            wrapper.eq(DetStandardMethod::getVersion, query.getVersion());
        }
        if (query.getIsCurrent() != null) {
            wrapper.eq(DetStandardMethod::getIsCurrent, query.getIsCurrent());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DetStandardMethod::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(DetStandardMethod::getCreateTime);

        Page<DetStandardMethod> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<DetStandardMethod> pageResult = this.page(page, wrapper);

        IPage<StandardMethodVO> voPage = pageResult.convert(method -> {
            StandardMethodVO vo = BeanUtil.copyProperties(method, StandardMethodVO.class);
            vo.setStandardTypeName(getStandardTypeName(method.getStandardType()));
            vo.setStatusName(getStatusName(method.getStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    public StandardMethodDetailVO getDetail(Long id) {
        DetStandardMethod method = this.getById(id);
        if (method == null) {
            throw new BizException("标准方法不存在");
        }

        StandardMethodDetailVO vo = BeanUtil.copyProperties(method, StandardMethodDetailVO.class);
        vo.setStandardTypeName(getStandardTypeName(method.getStandardType()));
        vo.setStatusName(getStatusName(method.getStatus()));

        try {
            if (StrUtil.isNotBlank(method.getTestItemIds())) {
                vo.setTestItemIdList(objectMapper.readValue(method.getTestItemIds(), List.class));
            }
            if (StrUtil.isNotBlank(method.getTestItemNames())) {
                vo.setTestItemNameList(objectMapper.readValue(method.getTestItemNames(), List.class));
            }
            if (StrUtil.isNotBlank(method.getEquipmentIds())) {
                vo.setEquipmentIdList(objectMapper.readValue(method.getEquipmentIds(), List.class));
            }
            if (StrUtil.isNotBlank(method.getEquipmentNames())) {
                vo.setEquipmentNameList(objectMapper.readValue(method.getEquipmentNames(), List.class));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("解析JSON数据失败");
        }

        List<DetStandardMethodVersion> versionList = versionMapper.selectList(
                new LambdaQueryWrapper<DetStandardMethodVersion>()
                        .eq(DetStandardMethodVersion::getMethodCode, method.getMethodCode())
                        .orderByDesc(DetStandardMethodVersion::getCreateTime)
        );
        vo.setVersionHistory(versionList.stream().map(v -> {
            StandardMethodVersionVO vvo = BeanUtil.copyProperties(v, StandardMethodVersionVO.class);
            return vvo;
        }).collect(Collectors.toList()));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMethod(StandardMethodSaveDTO dto) {
        DetStandardMethod method = BeanUtil.copyProperties(dto, DetStandardMethod.class);
        method.setIsCurrent(1);
        if (method.getStatus() == null) {
            method.setStatus(1);
        }
        if (StrUtil.isBlank(method.getVersion())) {
            method.setVersion("1.0");
        }

        try {
            if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                method.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
            }
            if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                method.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
            }
            if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                method.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
            }
            if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                method.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
            }
        } catch (JsonProcessingException e) {
            throw new BizException("序列化JSON数据失败");
        }

        this.save(method);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMethod(StandardMethodSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("标准方法ID不能为空");
        }

        DetStandardMethod oldMethod = this.getById(dto.getId());
        if (oldMethod == null) {
            throw new BizException("标准方法不存在");
        }

        if (dto.getCreateNewVersion() != null && dto.getCreateNewVersion() == 1) {
            DetStandardMethodVersion version = new DetStandardMethodVersion();
            version.setMethodId(oldMethod.getId());
            version.setMethodCode(oldMethod.getMethodCode());
            version.setVersion(oldMethod.getVersion());
            version.setChangeType("版本更新");
            version.setChangeContent(StrUtil.isNotBlank(dto.getChangeReason()) ? dto.getChangeReason() : "更新标准方法");
            version.setChangeReason(dto.getChangeReason());
            try {
                version.setBeforeContent(objectMapper.writeValueAsString(oldMethod));
            } catch (JsonProcessingException e) {
                throw new BizException("序列化旧版本数据失败");
            }
            version.setOperatorId(SecurityUtils.getCurrentUserId());
            version.setOperatorName(SecurityUtils.getCurrentUsername());
            version.setCreateTime(LocalDateTime.now());
            versionMapper.insert(version);

            oldMethod.setIsCurrent(0);
            this.updateById(oldMethod);

            DetStandardMethod newMethod = BeanUtil.copyProperties(dto, DetStandardMethod.class);
            newMethod.setId(null);
            newMethod.setMethodCode(oldMethod.getMethodCode());
            String oldVersion = oldMethod.getVersion();
            String newVersion = StrUtil.isNotBlank(dto.getVersion()) ? dto.getVersion() : incrementVersion(oldVersion);
            newMethod.setVersion(newVersion);
            newMethod.setIsCurrent(1);
            if (newMethod.getStatus() == null) {
                newMethod.setStatus(1);
            }

            try {
                if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                    newMethod.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
                }
                if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                    newMethod.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
                }
                if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                    newMethod.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
                }
                if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                    newMethod.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
                }
            } catch (JsonProcessingException e) {
                throw new BizException("序列化JSON数据失败");
            }

            this.save(newMethod);
        } else {
            DetStandardMethod method = BeanUtil.copyProperties(dto, DetStandardMethod.class);
            method.setMethodCode(oldMethod.getMethodCode());
            method.setVersion(oldMethod.getVersion());
            method.setIsCurrent(oldMethod.getIsCurrent());

            try {
                if (dto.getTestItemIdList() != null && !dto.getTestItemIdList().isEmpty()) {
                    method.setTestItemIds(objectMapper.writeValueAsString(dto.getTestItemIdList()));
                }
                if (dto.getTestItemNameList() != null && !dto.getTestItemNameList().isEmpty()) {
                    method.setTestItemNames(objectMapper.writeValueAsString(dto.getTestItemNameList()));
                }
                if (dto.getEquipmentIdList() != null && !dto.getEquipmentIdList().isEmpty()) {
                    method.setEquipmentIds(objectMapper.writeValueAsString(dto.getEquipmentIdList()));
                }
                if (dto.getEquipmentNameList() != null && !dto.getEquipmentNameList().isEmpty()) {
                    method.setEquipmentNames(objectMapper.writeValueAsString(dto.getEquipmentNameList()));
                }
            } catch (JsonProcessingException e) {
                throw new BizException("序列化JSON数据失败");
            }

            this.updateById(method);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMethod(Long id) {
        DetStandardMethod method = this.getById(id);
        if (method == null) {
            throw new BizException("标准方法不存在");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableMethod(Long id) {
        DetStandardMethod method = this.getById(id);
        if (method == null) {
            throw new BizException("标准方法不存在");
        }
        method.setStatus(1);
        this.updateById(method);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableMethod(Long id) {
        DetStandardMethod method = this.getById(id);
        if (method == null) {
            throw new BizException("标准方法不存在");
        }
        method.setStatus(0);
        this.updateById(method);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setCurrentVersion(Long id) {
        DetStandardMethod method = this.getById(id);
        if (method == null) {
            throw new BizException("标准方法不存在");
        }

        this.update(
                new LambdaQueryWrapper<DetStandardMethod>()
                        .eq(DetStandardMethod::getMethodCode, method.getMethodCode())
                        .set(DetStandardMethod::getIsCurrent, 0)
        );

        method.setIsCurrent(1);
        this.updateById(method);
    }

    @Override
    public List<StandardMethodVO> getCurrentVersionList() {
        List<DetStandardMethod> list = this.list(
                new LambdaQueryWrapper<DetStandardMethod>()
                        .eq(DetStandardMethod::getIsCurrent, 1)
                        .eq(DetStandardMethod::getStatus, 1)
                        .orderByAsc(DetStandardMethod::getMethodCode)
        );
        return list.stream().map(method -> {
            StandardMethodVO vo = BeanUtil.copyProperties(method, StandardMethodVO.class);
            vo.setStandardTypeName(getStandardTypeName(method.getStandardType()));
            vo.setStatusName(getStatusName(method.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StandardMethodVO> getMethodVersions(String methodCode) {
        if (StrUtil.isBlank(methodCode)) {
            return Collections.emptyList();
        }
        List<DetStandardMethod> list = this.list(
                new LambdaQueryWrapper<DetStandardMethod>()
                        .eq(DetStandardMethod::getMethodCode, methodCode)
                        .orderByDesc(DetStandardMethod::getVersion)
        );
        return list.stream().map(method -> {
            StandardMethodVO vo = BeanUtil.copyProperties(method, StandardMethodVO.class);
            vo.setStandardTypeName(getStandardTypeName(method.getStandardType()));
            vo.setStatusName(getStatusName(method.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    private String incrementVersion(String version) {
        if (StrUtil.isBlank(version)) {
            return "1.0";
        }
        try {
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                return major + "." + (minor + 1);
            }
        } catch (Exception e) {
            return version;
        }
        return version;
    }
}
