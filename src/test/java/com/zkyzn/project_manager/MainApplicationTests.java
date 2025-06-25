package com.zkyzn.project_manager;

import com.zkyzn.project_manager.crons.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainApplicationTests {

    @Resource
    private DelayNoticeCron delayNoticeCron;

    @Resource
    private DueDateNoticeCron dueDateNoticeCron;

    @Resource
    private WeeklyReportCron weeklyReportCron;

    @Resource
    private MonthlyReportCron monthlyReportCron;

    @Resource
    private AnnualReportCron annualReportCron;

    @Test
    void contextLoads() {
    }

//    @Test
//    void delayNoticeCron() {
//        delayNoticeCron.checkAndSendDelayNotices();
//    }
//
//    @Test
//    void dueDateNoticeCron() {
//        dueDateNoticeCron.checkAndSendDueDateNotices();
//    }
//
//    @Test
//    void weeklyReportCron() {
//        weeklyReportCron.sendWeeklyReports();
//    }
//
//    @Test
//    void monthlyReportCron() {
//        monthlyReportCron.sendMonthlyReports();
//    }
//
//    @Test
//    void annualReportCron() {
//        annualReportCron.sendAnnualReports();
//    }
}
