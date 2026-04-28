package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobLogReport;

import java.util.Date;
import java.util.List;

/**
 * job log
 * @author xuxueli 2019-11-22
 */
public interface XxlJobLogReportMapper {

	/*public int save(XxlJobLogReport xxlJobLogReport);

	public int update(XxlJobLogReport xxlJobLogReport);*/

	int saveOrUpdate(XxlJobLogReport xxlJobLogReport);

	List<XxlJobLogReport> queryLogReport(Date triggerDayFrom,
										 Date triggerDayTo);

	XxlJobLogReport queryLogReportTotal();

}
