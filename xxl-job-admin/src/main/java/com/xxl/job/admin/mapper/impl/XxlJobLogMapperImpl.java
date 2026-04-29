package com.xxl.job.admin.mapper.impl;

import com.xxl.job.admin.mapper.XxlJobLogMapper;
import com.xxl.job.admin.model.XxlJobLog;
import com.xxl.job.admin.repository.OffsetBasedPageRequest;
import com.xxl.job.admin.repository.XxlJobLogRepository;
import com.xxl.job.admin.repository.XxlJobRegistryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class XxlJobLogMapperImpl implements XxlJobLogMapper {

    private final XxlJobLogRepository xxlJobLogRepository;
    private final XxlJobRegistryRepository xxlJobRegistryRepository;

    public XxlJobLogMapperImpl(XxlJobLogRepository xxlJobLogRepository, XxlJobRegistryRepository xxlJobRegistryRepository) {
        this.xxlJobLogRepository = xxlJobLogRepository;
        this.xxlJobRegistryRepository = xxlJobRegistryRepository;
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
        long triggerDayCount = xxlJobLogRepository.count(XxlJobLogSpecifications.betweenTriggerTime(from, to));
        long triggerDayCountRunning = xxlJobLogRepository.count(XxlJobLogSpecifications.runningBetween(from, to));
        long triggerDayCountSuc = xxlJobLogRepository.count(XxlJobLogSpecifications.handleCodeBetween(from, to, 200));
        Map<String, Object> map = new HashMap<>();
        map.put("triggerDayCount", triggerDayCount);
        map.put("triggerDayCountRunning", triggerDayCountRunning);
        map.put("triggerDayCountSuc", triggerDayCountSuc);
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
        long count = xxlJobLogRepository.countByIdIn(logIds);
        xxlJobLogRepository.deleteAllByIdInBatch(logIds);
        return (int) count;
    }

    @Override
    public List<Long> findFailJobLogIds(int pagesize) {
        Specification<XxlJobLog> specification = XxlJobLogSpecifications.failNeedAlarmSpec();
        return xxlJobLogRepository.findAll(specification, PageRequest.of(0, pagesize, Sort.by(Sort.Direction.ASC, "id")))
                .getContent()
                .stream()
                .map(XxlJobLog::getId)
                .toList();
    }

    @Override
    @Transactional
    public int updateAlarmStatus(long logId, int oldAlarmStatus, int newAlarmStatus) {
        XxlJobLog exist = xxlJobLogRepository.findById(logId).orElse(null);
        if (exist == null) {
            return 0;
        }
        if (exist.getAlarmStatus() != oldAlarmStatus) {
            return 0;
        }
        exist.setAlarmStatus(newAlarmStatus);
        xxlJobLogRepository.save(exist);
        return 1;
    }

    @Override
    public List<Long> findLostJobIds(Date losedTime) {
        List<XxlJobLog> candidates = xxlJobLogRepository.findAll(XxlJobLogSpecifications.lostCandidateSpec(losedTime));
        if (candidates.isEmpty()) {
            return List.of();
        }

        List<String> addresses = candidates.stream()
                .map(XxlJobLog::getExecutorAddress)
                .filter(it -> it != null && !it.isBlank())
                .distinct()
                .toList();

        Set<String> aliveAddresses;
        if (addresses.isEmpty()) {
            aliveAddresses = Set.of();
        } else {
            aliveAddresses = xxlJobRegistryRepository.findByRegistryValueIn(addresses)
                    .stream()
                    .map(it -> it.getRegistryValue())
                    .collect(java.util.stream.Collectors.toSet());
        }

        return candidates.stream()
                .filter(it -> it.getExecutorAddress() == null || !aliveAddresses.contains(it.getExecutorAddress()))
                .map(XxlJobLog::getId)
                .toList();
    }

    private static class XxlJobLogSpecifications {
        private static Specification<XxlJobLog> betweenTriggerTime(Date from, Date to) {
            return (root, query, cb) -> cb.between(root.get("triggerTime"), from, to);
        }

        private static Specification<XxlJobLog> runningBetween(Date from, Date to) {
            return (root, query, cb) -> cb.and(
                    cb.between(root.get("triggerTime"), from, to),
                    root.get("triggerCode").in(0, 200),
                    cb.equal(root.get("handleCode"), 0)
            );
        }

        private static Specification<XxlJobLog> handleCodeBetween(Date from, Date to, int handleCode) {
            return (root, query, cb) -> cb.and(
                    cb.between(root.get("triggerTime"), from, to),
                    cb.equal(root.get("handleCode"), handleCode)
            );
        }

        private static Specification<XxlJobLog> failNeedAlarmSpec() {
            return (root, query, cb) -> {
                var running = cb.and(root.get("triggerCode").in(0, 200), cb.equal(root.get("handleCode"), 0));
                var success = cb.equal(root.get("handleCode"), 200);
                var isFail = cb.not(cb.or(running, success));
                return cb.and(isFail, cb.equal(root.get("alarmStatus"), 0));
            };
        }

        private static Specification<XxlJobLog> lostCandidateSpec(Date losedTime) {
            return (root, query, cb) -> cb.and(
                    cb.equal(root.get("triggerCode"), 200),
                    cb.equal(root.get("handleCode"), 0),
                    cb.lessThanOrEqualTo(root.get("triggerTime"), losedTime)
            );
        }

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
