package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.tool.core.StringTool;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class XxlJobInfoMapperImpl implements XxlJobInfoMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        QueryParts queryParts = buildPageQuery(jobGroup, triggerStatus, jobDesc, executorHandler, author);
        TypedQuery<XxlJobInfo> query = entityManager.createQuery(queryParts.jpql + " order by i.id desc", XxlJobInfo.class);
        queryParts.params.forEach(query::setParameter);
        return query.setFirstResult(offset).setMaxResults(pagesize).getResultList();
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        QueryParts queryParts = buildPageQuery(jobGroup, triggerStatus, jobDesc, executorHandler, author);
        TypedQuery<Long> query = entityManager.createQuery("select count(i) " + queryParts.jpql, Long.class);
        queryParts.params.forEach(query::setParameter);
        return query.getSingleResult().intValue();
    }

    @Override
    @Transactional
    public int save(XxlJobInfo info) {
        entityManager.persist(info);
        return 1;
    }

    @Override
    public XxlJobInfo loadById(int id) {
        return entityManager.find(XxlJobInfo.class, id);
    }

    @Override
    @Transactional
    public int update(XxlJobInfo xxlJobInfo) {
        XxlJobInfo exist = entityManager.find(XxlJobInfo.class, xxlJobInfo.getId());
        if (exist == null) {
            return 0;
        }
        exist.setJobGroup(xxlJobInfo.getJobGroup());
        exist.setJobDesc(xxlJobInfo.getJobDesc());
        exist.setAddTime(xxlJobInfo.getAddTime());
        exist.setUpdateTime(xxlJobInfo.getUpdateTime());
        exist.setAuthor(xxlJobInfo.getAuthor());
        exist.setAlarmEmail(xxlJobInfo.getAlarmEmail());
        exist.setScheduleType(xxlJobInfo.getScheduleType());
        exist.setScheduleConf(xxlJobInfo.getScheduleConf());
        exist.setMisfireStrategy(xxlJobInfo.getMisfireStrategy());
        exist.setExecutorRouteStrategy(xxlJobInfo.getExecutorRouteStrategy());
        exist.setExecutorHandler(xxlJobInfo.getExecutorHandler());
        exist.setExecutorParam(xxlJobInfo.getExecutorParam());
        exist.setExecutorBlockStrategy(xxlJobInfo.getExecutorBlockStrategy());
        exist.setExecutorTimeout(xxlJobInfo.getExecutorTimeout());
        exist.setExecutorFailRetryCount(xxlJobInfo.getExecutorFailRetryCount());
        exist.setGlueType(xxlJobInfo.getGlueType());
        exist.setGlueSource(xxlJobInfo.getGlueSource());
        exist.setGlueRemark(xxlJobInfo.getGlueRemark());
        exist.setGlueUpdatetime(xxlJobInfo.getGlueUpdatetime());
        exist.setChildJobId(xxlJobInfo.getChildJobId());
        exist.setTriggerStatus(xxlJobInfo.getTriggerStatus());
        exist.setTriggerLastTime(xxlJobInfo.getTriggerLastTime());
        exist.setTriggerNextTime(xxlJobInfo.getTriggerNextTime());
        return 1;
    }

    @Override
    @Transactional
    public int delete(long id) {
        XxlJobInfo exist = entityManager.find(XxlJobInfo.class, (int) id);
        if (exist == null) {
            return 0;
        }
        entityManager.remove(exist);
        return 1;
    }

    @Override
    public List<XxlJobInfo> getJobsByGroup(int jobGroup) {
        return entityManager
                .createQuery("from XxlJobInfo i where i.jobGroup = :jobGroup", XxlJobInfo.class)
                .setParameter("jobGroup", jobGroup)
                .getResultList();
    }

    @Override
    public int findAllCount() {
        return entityManager
                .createQuery("select count(i) from XxlJobInfo i", Long.class)
                .getSingleResult()
                .intValue();
    }

    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        return entityManager
                .createQuery(
                        "from XxlJobInfo i where i.triggerStatus = 1 and i.triggerNextTime <= :maxNextTime order by i.id asc",
                        XxlJobInfo.class
                )
                .setParameter("maxNextTime", maxNextTime)
                .setMaxResults(pagesize)
                .getResultList();
    }

    @Override
    @Transactional
    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        if (xxlJobInfo.getTriggerStatus() >= 0) {
            return entityManager
                    .createQuery(
                            "update XxlJobInfo i set i.triggerLastTime = :lastTime, i.triggerNextTime = :nextTime, i.triggerStatus = :status " +
                                    "where i.id = :id and i.triggerStatus = 1"
                    )
                    .setParameter("lastTime", xxlJobInfo.getTriggerLastTime())
                    .setParameter("nextTime", xxlJobInfo.getTriggerNextTime())
                    .setParameter("status", xxlJobInfo.getTriggerStatus())
                    .setParameter("id", xxlJobInfo.getId())
                    .executeUpdate();
        }

        return entityManager
                .createQuery(
                        "update XxlJobInfo i set i.triggerLastTime = :lastTime, i.triggerNextTime = :nextTime where i.id = :id and i.triggerStatus = 1"
                )
                .setParameter("lastTime", xxlJobInfo.getTriggerLastTime())
                .setParameter("nextTime", xxlJobInfo.getTriggerNextTime())
                .setParameter("id", xxlJobInfo.getId())
                .executeUpdate();
    }

    @Override
    @Transactional
    public int scheduleBatchUpdate(List<XxlJobInfo> jobInfoList) {
        if (jobInfoList == null || jobInfoList.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("update xxl_job_info set ");

        sql.append("trigger_last_time = case id");
        for (int i = 0; i < jobInfoList.size(); i++) {
            sql.append(" when :id").append(i).append(" then :last").append(i);
        }
        sql.append(" else trigger_last_time end, ");

        sql.append("trigger_next_time = case id");
        for (int i = 0; i < jobInfoList.size(); i++) {
            sql.append(" when :id").append(i).append(" then :next").append(i);
        }
        sql.append(" else trigger_next_time end, ");

        sql.append("trigger_status = case id");
        for (int i = 0; i < jobInfoList.size(); i++) {
            XxlJobInfo item = jobInfoList.get(i);
            if (item.getTriggerStatus() >= 0) {
                sql.append(" when :id").append(i).append(" then :status").append(i);
            } else {
                sql.append(" when :id").append(i).append(" then trigger_status");
            }
        }
        sql.append(" else trigger_status end ");

        sql.append("where id in (");
        for (int i = 0; i < jobInfoList.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append(":id").append(i);
        }
        sql.append(") and trigger_status = 1");

        jakarta.persistence.Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < jobInfoList.size(); i++) {
            XxlJobInfo item = jobInfoList.get(i);
            query.setParameter("id" + i, item.getId());
            query.setParameter("last" + i, item.getTriggerLastTime());
            query.setParameter("next" + i, item.getTriggerNextTime());
            if (item.getTriggerStatus() >= 0) {
                query.setParameter("status" + i, item.getTriggerStatus());
            }
        }
        return query.executeUpdate();
    }

    private QueryParts buildPageQuery(int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        StringBuilder sb = new StringBuilder("from XxlJobInfo i where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (jobGroup > 0) {
            sb.append(" and i.jobGroup = :jobGroup");
            params.put("jobGroup", jobGroup);
        }
        if (triggerStatus >= 0) {
            sb.append(" and i.triggerStatus = :triggerStatus");
            params.put("triggerStatus", triggerStatus);
        }
        if (StringTool.isNotBlank(jobDesc)) {
            sb.append(" and i.jobDesc like :jobDesc");
            params.put("jobDesc", "%" + jobDesc + "%");
        }
        if (StringTool.isNotBlank(executorHandler)) {
            sb.append(" and i.executorHandler like :executorHandler");
            params.put("executorHandler", "%" + executorHandler + "%");
        }
        if (StringTool.isNotBlank(author)) {
            sb.append(" and i.author like :author");
            params.put("author", "%" + author + "%");
        }
        return new QueryParts(sb.toString(), params);
    }

    private record QueryParts(String jpql, Map<String, Object> params) {
    }
}

