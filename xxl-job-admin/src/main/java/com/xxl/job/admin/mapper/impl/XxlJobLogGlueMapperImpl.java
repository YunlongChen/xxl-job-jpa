package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogGlueMapper;
import com.xxl.job.admin.model.XxlJobLogGlue;
import com.xxl.job.admin.repository.XxlJobLogGlueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        List<Integer> keepIds = xxlJobLogGlueRepository.findKeepIds(jobId, PageRequest.of(0, limit));

        if (keepIds.isEmpty()) {
            return deleteByJobId(jobId);
        }

        return xxlJobLogGlueRepository.deleteOld(jobId, keepIds);
    }

    @Override
    @Transactional
    public int deleteByJobId(int jobId) {
        return xxlJobLogGlueRepository.deleteByJobId(jobId);
    }
}
