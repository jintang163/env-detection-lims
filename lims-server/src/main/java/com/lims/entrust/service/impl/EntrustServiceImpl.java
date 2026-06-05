package com.lims.entrust.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.dict.entity.DictItemStandard;
import com.lims.dict.entity.DictTestItem;
import com.lims.dict.entity.DictTestStandard;
import com.lims.dict.mapper.DictItemStandardMapper;
import com.lims.dict.mapper.DictTestItemMapper;
import com.lims.dict.mapper.DictTestStandardMapper;
import com.lims.entrust.dto.*;
import com.lims.entrust.entity.*;
import com.lims.entrust.mapper.*;
import com.lims.entrust.service.EntrustService;
import com.lims.entrust.vo.EntrustDetailVO;
import com.lims.entrust.vo.EntrustItemVO;
import com.lims.entrust.vo.EntrustVO;
import com.lims.entrust.vo.SubcontractVO;
import com.lims.security.entity.LoginUser;
import com.lims.security.utils.SecurityUtils;
import com.lims.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EntrustServiceImpl implements EntrustService {

    public static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    public static final Map<Integer, String> ENTRUST_TYPE_MAP = new HashMap<>();
    public static final Map<Integer, String> APPROVAL_STATUS_MAP = new HashMap<>();
    public static final Map<Integer, String> REVIEW_RESULT_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(0, "草稿");
        STATUS_MAP.put(1, "待受理");
        STATUS_MAP.put(2, "已受理");
        STATUS_MAP.put(3, "采样中");
        STATUS_MAP.put(4, "检测中");
        STATUS_MAP.put(5, "报告编制中");
        STATUS_MAP.put(6, "报告审核中");
        STATUS_MAP.put(7, "已完成");
        STATUS_MAP.put(8, "已取消");

        ENTRUST_TYPE_MAP.put(1, "常规委托");
        ENTRUST_TYPE_MAP.put(2, "委托采样");
        ENTRUST_TYPE_MAP.put(3, "送样检测");

        APPROVAL_STATUS_MAP.put(0, "待评审");
        APPROVAL_STATUS_MAP.put(1, "评审中");
        APPROVAL_STATUS_MAP.put(2, "评审通过");
        APPROVAL_STATUS_MAP.put(3, "评审驳回");

        REVIEW_RESULT_MAP.put(1, "通过");
        REVIEW_RESULT_MAP.put(2, "驳回");
    }

    @Autowired
    private EntEntrustMapper entrustMapper;

    @Autowired
    private EntEntrustItemMapper entrustItemMapper;

    @Autowired
    private EntEntrustStatusLogMapper statusLogMapper;

    @Autowired
    private EntEntrustReviewMapper reviewMapper;

    @Autowired
    private DictTestItemMapper testItemMapper;

    @Autowired
    private DictTestStandardMapper testStandardMapper;

    @Autowired
    private DictItemStandardMapper itemStandardMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private EntSubcontractMapper subcontractMapper;

    @Override
    public PageResult<EntrustVO> page(EntrustQuery query) {
        LambdaQueryWrapper<EntEntrust> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getEntrustNo())) {
            wrapper.like(EntEntrust::getEntrustNo, query.getEntrustNo());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(EntEntrust::getCustomerId, query.getCustomerId());
        }
        if (StrUtil.isNotBlank(query.getCustomerName())) {
            wrapper.like(EntEntrust::getCustomerName, query.getCustomerName());
        }
        if (query.getEntrustType() != null) {
            wrapper.eq(EntEntrust::getEntrustType, query.getEntrustType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(EntEntrust::getStatus, query.getStatus());
        }
        if (query.getIsUrgent() != null) {
            wrapper.eq(EntEntrust::getIsUrgent, query.getIsUrgent());
        }
        if (query.getIsSubcontract() != null) {
            wrapper.eq(EntEntrust::getIsSubcontract, query.getIsSubcontract());
        }
        if (query.getApprovalStatus() != null) {
            wrapper.eq(EntEntrust::getApprovalStatus, query.getApprovalStatus());
        }
        if (query.getContractId() != null) {
            wrapper.eq(EntEntrust::getContractId, query.getContractId());
        }
        if (query.getCreateTimeStart() != null) {
            wrapper.ge(EntEntrust::getCreateTime, query.getCreateTimeStart());
        }
        if (query.getCreateTimeEnd() != null) {
            wrapper.le(EntEntrust::getCreateTime, query.getCreateTimeEnd());
        }
        if (query.getExpectedReportStart() != null) {
            wrapper.ge(EntEntrust::getExpectedReportTime, query.getExpectedReportStart());
        }
        if (query.getExpectedReportEnd() != null) {
            wrapper.le(EntEntrust::getExpectedReportTime, query.getExpectedReportEnd());
        }
        wrapper.orderByDesc(EntEntrust::getCreateTime);

        Page<EntEntrust> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<EntEntrust> result = entrustMapper.selectPage(page, wrapper);

        List<EntrustVO> voList = result.getRecords().stream().map(e -> {
            EntrustVO vo = BeanUtil.copyProperties(e, EntrustVO.class);
            vo.setStatusName(STATUS_MAP.get(e.getStatus()));
            vo.setEntrustTypeName(ENTRUST_TYPE_MAP.get(e.getEntrustType()));
            vo.setApprovalStatusName(APPROVAL_STATUS_MAP.get(e.getApprovalStatus()));
            LambdaQueryWrapper<EntEntrustItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(EntEntrustItem::getEntrustId, e.getId());
            vo.setItemCount(entrustItemMapper.selectCount(itemWrapper).intValue());
            return vo;
        }).collect(Collectors.toList());

        PageResult<EntrustVO> pageResult = PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
        pageResult.setList(voList);
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(EntrustSaveDTO dto) {
        SysUser user = SecurityUtils.getCurrentUser().getUser();

        EntEntrust entrust = BeanUtil.copyProperties(dto, EntEntrust.class);
        entrust.setEntrustNo(codeGenerator.generateEntrustNo());
        entrust.setStatus(0);
        entrust.setApprovalStatus(0);

        calculateAmount(entrust, dto.getItems());

        entrustMapper.insert(entrust);

        saveItems(entrust.getId(), dto.getItems());

        recordStatusLog(entrust.getId(), null, 0, "创建委托单", user);

        return entrust.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EntrustSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException("委托单ID不能为空");
        }

        EntEntrust entrust = entrustMapper.selectById(dto.getId());
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getStatus() > 0) {
            throw new BizException("只能修改草稿状态的委托单");
        }

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        BeanUtil.copyProperties(dto, entrust);
        calculateAmount(entrust, dto.getItems());
        entrustMapper.updateById(entrust);

        LambdaQueryWrapper<EntEntrustItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EntEntrustItem::getEntrustId, dto.getId());
        entrustItemMapper.delete(wrapper);

        saveItems(entrust.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        EntEntrust entrust = entrustMapper.selectById(id);
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getStatus() > 0) {
            throw new BizException("只能删除草稿状态的委托单");
        }

        entrustMapper.deleteById(id);

        LambdaQueryWrapper<EntEntrustItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(EntEntrustItem::getEntrustId, id);
        entrustItemMapper.delete(itemWrapper);

        LambdaQueryWrapper<EntEntrustStatusLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(EntEntrustStatusLog::getEntrustId, id);
        statusLogMapper.delete(logWrapper);
    }

    @Override
    public EntrustDetailVO getDetail(Long id) {
        EntEntrust entrust = entrustMapper.selectById(id);
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        EntrustDetailVO vo = BeanUtil.copyProperties(entrust, EntrustDetailVO.class);
        vo.setStatusName(STATUS_MAP.get(entrust.getStatus()));
        vo.setEntrustTypeName(ENTRUST_TYPE_MAP.get(entrust.getEntrustType()));
        vo.setApprovalStatusName(APPROVAL_STATUS_MAP.get(entrust.getApprovalStatus()));

        LambdaQueryWrapper<EntEntrustItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(EntEntrustItem::getEntrustId, id);
        itemWrapper.orderByAsc(EntEntrustItem::getSortOrder);
        List<EntEntrustItem> items = entrustItemMapper.selectList(itemWrapper);
        vo.setItems(BeanUtil.copyToList(items, EntrustItemVO.class));

        LambdaQueryWrapper<EntEntrustStatusLog> logWrapper = new LambdaQueryWrapper<>();
        logWrapper.eq(EntEntrustStatusLog::getEntrustId, id);
        logWrapper.orderByAsc(EntEntrustStatusLog::getId);
        List<EntEntrustStatusLog> statusLogs = statusLogMapper.selectList(logWrapper);
        List<EntrustDetailVO.StatusLogVO> statusLogVOS = statusLogs.stream().map(sl -> {
            EntrustDetailVO.StatusLogVO logVO = new EntrustDetailVO.StatusLogVO();
            BeanUtil.copyProperties(sl, logVO);
            logVO.setBeforeStatusName(STATUS_MAP.get(sl.getBeforeStatus()));
            logVO.setAfterStatusName(STATUS_MAP.get(sl.getAfterStatus()));
            return logVO;
        }).collect(Collectors.toList());
        vo.setStatusLogs(statusLogVOS);

        LambdaQueryWrapper<EntEntrustReview> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.eq(EntEntrustReview::getEntrustId, id);
        reviewWrapper.orderByAsc(EntEntrustReview::getId);
        List<EntEntrustReview> reviews = reviewMapper.selectList(reviewWrapper);
        List<EntrustDetailVO.ReviewLogVO> reviewLogVOS = reviews.stream().map(r -> {
            EntrustDetailVO.ReviewLogVO reviewVO = new EntrustDetailVO.ReviewLogVO();
            BeanUtil.copyProperties(r, reviewVO);
            reviewVO.setReviewResultName(REVIEW_RESULT_MAP.get(r.getReviewResult()));
            return reviewVO;
        }).collect(Collectors.toList());
        vo.setReviewLogs(reviewLogVOS);

        LambdaQueryWrapper<EntSubcontract> subcontractWrapper = new LambdaQueryWrapper<>();
        subcontractWrapper.eq(EntSubcontract::getEntrustId, id);
        subcontractWrapper.orderByAsc(EntSubcontract::getId);
        List<EntSubcontract> subcontracts = subcontractMapper.selectList(subcontractWrapper);
        List<SubcontractVO> subcontractVOS = subcontracts.stream().map(s -> {
            SubcontractVO subcontractVO = BeanUtil.copyProperties(s, SubcontractVO.class);
            subcontractVO.setStatusName(SubcontractServiceImpl.STATUS_MAP.get(s.getStatus()));
            return subcontractVO;
        }).collect(Collectors.toList());
        vo.setSubcontracts(subcontractVOS);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        EntEntrust entrust = entrustMapper.selectById(id);
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getStatus() != 0) {
            throw new BizException("只能提交草稿状态的委托单");
        }

        LambdaQueryWrapper<EntEntrustItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(EntEntrustItem::getEntrustId, id);
        Integer count = entrustItemMapper.selectCount(itemWrapper).intValue();
        if (count == 0) {
            throw new BizException("委托单没有检测项目，无法提交");
        }

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        Integer oldStatus = entrust.getStatus();
        entrust.setStatus(1);
        entrust.setApprovalStatus(1);
        entrustMapper.updateById(entrust);

        recordStatusLog(id, oldStatus, 1, "提交受理，进入合同评审", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void review(EntrustReviewDTO dto) {
        EntEntrust entrust = entrustMapper.selectById(dto.getEntrustId());
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getStatus() != 1) {
            throw new BizException("只能评审待受理状态的委托单");
        }

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        EntEntrustReview review = new EntEntrustReview();
        review.setEntrustId(dto.getEntrustId());
        review.setReviewResult(dto.getReviewResult());
        review.setReviewOpinion(dto.getReviewOpinion());
        review.setReviewerId(user.getId());
        review.setReviewerName(user.getRealName());
        review.setReviewTime(LocalDateTime.now());
        review.setReviewNode(dto.getReviewNode() == null ? "合同评审" : dto.getReviewNode());
        reviewMapper.insert(review);

        Integer oldStatus = entrust.getStatus();

        if (dto.getReviewResult() == 1) {
            entrust.setStatus(2);
            entrust.setApprovalStatus(2);
            entrust.setReviewOpinion(dto.getReviewOpinion());
            entrustMapper.updateById(entrust);
            recordStatusLog(dto.getEntrustId(), oldStatus, 2, "合同评审通过，已受理", user);
        } else {
            entrust.setStatus(0);
            entrust.setApprovalStatus(3);
            entrust.setReviewOpinion(dto.getReviewOpinion());
            entrustMapper.updateById(entrust);
            recordStatusLog(dto.getEntrustId(), oldStatus, 0, "合同评审驳回，退回修改", user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(EntrustStatusChangeDTO dto) {
        EntEntrust entrust = entrustMapper.selectById(dto.getEntrustId());
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        Integer currentStatus = entrust.getStatus();
        Integer targetStatus = dto.getTargetStatus();

        validateStatusTransition(currentStatus, targetStatus);

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        entrust.setStatus(targetStatus);
        if (targetStatus == 7) {
            entrust.setActualReportTime(LocalDateTime.now());
        }
        entrustMapper.updateById(entrust);

        String content = StrUtil.isNotBlank(dto.getOperateContent())
                ? dto.getOperateContent()
                : String.format("状态变更：%s → %s", STATUS_MAP.get(currentStatus), STATUS_MAP.get(targetStatus));

        String operateType = dto.getOperateType() == null ? "状态变更" : dto.getOperateType();

        recordStatusLog(dto.getEntrustId(), currentStatus, targetStatus, content, operateType, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void urgent(Long id) {
        EntEntrust entrust = entrustMapper.selectById(id);
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        if (entrust.getIsUrgent() != null && entrust.getIsUrgent() == 1) {
            throw new BizException("该委托单已是加急状态");
        }

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        BigDecimal urgentFee = entrust.getTotalAmount() != null
                ? entrust.getTotalAmount().multiply(new BigDecimal("0.3")).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        entrust.setIsUrgent(1);
        entrust.setUrgentFee(urgentFee);
        calculateActualAmount(entrust);
        entrustMapper.updateById(entrust);

        recordStatusLog(id, entrust.getStatus(), entrust.getStatus(),
                String.format("加急处理，加急费：%s元", urgentFee), "加急处理", user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(AdjustDTO dto) {
        EntEntrust entrust = entrustMapper.selectById(dto.getEntrustId());
        if (entrust == null) {
            throw new BizException("委托单不存在");
        }

        SysUser user = SecurityUtils.getCurrentUser().getUser();

        BigDecimal currentAdjust = entrust.getAdjustAmount() == null ? BigDecimal.ZERO : entrust.getAdjustAmount();
        entrust.setIsAdjust(1);
        entrust.setAdjustAmount(currentAdjust.add(dto.getAdjustAmount()));
        entrust.setAdjustReason(dto.getAdjustReason());
        calculateActualAmount(entrust);
        entrustMapper.updateById(entrust);

        recordStatusLog(dto.getEntrustId(), entrust.getStatus(), entrust.getStatus(),
                String.format("调账处理：%s元，原因：%s", dto.getAdjustAmount(), dto.getAdjustReason()),
                "调账处理", user);
    }

    private void saveItems(Long entrustId, List<EntrustItemSaveDTO> itemDTOs) {
        Set<Long> itemIds = itemDTOs.stream()
                .map(EntrustItemSaveDTO::getItemId)
                .collect(Collectors.toSet());
        Set<Long> standardIds = itemDTOs.stream()
                .map(EntrustItemSaveDTO::getStandardId)
                .collect(Collectors.toSet());

        Map<Long, DictTestItem> itemMap = testItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(DictTestItem::getId, i -> i));

        Map<Long, DictTestStandard> standardMap = testStandardMapper.selectBatchIds(standardIds).stream()
                .collect(Collectors.toMap(DictTestStandard::getId, s -> s));

        LambdaQueryWrapper<DictItemStandard> itemStandardWrapper = new LambdaQueryWrapper<>();
        itemStandardWrapper.in(DictItemStandard::getItemId, itemIds);
        itemStandardWrapper.in(DictItemStandard::getStandardId, standardIds);
        List<DictItemStandard> itemStandards = itemStandardMapper.selectList(itemStandardWrapper);
        Map<String, DictItemStandard> itemStandardMap = itemStandards.stream()
                .collect(Collectors.toMap(
                        is -> is.getItemId() + "_" + is.getStandardId(),
                        is -> is
                ));

        int sortOrder = 1;
        for (EntrustItemSaveDTO itemDTO : itemDTOs) {
            DictTestItem testItem = itemMap.get(itemDTO.getItemId());
            if (testItem == null) {
                throw new BizException("检测项目不存在：" + itemDTO.getItemId());
            }
            DictTestStandard standard = standardMap.get(itemDTO.getStandardId());
            if (standard == null) {
                throw new BizException("检测标准不存在：" + itemDTO.getStandardId());
            }

            String key = itemDTO.getItemId() + "_" + itemDTO.getStandardId();
            DictItemStandard itemStandard = itemStandardMap.get(key);
            if (itemStandard == null) {
                throw new BizException(String.format("检测项目[%s]与检测标准[%s]未关联", testItem.getItemName(), standard.getStandardName()));
            }

            EntEntrustItem item = new EntEntrustItem();
            BeanUtil.copyProperties(itemDTO, item);
            item.setEntrustId(entrustId);
            item.setItemCode(testItem.getItemCode());
            item.setItemName(testItem.getItemName());
            item.setStandardNo(standard.getStandardNo());
            item.setStandardName(standard.getStandardName());
            item.setUnit(testItem.getUnit());
            item.setLimitValue(itemStandard.getLimitValue());
            item.setSortOrder(sortOrder++);

            BigDecimal unitPrice = testItem.getStandardPrice();
            if (itemDTO.getUnitPrice() != null) {
                unitPrice = itemDTO.getUnitPrice();
            }
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(itemDTO.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            entrustItemMapper.insert(item);
        }
    }

    private void calculateAmount(EntEntrust entrust, List<EntrustItemSaveDTO> itemDTOs) {
        Set<Long> itemIds = itemDTOs.stream()
                .map(EntrustItemSaveDTO::getItemId)
                .collect(Collectors.toSet());

        Map<Long, DictTestItem> itemMap = testItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(DictTestItem::getId, i -> i));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (EntrustItemSaveDTO itemDTO : itemDTOs) {
            DictTestItem testItem = itemMap.get(itemDTO.getItemId());
            if (testItem == null) {
                throw new BizException("检测项目不存在：" + itemDTO.getItemId());
            }

            BigDecimal unitPrice = testItem.getStandardPrice();
            if (itemDTO.getUnitPrice() != null) {
                unitPrice = itemDTO.getUnitPrice();
            }
            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(itemDTO.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            totalAmount = totalAmount.add(subtotal);
        }

        entrust.setTotalAmount(totalAmount);

        BigDecimal discountRate = entrust.getDiscountRate() == null ? BigDecimal.valueOf(100) : entrust.getDiscountRate();
        BigDecimal discountAmount = totalAmount.multiply(BigDecimal.valueOf(100).subtract(discountRate))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        entrust.setDiscountAmount(discountAmount);

        calculateActualAmount(entrust);
    }

    private void calculateActualAmount(EntEntrust entrust) {
        BigDecimal totalAmount = entrust.getTotalAmount() == null ? BigDecimal.ZERO : entrust.getTotalAmount();
        BigDecimal discountAmount = entrust.getDiscountAmount() == null ? BigDecimal.ZERO : entrust.getDiscountAmount();
        BigDecimal urgentFee = entrust.getUrgentFee() == null ? BigDecimal.ZERO : entrust.getUrgentFee();
        BigDecimal subcontractAmount = entrust.getSubcontractAmount() == null ? BigDecimal.ZERO : entrust.getSubcontractAmount();
        BigDecimal adjustAmount = entrust.getAdjustAmount() == null ? BigDecimal.ZERO : entrust.getAdjustAmount();

        BigDecimal actualAmount = totalAmount.subtract(discountAmount)
                .add(urgentFee)
                .add(subcontractAmount)
                .subtract(adjustAmount)
                .setScale(2, RoundingMode.HALF_UP);

        entrust.setActualAmount(actualAmount);
    }

    private void validateStatusTransition(Integer currentStatus, Integer targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            throw new BizException("状态不能为空");
        }

        if (currentStatus.equals(targetStatus)) {
            throw new BizException("目标状态与当前状态相同");
        }

        if (currentStatus == 7 || currentStatus == 8) {
            throw new BizException("已完成或已取消的委托单不能变更状态");
        }

        List<Integer> validTransitions = getValidTransitions(currentStatus);
        if (!validTransitions.contains(targetStatus)) {
            throw new BizException(String.format("不能从%s状态变更为%s状态",
                    STATUS_MAP.get(currentStatus), STATUS_MAP.get(targetStatus)));
        }
    }

    private List<Integer> getValidTransitions(Integer currentStatus) {
        Map<Integer, List<Integer>> transitionMap = new HashMap<>();
        transitionMap.put(0, Arrays.asList(1, 8));
        transitionMap.put(1, Arrays.asList(0, 2));
        transitionMap.put(2, Arrays.asList(3, 8));
        transitionMap.put(3, Arrays.asList(4, 8));
        transitionMap.put(4, Arrays.asList(5, 8));
        transitionMap.put(5, Arrays.asList(6, 8));
        transitionMap.put(6, Arrays.asList(7, 5));
        return transitionMap.getOrDefault(currentStatus, Collections.emptyList());
    }

    private void recordStatusLog(Long entrustId, Integer oldStatus, Integer newStatus, String content, SysUser operator) {
        recordStatusLog(entrustId, oldStatus, newStatus, content, "状态变更", operator);
    }

    private void recordStatusLog(Long entrustId, Integer oldStatus, Integer newStatus, String content, String operateType, SysUser operator) {
        EntEntrustStatusLog statusLog = new EntEntrustStatusLog();
        statusLog.setEntrustId(entrustId);
        statusLog.setBeforeStatus(oldStatus);
        statusLog.setAfterStatus(newStatus);
        statusLog.setOperateType(operateType);
        statusLog.setOperateContent(content);
        statusLog.setOperatorId(operator.getId());
        statusLog.setOperatorName(operator.getRealName());
        statusLog.setOperateTime(LocalDateTime.now());
        statusLogMapper.insert(statusLog);
    }
}
