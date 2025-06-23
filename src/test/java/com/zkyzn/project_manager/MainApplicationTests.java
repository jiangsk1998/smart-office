package com.zkyzn.project_manager;

import com.zkyzn.project_manager.crons.DelayNoticeCron;
import com.zkyzn.project_manager.crons.DueDateNoticeCron;
import com.zkyzn.project_manager.crons.MonthlyReportCron;
import com.zkyzn.project_manager.crons.WeeklyReportCron;
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

    @Test
    void contextLoads() {
    }

    @Test
    void delayNoticeCron() {
        delayNoticeCron.checkAndSendDelayNotices();
    }

    @Test
    void dueDateNoticeCron() {
        dueDateNoticeCron.checkAndSendDueDateNotices();
    }

    @Test
    void weeklyReportCron() {
        weeklyReportCron.sendWeeklyReports();
    }

    @Test
    void monthlyReportCron() {
        monthlyReportCron.sendMonthlyReports();
    }
}
