package com.xxl.job.admin.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jakarta.annotation.Resource;

import com.xxl.job.admin.mapper.XxlJobLockMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JobScheduleTest {
    @Resource
    private PlatformTransactionManager transactionManager;

    @Resource
    private XxlJobLockMapper xxlJobLockMapper;

    @Test
    public void testLock() {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            String lockedRecord = xxlJobLockMapper.scheduleLock();
            assertNotNull(lockedRecord);
        } finally {
            transactionManager.commit(transactionStatus);
        }
    }
}
