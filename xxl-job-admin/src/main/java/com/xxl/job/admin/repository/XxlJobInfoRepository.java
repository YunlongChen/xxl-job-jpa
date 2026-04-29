package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface XxlJobInfoRepository extends JpaRepository<XxlJobInfo, Integer>, JpaSpecificationExecutor<XxlJobInfo> {

    List<XxlJobInfo> findByJobGroup(int jobGroup);

    long countBy();

    @Query("from XxlJobInfo i where i.triggerStatus = 1 and i.triggerNextTime <= :maxNextTime order by i.id asc")
    List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, Pageable pageable);

    @Modifying
    @Query("update XxlJobInfo i set i.triggerLastTime = :lastTime, i.triggerNextTime = :nextTime, i.triggerStatus = :status where i.id = :id and i.triggerStatus = 1")
    int scheduleUpdateWithStatus(@Param("id") int id, @Param("lastTime") long lastTime, @Param("nextTime") long nextTime, @Param("status") int status);

    @Modifying
    @Query("update XxlJobInfo i set i.triggerLastTime = :lastTime, i.triggerNextTime = :nextTime where i.id = :id and i.triggerStatus = 1")
    int scheduleUpdateNoStatus(@Param("id") int id, @Param("lastTime") long lastTime, @Param("nextTime") long nextTime);
}

