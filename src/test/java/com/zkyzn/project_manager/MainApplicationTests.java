package com.zkyzn.project_manager;

import com.zkyzn.project_manager.crons.DelayNoticeCron;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainApplicationTests {

    @Resource
    private DelayNoticeCron delayNoticeCron;

    @Test
    void contextLoads() {
    }

    @Test
    void delayNoticeCron() {
        delayNoticeCron.checkAndSendDelayNotices();
    }
}
