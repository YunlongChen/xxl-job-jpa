package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface XxlJobLogRepository extends JpaRepository<XxlJobLog, Long>, JpaSpecificationExecutor<XxlJobLog> {

    int deleteByJobId(int jobId);

    long countByIdIn(List<Long> ids);
}
