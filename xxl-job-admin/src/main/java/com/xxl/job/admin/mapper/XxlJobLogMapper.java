package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * job log
 * @author xuxueli 2016-1-12 18:03:06
 */
public interface XxlJobLogMapper {

	// exist jobId not use jobGroup, not exist use jobGroup
	List<XxlJobLog> pageList(int offset,
							 int pagesize,
							 int jobGroup,
							 int jobId,
							 Date triggerTimeStart,
							 Date triggerTimeEnd,
							 int logStatus);
	int pageListCount(int offset,
					  int pagesize,
					  int jobGroup,
					  int jobId,
					  Date triggerTimeStart,
					  Date triggerTimeEnd,
					  int logStatus);
	
	XxlJobLog load(long id);

	long save(XxlJobLog xxlJobLog);

	int updateTriggerInfo(XxlJobLog xxlJobLog);

	int updateHandleInfo(XxlJobLog xxlJobLog);
	
	int delete(int jobId);

	Map<String, Object> findLogReport(Date from,
									  Date to);

	List<Long> findClearLogIds(int jobGroup,
							   int jobId,
							   Date clearBeforeTime,
							   int clearBeforeNum,
							   int pagesize);
	int clearLog(List<Long> logIds);

	List<Long> findFailJobLogIds(int pagesize);

	int updateAlarmStatus(long logId,
						  int oldAlarmStatus,
						  int newAlarmStatus);

	List<Long> findLostJobIds(Date losedTime);

}
