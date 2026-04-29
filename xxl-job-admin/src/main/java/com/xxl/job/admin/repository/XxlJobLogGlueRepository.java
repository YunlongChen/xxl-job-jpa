package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLogGlue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface XxlJobLogGlueRepository extends JpaRepository<XxlJobLogGlue, Integer>, JpaSpecificationExecutor<XxlJobLogGlue> {

    List<XxlJobLogGlue> findByJobIdOrderByIdDesc(int jobId);

    @Query("select g.id from XxlJobLogGlue g where g.jobId = :jobId order by g.updateTime desc")
    List<Integer> findKeepIds(@Param("jobId") int jobId, Pageable pageable);

    @Modifying
    @Query("delete from XxlJobLogGlue g where g.jobId = :jobId and g.id not in :keepIds")
    int deleteOld(@Param("jobId") int jobId, @Param("keepIds") List<Integer> keepIds);

    @Modifying
    @Query("delete from XxlJobLogGlue g where g.jobId = :jobId")
    int deleteByJobId(@Param("jobId") int jobId);
}

