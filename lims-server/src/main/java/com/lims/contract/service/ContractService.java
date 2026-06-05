package com.lims.contract.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.contract.dto.ContractApprovalDTO;
import com.lims.contract.dto.ContractChangeSaveDTO;
import com.lims.contract.dto.ContractPerformanceSaveDTO;
import com.lims.contract.dto.ContractQuery;
import com.lims.contract.dto.ContractSaveDTO;
import com.lims.contract.entity.ConContract;
import com.lims.contract.entity.ConContractPerformance;
import com.lims.contract.vo.ContractDetailVO;
import com.lims.contract.vo.ContractVO;

public interface ContractService extends IService<ConContract> {

    PageResult<ContractVO> selectPage(ContractQuery query);

    void addContract(ContractSaveDTO dto);

    void updateContract(ContractSaveDTO dto);

    void deleteContract(Long id);

    ContractDetailVO getContractDetail(Long id);

    void submitApproval(Long id);

    void approvalContract(ContractApprovalDTO dto);

    void applyChange(ContractChangeSaveDTO dto);

    void approvalChange(ContractApprovalDTO dto);

    void addPerformance(ContractPerformanceSaveDTO dto);

    void updatePerformance(ContractPerformanceSaveDTO dto);

    void deletePerformance(Long id);

    ConContractPerformance getPerformance(Long id);

    void updatePerformanceProgress(Long id, Integer progress);

    void terminateContract(Long id, String reason);
}
