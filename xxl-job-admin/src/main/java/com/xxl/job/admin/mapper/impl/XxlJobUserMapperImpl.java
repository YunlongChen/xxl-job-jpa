package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUser;
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
public class XxlJobUserMapperImpl implements XxlJobUserMapper {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
        QueryParts queryParts = buildQuery(username, role);
        TypedQuery<XxlJobUser> query = entityManager.createQuery(queryParts.jpql + " order by u.username asc", XxlJobUser.class);
        queryParts.params.forEach(query::setParameter);
        return query.setFirstResult(offset).setMaxResults(pagesize).getResultList();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String username, int role) {
        QueryParts queryParts = buildQuery(username, role);
        TypedQuery<Long> query = entityManager.createQuery("select count(u) " + queryParts.jpql, Long.class);
        queryParts.params.forEach(query::setParameter);
        return query.getSingleResult().intValue();
    }

    @Override
    public XxlJobUser loadByUserName(String username) {
        List<XxlJobUser> list = entityManager
                .createQuery("from XxlJobUser u where u.username = :username", XxlJobUser.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.getFirst();
    }

    @Override
    public XxlJobUser loadById(int id) {
        return entityManager.find(XxlJobUser.class, id);
    }

    @Override
    @Transactional
    public int save(XxlJobUser xxlJobUser) {
        entityManager.persist(xxlJobUser);
        return 1;
    }

    @Override
    @Transactional
    public int update(XxlJobUser xxlJobUser) {
        XxlJobUser exist = entityManager.find(XxlJobUser.class, xxlJobUser.getId());
        if (exist == null) {
            return 0;
        }
        if (StringTool.isNotBlank(xxlJobUser.getPassword())) {
            exist.setPassword(xxlJobUser.getPassword());
        }
        exist.setRole(xxlJobUser.getRole());
        exist.setPermission(xxlJobUser.getPermission());
        return 1;
    }

    @Override
    @Transactional
    public int delete(int id) {
        XxlJobUser exist = entityManager.find(XxlJobUser.class, id);
        if (exist == null) {
            return 0;
        }
        entityManager.remove(exist);
        return 1;
    }

    @Override
    @Transactional
    public int updateToken(int id, String token) {
        return entityManager.createQuery("update XxlJobUser u set u.token = :token where u.id = :id")
                .setParameter("token", token)
                .setParameter("id", id)
                .executeUpdate();
    }

    private QueryParts buildQuery(String username, int role) {
        StringBuilder sb = new StringBuilder("from XxlJobUser u where 1=1");
        Map<String, Object> params = new HashMap<>();
        if (StringTool.isNotBlank(username)) {
            sb.append(" and u.username like :username");
            params.put("username", "%" + username + "%");
        }
        if (role > -1) {
            sb.append(" and u.role = :role");
            params.put("role", role);
        }
        return new QueryParts(sb.toString(), params);
    }

    private record QueryParts(String jpql, Map<String, Object> params) {
    }
}

