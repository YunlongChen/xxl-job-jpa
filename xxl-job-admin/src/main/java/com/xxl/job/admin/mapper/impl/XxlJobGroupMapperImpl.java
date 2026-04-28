package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.model.XxlJobGroup;
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
public class XxlJobGroupMapperImpl implements XxlJobGroupMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<XxlJobGroup> findAll() {
        return entityManager
                .createQuery("from XxlJobGroup g order by g.appname, g.title, g.id asc", XxlJobGroup.class)
                .getResultList();
    }

    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        return entityManager
                .createQuery("from XxlJobGroup g where g.addressType = :addressType order by g.appname, g.title, g.id asc", XxlJobGroup.class)
                .setParameter("addressType", addressType)
                .getResultList();
    }

    @Override
    @Transactional
    public int save(XxlJobGroup xxlJobGroup) {
        entityManager.persist(xxlJobGroup);
        return 1;
    }

    @Override
    @Transactional
    public int update(XxlJobGroup xxlJobGroup) {
        XxlJobGroup exist = entityManager.find(XxlJobGroup.class, xxlJobGroup.getId());
        if (exist == null) {
            return 0;
        }
        exist.setAppname(xxlJobGroup.getAppname());
        exist.setTitle(xxlJobGroup.getTitle());
        exist.setAddressType(xxlJobGroup.getAddressType());
        exist.setAddressList(xxlJobGroup.getAddressList());
        exist.setUpdateTime(xxlJobGroup.getUpdateTime());
        return 1;
    }

    @Override
    @Transactional
    public int remove(int id) {
        XxlJobGroup exist = entityManager.find(XxlJobGroup.class, id);
        if (exist == null) {
            return 0;
        }
        entityManager.remove(exist);
        return 1;
    }

    @Override
    public XxlJobGroup load(int id) {
        return entityManager.find(XxlJobGroup.class, id);
    }

    @Override
    public List<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {
        QueryParts queryParts = buildQuery(appname, title);
        TypedQuery<XxlJobGroup> query = entityManager.createQuery(queryParts.jpql + " order by g.appname, g.title, g.id asc", XxlJobGroup.class);
        queryParts.params.forEach(query::setParameter);
        return query.setFirstResult(offset).setMaxResults(pagesize).getResultList();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String appname, String title) {
        QueryParts queryParts = buildQuery(appname, title);
        TypedQuery<Long> query = entityManager.createQuery("select count(g) " + queryParts.jpql, Long.class);
        queryParts.params.forEach(query::setParameter);
        return query.getSingleResult().intValue();
    }

    private QueryParts buildQuery(String appname, String title) {
        StringBuilder sb = new StringBuilder("from XxlJobGroup g where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (StringTool.isNotBlank(appname)) {
            sb.append(" and g.appname like :appname");
            params.put("appname", "%" + appname + "%");
        }
        if (StringTool.isNotBlank(title)) {
            sb.append(" and g.title like :title");
            params.put("title", "%" + title + "%");
        }
        return new QueryParts(sb.toString(), params);
    }

    private record QueryParts(String jpql, Map<String, Object> params) {
    }
}

