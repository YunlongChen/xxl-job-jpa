package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.List;

public interface XxlJobInfoRepository extends JpaRepository<XxlJobInfo, Integer>, JpaSpecificationExecutor<XxlJobInfo> {

    List<XxlJobInfo> findByJobGroup(int jobGroup);

    long countBy();

    List<XxlJobInfo> findByTriggerStatusAndTriggerNextTimeLessThanEqualOrderByIdAsc(int triggerStatus, long maxNextTime, Pageable pageable);

}
