package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface XxlJobGroupRepository extends JpaRepository<XxlJobGroup, Integer>, JpaSpecificationExecutor<XxlJobGroup> {

    List<XxlJobGroup> findAllByOrderByAppnameAscTitleAscIdAsc();

    List<XxlJobGroup> findByAddressTypeOrderByAppnameAscTitleAscIdAsc(int addressType);
}

