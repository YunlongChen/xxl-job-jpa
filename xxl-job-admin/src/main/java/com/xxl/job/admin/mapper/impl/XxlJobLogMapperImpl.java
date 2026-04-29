package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.repository.OffsetBasedPageRequest;
import com.xxl.job.admin.repository.XxlJobLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class XxlJobLogMapperImpl implements XxlJobLogMapper {

    private final XxlJobLogRepository xxlJobLogRepository;

    public XxlJobLogMapperImpl(XxlJobLogRepository xxlJobLogRepository) {
        this.xxlJobLogRepository = xxlJobLogRepository;
    }

    @Override
    public List<XxlJobLog> pageList(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Specification<XxlJobLog> specification = XxlJobLogSpecifications.pageSpec(jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return xxlJobLogRepository.findAll(
                specification,
                new OffsetBasedPageRequest(offset, pagesize, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();
    }

    @Override
    public int pageListCount(int offset, int pagesize, int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
        Specification<XxlJobLog> specification = XxlJobLogSpecifications.pageSpec(jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        return (int) xxlJobLogRepository.count(specification);
    }

    @Override
    public XxlJobLog load(long id) {
        return xxlJobLogRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public long save(XxlJobLog xxlJobLog) {
        return xxlJobLogRepository.save(xxlJobLog).getId();
    }

    @Override
    @Transactional
    public int updateTriggerInfo(XxlJobLog xxlJobLog) {
        XxlJobLog exist = xxlJobLogRepository.findById(xxlJobLog.getId()).orElse(null);
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
        xxlJobLogRepository.save(exist);
        return 1;
    }

    @Override
    @Transactional
    public int updateHandleInfo(XxlJobLog xxlJobLog) {
        XxlJobLog exist = xxlJobLogRepository.findById(xxlJobLog.getId()).orElse(null);
        if (exist == null) {
            return 0;
        }
        exist.setHandleTime(xxlJobLog.getHandleTime());
        exist.setHandleCode(xxlJobLog.getHandleCode());
        exist.setHandleMsg(xxlJobLog.getHandleMsg());
        xxlJobLogRepository.save(exist);
        return 1;
    }

    @Override
    @Transactional
    public int delete(int jobId) {
        return xxlJobLogRepository.deleteByJobId(jobId);
    }

    @Override
    public Map<String, Object> findLogReport(Date from, Date to) {
        Object[] row = xxlJobLogRepository.findLogReportAgg(from, to);

        Map<String, Object> map = new HashMap<>();
        map.put("triggerDayCount", ((Number) row[0]).longValue());
        map.put("triggerDayCountRunning", ((Number) row[1]).longValue());
        map.put("triggerDayCountSuc", ((Number) row[2]).longValue());
        return map;
    }

    @Override
    public List<Long> findClearLogIds(int jobGroup, int jobId, Date clearBeforeTime, int clearBeforeNum, int pagesize) {
        Specification<XxlJobLog> baseSpec = XxlJobLogSpecifications.groupAndJobIdSpec(jobGroup, jobId);

        List<Long> keepIds = null;
        if (clearBeforeNum > 0) {
            keepIds = xxlJobLogRepository.findAll(
                    baseSpec,
                    new OffsetBasedPageRequest(0, clearBeforeNum, Sort.by(Sort.Direction.DESC, "triggerTime"))
            ).stream().map(XxlJobLog::getId).toList();
        }

        Specification<XxlJobLog> clearSpec = XxlJobLogSpecifications.clearSpec(baseSpec, clearBeforeTime, keepIds);

        return xxlJobLogRepository.findAll(
                clearSpec,
                new OffsetBasedPageRequest(0, pagesize, Sort.by(Sort.Direction.ASC, "id"))
        ).stream().map(XxlJobLog::getId).toList();
    }

    @Override
    @Transactional
    public int clearLog(List<Long> logIds) {
        if (logIds == null || logIds.isEmpty()) {
            return 0;
        }
        return xxlJobLogRepository.deleteByIds(logIds);
    }

    @Override
    public List<Long> findFailJobLogIds(int pagesize) {
        return xxlJobLogRepository.findFailJobLogIds(PageRequest.of(0, pagesize));
    }

    @Override
    @Transactional
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        return xxlJobLogRepository.updateAlarmStatus(logId, oldAlarmStatus, newAlarmStatus);
    }

    @Override
    public List<Long> findLostJobIds(Date losedTime) {
        return xxlJobLogRepository.findLostJobIds(losedTime);
    }

    private static class XxlJobLogSpecifications {
        private static Specification<XxlJobLog> groupAndJobIdSpec(int jobGroup, int jobId) {
            return (root, query, cb) -> {
                var predicate = cb.conjunction();
                if (jobGroup > 0) {
                    predicate = cb.and(predicate, cb.equal(root.get("jobGroup"), jobGroup));
                }
                if (jobId > 0) {
                    predicate = cb.and(predicate, cb.equal(root.get("jobId"), jobId));
                }
                return predicate;
            };
        }

        private static Specification<XxlJobLog> pageSpec(int jobGroup, int jobId, Date triggerTimeStart, Date triggerTimeEnd, int logStatus) {
            return (root, query, cb) -> {
                var predicate = groupAndJobIdSpec(jobGroup, jobId).toPredicate(root, query, cb);
                if (triggerTimeStart != null) {
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("triggerTime"), triggerTimeStart));
                }
                if (triggerTimeEnd != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("triggerTime"), triggerTimeEnd));
                }
                if (logStatus == 1) {
                    predicate = cb.and(predicate, cb.equal(root.get("handleCode"), 200));
                } else if (logStatus == 2) {
                    predicate = cb.and(predicate, cb.or(
                            cb.not(root.get("triggerCode").in(0, 200)),
                            cb.not(root.get("handleCode").in(0, 200))
                    ));
                } else if (logStatus == 3) {
                    predicate = cb.and(predicate,
                            cb.equal(root.get("triggerCode"), 200),
                            cb.equal(root.get("handleCode"), 0)
                    );
                }
                return predicate;
            };
        }

        private static Specification<XxlJobLog> clearSpec(Specification<XxlJobLog> baseSpec, Date clearBeforeTime, List<Long> keepIds) {
            return (root, query, cb) -> {
                var predicate = baseSpec.toPredicate(root, query, cb);
                if (clearBeforeTime != null) {
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("triggerTime"), clearBeforeTime));
                }
                if (keepIds != null && !keepIds.isEmpty()) {
                    predicate = cb.and(predicate, cb.not(root.get("id").in(keepIds)));
                }
                return predicate;
            };
        }
    }
}
