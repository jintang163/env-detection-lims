package com.lims.equipment.task;

import com.lims.equipment.service.CalibrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CalibrationReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(CalibrationReminderTask.class);

    @Autowired
    private CalibrationService calibrationService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkCalibrationStatus() {
        logger.info("开始执行校准到期状态检查任务");
        try {
            calibrationService.checkAndUpdatePlanStatus();
            logger.info("校准到期状态检查任务执行完成");
        } catch (Exception e) {
            logger.error("校准到期状态检查任务执行失败", e);
        }
    }
}
