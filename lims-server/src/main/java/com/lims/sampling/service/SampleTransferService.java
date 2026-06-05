package com.lims.sampling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.sampling.dto.SampleTransferQuery;
import com.lims.sampling.dto.SampleTransferSaveDTO;
import com.lims.sampling.entity.SmpSampleTransfer;
import com.lims.sampling.vo.SampleTransferVO;

public interface SampleTransferService extends IService<SmpSampleTransfer> {

    PageResult<SampleTransferVO> selectPage(SampleTransferQuery query);

    void createTransfer(SampleTransferSaveDTO dto);

    void confirmTransfer(Long id);

    void rejectTransfer(Long id, String reason);

    SmpSampleTransfer getTransferDetail(Long id);
}
