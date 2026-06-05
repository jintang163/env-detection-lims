package com.lims.contract.service.impl;

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
import com.lims.contract.dto.ContractApprovalDTO;
import com.lims.contract.dto.ContractChangeSaveDTO;
import com.lims.contract.dto.ContractPerformanceSaveDTO;
import com.lims.contract.dto.ContractQuery;
import com.lims.contract.dto.ContractSaveDTO;
import com.lims.contract.entity.ConContract;
import com.lims.contract.entity.ConContractApproval;
import com.lims.contract.entity.ConContractChange;
import com.lims.contract.entity.ConContractPerformance;
import com.lims.contract.mapper.ConContractApprovalMapper;
import com.lims.contract.mapper.ConContractChangeMapper;
import com.lims.contract.mapper.ConContractMapper;
import com.lims.contract.mapper.ConContractPerformanceMapper;
import com.lims.contract.service.ContractService;
import com.lims.contract.vo.ContractDetailVO;
import com.lims.contract.vo.ContractVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContractServiceImpl extends ServiceImpl<ConContractMapper, ConContract> implements ContractService {

    @Autowired
    private CodeGenerator codeGenerator;

    @Autowired
    private ConContractChangeMapper contractChangeMapper;

    @Autowired
    private ConContractApprovalMapper contractApprovalMapper;

    @Autowired
    private ConContractPerformanceMapper contractPerformanceMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final int STATUS_DRAFT = 0;
    private static final int STATUS_APPROVING = 1;
    private static final int STATUS_EFFECTIVE = 2;
    private static final int STATUS_CHANGING = 3;
    private static final int STATUS_COMPLETED = 4;
    private static final int STATUS_TERMINATED = 5;

    private static final int APPROVAL_RESULT_PASS = 1;
    private static final int APPROVAL_RESULT_REJECT = 2;

    private static final int APPROVAL_TYPE_CONTRACT = 1;
    private static final int APPROVAL_TYPE_CHANGE = 2;

    private static final int CHANGE_APPROVAL_PENDING = 0;
    private static final int CHANGE_APPROVAL_PASS = 1;
    private static final int CHANGE_APPROVAL_REJECT = 2;

    private String getStatusName(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "草稿";
            case 1: return "审批中";
            case 2: return "已生效";
            case 3: return "变更中";
            case 4: return "已完成";
            case 5: return "已终止";
            default: return "";
        }
    }

    @Override
    public PageResult<ContractVO> selectPage(ContractQuery query) {
        LambdaQueryWrapper<ConContract> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getContractNo())) {
            wrapper.like(ConContract::getContractNo, query.getContractNo());
        }
        if (StrUtil.isNotBlank(query.getContractName())) {
            wrapper.like(ConContract::getContractName, query.getContractName());
        }
        if (StrUtil.isNotBlank(query.getContractType())) {
            wrapper.eq(ConContract::getContractType, query.getContractType());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(ConContract::getCustomerId, query.getCustomerId());
        }
        if (StrUtil.isNotBlank(query.getCustomerName())) {
            wrapper.like(ConContract::getCustomerName, query.getCustomerName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(ConContract::getStatus, query.getStatus());
        }
        if (query.getManagerId() != null) {
            wrapper.eq(ConContract::getManagerId, query.getManagerId());
        }
        if (query.getDeptId() != null) {
            wrapper.eq(ConContract::getDeptId, query.getDeptId());
        }
        if (query.getSignDateStart() != null) {
            wrapper.ge(ConContract::getSignDate, query.getSignDateStart());
        }
        if (query.getSignDateEnd() != null) {
            wrapper.le(ConContract::getSignDate, query.getSignDateEnd());
        }
        if (query.getExpireDateStart() != null) {
            wrapper.ge(ConContract::getExpireDate, query.getExpireDateStart());
        }
        if (query.getExpireDateEnd() != null) {
            wrapper.le(ConContract::getExpireDate, query.getExpireDateEnd());
        }
        wrapper.orderByDesc(ConContract::getCreateTime);

        Page<ConContract> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<ConContract> pageResult = this.page(page, wrapper);

        IPage<ContractVO> voPage = pageResult.convert(contract -> {
            ContractVO vo = BeanUtil.copyProperties(contract, ContractVO.class);
            vo.setStatusName(getStatusName(contract.getStatus()));
            return vo;
        });

        return PageResult.of(voPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addContract(ContractSaveDTO dto) {
        ConContract contract = BeanUtil.copyProperties(dto, ConContract.class);
        contract.setContractNo(codeGenerator.generateContractNo());
        contract.setStatus(STATUS_DRAFT);
        contract.setPerformanceProgress(0);
        contract.setReceivedAmount(BigDecimal.ZERO);
        if (dto.getContractAmount() != null) {
            contract.setUnpaidAmount(dto.getContractAmount());
        }
        this.save(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(ContractSaveDTO dto) {
        ConContract contract = this.getById(dto.getId());
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的合同可修改");
        }
        BeanUtil.copyProperties(dto, contract, "id", "contractNo", "status", "createBy", "createTime");
        if (dto.getContractAmount() != null) {
            if (contract.getReceivedAmount() == null) {
                contract.setReceivedAmount(BigDecimal.ZERO);
            }
            contract.setUnpaidAmount(dto.getContractAmount().subtract(contract.getReceivedAmount()));
        }
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(Long id) {
        ConContract contract = this.getById(id);
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的合同可删除");
        }
        this.removeById(id);
    }

    @Override
    public ContractDetailVO getContractDetail(Long id) {
        ConContract contract = this.getById(id);
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        ContractDetailVO vo = BeanUtil.copyProperties(contract, ContractDetailVO.class);
        vo.setStatusName(getStatusName(contract.getStatus()));

        List<ConContractChange> changeList = contractChangeMapper.selectList(
                new LambdaQueryWrapper<ConContractChange>()
                        .eq(ConContractChange::getContractId, id)
                        .orderByDesc(ConContractChange::getCreateTime)
        );
        vo.setChangeList(changeList);

        List<ConContractApproval> approvalList = contractApprovalMapper.selectList(
                new LambdaQueryWrapper<ConContractApproval>()
                        .eq(ConContractApproval::getContractId, id)
                        .orderByAsc(ConContractApproval::getCreateTime)
        );
        vo.setApprovalList(approvalList);

        List<ConContractPerformance> performanceList = contractPerformanceMapper.selectList(
                new LambdaQueryWrapper<ConContractPerformance>()
                        .eq(ConContractPerformance::getContractId, id)
                        .orderByAsc(ConContractPerformance::getPlanDate)
        );
        vo.setPerformanceList(performanceList);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitApproval(Long id) {
        ConContract contract = this.getById(id);
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_DRAFT) {
            throw new BizException("仅草稿状态的合同可提交审批");
        }
        contract.setStatus(STATUS_APPROVING);
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalContract(ContractApprovalDTO dto) {
        ConContract contract = this.getById(dto.getContractId());
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_APPROVING) {
            throw new BizException("合同不是审批中状态");
        }

        ConContractApproval approval = new ConContractApproval();
        approval.setContractId(dto.getContractId());
        approval.setContractNo(contract.getContractNo());
        approval.setApprovalType(APPROVAL_TYPE_CONTRACT);
        approval.setApprovalNode(dto.getApprovalNode());
        approval.setApproverId(dto.getApproverId());
        approval.setApproverName(dto.getApproverName());
        approval.setApprovalOpinion(dto.getApprovalOpinion());
        approval.setApprovalResult(dto.getApprovalResult());
        approval.setApprovalDate(LocalDate.now());
        contractApprovalMapper.insert(approval);

        if (dto.getApprovalResult() == APPROVAL_RESULT_PASS) {
            contract.setStatus(STATUS_EFFECTIVE);
        } else if (dto.getApprovalResult() == APPROVAL_RESULT_REJECT) {
            contract.setStatus(STATUS_DRAFT);
        }
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyChange(ContractChangeSaveDTO dto) {
        ConContract contract = this.getById(dto.getContractId());
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_EFFECTIVE) {
            throw new BizException("仅已生效状态的合同可申请变更");
        }

        ConContractChange change = new ConContractChange();
        change.setContractId(dto.getContractId());
        change.setContractNo(contract.getContractNo());
        change.setChangeNo(codeGenerator.generateChangeNo());
        change.setChangeType(dto.getChangeType());
        change.setChangeReason(dto.getChangeReason());
        change.setChangeDescription(dto.getChangeDescription());
        change.setChangeDate(dto.getChangeDate() != null ? dto.getChangeDate() : LocalDate.now());
        change.setApprovalStatus(CHANGE_APPROVAL_PENDING);

        try {
            String beforeContent = objectMapper.writeValueAsString(contract);
            change.setBeforeContent(beforeContent);

            ConContract afterContract = BeanUtil.copyProperties(contract, ConContract.class);
            if (StrUtil.isNotBlank(dto.getContractName())) {
                afterContract.setContractName(dto.getContractName());
            }
            if (StrUtil.isNotBlank(dto.getContractType())) {
                afterContract.setContractType(dto.getContractType());
            }
            if (dto.getCustomerId() != null) {
                afterContract.setCustomerId(dto.getCustomerId());
            }
            if (StrUtil.isNotBlank(dto.getCustomerName())) {
                afterContract.setCustomerName(dto.getCustomerName());
            }
            if (dto.getSignDate() != null) {
                afterContract.setSignDate(dto.getSignDate());
            }
            if (dto.getEffectiveDate() != null) {
                afterContract.setEffectiveDate(dto.getEffectiveDate());
            }
            if (dto.getExpireDate() != null) {
                afterContract.setExpireDate(dto.getExpireDate());
            }
            if (dto.getContractAmount() != null) {
                afterContract.setContractAmount(dto.getContractAmount());
                if (afterContract.getReceivedAmount() == null) {
                    afterContract.setReceivedAmount(BigDecimal.ZERO);
                }
                afterContract.setUnpaidAmount(dto.getContractAmount().subtract(afterContract.getReceivedAmount()));
            }
            if (dto.getManagerId() != null) {
                afterContract.setManagerId(dto.getManagerId());
            }
            if (StrUtil.isNotBlank(dto.getManagerName())) {
                afterContract.setManagerName(dto.getManagerName());
            }
            if (dto.getDeptId() != null) {
                afterContract.setDeptId(dto.getDeptId());
            }
            if (StrUtil.isNotBlank(dto.getDeptName())) {
                afterContract.setDeptName(dto.getDeptName());
            }
            if (StrUtil.isNotBlank(dto.getContent())) {
                afterContract.setContent(dto.getContent());
            }
            if (StrUtil.isNotBlank(dto.getAttachments())) {
                afterContract.setAttachments(dto.getAttachments());
            }
            if (StrUtil.isNotBlank(dto.getRemark())) {
                afterContract.setRemark(dto.getRemark());
            }

            String afterContent = objectMapper.writeValueAsString(afterContract);
            change.setAfterContent(afterContent);
        } catch (JsonProcessingException e) {
            throw new BizException("序列化变更内容失败");
        }

        contractChangeMapper.insert(change);
        contract.setStatus(STATUS_CHANGING);
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalChange(ContractApprovalDTO dto) {
        ConContract contract = this.getById(dto.getContractId());
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_CHANGING) {
            throw new BizException("合同不是变更中状态");
        }
        if (dto.getChangeId() == null) {
            throw new BizException("变更ID不能为空");
        }

        ConContractChange change = contractChangeMapper.selectById(dto.getChangeId());
        if (change == null) {
            throw new BizException("变更记录不存在");
        }
        if (change.getApprovalStatus() != CHANGE_APPROVAL_PENDING) {
            throw new BizException("该变更已处理");
        }

        ConContractApproval approval = new ConContractApproval();
        approval.setContractId(dto.getContractId());
        approval.setContractNo(contract.getContractNo());
        approval.setChangeId(dto.getChangeId());
        approval.setApprovalType(APPROVAL_TYPE_CHANGE);
        approval.setApprovalNode(dto.getApprovalNode());
        approval.setApproverId(dto.getApproverId());
        approval.setApproverName(dto.getApproverName());
        approval.setApprovalOpinion(dto.getApprovalOpinion());
        approval.setApprovalResult(dto.getApprovalResult());
        approval.setApprovalDate(LocalDate.now());
        contractApprovalMapper.insert(approval);

        change.setApproverId(dto.getApproverId());
        change.setApproverName(dto.getApproverName());
        change.setApprovalOpinion(dto.getApprovalOpinion());
        change.setApprovalDate(LocalDate.now());

        if (dto.getApprovalResult() == APPROVAL_RESULT_PASS) {
            change.setApprovalStatus(CHANGE_APPROVAL_PASS);
            try {
                ConContract afterContract = objectMapper.readValue(change.getAfterContent(), ConContract.class);
                BeanUtil.copyProperties(afterContract, contract, "id", "contractNo", "status", "createBy", "createTime");
                contract.setStatus(STATUS_EFFECTIVE);
            } catch (JsonProcessingException e) {
                throw new BizException("反序列化变更内容失败");
            }
        } else if (dto.getApprovalResult() == APPROVAL_RESULT_REJECT) {
            change.setApprovalStatus(CHANGE_APPROVAL_REJECT);
            contract.setStatus(STATUS_EFFECTIVE);
        }

        contractChangeMapper.updateById(change);
        this.updateById(contract);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPerformance(ContractPerformanceSaveDTO dto) {
        ConContract contract = this.getById(dto.getContractId());
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_EFFECTIVE && contract.getStatus() != STATUS_CHANGING) {
            throw new BizException("仅已生效或变更中的合同可添加履约记录");
        }
        ConContractPerformance performance = BeanUtil.copyProperties(dto, ConContractPerformance.class);
        performance.setContractNo(contract.getContractNo());
        if (performance.getStatus() == null) {
            performance.setStatus(0);
        }
        if (performance.getProgress() == null) {
            performance.setProgress(0);
        }
        contractPerformanceMapper.insert(performance);
        recalculatePerformanceProgress(dto.getContractId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePerformance(ContractPerformanceSaveDTO dto) {
        ConContractPerformance performance = contractPerformanceMapper.selectById(dto.getId());
        if (performance == null) {
            throw new BizException("履约记录不存在");
        }
        BeanUtil.copyProperties(dto, performance, "id", "contractId", "contractNo", "createBy", "createTime");
        contractPerformanceMapper.updateById(performance);
        recalculatePerformanceProgress(performance.getContractId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePerformance(Long id) {
        ConContractPerformance performance = contractPerformanceMapper.selectById(id);
        if (performance == null) {
            throw new BizException("履约记录不存在");
        }
        contractPerformanceMapper.deleteById(id);
        recalculatePerformanceProgress(performance.getContractId());
    }

    @Override
    public ConContractPerformance getPerformance(Long id) {
        ConContractPerformance performance = contractPerformanceMapper.selectById(id);
        if (performance == null) {
            throw new BizException("履约记录不存在");
        }
        return performance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePerformanceProgress(Long id, Integer progress) {
        if (progress == null || progress < 0 || progress > 100) {
            throw new BizException("进度值必须在0-100之间");
        }
        ConContractPerformance performance = contractPerformanceMapper.selectById(id);
        if (performance == null) {
            throw new BizException("履约记录不存在");
        }
        performance.setProgress(progress);
        if (progress == 100) {
            performance.setStatus(2);
            if (performance.getActualDate() == null) {
                performance.setActualDate(LocalDate.now());
            }
        } else if (progress > 0) {
            performance.setStatus(1);
        }
        contractPerformanceMapper.updateById(performance);
        recalculatePerformanceProgress(performance.getContractId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateContract(Long id, String reason) {
        ConContract contract = this.getById(id);
        if (contract == null) {
            throw new BizException("合同不存在");
        }
        if (contract.getStatus() != STATUS_EFFECTIVE && contract.getStatus() != STATUS_CHANGING) {
            throw new BizException("仅已生效或变更中的合同可终止");
        }
        contract.setStatus(STATUS_TERMINATED);
        contract.setRemark(StrUtil.isNotBlank(contract.getRemark())
                ? contract.getRemark() + "; 终止原因：" + reason
                : "终止原因：" + reason);
        this.updateById(contract);
    }

    private void recalculatePerformanceProgress(Long contractId) {
        List<ConContractPerformance> list = contractPerformanceMapper.selectList(
                new LambdaQueryWrapper<ConContractPerformance>()
                        .eq(ConContractPerformance::getContractId, contractId)
        );
        if (list == null || list.isEmpty()) {
            ConContract contract = this.getById(contractId);
            if (contract != null) {
                contract.setPerformanceProgress(0);
                this.updateById(contract);
            }
            return;
        }
        int totalProgress = list.stream()
                .mapToInt(p -> p.getProgress() != null ? p.getProgress() : 0)
                .sum();
        int avgProgress = totalProgress / list.size();
        ConContract contract = this.getById(contractId);
        if (contract != null) {
            contract.setPerformanceProgress(avgProgress);
            if (avgProgress == 100 && contract.getStatus() == STATUS_EFFECTIVE) {
                contract.setStatus(STATUS_COMPLETED);
            }
            this.updateById(contract);
        }
    }
}
