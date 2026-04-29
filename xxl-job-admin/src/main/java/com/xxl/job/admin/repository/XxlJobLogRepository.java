package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface XxlJobLogRepository extends JpaRepository<XxlJobLog, Long>, JpaSpecificationExecutor<XxlJobLog> {

    @Modifying
    @Query("delete from XxlJobLog l where l.jobId = :jobId")
    int deleteByJobId(@Param("jobId") int jobId);

    @Modifying
    @Query("delete from XxlJobLog l where l.id in :ids")
    int deleteByIds(@Param("ids") List<Long> ids);

    @Query(
            "select count(l.handleCode), " +
                    "coalesce(sum(case when (l.triggerCode in (0,200) and l.handleCode = 0) then 1 else 0 end),0), " +
                    "coalesce(sum(case when l.handleCode = 200 then 1 else 0 end),0) " +
                    "from XxlJobLog l where l.triggerTime between :from and :to"
    )
    Object[] findLogReportAgg(@Param("from") Date from, @Param("to") Date to);

    @Query(
            "select l.id from XxlJobLog l " +
                    "where not ( (l.triggerCode in (0,200) and l.handleCode = 0) or (l.handleCode = 200) ) " +
                    "and l.alarmStatus = 0 " +
                    "order by l.id asc"
    )
    List<Long> findFailJobLogIds(Pageable pageable);

    @Modifying
    @Query("update XxlJobLog l set l.alarmStatus = :newAlarmStatus where l.id = :logId and l.alarmStatus = :oldAlarmStatus")
    int updateAlarmStatus(@Param("logId") long logId, @Param("oldAlarmStatus") int oldAlarmStatus, @Param("newAlarmStatus") int newAlarmStatus);

    @Query(
            "select l.id from XxlJobLog l " +
                    "where l.triggerCode = 200 and l.handleCode = 0 and l.triggerTime <= :losedTime " +
                    "and not exists (select 1 from XxlJobRegistry r where r.registryValue = l.executorAddress)"
    )
    List<Long> findLostJobIds(@Param("losedTime") Date losedTime);
}
