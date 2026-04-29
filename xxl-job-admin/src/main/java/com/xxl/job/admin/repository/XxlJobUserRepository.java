package com.xxl.job.admin.repository;

import com.xxl.job.admin.model.XxlJobUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface XxlJobUserRepository extends JpaRepository<XxlJobUser, Integer>, JpaSpecificationExecutor<XxlJobUser> {

    Optional<XxlJobUser> findFirstByUsername(String username);
}

