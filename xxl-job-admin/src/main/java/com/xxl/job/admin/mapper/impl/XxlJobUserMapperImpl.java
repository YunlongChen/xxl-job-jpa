package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobUserMapper;
import com.xxl.job.admin.model.XxlJobUser;
import com.xxl.job.admin.repository.OffsetBasedPageRequest;
import com.xxl.job.admin.repository.XxlJobUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class XxlJobUserMapperImpl implements XxlJobUserMapper {

    private final XxlJobUserRepository xxlJobUserRepository;

    public XxlJobUserMapperImpl(XxlJobUserRepository xxlJobUserRepository) {
        this.xxlJobUserRepository = xxlJobUserRepository;
    }

    @Override
    public List<XxlJobUser> pageList(int offset, int pagesize, String username, int role) {
        Specification<XxlJobUser> specification = XxlJobUserSpecifications.build(username, role);
        return xxlJobUserRepository.findAll(
                specification,
                new OffsetBasedPageRequest(offset, pagesize, Sort.by(Sort.Direction.ASC, "username"))
        ).getContent();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String username, int role) {
        return (int) xxlJobUserRepository.count(XxlJobUserSpecifications.build(username, role));
    }

    @Override
    public XxlJobUser loadByUserName(String username) {
        return xxlJobUserRepository.findFirstByUsername(username).orElse(null);
    }

    @Override
    public XxlJobUser loadById(int id) {
        return xxlJobUserRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public int save(XxlJobUser xxlJobUser) {
        xxlJobUserRepository.save(xxlJobUser);
        return 1;
    }

    @Override
    @Transactional
    public int update(XxlJobUser xxlJobUser) {
        XxlJobUser exist = xxlJobUserRepository.findById(xxlJobUser.getId()).orElse(null);
        if (exist == null) {
            return 0;
        }
        if (xxlJobUser.getPassword() != null && !xxlJobUser.getPassword().isBlank()) {
            exist.setPassword(xxlJobUser.getPassword());
        }
        exist.setRole(xxlJobUser.getRole());
        exist.setPermission(xxlJobUser.getPermission());
        xxlJobUserRepository.save(exist);
        return 1;
    }

    @Override
    @Transactional
    public int delete(int id) {
        if (!xxlJobUserRepository.existsById(id)) {
            return 0;
        }
        xxlJobUserRepository.deleteById(id);
        return 1;
    }

    @Override
    @Transactional
    public int updateToken(int id, String token) {
        XxlJobUser user = xxlJobUserRepository.findById(id).orElse(null);
        if (user == null) {
            return 0;
        }
        user.setToken(token);
        xxlJobUserRepository.save(user);
        return 1;
    }

    private static class XxlJobUserSpecifications {
        private static Specification<XxlJobUser> build(String username, int role) {
            return (root, query, cb) -> {
                var predicate = cb.conjunction();
                if (username != null && !username.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("username"), "%" + username + "%"));
                }
                if (role > -1) {
                    predicate = cb.and(predicate, cb.equal(root.get("role"), role));
                }
                return predicate;
            };
        }
    }
}
