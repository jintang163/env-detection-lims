package com.lims.detection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lims.detection.entity.DetDataTrace;
import com.lims.detection.vo.DataTraceNodeVO;

import java.util.List;

public interface DataTraceService extends IService<DetDataTrace> {

    DataTraceNodeVO getTraceTree(String traceType, Long sourceId);

    DataTraceNodeVO getTraceTreeFromSample(Long sampleId);

    DataTraceNodeVO getTraceTreeFromTask(Long taskId);

    DataTraceNodeVO getTraceTreeFromEquipment(Long equipmentId);

    void addTraceRelation(String traceType,
                          Long sourceId, String sourceType, String sourceName, String sourceNo,
                          Long targetId, String targetType, String targetName, String targetNo,
                          String relationType, String relationDesc);

    void buildTraceForDataRecord(Long dataRecordId);

    List<DetDataTrace> getTraceRelations(String sourceType, Long sourceId);

    List<DetDataTrace> getReverseTraceRelations(String targetType, Long targetId);
}
