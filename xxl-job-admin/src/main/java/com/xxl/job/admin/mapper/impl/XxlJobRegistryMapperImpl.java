package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobRegistryMapper;
import com.xxl.job.admin.model.XxlJobRegistry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public class XxlJobRegistryMapperImpl implements XxlJobRegistryMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Integer> findDead(int timeout, Date nowTime) {
        Date threshold = new Date(nowTime.getTime() - timeout * 1000L);
        List<Long> ids = entityManager
                .createQuery("select r.id from XxlJobRegistry r where r.updateTime < :threshold", Long.class)
                .setParameter("threshold", threshold)
                .getResultList();
        return ids.stream().map(Long::intValue).toList();
    }

    @Override
    @Transactional
    public int removeDead(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        List<Long> longIds = ids.stream().map(Integer::longValue).toList();
        return entityManager.createQuery("delete from XxlJobRegistry r where r.id in :ids")
                .setParameter("ids", longIds)
                .executeUpdate();
    }

    @Override
    public List<XxlJobRegistry> findAll(int timeout, Date nowTime) {
        Date threshold = new Date(nowTime.getTime() - timeout * 1000L);
        return entityManager
                .createQuery("from XxlJobRegistry r where r.updateTime > :threshold", XxlJobRegistry.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }

    @Override
    @Transactional
    public int registrySaveOrUpdate(String registryGroup, String registryKey, String registryValue, Date updateTime) {
        List<XxlJobRegistry> list = entityManager
                .createQuery(
                        "from XxlJobRegistry r where r.registryGroup = :registryGroup and r.registryKey = :registryKey and r.registryValue = :registryValue",
                        XxlJobRegistry.class
                )
                .setParameter("registryGroup", registryGroup)
                .setParameter("registryKey", registryKey)
                .setParameter("registryValue", registryValue)
                .setMaxResults(1)
                .getResultList();

        XxlJobRegistry registry;
        if (list.isEmpty()) {
            registry = new XxlJobRegistry();
            registry.setRegistryGroup(registryGroup);
            registry.setRegistryKey(registryKey);
            registry.setRegistryValue(registryValue);
            registry.setUpdateTime(updateTime);
            entityManager.persist(registry);
        } else {
            registry = list.getFirst();
            registry.setUpdateTime(updateTime);
        }
        return 1;
    }

    @Override
    @Transactional
    public int registryDelete(String registryGroup, String registryKey, String registryValue) {
        return entityManager.createQuery(
                        "delete from XxlJobRegistry r where r.registryGroup = :registryGroup and r.registryKey = :registryKey and r.registryValue = :registryValue"
                )
                .setParameter("registryGroup", registryGroup)
                .setParameter("registryKey", registryKey)
                .setParameter("registryValue", registryValue)
                .executeUpdate();
    }

    @Override
    @Transactional
    public int removeByRegistryGroupAndKey(String registryGroup, String registryKey) {
        return entityManager.createQuery(
                        "delete from XxlJobRegistry r where r.registryGroup = :registryGroup and r.registryKey = :registryKey"
                )
                .setParameter("registryGroup", registryGroup)
                .setParameter("registryKey", registryKey)
                .executeUpdate();
    }
}

