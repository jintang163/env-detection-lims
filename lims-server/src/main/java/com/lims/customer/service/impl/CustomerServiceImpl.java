package com.lims.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lims.common.exception.BizException;
import com.lims.common.page.PageResult;
import com.lims.common.result.ResultCode;
import com.lims.common.utils.CodeGenerator;
import com.lims.customer.dto.*;
import com.lims.customer.entity.CusCustomer;
import com.lims.customer.entity.CusCustomerCredit;
import com.lims.customer.entity.CusCustomerFollow;
import com.lims.customer.entity.CusCustomerQualification;
import com.lims.customer.mapper.*;
import com.lims.customer.service.CustomerService;
import com.lims.customer.vo.CustomerDetailVO;
import com.lims.customer.vo.CustomerVO;
import com.lims.security.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl extends ServiceImpl<CusCustomerMapper, CusCustomer> implements CustomerService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private CusCustomerMapper customerMapper;

    @Autowired
    private CusCustomerQualificationMapper qualificationMapper;

    @Autowired
    private CusCustomerFollowMapper followMapper;

    @Autowired
    private CusCustomerCreditMapper creditMapper;

    private static final Map<Integer, String> CUSTOMER_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> CUSTOMER_LEVEL_MAP = new HashMap<>();
    private static final Map<Integer, String> CUSTOMER_SOURCE_MAP = new HashMap<>();
    private static final Map<Integer, String> CUSTOMER_STATUS_MAP = new HashMap<>();

    static {
        CUSTOMER_TYPE_MAP.put(1, "企业客户");
        CUSTOMER_TYPE_MAP.put(2, "个人客户");
        CUSTOMER_TYPE_MAP.put(3, "政府机构");

        CUSTOMER_LEVEL_MAP.put(1, "VIP");
        CUSTOMER_LEVEL_MAP.put(2, "重要");
        CUSTOMER_LEVEL_MAP.put(3, "普通");
        CUSTOMER_LEVEL_MAP.put(4, "潜在");

        CUSTOMER_SOURCE_MAP.put(1, "线上推广");
        CUSTOMER_SOURCE_MAP.put(2, "线下拜访");
        CUSTOMER_SOURCE_MAP.put(3, "转介绍");
        CUSTOMER_SOURCE_MAP.put(4, "展会");
        CUSTOMER_SOURCE_MAP.put(5, "其他");

        CUSTOMER_STATUS_MAP.put(0, "潜在");
        CUSTOMER_STATUS_MAP.put(1, "意向");
        CUSTOMER_STATUS_MAP.put(2, "成交");
        CUSTOMER_STATUS_MAP.put(3, "流失");
    }

    @Override
    public PageResult<CustomerVO> page(CustomerQuery query) {
        Page<CustomerVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<CustomerVO> pageResult = customerMapper.selectCustomerPage(
                page,
                query.getCustomerName(),
                query.getCustomerType(),
                query.getCustomerLevel(),
                query.getIsPublic(),
                query.getCustomerStatus(),
                query.getOwnerId(),
                query.getDeptId()
        );
        pageResult.getRecords().forEach(this::fillCustomerVO);
        return PageResult.of(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CustomerSaveDTO dto) {
        CusCustomer customer = new CusCustomer();
        BeanUtils.copyProperties(dto, customer);

        String customerNo = codeGenerator.generateCustomerNo();
        while (customerMapper.countByCustomerNo(customerNo, null) > 0) {
            customerNo = codeGenerator.generateCustomerNo();
        }
        customer.setCustomerNo(customerNo);

        if (customer.getCustomerLevel() == null) {
            customer.setCustomerLevel(3);
        }
        if (customer.getCustomerStatus() == null) {
            customer.setCustomerStatus(0);
        }
        if (customer.getIsPublic() == null) {
            customer.setIsPublic(0);
        }
        if (customer.getStatus() == null) {
            customer.setStatus(1);
        }
        if (customer.getCreditScore() == null) {
            customer.setCreditScore(100);
        }
        if (customer.getCreditLevel() == null) {
            customer.setCreditLevel("A");
        }

        customerMapper.insert(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "客户ID不能为空");
        }
        CusCustomer customer = customerMapper.selectById(dto.getId());
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        BeanUtils.copyProperties(dto, customer);
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        customerMapper.deleteById(id);
    }

    @Override
    public CustomerDetailVO getDetail(Long id) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        CustomerDetailVO vo = new CustomerDetailVO();
        BeanUtils.copyProperties(customer, vo);
        fillCustomerDetailVO(vo);

        vo.setQualifications(qualificationMapper.selectByCustomerId(id));
        vo.setFollows(followMapper.selectByCustomerId(id));
        vo.setCredits(creditMapper.selectByCustomerId(id));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveToPublic(Long id, String reason) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        if (customer.getIsPublic() == 1) {
            throw new BizException("客户已在公海中");
        }
        customer.setIsPublic(1);
        customer.setPublicEnterTime(LocalDate.now());
        customer.setPublicReason(reason);
        customer.setOwnerId(null);
        customer.setOwnerName(null);
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFromPublic(Long id, Long ownerId, String ownerName) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        if (customer.getIsPublic() == 0) {
            throw new BizException("客户不在公海中");
        }
        customer.setIsPublic(0);
        customer.setPublicEnterTime(null);
        customer.setPublicReason(null);
        customer.setOwnerId(ownerId);
        customer.setOwnerName(ownerName);
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustLevel(Long id, Integer level) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        if (level < 1 || level > 4) {
            throw new BizException(ResultCode.BAD_REQUEST, "客户等级不正确");
        }
        customer.setCustomerLevel(level);
        customerMapper.updateById(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustCredit(CustomerCreditSaveDTO dto) {
        CusCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }

        int beforeScore = customer.getCreditScore() != null ? customer.getCreditScore() : 0;
        int changeScore = dto.getChangeScore();
        int afterScore;

        if (dto.getChangeType() == 1) {
            afterScore = beforeScore + changeScore;
        } else if (dto.getChangeType() == 2) {
            afterScore = beforeScore - changeScore;
        } else {
            throw new BizException(ResultCode.BAD_REQUEST, "变更类型不正确");
        }

        if (afterScore < 0) {
            afterScore = 0;
        }

        String creditLevel;
        if (afterScore >= 90) {
            creditLevel = "A";
        } else if (afterScore >= 75) {
            creditLevel = "B";
        } else if (afterScore >= 60) {
            creditLevel = "C";
        } else {
            creditLevel = "D";
        }

        customer.setCreditScore(afterScore);
        customer.setCreditLevel(creditLevel);
        customerMapper.updateById(customer);

        CusCustomerCredit credit = new CusCustomerCredit();
        BeanUtils.copyProperties(dto, credit);
        credit.setBeforeScore(beforeScore);
        credit.setAfterScore(afterScore);
        credit.setOperatorId(SecurityUtils.getCurrentUserId());
        credit.setOperatorName(SecurityUtils.getCurrentUsername());
        credit.setOperateTime(LocalDateTime.now());
        credit.setStatus(1);
        creditMapper.insert(credit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveQualification(CustomerQualificationSaveDTO dto) {
        CusCustomerQualification qualification = new CusCustomerQualification();
        BeanUtils.copyProperties(dto, qualification);
        if (qualification.getStatus() == null) {
            qualification.setStatus(1);
        }
        if (qualification.getAuditStatus() == null) {
            qualification.setAuditStatus(0);
        }
        qualificationMapper.insert(qualification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQualification(CustomerQualificationSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "资质ID不能为空");
        }
        CusCustomerQualification qualification = qualificationMapper.selectById(dto.getId());
        if (qualification == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        BeanUtils.copyProperties(dto, qualification);
        qualificationMapper.updateById(qualification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualification(Long id) {
        CusCustomerQualification qualification = qualificationMapper.selectById(id);
        if (qualification == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "资质不存在");
        }
        qualificationMapper.deleteById(id);
    }

    @Override
    public List<CusCustomerQualification> getQualificationList(Long customerId) {
        return qualificationMapper.selectByCustomerId(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFollow(CustomerFollowSaveDTO dto) {
        CusCustomerFollow follow = new CusCustomerFollow();
        BeanUtils.copyProperties(dto, follow);
        if (follow.getFollowBy() == null) {
            follow.setFollowBy(SecurityUtils.getCurrentUserId());
        }
        if (follow.getFollowName() == null) {
            follow.setFollowName(SecurityUtils.getCurrentUsername());
        }
        if (follow.getFollowTime() == null) {
            follow.setFollowTime(LocalDateTime.now());
        }
        if (follow.getStatus() == null) {
            follow.setStatus(1);
        }
        followMapper.insert(follow);

        CusCustomer customer = customerMapper.selectById(dto.getCustomerId());
        if (customer != null) {
            customer.setLastFollowTime(LocalDate.now());
            customer.setNextFollowTime(dto.getNextFollowTime());
            customerMapper.updateById(customer);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFollow(CustomerFollowSaveDTO dto) {
        if (dto.getId() == null) {
            throw new BizException(ResultCode.BAD_REQUEST, "跟进记录ID不能为空");
        }
        CusCustomerFollow follow = followMapper.selectById(dto.getId());
        if (follow == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "跟进记录不存在");
        }
        BeanUtils.copyProperties(dto, follow);
        followMapper.updateById(follow);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFollow(Long id) {
        CusCustomerFollow follow = followMapper.selectById(id);
        if (follow == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "跟进记录不存在");
        }
        followMapper.deleteById(id);
    }

    @Override
    public List<CusCustomerFollow> getFollowList(Long customerId) {
        return followMapper.selectByCustomerId(customerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimPublicCustomer(Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String currentUsername = SecurityUtils.getCurrentUsername();
        removeFromPublic(id, currentUserId, currentUsername);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignCustomer(Long id, Long ownerId, String ownerName) {
        CusCustomer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BizException(ResultCode.DATA_NOT_EXIST, "客户不存在");
        }
        customer.setOwnerId(ownerId);
        customer.setOwnerName(ownerName);
        customer.setIsPublic(0);
        customer.setPublicEnterTime(null);
        customer.setPublicReason(null);
        customerMapper.updateById(customer);
    }

    @Override
    public List<CusCustomerCredit> getCreditList(Long customerId) {
        return creditMapper.selectByCustomerId(customerId);
    }

    private void fillCustomerVO(CustomerVO vo) {
        if (vo.getCustomerType() != null) {
            vo.setCustomerTypeName(CUSTOMER_TYPE_MAP.get(vo.getCustomerType()));
        }
        if (vo.getCustomerLevel() != null) {
            vo.setCustomerLevelName(CUSTOMER_LEVEL_MAP.get(vo.getCustomerLevel()));
        }
        if (vo.getCustomerSource() != null) {
            vo.setCustomerSourceName(CUSTOMER_SOURCE_MAP.get(vo.getCustomerSource()));
        }
        if (vo.getCustomerStatus() != null) {
            vo.setCustomerStatusName(CUSTOMER_STATUS_MAP.get(vo.getCustomerStatus()));
        }
    }

    private void fillCustomerDetailVO(CustomerDetailVO vo) {
        if (vo.getCustomerType() != null) {
            vo.setCustomerTypeName(CUSTOMER_TYPE_MAP.get(vo.getCustomerType()));
        }
        if (vo.getCustomerLevel() != null) {
            vo.setCustomerLevelName(CUSTOMER_LEVEL_MAP.get(vo.getCustomerLevel()));
        }
        if (vo.getCustomerSource() != null) {
            vo.setCustomerSourceName(CUSTOMER_SOURCE_MAP.get(vo.getCustomerSource()));
        }
        if (vo.getCustomerStatus() != null) {
            vo.setCustomerStatusName(CUSTOMER_STATUS_MAP.get(vo.getCustomerStatus()));
        }
    }
}
