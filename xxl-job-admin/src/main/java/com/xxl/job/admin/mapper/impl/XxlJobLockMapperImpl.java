package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLockMapper;
import com.xxl.job.admin.repository.XxlJobLockRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class XxlJobLockMapperImpl implements XxlJobLockMapper {

    private final XxlJobLockRepository xxlJobLockRepository;

    public XxlJobLockMapperImpl(XxlJobLockRepository xxlJobLockRepository) {
        this.xxlJobLockRepository = xxlJobLockRepository;
    }

    @Override
    @Transactional
    public String scheduleLock() {
        return xxlJobLockRepository.findByLockName("schedule_lock").map(it -> it.getLockName()).orElse(null);
    }
}
