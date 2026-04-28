package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobInfo;

import java.util.List;


/**
 * job info
 * @author xuxueli 2016-1-12 18:03:45
 */
public interface XxlJobInfoMapper {

	List<XxlJobInfo> pageList(int offset,
							  int pagesize,
							  int jobGroup,
							  int triggerStatus,
							  String jobDesc,
							  String executorHandler,
							  String author);
	int pageListCount(int offset,
					  int pagesize,
					  int jobGroup,
					  int triggerStatus,
					  String jobDesc,
					  String executorHandler,
					  String author);
	
	int save(XxlJobInfo info);

	XxlJobInfo loadById(int id);
	
	int update(XxlJobInfo xxlJobInfo);
	
	int delete(long id);

	List<XxlJobInfo> getJobsByGroup(int jobGroup);

	int findAllCount();

	/**
	 * find schedule job, limit "trigger_status = 1"
	 *
	 * @param maxNextTime
	 * @param pagesize
	 * @return
	 */
	List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize );

	/**
	 * update schedule job
	 *
	 * 	1、can only update "trigger_status = 1", Avoid stopping tasks from being opened
	 * 	2、valid "triggerStatus gte 0", filter illegal state
	 *
	 * @param xxlJobInfo
	 * @return
	 */
	int scheduleUpdate(XxlJobInfo xxlJobInfo);

	/**
	 * batch update job info
	 *
	 * @param jobInfoList
	 * @return
	 */
	int scheduleBatchUpdate(List<XxlJobInfo> jobInfoList);

}
