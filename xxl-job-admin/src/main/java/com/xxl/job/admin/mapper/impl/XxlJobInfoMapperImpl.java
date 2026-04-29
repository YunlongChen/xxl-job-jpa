package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobInfoMapper;
import com.xxl.job.admin.model.XxlJobInfo;
import com.xxl.job.admin.repository.OffsetBasedPageRequest;
import com.xxl.job.admin.repository.XxlJobInfoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class XxlJobInfoMapperImpl implements XxlJobInfoMapper {

    private final XxlJobInfoRepository xxlJobInfoRepository;

    public XxlJobInfoMapperImpl(XxlJobInfoRepository xxlJobInfoRepository) {
        this.xxlJobInfoRepository = xxlJobInfoRepository;
    }

    @Override
    public List<XxlJobInfo> pageList(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        Specification<XxlJobInfo> specification = XxlJobInfoSpecifications.build(jobGroup, triggerStatus, jobDesc, executorHandler, author);
        return xxlJobInfoRepository.findAll(
                specification,
                new OffsetBasedPageRequest(offset, pagesize, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        return (int) xxlJobInfoRepository.count(XxlJobInfoSpecifications.build(jobGroup, triggerStatus, jobDesc, executorHandler, author));
    }

    @Override
    @Transactional
    public int save(XxlJobInfo info) {
        xxlJobInfoRepository.save(info);
        return 1;
    }

    @Override
    public XxlJobInfo loadById(int id) {
        return xxlJobInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public int update(XxlJobInfo xxlJobInfo) {
        XxlJobInfo exist = xxlJobInfoRepository.findById(xxlJobInfo.getId()).orElse(null);
        if (exist == null) {
            return 0;
        }
        exist.setJobGroup(xxlJobInfo.getJobGroup());
        exist.setJobDesc(xxlJobInfo.getJobDesc());
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
        xxlJobInfoRepository.save(exist);
        return 1;
    }

    @Override
    @Transactional
    public int delete(long id) {
        int intId = (int) id;
        if (!xxlJobInfoRepository.existsById(intId)) {
            return 0;
        }
        xxlJobInfoRepository.deleteById(intId);
        return 1;
    }

    @Override
    public List<XxlJobInfo> getJobsByGroup(int jobGroup) {
        return xxlJobInfoRepository.findByJobGroup(jobGroup);
    }

    @Override
    public int findAllCount() {
        return (int) xxlJobInfoRepository.countBy();
    }

    @Override
    public List<XxlJobInfo> scheduleJobQuery(long maxNextTime, int pagesize) {
        return xxlJobInfoRepository.scheduleJobQuery(maxNextTime, PageRequest.of(0, pagesize));
    }

    @Override
    @Transactional
    public int scheduleUpdate(XxlJobInfo xxlJobInfo) {
        if (xxlJobInfo.getTriggerStatus() >= 0) {
            return xxlJobInfoRepository.scheduleUpdateWithStatus(
                    xxlJobInfo.getId(),
                    xxlJobInfo.getTriggerLastTime(),
                    xxlJobInfo.getTriggerNextTime(),
                    xxlJobInfo.getTriggerStatus()
            );
        }

        return xxlJobInfoRepository.scheduleUpdateNoStatus(
                xxlJobInfo.getId(),
                xxlJobInfo.getTriggerLastTime(),
                xxlJobInfo.getTriggerNextTime()
        );
    }

    @Override
    @Transactional
    public int scheduleBatchUpdate(List<XxlJobInfo> jobInfoList) {
        if (jobInfoList == null || jobInfoList.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (XxlJobInfo item : jobInfoList) {
            total += scheduleUpdate(item);
        }
        return total;
    }

    private static class XxlJobInfoSpecifications {
        private static Specification<XxlJobInfo> build(int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
            return (root, query, cb) -> {
                var predicate = cb.conjunction();
                if (jobGroup > 0) {
                    predicate = cb.and(predicate, cb.equal(root.get("jobGroup"), jobGroup));
                }
                if (triggerStatus >= 0) {
                    predicate = cb.and(predicate, cb.equal(root.get("triggerStatus"), triggerStatus));
                }
                if (jobDesc != null && !jobDesc.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("jobDesc"), "%" + jobDesc + "%"));
                }
                if (executorHandler != null && !executorHandler.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("executorHandler"), "%" + executorHandler + "%"));
                }
                if (author != null && !author.isBlank()) {
                    predicate = cb.and(predicate, cb.like(root.get("author"), "%" + author + "%"));
                }
                return predicate;
            };
        }
    }
}
