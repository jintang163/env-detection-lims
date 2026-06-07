package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.CapaProcessDTO;
import com.lims.detection.dto.CapaQuery;
import com.lims.detection.dto.CapaSaveDTO;
import com.lims.detection.dto.CapaVerificationDTO;
import com.lims.detection.entity.CapaProcessLog;
import com.lims.detection.entity.CapaRecord;
import com.lims.detection.vo.CapaProcessLogVO;
import com.lims.detection.vo.CapaRecordVO;
import com.lims.detection.vo.CapaStatsVO;

import java.util.List;

public interface CapaService extends IService<CapaRecord> {

    PageResult<CapaRecordVO> selectPage(CapaQuery query);

    CapaRecordVO getDetail(Long id);

    Long createCapa(CapaSaveDTO dto);

    void updateCapa(CapaSaveDTO dto);

    void deleteCapa(Long id);

    void submitApproval(Long id);

    void approve(Long id, CapaProcessDTO dto);

    void reject(Long id, CapaProcessDTO dto);

    void executeComplete(Long id, CapaProcessDTO dto);

    void verifyPass(Long id, CapaVerificationDTO dto);

    void verifyFail(Long id, CapaProcessDTO dto);

    void closeCapa(Long id, CapaProcessDTO dto);

    List<CapaProcessLogVO> getProcessLogs(Long capaId);

    CapaStatsVO getStats();

    List<CapaRecordVO> getOverdueCapa();

    void addProcessLog(Long capaId, String operationType, String operationStatus, String content, String operatorName);
}
