package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface XxlJobLockRepository extends JpaRepository<XxlJobLock, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from XxlJobLock l where l.lockName = :lockName")
    Optional<XxlJobLock> findForUpdate(@Param("lockName") String lockName);
}

