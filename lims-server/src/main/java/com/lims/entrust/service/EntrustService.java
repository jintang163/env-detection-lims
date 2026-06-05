package com.lims.entrust.service;

import com.lims.common.page.PageResult;
import com.lims.entrust.dto.*;
import com.lims.entrust.vo.EntrustDetailVO;
import com.lims.entrust.vo.EntrustVO;

public interface EntrustService {

    PageResult<EntrustVO> page(EntrustQuery query);

    Long create(EntrustSaveDTO dto);

    void update(EntrustSaveDTO dto);

    void delete(Long id);

    EntrustDetailVO getDetail(Long id);

    void submit(Long id);

    void review(EntrustReviewDTO dto);

    void changeStatus(EntrustStatusChangeDTO dto);

    void urgent(Long id);

    void adjust(AdjustDTO dto);
}
