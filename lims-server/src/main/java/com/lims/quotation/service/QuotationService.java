package com.lims.quotation.service;

import com.lims.common.page.PageResult;
import com.lims.quotation.dto.*;
import com.lims.quotation.vo.QuotationDetailVO;
import com.lims.quotation.vo.QuotationPrintVO;
import com.lims.quotation.vo.QuotationVO;

public interface QuotationService {

    PageResult<QuotationVO> page(QuotationQuery query);

    Long save(QuotationSaveDTO dto);

    void update(QuotationSaveDTO dto);

    void delete(Long id);

    QuotationDetailVO getDetail(Long id);

    void submitApproval(Long id);

    void approval(QuotationApprovalDTO dto);

    void customerConfirm(Long id, String confirmPerson);

    void cancel(Long id);

    Long convertToEntrust(ConvertToEntrustDTO dto);

    QuotationPrintVO getPrintData(Long id);
}
