package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface XxlJobLogReportRepository extends JpaRepository<XxlJobLogReport, Integer>, JpaSpecificationExecutor<XxlJobLogReport> {

    Optional<XxlJobLogReport> findFirstByTriggerDay(Date triggerDay);

    List<XxlJobLogReport> findByTriggerDayBetweenOrderByTriggerDayAsc(Date triggerDayFrom, Date triggerDayTo);

    @Query("select coalesce(sum(r.runningCount),0), coalesce(sum(r.sucCount),0), coalesce(sum(r.failCount),0) from XxlJobLogReport r")
    Object[] queryLogReportTotalAgg();

    @Query("select r from XxlJobLogReport r where r.triggerDay between :from and :to order by r.triggerDay asc")
    List<XxlJobLogReport> queryLogReport(@Param("from") Date triggerDayFrom, @Param("to") Date triggerDayTo);
}

