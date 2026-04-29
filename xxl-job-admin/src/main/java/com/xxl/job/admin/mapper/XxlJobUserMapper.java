package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobUser;
import java.util.List;

/**
 * @author xuxueli 2019-05-04 16:44:59
 */
public interface XxlJobUserMapper {

	List<XxlJobUser> pageList(int offset,
							  int pagesize,
							  String username,
							  int role);
	int pageListCount(int offset,
					  int pagesize,
					  String username,
					  int role);

	XxlJobUser loadByUserName(String username);

	XxlJobUser loadById(int id);

	int save(XxlJobUser xxlJobUser);

	int update(XxlJobUser xxlJobUser);
	
	int delete(int id);

	int updateToken(int id, String token);

}
