package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface XxlJobLockRepository extends JpaRepository<XxlJobLock, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<XxlJobLock> findByLockName(String lockName);
}
