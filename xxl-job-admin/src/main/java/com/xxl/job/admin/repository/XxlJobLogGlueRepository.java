package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLogGlue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface XxlJobLogGlueRepository extends JpaRepository<XxlJobLogGlue, Integer>, JpaSpecificationExecutor<XxlJobLogGlue> {

    List<XxlJobLogGlue> findByJobIdOrderByIdDesc(int jobId);

    List<XxlJobLogGlue> findByJobIdOrderByUpdateTimeDesc(int jobId, Pageable pageable);

    List<XxlJobLogGlue> findByJobId(int jobId);

    int deleteByJobId(int jobId);
}
