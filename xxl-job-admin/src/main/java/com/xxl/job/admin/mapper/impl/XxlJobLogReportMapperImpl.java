package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogReportMapper;
import com.xxl.job.admin.model.XxlJobLogReport;
import com.xxl.job.admin.repository.XxlJobLogReportRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class XxlJobLogReportMapperImpl implements XxlJobLogReportMapper {

    private final XxlJobLogReportRepository xxlJobLogReportRepository;

    public XxlJobLogReportMapperImpl(XxlJobLogReportRepository xxlJobLogReportRepository) {
        this.xxlJobLogReportRepository = xxlJobLogReportRepository;
    }

    @Override
    @Transactional
    public int saveOrUpdate(XxlJobLogReport xxlJobLogReport) {
        XxlJobLogReport exist = xxlJobLogReportRepository.findFirstByTriggerDay(xxlJobLogReport.getTriggerDay()).orElse(null);
        if (exist == null) {
            xxlJobLogReportRepository.save(xxlJobLogReport);
            return 1;
        }
        exist.setRunningCount(xxlJobLogReport.getRunningCount());
        exist.setSucCount(xxlJobLogReport.getSucCount());
        exist.setFailCount(xxlJobLogReport.getFailCount());
        exist.setUpdateTime(xxlJobLogReport.getUpdateTime());
        xxlJobLogReportRepository.save(exist);
        return 1;
    }

    @Override
    public List<XxlJobLogReport> queryLogReport(Date triggerDayFrom, Date triggerDayTo) {
        return xxlJobLogReportRepository.findByTriggerDayBetweenOrderByTriggerDayAsc(triggerDayFrom, triggerDayTo);
    }

    @Override
    public XxlJobLogReport queryLogReportTotal() {
        Object[] row = xxlJobLogReportRepository.queryLogReportTotalAgg();

        XxlJobLogReport report = new XxlJobLogReport();
        report.setRunningCount(((Number) row[0]).intValue());
        report.setSucCount(((Number) row[1]).intValue());
        report.setFailCount(((Number) row[2]).intValue());
        return report;
    }
}
