package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface XxlJobRegistryRepository extends JpaRepository<XxlJobRegistry, Long>, JpaSpecificationExecutor<XxlJobRegistry> {

    @Query("select r.id from XxlJobRegistry r where r.updateTime < :threshold")
    List<Long> findDeadIds(@Param("threshold") Date threshold);

    @Modifying
    @Query("delete from XxlJobRegistry r where r.id in :ids")
    int deleteByIds(@Param("ids") List<Long> ids);

    @Query("from XxlJobRegistry r where r.updateTime > :threshold")
    List<XxlJobRegistry> findAlive(@Param("threshold") Date threshold);

    @Modifying
    @Query(
            "update XxlJobRegistry r set r.updateTime = :updateTime " +
                    "where r.registryGroup = :registryGroup and r.registryKey = :registryKey and r.registryValue = :registryValue"
    )
    int updateTime(@Param("registryGroup") String registryGroup,
                   @Param("registryKey") String registryKey,
                   @Param("registryValue") String registryValue,
                   @Param("updateTime") Date updateTime);

    @Modifying
    @Query(
            "delete from XxlJobRegistry r " +
                    "where r.registryGroup = :registryGroup and r.registryKey = :registryKey and r.registryValue = :registryValue"
    )
    int deleteOne(@Param("registryGroup") String registryGroup,
                  @Param("registryKey") String registryKey,
                  @Param("registryValue") String registryValue);

    @Modifying
    @Query("delete from XxlJobRegistry r where r.registryGroup = :registryGroup and r.registryKey = :registryKey")
    int deleteByGroupAndKey(@Param("registryGroup") String registryGroup, @Param("registryKey") String registryKey);
}

