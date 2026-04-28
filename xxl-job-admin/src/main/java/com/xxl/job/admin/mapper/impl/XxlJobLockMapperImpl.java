package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLockMapper;
import com.xxl.job.admin.model.XxlJobLock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class XxlJobLockMapperImpl implements XxlJobLockMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public String scheduleLock() {
        XxlJobLock lock = entityManager.find(XxlJobLock.class, "schedule_lock", LockModeType.PESSIMISTIC_WRITE);
        return lock == null ? null : lock.getLockName();
    }
}

