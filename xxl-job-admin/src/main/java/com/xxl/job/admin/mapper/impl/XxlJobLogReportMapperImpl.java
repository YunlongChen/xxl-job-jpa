package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogReportMapper;
import com.xxl.job.admin.model.XxlJobLogReport;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class XxlJobLogReportMapperImpl implements XxlJobLogReportMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int saveOrUpdate(XxlJobLogReport xxlJobLogReport) {
        List<XxlJobLogReport> list = entityManager
                .createQuery("from XxlJobLogReport r where r.triggerDay = :triggerDay", XxlJobLogReport.class)
                .setParameter("triggerDay", xxlJobLogReport.getTriggerDay())
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) {
            entityManager.persist(xxlJobLogReport);
        } else {
            XxlJobLogReport exist = list.getFirst();
            exist.setRunningCount(xxlJobLogReport.getRunningCount());
            exist.setSucCount(xxlJobLogReport.getSucCount());
            exist.setFailCount(xxlJobLogReport.getFailCount());
            exist.setUpdateTime(xxlJobLogReport.getUpdateTime());
        }
        return 1;
    }

    @Override
    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        return entityManager
                .createQuery(
                        "from XxlJobLogReport r where r.triggerDay between :from and :to order by r.triggerDay asc",
                        XxlJobLogReport.class
                )
                .setParameter("from", triggerDayFrom)
                .setParameter("to", triggerDayTo)
                .getResultList();
    }

    @Override
    public XxlJobLogReport queryLogReportTotal() {
        Object[] row = entityManager
                .createQuery(
                        "select coalesce(sum(r.runningCount),0), coalesce(sum(r.sucCount),0), coalesce(sum(r.failCount),0) from XxlJobLogReport r",
                        Object[].class
                )
                .getSingleResult();

        XxlJobLogReport report = new XxlJobLogReport();
        report.setRunningCount(((Number) row[0]).intValue());
        report.setSucCount(((Number) row[1]).intValue());
        report.setFailCount(((Number) row[2]).intValue());
        return report;
    }
}

