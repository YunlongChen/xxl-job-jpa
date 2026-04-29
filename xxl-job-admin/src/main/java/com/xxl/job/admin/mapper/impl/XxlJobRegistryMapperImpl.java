package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.model.XxlJobRegistry;
import com.xxl.job.admin.repository.XxlJobRegistryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class XxlJobRegistryMapperImpl implements XxlJobRegistryMapper {

    private final XxlJobRegistryRepository xxlJobRegistryRepository;

    public XxlJobRegistryMapperImpl(XxlJobRegistryRepository xxlJobRegistryRepository) {
        this.xxlJobRegistryRepository = xxlJobRegistryRepository;
    }

    @Override
    public List<Integer> findDead(int timeout, Date nowTime) {
        Date threshold = new Date(nowTime.getTime() - timeout * 1000L);
        return xxlJobRegistryRepository.findByUpdateTimeBefore(threshold).stream().map(it -> (int) it.getId()).toList();
    }

    @Override
    @Transactional
    public int removeDead(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> longIds = ids.stream().map(Integer::longValue).toList();
        xxlJobRegistryRepository.deleteAllByIdInBatch(longIds);
        return longIds.size();
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        Date threshold = new Date(nowTime.getTime() - timeout * 1000L);
        return xxlJobRegistryRepository.findByUpdateTimeAfter(threshold);
    }

    @Override
    @Transactional
    public int registrySaveOrUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        XxlJobRegistry exist = xxlJobRegistryRepository.findFirstByRegistryGroupAndRegistryKeyAndRegistryValue(registryGroup, registryKey, registryValue).orElse(null);
        if (exist != null) {
            exist.setUpdateTime(updateTime);
            xxlJobRegistryRepository.save(exist);
            return 1;
        }

        XxlJobRegistry registry = new XxlJobRegistry();
        registry.setRegistryGroup(registryGroup);
        registry.setRegistryKey(registryKey);
        registry.setRegistryValue(registryValue);
        registry.setUpdateTime(updateTime);
        try {
            xxlJobRegistryRepository.saveAndFlush(registry);
        } catch (DataIntegrityViolationException e) {
            XxlJobRegistry concurrent = xxlJobRegistryRepository.findFirstByRegistryGroupAndRegistryKeyAndRegistryValue(registryGroup, registryKey, registryValue).orElse(null);
            if (concurrent == null) {
                return 0;
            }
            concurrent.setUpdateTime(updateTime);
            xxlJobRegistryRepository.save(concurrent);
            return 1;
        }
        return 1;
    }

    @Override
    @Transactional
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        return xxlJobRegistryRepository.deleteByRegistryGroupAndRegistryKeyAndRegistryValue(registryGroup, registryKey, registryValue);
    }

    @Override
    @Transactional
    public int removeByRegistryGroupAndKey(String registryGroup, String registryKey) {
        return xxlJobRegistryRepository.deleteByRegistryGroupAndRegistryKey(registryGroup, registryKey);
    }
}
