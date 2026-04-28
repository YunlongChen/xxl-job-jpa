package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.model.XxlJobLogGlue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class XxlJobLogGlueMapperImpl implements XxlJobLogGlueMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public int save(XxlJobLogGlue xxlJobLogGlue) {
        entityManager.persist(xxlJobLogGlue);
        return 1;
    }

    @Override
    public List<XxlJobLogGlue> findByJobId(int jobId) {
        return entityManager
                .createQuery("from XxlJobLogGlue g where g.jobId = :jobId order by g.id desc", XxlJobLogGlue.class)
                .setParameter("jobId", jobId)
                .getResultList();
    }

    @Override
    @Transactional
    public int removeOld(int jobId, int limit) {
        if (limit <= 0) {
            return deleteByJobId(jobId);
        }

        List<Integer> keepIds = entityManager
                .createQuery("select g.id from XxlJobLogGlue g where g.jobId = :jobId order by g.updateTime desc", Integer.class)
                .setParameter("jobId", jobId)
                .setMaxResults(limit)
                .getResultList();

        if (keepIds.isEmpty()) {
            return deleteByJobId(jobId);
        }

        return entityManager
                .createQuery("delete from XxlJobLogGlue g where g.jobId = :jobId and g.id not in :keepIds")
                .setParameter("jobId", jobId)
                .setParameter("keepIds", keepIds)
                .executeUpdate();
    }

    @Override
    @Transactional
    public int deleteByJobId(int jobId) {
        return entityManager
                .createQuery("delete from XxlJobLogGlue g where g.jobId = :jobId")
                .setParameter("jobId", jobId)
                .executeUpdate();
    }
}

