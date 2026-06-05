package com.lims.entrust.service;

import com.lims.common.page.PageResult;
import com.lims.entrust.dto.SubcontractSaveDTO;
import com.lims.entrust.vo.SubcontractVO;

public interface SubcontractService {

    PageResult<SubcontractVO> page(Long entrustId, Integer pageNum, Integer pageSize);

    Long create(SubcontractSaveDTO dto);

    void updateStatus(Long id, Integer status, String remark);

    SubcontractVO getDetail(Long id);
}
