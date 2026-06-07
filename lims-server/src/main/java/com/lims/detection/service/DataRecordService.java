package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.common.page.PageResult;
import com.lims.detection.dto.DataRecordQuery;
import com.lims.detection.dto.DataRecordSaveDTO;
import com.lims.detection.entity.DetDataRecord;
import com.lims.detection.vo.DataRecordDetailVO;
import com.lims.detection.vo.DataRecordVO;
import com.lims.detection.vo.DataValidationResultVO;
import com.lims.detection.vo.FormFieldVO;

import java.util.List;

public interface DataRecordService extends IService<DetDataRecord> {

    PageResult<DataRecordVO> selectPage(DataRecordQuery query);

    DataRecordDetailVO getDetail(Long id);

    List<FormFieldVO> generateDynamicForm(Long methodId);

    DataValidationResultVO validateData(DataRecordSaveDTO dto);

    void saveDataRecord(DataRecordSaveDTO dto);

    void updateDataRecord(DataRecordSaveDTO dto);

    void submitDataRecord(Long id);

    void deleteDataRecord(Long id);

    void importInstrumentData(Long taskId, String fileUrl, String fileName);

    void saveAuditLog(String dataType, Long dataId, String dataNo, String operateType,
                      String fieldName, String oldValue, String newValue, boolean tamperAttempt, String blockReason);

    boolean verifyDataIntegrity(Long id);

    String calculateDataHash(DataRecordSaveDTO dto);
}
