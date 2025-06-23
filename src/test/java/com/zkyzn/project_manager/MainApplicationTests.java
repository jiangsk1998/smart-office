package com.zkyzn.project_manager;

import com.zkyzn.project_manager.crons.DelayNoticeCron;
import com.zkyzn.project_manager.crons.DueDateNoticeCron;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainApplicationTests {

    @Resource
    private DelayNoticeCron delayNoticeCron;

    @Resource
    private DueDateNoticeCron dueDateNoticeCron;

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
}
