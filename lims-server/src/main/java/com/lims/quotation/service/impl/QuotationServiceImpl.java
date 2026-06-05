package com.lims.quotation.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.utils.CodeGenerator;
import com.lims.dict.entity.DictTestItem;
import com.lims.dict.entity.DictTestStandard;
import com.lims.dict.mapper.DictTestItemMapper;
import com.lims.dict.mapper.DictTestStandardMapper;
import com.lims.entrust.dto.EntrustItemSaveDTO;
import com.lims.entrust.dto.EntrustSaveDTO;
import com.lims.entrust.service.EntrustService;
import com.lims.quotation.dto.*;
import com.lims.quotation.entity.QuoQuotation;
import com.lims.quotation.entity.QuoQuotationApproval;
import com.lims.quotation.entity.QuoQuotationItem;
import com.lims.quotation.mapper.QuoQuotationApprovalMapper;
import com.lims.quotation.mapper.QuoQuotationItemMapper;
import com.lims.quotation.mapper.QuoQuotationMapper;
import com.lims.quotation.service.QuotationService;
import com.lims.quotation.vo.QuotationDetailVO;
import com.lims.quotation.vo.QuotationItemVO;
import com.lims.quotation.vo.QuotationPrintVO;
import com.lims.quotation.vo.QuotationVO;
import com.lims.security.entity.LoginUser;
import com.lims.security.utils.SecurityUtils;
import com.lims.system.entity.SysUser;
import com.lims.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    private QuoQuotationMapper quotationMapper;

    @Autowired
    private QuoQuotationItemMapper quotationItemMapper;

    @Autowired
    private QuoQuotationApprovalMapper quotationApprovalMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private DictTestItemMapper testItemMapper;

    @Autowired
    private DictTestStandardMapper testStandardMapper;

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private EntrustService entrustService;

    private static final Map<Integer, String> STATUS_MAP = Map.of(
            0, "草稿",
            1, "审批中",
            2, "已通过",
            3, "已驳回",
            4, "已确认",
            5, "已作废"
    );

    private static final Map<Integer, String> APPROVAL_STATUS_MAP = Map.of(
            0, "待审批",
            1, "审批中",
            2, "已通过",
            3, "已驳回"
    );

    private static final Map<Integer, String> APPROVAL_RESULT_MAP = Map.of(
            1, "通过",
            2, "驳回"
    );

    @Override
    public PageResult<QuotationVO> page(QuotationQuery query) {
        LambdaQueryWrapper<QuoQuotation> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getQuotationNo())) {
            wrapper.like(QuoQuotation::getQuotationNo, query.getQuotationNo());
        }
        if (StrUtil.isNotBlank(query.getQuotationName())) {
            wrapper.like(QuoQuotation::getQuotationName, query.getQuotationName());
        }
        if (StrUtil.isNotBlank(query.getCustomerName())) {
            wrapper.like(QuoQuotation::getCustomerName, query.getCustomerName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(QuoQuotation::getStatus, query.getStatus());
        }
        if (query.getQuotationDateStart() != null) {
            wrapper.ge(QuoQuotation::getQuotationDate, query.getQuotationDateStart());
        }
        if (query.getQuotationDateEnd() != null) {
            wrapper.le(QuoQuotation::getQuotationDate, query.getQuotationDateEnd());
        }
        if (query.getIsConverted() != null) {
            wrapper.eq(QuoQuotation::getIsConverted, query.getIsConverted());
        }

        String orderBy = StrUtil.isNotBlank(query.getOrderBy()) ? query.getOrderBy() : "id";
        boolean isAsc = "asc".equalsIgnoreCase(query.getOrderType());
        if (isAsc) {
            wrapper.orderByAsc(StrUtil.toUnderlineCase(orderBy));
        } else {
            wrapper.orderByDesc(StrUtil.toUnderlineCase(orderBy));
        }

        Page<QuoQuotation> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<QuoQuotation> pageResult = quotationMapper.selectPage(page, wrapper);

        List<QuoQuotation> records = pageResult.getRecords();
        List<Long> userIds = records.stream().map(QuoQuotation::getCreateBy).distinct().collect(Collectors.toList());
        Map<Long, String> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        List<QuotationVO> voList = records.stream().map(item -> {
            QuotationVO vo = BeanUtil.copyProperties(item, QuotationVO.class);
            vo.setStatusName(STATUS_MAP.get(item.getStatus()));
            vo.setCreateByName(userMap.getOrDefault(item.getCreateBy(), ""));
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(QuotationSaveDTO dto) {
        QuoQuotation quotation = new QuoQuotation();
        BeanUtil.copyProperties(dto, quotation);

        String quotationNo = codeGenerator.generateQuotationNo();
        quotation.setQuotationNo(quotationNo);
        quotation.setStatus(0);
        quotation.setApprovalStatus(0);
        quotation.setIsConverted(0);

        BigDecimal totalAmount = calculateTotalAmount(dto.getItems());
        BigDecimal actualAmount = calculateActualAmount(totalAmount, dto.getDiscountRate());
        quotation.setTotalAmount(totalAmount);
        quotation.setActualAmount(actualAmount);

        quotationMapper.insert(quotation);

        saveItems(quotation.getId(), dto.getItems());

        return quotation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QuotationSaveDTO dto) {
        QuoQuotation quotation = quotationMapper.selectById(dto.getId());
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 0 && quotation.getStatus() != 3) {
            throw new BizException("仅草稿或已驳回状态的报价单可修改");
        }

        BeanUtil.copyProperties(dto, quotation);

        BigDecimal totalAmount = calculateTotalAmount(dto.getItems());
        BigDecimal actualAmount = calculateActualAmount(totalAmount, dto.getDiscountRate());
        quotation.setTotalAmount(totalAmount);
        quotation.setActualAmount(actualAmount);

        quotationMapper.updateById(quotation);

        LambdaQueryWrapper<QuoQuotationItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(QuoQuotationItem::getQuotationId, dto.getId());
        quotationItemMapper.delete(itemWrapper);

        saveItems(dto.getId(), dto.getItems());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 0 && quotation.getStatus() != 3) {
            throw new BizException("仅草稿或已驳回状态的报价单可删除");
        }

        LambdaQueryWrapper<QuoQuotationItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(QuoQuotationItem::getQuotationId, id);
        quotationItemMapper.delete(itemWrapper);

        LambdaQueryWrapper<QuoQuotationApproval> approvalWrapper = new LambdaQueryWrapper<>();
        approvalWrapper.eq(QuoQuotationApproval::getQuotationId, id);
        quotationApprovalMapper.delete(approvalWrapper);

        quotationMapper.deleteById(id);
    }

    @Override
    public QuotationDetailVO getDetail(Long id) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }

        QuotationDetailVO vo = BeanUtil.copyProperties(quotation, QuotationDetailVO.class);
        vo.setStatusName(STATUS_MAP.get(quotation.getStatus()));

        SysUser createUser = sysUserMapper.selectById(quotation.getCreateBy());
        if (createUser != null) {
            vo.setCreateByName(createUser.getRealName());
        }

        LambdaQueryWrapper<QuoQuotationItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(QuoQuotationItem::getQuotationId, id);
        itemWrapper.orderByAsc(QuoQuotationItem::getSortOrder);
        List<QuoQuotationItem> items = quotationItemMapper.selectList(itemWrapper);

        List<QuotationItemVO> itemVOList = items.stream().map(item -> {
            QuotationItemVO itemVO = BeanUtil.copyProperties(item, QuotationItemVO.class);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOList);

        LambdaQueryWrapper<QuoQuotationApproval> approvalWrapper = new LambdaQueryWrapper<>();
        approvalWrapper.eq(QuoQuotationApproval::getQuotationId, id);
        approvalWrapper.orderByAsc(QuoQuotationApproval::getId);
        List<QuoQuotationApproval> approvals = quotationApprovalMapper.selectList(approvalWrapper);

        List<QuotationDetailVO.QuotationApprovalRecordVO> approvalVOList = approvals.stream().map(item -> {
            QuotationDetailVO.QuotationApprovalRecordVO recordVO = new QuotationDetailVO.QuotationApprovalRecordVO();
            BeanUtil.copyProperties(item, recordVO);
            recordVO.setApprovalResultName(APPROVAL_RESULT_MAP.get(item.getApprovalResult()));
            return recordVO;
        }).collect(Collectors.toList());
        vo.setApprovalRecords(approvalVOList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 0 && quotation.getStatus() != 3) {
            throw new BizException("仅草稿或已驳回状态的报价单可提交审批");
        }
        if (quotation.getValidDate().isBefore(LocalDate.now())) {
            throw new BizException("报价单已过期，无法提交审批");
        }

        quotation.setStatus(1);
        quotation.setApprovalStatus(1);
        quotationMapper.updateById(quotation);

        QuoQuotationApproval approval = new QuoQuotationApproval();
        approval.setQuotationId(id);
        approval.setApprovalNode("提交审批");
        LoginUser loginUser = getCurrentUser();
        approval.setApproverId(loginUser.getUser().getId());
        approval.setApproverName(loginUser.getUser().getRealName());
        approval.setApprovalOpinion("提交审批");
        approval.setApprovalTime(LocalDateTime.now());
        quotationApprovalMapper.insert(approval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approval(QuotationApprovalDTO dto) {
        QuoQuotation quotation = quotationMapper.selectById(dto.getId());
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 1) {
            throw new BizException("仅审批中状态的报价单可审批");
        }

        LoginUser loginUser = getCurrentUser();
        if (dto.getApprovalResult() == 1) {
            quotation.setStatus(2);
            quotation.setApprovalStatus(2);
        } else if (dto.getApprovalResult() == 2) {
            quotation.setStatus(3);
            quotation.setApprovalStatus(3);
        } else {
            throw new BizException("审批结果不正确");
        }
        quotationMapper.updateById(quotation);

        QuoQuotationApproval approval = new QuoQuotationApproval();
        approval.setQuotationId(dto.getId());
        approval.setApprovalNode("审批");
        approval.setApproverId(loginUser.getUser().getId());
        approval.setApproverName(loginUser.getUser().getRealName());
        approval.setApprovalResult(dto.getApprovalResult());
        approval.setApprovalOpinion(dto.getApprovalRemark());
        approval.setApprovalTime(LocalDateTime.now());
        quotationApprovalMapper.insert(approval);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void customerConfirm(Long id, String confirmPerson) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 2) {
            throw new BizException("仅已通过状态的报价单可客户确认");
        }
        if (quotation.getValidDate().isBefore(LocalDate.now())) {
            throw new BizException("报价单已过期，无法确认");
        }

        quotation.setStatus(4);
        quotation.setConfirmTime(LocalDateTime.now());
        quotationMapper.updateById(quotation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() == 5) {
            throw new BizException("报价单已作废");
        }
        if (quotation.getIsConverted() != null && quotation.getIsConverted() == 1) {
            throw new BizException("已转委托的报价单不可作废");
        }

        quotation.setStatus(5);
        quotationMapper.updateById(quotation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long convertToEntrust(ConvertToEntrustDTO dto) {
        QuoQuotation quotation = quotationMapper.selectById(dto.getQuotationId());
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }
        if (quotation.getStatus() != 4) {
            throw new BizException("仅已确认状态的报价单可转委托");
        }
        if (quotation.getIsConverted() != null && quotation.getIsConverted() == 1) {
            throw new BizException("报价单已转委托");
        }

        Long entrustId = createEntrust(quotation, dto);

        quotation.setIsConverted(1);
        quotation.setConvertEntrustId(entrustId);
        quotationMapper.updateById(quotation);

        return entrustId;
    }

    @Override
    public QuotationPrintVO getPrintData(Long id) {
        QuoQuotation quotation = quotationMapper.selectById(id);
        if (quotation == null) {
            throw new BizException("报价单不存在");
        }

        QuotationPrintVO vo = BeanUtil.copyProperties(quotation, QuotationPrintVO.class);
        vo.setTotalAmountCn(convertToChinese(quotation.getTotalAmount()));
        vo.setActualAmountCn(convertToChinese(quotation.getActualAmount()));

        LambdaQueryWrapper<QuoQuotationItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(QuoQuotationItem::getQuotationId, id);
        itemWrapper.orderByAsc(QuoQuotationItem::getSortOrder);
        List<QuoQuotationItem> items = quotationItemMapper.selectList(itemWrapper);

        List<QuotationItemVO> itemVOList = items.stream().map(item -> {
            QuotationItemVO itemVO = BeanUtil.copyProperties(item, QuotationItemVO.class);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOList);

        return vo;
    }

    private void saveItems(Long quotationId, List<QuotationItemSaveDTO> items) {
        Set<Long> itemIds = items.stream()
                .map(QuotationItemSaveDTO::getItemId)
                .collect(Collectors.toSet());
        Set<Long> standardIds = items.stream()
                .map(QuotationItemSaveDTO::getStandardId)
                .collect(Collectors.toSet());

        Map<Long, DictTestItem> itemMap = testItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(DictTestItem::getId, i -> i));
        Map<Long, DictTestStandard> standardMap = testStandardMapper.selectBatchIds(standardIds).stream()
                .collect(Collectors.toMap(DictTestStandard::getId, s -> s));

        int sort = 1;
        for (QuotationItemSaveDTO itemDTO : items) {
            QuoQuotationItem item = new QuoQuotationItem();
            BeanUtil.copyProperties(itemDTO, item);
            item.setQuotationId(quotationId);

            DictTestItem testItem = itemMap.get(itemDTO.getItemId());
            if (testItem != null) {
                item.setItemCode(testItem.getItemCode());
                item.setItemName(testItem.getItemName());
                item.setItemCategory(testItem.getItemCategory());
                item.setUnit(testItem.getUnit());
            }

            DictTestStandard standard = standardMap.get(itemDTO.getStandardId());
            if (standard != null) {
                item.setStandardNo(standard.getStandardNo());
                item.setStandardName(standard.getStandardName());
            }

            BigDecimal subtotal = itemDTO.getUnitPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            item.setSubtotal(subtotal);
            item.setSortOrder(itemDTO.getSortOrder() != null ? itemDTO.getSortOrder() : sort++);
            quotationItemMapper.insert(item);
        }
    }

    private BigDecimal calculateTotalAmount(List<QuotationItemSaveDTO> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (QuotationItemSaveDTO item : items) {
            BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(subtotal);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateActualAmount(BigDecimal totalAmount, BigDecimal discountRate) {
        if (discountRate == null) {
            discountRate = BigDecimal.valueOf(100);
        }
        return totalAmount.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private Long createEntrust(QuoQuotation quotation, ConvertToEntrustDTO dto) {
        LambdaQueryWrapper<QuoQuotationItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(QuoQuotationItem::getQuotationId, dto.getQuotationId());
        itemWrapper.orderByAsc(QuoQuotationItem::getSortOrder);
        List<QuoQuotationItem> quotationItems = quotationItemMapper.selectList(itemWrapper);

        if (quotationItems.isEmpty()) {
            throw new BizException("报价单没有检测项目，无法转委托");
        }

        EntrustSaveDTO entrustSaveDTO = new EntrustSaveDTO();
        BeanUtil.copyProperties(dto, entrustSaveDTO);
        entrustSaveDTO.setCustomerId(quotation.getCustomerId());
        entrustSaveDTO.setCustomerName(quotation.getCustomerName());
        entrustSaveDTO.setDiscountRate(quotation.getDiscountRate());

        List<EntrustItemSaveDTO> entrustItems = quotationItems.stream().map(qi -> {
            EntrustItemSaveDTO item = new EntrustItemSaveDTO();
            item.setItemId(qi.getItemId());
            item.setItemCode(qi.getItemCode());
            item.setItemName(qi.getItemName());
            item.setStandardId(qi.getStandardId());
            item.setStandardNo(qi.getStandardNo());
            item.setStandardName(qi.getStandardName());
            item.setUnit(qi.getUnit());
            item.setUnitPrice(qi.getUnitPrice());
            item.setQuantity(qi.getQuantity());
            item.setSubtotal(qi.getSubtotal());
            item.setSortOrder(qi.getSortOrder());
            item.setIsSubcontract(0);
            return item;
        }).collect(Collectors.toList());

        entrustSaveDTO.setItems(entrustItems);

        return entrustService.create(entrustSaveDTO);
    }

    private LoginUser getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    private String convertToChinese(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "零元整";
        }

        String[] digits = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[] units = {"", "拾", "佰", "仟"};
        String[] bigUnits = {"", "万", "亿"};

        String amountStr = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
        String[] parts = amountStr.split("\\.");

        String integerPart = parts[0];
        String decimalPart = parts.length > 1 ? parts[1] : "00";

        StringBuilder result = new StringBuilder();

        int len = integerPart.length();
        boolean zeroFlag = false;

        for (int i = 0; i < len; i++) {
            int digit = integerPart.charAt(i) - '0';
            int pos = len - i - 1;
            int bigUnitPos = pos / 4;
            int unitPos = pos % 4;

            if (digit == 0) {
                zeroFlag = true;
                if (unitPos == 0 && bigUnitPos > 0) {
                    boolean allZero = true;
                    for (int j = i; j >= Math.max(0, i - 3); j--) {
                        if (integerPart.charAt(j) != '0') {
                            allZero = false;
                            break;
                        }
                    }
                    if (!allZero) {
                        result.append(bigUnits[bigUnitPos]);
                    }
                }
            } else {
                if (zeroFlag) {
                    result.append("零");
                    zeroFlag = false;
                }
                result.append(digits[digit]).append(units[unitPos]);
                if (unitPos == 0 && bigUnitPos > 0) {
                    result.append(bigUnits[bigUnitPos]);
                }
            }
        }

        if (result.length() > 0) {
            result.append("元");
        }

        int jiao = decimalPart.charAt(0) - '0';
        int fen = decimalPart.charAt(1) - '0';

        if (jiao == 0 && fen == 0) {
            result.append("整");
        } else {
            if (jiao != 0) {
                result.append(digits[jiao]).append("角");
            } else if (result.length() > 0) {
                result.append("零");
            }
            if (fen != 0) {
                result.append(digits[fen]).append("分");
            }
        }

        return result.toString();
    }
}
