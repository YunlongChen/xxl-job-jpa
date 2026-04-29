package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLogReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface XxlJobLogReportRepository extends JpaRepository<XxlJobLogReport, Integer>, JpaSpecificationExecutor<XxlJobLogReport> {

    Optional<XxlJobLogReport> findFirstByTriggerDay(Date triggerDay);

    List<XxlJobLogReport> findByTriggerDayBetweenOrderByTriggerDayAsc(Date triggerDayFrom, Date triggerDayTo);
}
