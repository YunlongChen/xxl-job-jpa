package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface XxlJobRegistryRepository extends JpaRepository<XxlJobRegistry, Long>, JpaSpecificationExecutor<XxlJobRegistry> {

    List<XxlJobRegistry> findByUpdateTimeBefore(Date threshold);

    List<XxlJobRegistry> findByUpdateTimeAfter(Date threshold);

    Optional<XxlJobRegistry> findFirstByRegistryGroupAndRegistryKeyAndRegistryValue(String registryGroup, String registryKey, String registryValue);

    int deleteByRegistryGroupAndRegistryKeyAndRegistryValue(String registryGroup, String registryKey, String registryValue);

    int deleteByRegistryGroupAndRegistryKey(String registryGroup, String registryKey);

    List<XxlJobRegistry> findByRegistryValueIn(List<String> registryValues);

    boolean existsByRegistryValue(String registryValue);
}
