package com.lims.detection.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.common.exception.BizException;
import com.lims.detection.entity.*;
import com.lims.detection.mapper.*;
import com.lims.detection.service.DataTraceService;
import com.lims.detection.vo.DataTraceNodeVO;
import com.lims.sampling.entity.SmpEquipment;
import com.lims.sampling.entity.SmpSampleRecord;
import com.lims.sampling.mapper.SmpEquipmentMapper;
import com.lims.sampling.mapper.SmpSampleRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataTraceServiceImpl extends ServiceImpl<DetDataTraceMapper, DetDataTrace> implements DataTraceService {

    @Autowired
    private DetDetectionTaskMapper detectionTaskMapper;

    @Autowired
    private DetDataRecordMapper dataRecordMapper;

    @Autowired
    private DetOriginalRecordMapper originalRecordMapper;

    @Autowired
    private SmpSampleRecordMapper sampleRecordMapper;

    @Autowired
    private SmpEquipmentMapper equipmentMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String NODE_TYPE_SAMPLE = "SAMPLE";
    private static final String NODE_TYPE_TASK = "TASK";
    private static final String NODE_TYPE_DATA_RECORD = "DATA_RECORD";
    private static final String NODE_TYPE_ORIGINAL_RECORD = "ORIGINAL_RECORD";
    private static final String NODE_TYPE_REPORT = "REPORT";
    private static final String NODE_TYPE_EQUIPMENT = "EQUIPMENT";
    private static final String NODE_TYPE_STANDARD_SUBSTANCE = "STANDARD_SUBSTANCE";

    private static final String RELATION_TYPE_BELONG_TO = "belong_to";
    private static final String RELATION_TYPE_USE_FOR = "use_for";
    private static final String RELATION_TYPE_GENERATE = "generate";

    private String getNodeTypeName(String nodeType) {
        if (nodeType == null) return "";
        switch (nodeType) {
            case NODE_TYPE_SAMPLE:
                return "样品";
            case NODE_TYPE_TASK:
                return "检测任务";
            case NODE_TYPE_DATA_RECORD:
                return "检测数据";
            case NODE_TYPE_ORIGINAL_RECORD:
                return "原始记录";
            case NODE_TYPE_REPORT:
                return "检测报告";
            case NODE_TYPE_EQUIPMENT:
                return "仪器设备";
            case NODE_TYPE_STANDARD_SUBSTANCE:
                return "标准物质";
            default:
                return "";
        }
    }

    @Override
    public DataTraceNodeVO getTraceTree(String traceType, Long sourceId) {
        if (StrUtil.isBlank(traceType) || sourceId == null) {
            throw new BizException("溯源类型和源ID不能为空");
        }

        DataTraceNodeVO rootNode = null;

        switch (traceType.toUpperCase()) {
            case NODE_TYPE_SAMPLE:
                rootNode = getTraceTreeFromSample(sourceId);
                break;
            case NODE_TYPE_TASK:
                rootNode = getTraceTreeFromTask(sourceId);
                break;
            case NODE_TYPE_EQUIPMENT:
                rootNode = getTraceTreeFromEquipment(sourceId);
                break;
            default:
                rootNode = buildTraceTree(traceType, sourceId);
        }

        return rootNode;
    }

    @Override
    public DataTraceNodeVO getTraceTreeFromSample(Long sampleId) {
        SmpSampleRecord sample = sampleRecordMapper.selectById(sampleId);
        if (sample == null) {
            throw new BizException("样品不存在");
        }

        DataTraceNodeVO rootNode = new DataTraceNodeVO();
        rootNode.setId(sampleId);
        rootNode.setNodeType(NODE_TYPE_SAMPLE);
        rootNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_SAMPLE));
        rootNode.setNodeName(sample.getSampleName());
        rootNode.setNodeNo(sample.getSampleNo());

        List<DetDetectionTask> tasks = detectionTaskMapper.selectList(
                new LambdaQueryWrapper<DetDetectionTask>()
                        .eq(DetDetectionTask::getSampleId, sampleId)
        );

        List<DataTraceNodeVO> taskNodes = new ArrayList<>();
        for (DetDetectionTask task : tasks) {
            DataTraceNodeVO taskNode = buildTaskNode(task);
            taskNode.setRelationType(RELATION_TYPE_BELONG_TO);
            taskNode.setRelationDesc("属于该样品");
            taskNodes.add(taskNode);
        }
        rootNode.setChildren(taskNodes);

        return rootNode;
    }

    @Override
    public DataTraceNodeVO getTraceTreeFromTask(Long taskId) {
        DetDetectionTask task = detectionTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException("检测任务不存在");
        }

        DataTraceNodeVO rootNode = buildTaskNode(task);

        if (task.getSampleId() != null) {
            SmpSampleRecord sample = sampleRecordMapper.selectById(task.getSampleId());
            if (sample != null) {
                DataTraceNodeVO sampleNode = new DataTraceNodeVO();
                sampleNode.setId(sample.getId());
                sampleNode.setNodeType(NODE_TYPE_SAMPLE);
                sampleNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_SAMPLE));
                sampleNode.setNodeName(sample.getSampleName());
                sampleNode.setNodeNo(sample.getSampleNo());
                sampleNode.setRelationType(RELATION_TYPE_GENERATE);
                sampleNode.setRelationDesc("生成该任务");

                DataTraceNodeVO wrapperNode = new DataTraceNodeVO();
                wrapperNode.setId(rootNode.getId());
                wrapperNode.setNodeType(rootNode.getNodeType());
                wrapperNode.setNodeTypeName(rootNode.getNodeTypeName());
                wrapperNode.setNodeName(rootNode.getNodeName());
                wrapperNode.setNodeNo(rootNode.getNodeNo());
                wrapperNode.setChildren(rootNode.getChildren());

                sampleNode.setChildren(Collections.singletonList(wrapperNode));
                return sampleNode;
            }
        }

        return rootNode;
    }

    @Override
    public DataTraceNodeVO getTraceTreeFromEquipment(Long equipmentId) {
        SmpEquipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            throw new BizException("仪器设备不存在");
        }

        DataTraceNodeVO rootNode = new DataTraceNodeVO();
        rootNode.setId(equipmentId);
        rootNode.setNodeType(NODE_TYPE_EQUIPMENT);
        rootNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_EQUIPMENT));
        rootNode.setNodeName(equipment.getEquipmentName());
        rootNode.setNodeNo(equipment.getEquipmentNo());

        List<DetDataTrace> traces = this.list(
                new LambdaQueryWrapper<DetDataTrace>()
                        .eq(DetDataTrace::getSourceType, NODE_TYPE_EQUIPMENT)
                        .eq(DetDataTrace::getSourceId, equipmentId)
        );

        List<DataTraceNodeVO> children = new ArrayList<>();
        for (DetDataTrace trace : traces) {
            DataTraceNodeVO childNode = buildNodeFromTrace(trace, false);
            if (childNode != null) {
                children.add(childNode);
            }
        }
        rootNode.setChildren(children);

        return rootNode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTraceRelation(String traceType,
                                  Long sourceId, String sourceType, String sourceName, String sourceNo,
                                  Long targetId, String targetType, String targetName, String targetNo,
                                  String relationType, String relationDesc) {
        if (sourceId == null || targetId == null) {
            throw new BizException("源节点ID和目标节点ID不能为空");
        }
        if (StrUtil.isBlank(sourceType) || StrUtil.isBlank(targetType)) {
            throw new BizException("源节点类型和目标节点类型不能为空");
        }

        DetDataTrace trace = new DetDataTrace();
        trace.setTraceType(traceType);
        trace.setSourceId(sourceId);
        trace.setSourceType(sourceType);
        trace.setSourceName(sourceName);
        trace.setSourceNo(sourceNo);
        trace.setTargetId(targetId);
        trace.setTargetType(targetType);
        trace.setTargetName(targetName);
        trace.setTargetNo(targetNo);
        trace.setRelationType(relationType);
        trace.setRelationDesc(relationDesc);
        trace.setCreateTime(LocalDateTime.now());

        this.save(trace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void buildTraceForDataRecord(Long dataRecordId) {
        DetDataRecord dataRecord = dataRecordMapper.selectById(dataRecordId);
        if (dataRecord == null) {
            throw new BizException("检测数据记录不存在");
        }

        if (dataRecord.getTaskId() != null) {
            DetDetectionTask task = detectionTaskMapper.selectById(dataRecord.getTaskId());
            if (task != null) {
                addTraceRelation(NODE_TYPE_TASK,
                        task.getId(), NODE_TYPE_TASK, task.getTaskName(), task.getTaskNo(),
                        dataRecord.getId(), NODE_TYPE_DATA_RECORD, dataRecord.getRecordNo(), dataRecord.getRecordNo(),
                        RELATION_TYPE_GENERATE, "生成检测数据");

                if (task.getSampleId() != null) {
                    SmpSampleRecord sample = sampleRecordMapper.selectById(task.getSampleId());
                    if (sample != null) {
                        addTraceRelation(NODE_TYPE_SAMPLE,
                                sample.getId(), NODE_TYPE_SAMPLE, sample.getSampleName(), sample.getSampleNo(),
                                task.getId(), NODE_TYPE_TASK, task.getTaskName(), task.getTaskNo(),
                                RELATION_TYPE_BELONG_TO, "样品用于检测任务");
                    }
                }
            }
        }

        if (StrUtil.isNotBlank(dataRecord.getEquipmentIds())) {
            try {
                List<Long> equipmentIds = objectMapper.readValue(dataRecord.getEquipmentIds(), List.class);
                List<String> equipmentNames = StrUtil.isNotBlank(dataRecord.getEquipmentNames())
                        ? objectMapper.readValue(dataRecord.getEquipmentNames(), List.class)
                        : Collections.emptyList();

                for (int i = 0; i < equipmentIds.size(); i++) {
                    Long equipmentId = equipmentIds.get(i);
                    String equipmentName = i < equipmentNames.size() ? equipmentNames.get(i) : null;

                    EqEquipment equipment = equipmentMapper.selectById(equipmentId);
                    if (equipment != null) {
                        addTraceRelation(NODE_TYPE_EQUIPMENT,
                                equipment.getId(), NODE_TYPE_EQUIPMENT,
                                equipment.getEquipmentName() != null ? equipment.getEquipmentName() : equipmentName,
                                equipment.getEquipmentNo(),
                                dataRecord.getId(), NODE_TYPE_DATA_RECORD, dataRecord.getRecordNo(), dataRecord.getRecordNo(),
                                RELATION_TYPE_USE_FOR, "仪器用于检测");
                    }
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析设备ID列表失败");
            }
        }

        List<DetOriginalRecord> originalRecords = originalRecordMapper.selectList(
                new LambdaQueryWrapper<DetOriginalRecord>()
                        .eq(DetOriginalRecord::getDataRecordId, dataRecordId)
        );

        for (DetOriginalRecord originalRecord : originalRecords) {
            addTraceRelation(NODE_TYPE_DATA_RECORD,
                    dataRecord.getId(), NODE_TYPE_DATA_RECORD, dataRecord.getRecordNo(), dataRecord.getRecordNo(),
                    originalRecord.getId(), NODE_TYPE_ORIGINAL_RECORD, originalRecord.getTitle(), originalRecord.getRecordNo(),
                    RELATION_TYPE_GENERATE, "生成原始记录");
        }
    }

    @Override
    public List<DetDataTrace> getTraceRelations(String sourceType, Long sourceId) {
        if (StrUtil.isBlank(sourceType) || sourceId == null) {
            throw new BizException("源节点类型和源ID不能为空");
        }

        return this.list(
                new LambdaQueryWrapper<DetDataTrace>()
                        .eq(DetDataTrace::getSourceType, sourceType)
                        .eq(DetDataTrace::getSourceId, sourceId)
                        .orderByDesc(DetDataTrace::getCreateTime)
        );
    }

    @Override
    public List<DetDataTrace> getReverseTraceRelations(String targetType, Long targetId) {
        if (StrUtil.isBlank(targetType) || targetId == null) {
            throw new BizException("目标节点类型和目标ID不能为空");
        }

        return this.list(
                new LambdaQueryWrapper<DetDataTrace>()
                        .eq(DetDataTrace::getTargetType, targetType)
                        .eq(DetDataTrace::getTargetId, targetId)
                        .orderByDesc(DetDataTrace::getCreateTime)
        );
    }

    private DataTraceNodeVO buildTraceTree(String traceType, Long sourceId) {
        List<DetDataTrace> forwardTraces = getTraceRelations(traceType, sourceId);
        List<DetDataTrace> reverseTraces = getReverseTraceRelations(traceType, sourceId);

        DataTraceNodeVO rootNode = null;
        if (!forwardTraces.isEmpty()) {
            DetDataTrace firstTrace = forwardTraces.get(0);
            rootNode = new DataTraceNodeVO();
            rootNode.setId(firstTrace.getSourceId());
            rootNode.setNodeType(firstTrace.getSourceType());
            rootNode.setNodeTypeName(getNodeTypeName(firstTrace.getSourceType()));
            rootNode.setNodeName(firstTrace.getSourceName());
            rootNode.setNodeNo(firstTrace.getSourceNo());
        } else if (!reverseTraces.isEmpty()) {
            DetDataTrace firstTrace = reverseTraces.get(0);
            rootNode = new DataTraceNodeVO();
            rootNode.setId(firstTrace.getTargetId());
            rootNode.setNodeType(firstTrace.getTargetType());
            rootNode.setNodeTypeName(getNodeTypeName(firstTrace.getTargetType()));
            rootNode.setNodeName(firstTrace.getTargetName());
            rootNode.setNodeNo(firstTrace.getTargetNo());
        } else {
            rootNode = new DataTraceNodeVO();
            rootNode.setId(sourceId);
            rootNode.setNodeType(traceType);
            rootNode.setNodeTypeName(getNodeTypeName(traceType));
        }

        List<DataTraceNodeVO> children = new ArrayList<>();
        for (DetDataTrace trace : forwardTraces) {
            DataTraceNodeVO childNode = buildNodeFromTrace(trace, true);
            if (childNode != null) {
                children.add(childNode);
            }
        }
        rootNode.setChildren(children);

        return rootNode;
    }

    private DataTraceNodeVO buildTaskNode(DetDetectionTask task) {
        DataTraceNodeVO taskNode = new DataTraceNodeVO();
        taskNode.setId(task.getId());
        taskNode.setNodeType(NODE_TYPE_TASK);
        taskNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_TASK));
        taskNode.setNodeName(task.getTaskName());
        taskNode.setNodeNo(task.getTaskNo());

        List<DetDataRecord> dataRecords = dataRecordMapper.selectList(
                new LambdaQueryWrapper<DetDataRecord>()
                        .eq(DetDataRecord::getTaskId, task.getId())
        );

        List<DataTraceNodeVO> dataRecordNodes = new ArrayList<>();
        for (DetDataRecord dataRecord : dataRecords) {
            DataTraceNodeVO dataRecordNode = buildDataRecordNode(dataRecord);
            dataRecordNode.setRelationType(RELATION_TYPE_GENERATE);
            dataRecordNode.setRelationDesc("任务生成检测数据");
            dataRecordNodes.add(dataRecordNode);
        }
        taskNode.setChildren(dataRecordNodes);

        return taskNode;
    }

    private DataTraceNodeVO buildDataRecordNode(DetDataRecord dataRecord) {
        DataTraceNodeVO dataRecordNode = new DataTraceNodeVO();
        dataRecordNode.setId(dataRecord.getId());
        dataRecordNode.setNodeType(NODE_TYPE_DATA_RECORD);
        dataRecordNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_DATA_RECORD));
        dataRecordNode.setNodeName(dataRecord.getRecordNo());
        dataRecordNode.setNodeNo(dataRecord.getRecordNo());

        List<DataTraceNodeVO> children = new ArrayList<>();

        List<DetOriginalRecord> originalRecords = originalRecordMapper.selectList(
                new LambdaQueryWrapper<DetOriginalRecord>()
                        .eq(DetOriginalRecord::getDataRecordId, dataRecord.getId())
        );

        for (DetOriginalRecord originalRecord : originalRecords) {
            DataTraceNodeVO originalRecordNode = new DataTraceNodeVO();
            originalRecordNode.setId(originalRecord.getId());
            originalRecordNode.setNodeType(NODE_TYPE_ORIGINAL_RECORD);
            originalRecordNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_ORIGINAL_RECORD));
            originalRecordNode.setNodeName(originalRecord.getTitle());
            originalRecordNode.setNodeNo(originalRecord.getRecordNo());
            originalRecordNode.setRelationType(RELATION_TYPE_GENERATE);
            originalRecordNode.setRelationDesc("检测数据生成原始记录");
            children.add(originalRecordNode);
        }

        if (StrUtil.isNotBlank(dataRecord.getEquipmentIds())) {
            try {
                List<Long> equipmentIds = objectMapper.readValue(dataRecord.getEquipmentIds(), List.class);
                List<String> equipmentNames = StrUtil.isNotBlank(dataRecord.getEquipmentNames())
                        ? objectMapper.readValue(dataRecord.getEquipmentNames(), List.class)
                        : Collections.emptyList();

                for (int i = 0; i < equipmentIds.size(); i++) {
                    Long equipmentId = equipmentIds.get(i);
                    SmpEquipment equipment = equipmentMapper.selectById(equipmentId);
                    if (equipment != null) {
                        DataTraceNodeVO equipmentNode = new DataTraceNodeVO();
                        equipmentNode.setId(equipment.getId());
                        equipmentNode.setNodeType(NODE_TYPE_EQUIPMENT);
                        equipmentNode.setNodeTypeName(getNodeTypeName(NODE_TYPE_EQUIPMENT));
                        equipmentNode.setNodeName(equipment.getEquipmentName());
                        equipmentNode.setNodeNo(equipment.getEquipmentNo());
                        equipmentNode.setRelationType(RELATION_TYPE_USE_FOR);
                        equipmentNode.setRelationDesc("使用该仪器检测");
                        children.add(equipmentNode);
                    }
                }
            } catch (JsonProcessingException e) {
                throw new BizException("解析设备ID列表失败");
            }
        }

        dataRecordNode.setChildren(children);
        return dataRecordNode;
    }

    private DataTraceNodeVO buildNodeFromTrace(DetDataTrace trace, boolean isForward) {
        DataTraceNodeVO node = new DataTraceNodeVO();
        if (isForward) {
            node.setId(trace.getTargetId());
            node.setNodeType(trace.getTargetType());
            node.setNodeTypeName(getNodeTypeName(trace.getTargetType()));
            node.setNodeName(trace.getTargetName());
            node.setNodeNo(trace.getTargetNo());
        } else {
            node.setId(trace.getSourceId());
            node.setNodeType(trace.getSourceType());
            node.setNodeTypeName(getNodeTypeName(trace.getSourceType()));
            node.setNodeName(trace.getSourceName());
            node.setNodeNo(trace.getSourceNo());
        }
        node.setRelationType(trace.getRelationType());
        node.setRelationDesc(trace.getRelationDesc());

        List<DetDataTrace> childrenTraces = getTraceRelations(
                isForward ? trace.getTargetType() : trace.getSourceType(),
                isForward ? trace.getTargetId() : trace.getSourceId()
        );

        List<DataTraceNodeVO> children = new ArrayList<>();
        for (DetDataTrace childTrace : childrenTraces) {
            DataTraceNodeVO childNode = buildNodeFromTrace(childTrace, true);
            if (childNode != null) {
                children.add(childNode);
            }
        }

        if (!children.isEmpty()) {
            node.setChildren(children);
        }

        return node;
    }
}
