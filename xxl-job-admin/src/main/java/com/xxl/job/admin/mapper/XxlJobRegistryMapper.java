package com.xxl.job.admin.mapper;

import com.xxl.job.admin.model.XxlJobRegistry;

import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
public interface XxlJobRegistryMapper {

    List<Integer> findDead(int timeout,
                           Date nowTime);

    int removeDead(List<Integer> ids);

    List<XxlJobRegistry> findAll(int timeout,
                                 Date nowTime);

    int registrySaveOrUpdate(String registryGroup,
                             String registryKey,
                             String registryValue,
                             Date updateTime);

    /*public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("updateTime") Date updateTime);

    public int registrySave(@Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue,
                            @Param("updateTime") Date updateTime);*/

    int registryDelete(String registryGroup,
                       String registryKey,
                       String registryValue);

    int removeByRegistryGroupAndKey(String registryGroup,
                                    String registryKey);

}
