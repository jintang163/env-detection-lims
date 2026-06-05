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

    private static final String PREFIX_CUSTOMER = "CUS";
    private static final String PREFIX_CONTRACT = "CON";
    private static final String PREFIX_ENTRUST = "ENT";
    private static final String PREFIX_QUOTATION = "QUO";
    private static final String PREFIX_REPORT = "RPT";
    private static final String PREFIX_CHANGE = "CHG";
    private static final String PREFIX_SUBCONTRACT = "SUB";

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

    private String generateCode(String prefix, AtomicInteger seq) {
        String datePart = DateUtil.format(new Date(), "yyyyMMdd");
        int sequence = seq.getAndIncrement();
        if (sequence > 9999) {
            seq.set(1);
            sequence = 1;
        }
        return String.format("%s%s%04d", prefix, datePart, sequence);
    }
}
