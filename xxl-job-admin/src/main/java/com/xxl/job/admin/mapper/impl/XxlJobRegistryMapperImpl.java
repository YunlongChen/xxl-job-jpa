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
        List<Long> ids = xxlJobRegistryRepository.findDeadIds(threshold);
        return ids.stream().map(Long::intValue).toList();
    }

    @Override
    @Transactional
    public int removeDead(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> longIds = ids.stream().map(Integer::longValue).toList();
        return xxlJobRegistryRepository.deleteByIds(longIds);
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        Date threshold = new Date(nowTime.getTime() - timeout * 1000L);
        return xxlJobRegistryRepository.findAlive(threshold);
    }

    @Override
    @Transactional
    public int registrySaveOrUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        int affected = xxlJobRegistryRepository.updateTime(registryGroup, registryKey, registryValue, updateTime);

        if (affected > 0) {
            return affected;
        }

        XxlJobRegistry registry = new XxlJobRegistry();
        registry.setRegistryGroup(registryGroup);
        registry.setRegistryKey(registryKey);
        registry.setRegistryValue(registryValue);
        registry.setUpdateTime(updateTime);
        try {
            xxlJobRegistryRepository.saveAndFlush(registry);
        } catch (DataIntegrityViolationException e) {
            return xxlJobRegistryRepository.updateTime(registryGroup, registryKey, registryValue, updateTime);
        }
        return 1;
    }

    @Override
    @Transactional
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        return xxlJobRegistryRepository.deleteOne(registryGroup, registryKey, registryValue);
    }

    @Override
    @Transactional
    public int removeByRegistryGroupAndKey(String registryGroup, String registryKey) {
        return xxlJobRegistryRepository.deleteByGroupAndKey(registryGroup, registryKey);
    }
}
