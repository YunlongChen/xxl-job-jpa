package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobGroup;

import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface XxlJobGroupMapper {

    List<XxlJobGroup> findAll();

    List<XxlJobGroup> findByAddressType(int addressType);

    int save(XxlJobGroup xxlJobGroup);

    int update(XxlJobGroup xxlJobGroup);

    int remove(int id);

    XxlJobGroup load(int id);

    List<XxlJobGroup> pageList(int offset,
                               int pagesize,
                               String appname,
                               String title);

    int pageListCount(int offset,
                      int pagesize,
                      String appname,
                      String title);

}
