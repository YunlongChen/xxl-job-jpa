package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobGroupMapper;
import com.xxl.job.admin.model.XxlJobGroup;
import com.xxl.job.admin.repository.OffsetBasedPageRequest;
import com.xxl.job.admin.repository.XxlJobGroupRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class XxlJobGroupMapperImpl implements XxlJobGroupMapper {

    private final XxlJobGroupRepository xxlJobGroupRepository;

    public XxlJobGroupMapperImpl(XxlJobGroupRepository xxlJobGroupRepository) {
        this.xxlJobGroupRepository = xxlJobGroupRepository;
    }

    @Override
    public List<XxlJobGroup> findAll() {
        return xxlJobGroupRepository.findAllByOrderByAppnameAscTitleAscIdAsc();
    }

    @Override
    public List<XxlJobGroup> findByAddressType(int addressType) {
        return xxlJobGroupRepository.findByAddressTypeOrderByAppnameAscTitleAscIdAsc(addressType);
    }

    @Override
    @Transactional
    public int save(XxlJobGroup xxlJobGroup) {
        xxlJobGroupRepository.save(xxlJobGroup);
        return 1;
    }

    @Override
    @Transactional
    public int update(XxlJobGroup xxlJobGroup) {
        if (!xxlJobGroupRepository.existsById(xxlJobGroup.getId())) {
            return 0;
        }
        xxlJobGroupRepository.save(xxlJobGroup);
        return 1;
    }

    @Override
    @Transactional
    public int remove(int id) {
        if (!xxlJobGroupRepository.existsById(id)) {
            return 0;
        }
        xxlJobGroupRepository.deleteById(id);
        return 1;
    }

    @Override
    public XxlJobGroup load(int id) {
        return xxlJobGroupRepository.findById(id).orElse(null);
    }

    @Override
    public List<XxlJobGroup> pageList(int offset, int pagesize, String appname, String title) {
        Specification<XxlJobGroup> specification = XxlJobGroupSpecifications.build(appname, title);
        return xxlJobGroupRepository.findAll(
                specification,
                new OffsetBasedPageRequest(offset, pagesize, Sort.by("appname", "title", "id"))
        ).getContent();
    }

    @Override
    public int pageListCount(int offset, int pagesize, String appname, String title) {
        return (int) xxlJobGroupRepository.count(XxlJobGroupSpecifications.build(appname, title));
    }

    private static class XxlJobGroupSpecifications {
        private static Specification<XxlJobGroup> build(String appname, String title) {
            return (root, query, cb) -> {
                var predicate = cb.conjunction();
                if (appname != null && !appname.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("appname"), "%" + appname + "%"));
                }
                if (title != null && !title.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
                }
                return predicate;
            };
        }
    }
}
