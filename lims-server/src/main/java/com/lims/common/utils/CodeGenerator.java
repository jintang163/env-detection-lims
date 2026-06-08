package com.lims.common.utils;

import cn.hutool.core.date.DateUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class CodeGenerator {

    private static final AtomicInteger customerSeq = new AtomicInteger(1);
    private static final AtomicInteger contractSeq = new AtomicInteger(1);
    private static final AtomicInteger entrustSeq = new AtomicInteger(1);
    private static final AtomicInteger quotationSeq = new AtomicInteger(1);
    private static final AtomicInteger reportSeq = new AtomicInteger(1);
    private static final AtomicInteger changeSeq = new AtomicInteger(1);
    private static final AtomicInteger subcontractSeq = new AtomicInteger(1);
    private static final AtomicInteger samplingPlanSeq = new AtomicInteger(1);
    private static final AtomicInteger samplingTaskSeq = new AtomicInteger(1);
    private static final AtomicInteger sampleSeq = new AtomicInteger(1);
    private static final AtomicInteger transferSeq = new AtomicInteger(1);
    private static final AtomicInteger equipmentSeq = new AtomicInteger(1);
    private static final AtomicInteger borrowSeq = new AtomicInteger(1);
    private static final AtomicInteger detectionTaskSeq = new AtomicInteger(1);
    private static final AtomicInteger standardMethodSeq = new AtomicInteger(1);
    private static final AtomicInteger schedulePlanSeq = new AtomicInteger(1);
    private static final AtomicInteger userQualificationSeq = new AtomicInteger(1);
    private static final AtomicInteger dataRecordSeq = new AtomicInteger(1);
    private static final AtomicInteger originalRecordSeq = new AtomicInteger(1);
    private static final AtomicInteger oosRecordSeq = new AtomicInteger(1);
    private static final AtomicInteger trainingPlanSeq = new AtomicInteger(1);
    private static final AtomicInteger employeeSeq = new AtomicInteger(1);

    private static final String PREFIX_CUSTOMER = "CUS";
    private static final String PREFIX_CONTRACT = "CON";
    private static final String PREFIX_ENTRUST = "ENT";
    private static final String PREFIX_QUOTATION = "QUO";
    private static final String PREFIX_REPORT = "RPT";
    private static final String PREFIX_CHANGE = "CHG";
    private static final String PREFIX_SUBCONTRACT = "SUB";
    private static final String PREFIX_SAMPLING_PLAN = "SP";
    private static final String PREFIX_SAMPLING_TASK = "ST";
    private static final String PREFIX_SAMPLE = "SPL";
    private static final String PREFIX_TRANSFER = "TRF";
    private static final String PREFIX_EQUIPMENT = "EQ";
    private static final String PREFIX_BORROW = "BRW";
    private static final String PREFIX_DETECTION_TASK = "DT";
    private static final String PREFIX_STANDARD_METHOD = "SM";
    private static final String PREFIX_SCHEDULE_PLAN = "SPL";
    private static final String PREFIX_USER_QUALIFICATION = "UQ";
    private static final String PREFIX_DATA_RECORD = "DR";
    private static final String PREFIX_ORIGINAL_RECORD = "OR";
    private static final String PREFIX_OOS_RECORD = "OOS";
    private static final String PREFIX_TRAINING_PLAN = "TP";
    private static final String PREFIX_EMPLOYEE = "EMP";

    public String generateCustomerNo() {
        return generateCode(PREFIX_CUSTOMER, customerSeq);
    }

    public String generateContractNo() {
        return generateCode(PREFIX_CONTRACT, contractSeq);
    }

    public String generateEntrustNo() {
        return generateCode(PREFIX_ENTRUST, entrustSeq);
    }

    public String generateQuotationNo() {
        return generateCode(PREFIX_QUOTATION, quotationSeq);
    }

    public String generateReportNo() {
        return generateCode(PREFIX_REPORT, reportSeq);
    }

    public String generateChangeNo() {
        return generateCode(PREFIX_CHANGE, changeSeq);
    }

    public String generateSubcontractNo() {
        return generateCode(PREFIX_SUBCONTRACT, subcontractSeq);
    }

    public String generateSamplingPlanNo() {
        return generateCode(PREFIX_SAMPLING_PLAN, samplingPlanSeq);
    }

    public String generateSamplingTaskNo() {
        return generateCode(PREFIX_SAMPLING_TASK, samplingTaskSeq);
    }

    public String generateSampleNo() {
        return generateCode(PREFIX_SAMPLE, sampleSeq);
    }

    public String generateTransferNo() {
        return generateCode(PREFIX_TRANSFER, transferSeq);
    }

    public String generateEquipmentNo() {
        return generateCode(PREFIX_EQUIPMENT, equipmentSeq);
    }

    public String generateBorrowNo() {
        return generateCode(PREFIX_BORROW, borrowSeq);
    }

    public String generateDetectionTaskNo() {
        return generateCode(PREFIX_DETECTION_TASK, detectionTaskSeq);
    }

    public String generateStandardMethodNo() {
        return generateCode(PREFIX_STANDARD_METHOD, standardMethodSeq);
    }

    public String generateSchedulePlanNo() {
        return generateCode(PREFIX_SCHEDULE_PLAN, schedulePlanSeq);
    }

    public String generateUserQualificationNo() {
        return generateCode(PREFIX_USER_QUALIFICATION, userQualificationSeq);
    }

    public String generateDataRecordNo() {
        return generateCode(PREFIX_DATA_RECORD, dataRecordSeq);
    }

    public String generateOriginalRecordNo() {
        return generateCode(PREFIX_ORIGINAL_RECORD, originalRecordSeq);
    }

    public String generateOosRecordNo() {
        return generateCode(PREFIX_OOS_RECORD, oosRecordSeq);
    }

    public String generateTrainingPlanNo() {
        return generateTrainingPlanCode(PREFIX_TRAINING_PLAN, trainingPlanSeq);
    }

    public String generateEmployeeNo() {
        return generateCode(PREFIX_EMPLOYEE, employeeSeq);
    }

    private String generateCode(String prefix, AtomicInteger seq) {
        String datePart = DateUtil.format(new Date(), "yyyyMMdd");
        int sequence = seq.getAndIncrement();
        if (sequence > 9999) {
            seq.set(1);
            sequence = 1;
        }
        return String.format("%s%s%04d", prefix, datePart, sequence);
    }

    private String generateTrainingPlanCode(String prefix, AtomicInteger seq) {
        String yearPart = DateUtil.format(new Date(), "yyyy");
        int sequence = seq.getAndIncrement();
        if (sequence > 9999) {
            seq.set(1);
            sequence = 1;
        }
        return String.format("%s%s%04d", prefix, yearPart, sequence);
    }
}
