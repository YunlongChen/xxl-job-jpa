package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.model.XxlJobLogGlue;
import com.xxl.job.admin.repository.XxlJobLogGlueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class XxlJobLogGlueMapperImpl implements XxlJobLogGlueMapper {

    private final XxlJobLogGlueRepository xxlJobLogGlueRepository;

    public XxlJobLogGlueMapperImpl(XxlJobLogGlueRepository xxlJobLogGlueRepository) {
        this.xxlJobLogGlueRepository = xxlJobLogGlueRepository;
    }

    @Override
    @Transactional
    public int save(XxlJobLogGlue xxlJobLogGlue) {
        xxlJobLogGlueRepository.save(xxlJobLogGlue);
        return 1;
    }

    @Override
    public List<XxlJobLogGlue> findByJobId(int jobId) {
        return xxlJobLogGlueRepository.findByJobIdOrderByIdDesc(jobId);
    }

    @Override
    @Transactional
    public int removeOld(int jobId, int limit) {
        if (limit <= 0) {
            return deleteByJobId(jobId);
        }

        Set<Integer> keepIds = xxlJobLogGlueRepository.findByJobIdOrderByUpdateTimeDesc(jobId, PageRequest.of(0, limit))
                .stream()
                .map(XxlJobLogGlue::getId)
                .collect(Collectors.toSet());

        if (keepIds.isEmpty()) {
            return deleteByJobId(jobId);
        }

        List<Integer> removeIds = xxlJobLogGlueRepository.findByJobId(jobId)
                .stream()
                .map(XxlJobLogGlue::getId)
                .filter(id -> !keepIds.contains(id))
                .toList();

        if (removeIds.isEmpty()) {
            return 0;
        }
        xxlJobLogGlueRepository.deleteAllByIdInBatch(removeIds);
        return removeIds.size();
    }

    @Override
    @Transactional
    public int deleteByJobId(int jobId) {
        return xxlJobLogGlueRepository.deleteByJobId(jobId);
    }
}
