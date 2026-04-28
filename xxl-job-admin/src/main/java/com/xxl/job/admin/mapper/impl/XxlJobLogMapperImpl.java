package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class XxlJobLogMapperImpl implements XxlJobLogMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        QueryParts queryParts = buildPageQuery(jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        TypedQuery<XxlJobLog> query = entityManager.createQuery(queryParts.jpql + " order by l.id desc", XxlJobLog.class);
        queryParts.params.forEach(query::setParameter);
        return query.setFirstResult(offset).setMaxResults(pagesize).getResultList();
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        QueryParts queryParts = buildPageQuery(jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        TypedQuery<Long> query = entityManager.createQuery("select count(l) " + queryParts.jpql, Long.class);
        queryParts.params.forEach(query::setParameter);
        return query.getSingleResult().intValue();
    }

    @Override
    public XxlJobLog load(long id) {
        return entityManager.find(XxlJobLog.class, id);
    }

    @Override
    @Transactional
    public long save(XxlJobLog xxlJobLog) {
        entityManager.persist(xxlJobLog);
        return xxlJobLog.getId();
    }

    @Override
    @Transactional
    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        XxlJobLog exist = entityManager.find(XxlJobLog.class, xxlJobLog.getId());
        if (exist == null) {
            return 0;
        }
        exist.setTriggerTime(xxlJobLog.getTriggerTime());
        exist.setTriggerCode(xxlJobLog.getTriggerCode());
        exist.setTriggerMsg(xxlJobLog.getTriggerMsg());
        exist.setExecutorAddress(xxlJobLog.getExecutorAddress());
        exist.setExecutorHandler(xxlJobLog.getExecutorHandler());
        exist.setExecutorParam(xxlJobLog.getExecutorParam());
        exist.setExecutorShardingParam(xxlJobLog.getExecutorShardingParam());
        exist.setExecutorFailRetryCount(xxlJobLog.getExecutorFailRetryCount());
        return 1;
    }

    @Override
    @Transactional
    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        XxlJobLog exist = entityManager.find(XxlJobLog.class, xxlJobLog.getId());
        if (exist == null) {
            return 0;
        }
        exist.setHandleTime(xxlJobLog.getHandleTime());
        exist.setHandleCode(xxlJobLog.getHandleCode());
        exist.setHandleMsg(xxlJobLog.getHandleMsg());
        return 1;
    }

    @Override
    @Transactional
    public int delete(int jobId) {
        return entityManager.createQuery("delete from XxlJobLog l where l.jobId = :jobId")
                .setParameter("jobId", jobId)
                .executeUpdate();
    }

    @Override
    public Map<String, Object> findLogReport(Date from, Date to) {
        Object[] row = entityManager
                .createQuery(
                        "select count(l.handleCode), " +
                                "coalesce(sum(case when (l.triggerCode in (0,200) and l.handleCode = 0) then 1 else 0 end),0), " +
                                "coalesce(sum(case when l.handleCode = 200 then 1 else 0 end),0) " +
                                "from XxlJobLog l where l.triggerTime between :from and :to",
                        Object[].class
                )
                .setParameter("from", from)
                .setParameter("to", to)
                .getSingleResult();

        Map<String, Object> map = new HashMap<>();
        map.put("triggerDayCount", ((Number) row[0]).longValue());
        map.put("triggerDayCountRunning", ((Number) row[1]).longValue());
        map.put("triggerDayCountSuc", ((Number) row[2]).longValue());
        return map;
    }

    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        List<Long> keepIds = null;
        if (clearBeforeNum > 0) {
            StringBuilder keepJpql = new StringBuilder("select l.id from XxlJobLog l where 1=1");
            Map<String, Object> keepParams = new HashMap<>();
            appendGroupAndJobId(keepJpql, keepParams, jobGroup, jobId);
            TypedQuery<Long> keepQuery = entityManager.createQuery(keepJpql + " order by l.triggerTime desc", Long.class);
            keepParams.forEach(keepQuery::setParameter);
            keepIds = keepQuery.setMaxResults(clearBeforeNum).getResultList();
        }

        StringBuilder jpql = new StringBuilder("select l.id from XxlJobLog l where 1=1");
        Map<String, Object> params = new HashMap<>();
        appendGroupAndJobId(jpql, params, jobGroup, jobId);
        if (clearBeforeTime != null) {
            jpql.append(" and l.triggerTime <= :clearBeforeTime");
            params.put("clearBeforeTime", clearBeforeTime);
        }
        if (keepIds != null && !keepIds.isEmpty()) {
            jpql.append(" and l.id not in :keepIds");
            params.put("keepIds", keepIds);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql + " order by l.id asc", Long.class);
        params.forEach(query::setParameter);
        return query.setMaxResults(pagesize).getResultList();
    }

    @Override
    @Transactional
    public int clearLog(List<Long> logIds) {
        if (logIds == null || logIds.isEmpty()) {
            return 0;
        }
        return entityManager.createQuery("delete from XxlJobLog l where l.id in :ids")
                .setParameter("ids", logIds)
                .executeUpdate();
    }

    @Override
    public List<Long> findFailJobLogIds(int pagesize) {
        return entityManager
                .createQuery(
                        "select l.id from XxlJobLog l " +
                                "where not ( (l.triggerCode in (0,200) and l.handleCode = 0) or (l.handleCode = 200) ) " +
                                "and l.alarmStatus = 0 " +
                                "order by l.id asc",
                        Long.class
                )
                .setMaxResults(pagesize)
                .getResultList();
    }

    @Override
    @Transactional
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        return entityManager.createQuery(
                        "update XxlJobLog l set l.alarmStatus = :newAlarmStatus where l.id = :logId and l.alarmStatus = :oldAlarmStatus"
                )
                .setParameter("newAlarmStatus", newAlarmStatus)
                .setParameter("logId", logId)
                .setParameter("oldAlarmStatus", oldAlarmStatus)
                .executeUpdate();
    }

    @Override
    public List<Long> findLostJobIds(Date losedTime) {
        return entityManager
                .createQuery(
                        "select l.id from XxlJobLog l " +
                                "where l.triggerCode = 200 and l.handleCode = 0 and l.triggerTime <= :losedTime " +
                                "and not exists (select 1 from XxlJobRegistry r where r.registryValue = l.executorAddress)",
                        Long.class
                )
                .setParameter("losedTime", losedTime)
                .getResultList();
    }

    private QueryParts buildPageQuery(int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        StringBuilder sb = new StringBuilder("from XxlJobLog l where 1=1");
        Map<String, Object> params = new HashMap<>();

        if (jobGroup > 0) {
            sb.append(" and l.jobGroup = :jobGroup");
            params.put("jobGroup", jobGroup);
        }
        if (jobId > 0) {
            sb.append(" and l.jobId = :jobId");
            params.put("jobId", jobId);
        }
        if (triggerTimeStart != null) {
            sb.append(" and l.triggerTime >= :triggerTimeStart");
            params.put("triggerTimeStart", triggerTimeStart);
        }
        if (triggerTimeEnd != null) {
            sb.append(" and l.triggerTime <= :triggerTimeEnd");
            params.put("triggerTimeEnd", triggerTimeEnd);
        }
        if (logStatus == 1) {
            sb.append(" and l.handleCode = 200");
        } else if (logStatus == 2) {
            sb.append(" and ( l.triggerCode not in (0,200) or l.handleCode not in (0,200) )");
        } else if (logStatus == 3) {
            sb.append(" and l.triggerCode = 200 and l.handleCode = 0");
        }
        return new QueryParts(sb.toString(), params);
    }

    private void appendGroupAndJobId(StringBuilder jpql, Map<String, Object> params, int jobGroup, int jobId) {
        if (jobGroup > 0) {
            jpql.append(" and l.jobGroup = :jobGroup");
            params.put("jobGroup", jobGroup);
        }
        if (jobId > 0) {
            jpql.append(" and l.jobId = :jobId");
            params.put("jobId", jobId);
        }
    }

    private record QueryParts(String jpql, Map<String, Object> params) {
    }
}
