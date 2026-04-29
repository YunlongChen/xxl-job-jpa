package com.xxl.job.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Date;

/**
 * Created by xuxueli on 16/9/30.
 */
@Entity
@Table(
        name = "xxl_job_registry",
        uniqueConstraints = {
                @UniqueConstraint(name = "i_g_k_v", columnNames = {"registry_group", "registry_key", "registry_value"})
        }
)
public class XxlJobRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "registry_group")
    private String registryGroup;

    @Column(name = "registry_key")
    private String registryKey;

    @Column(name = "registry_value")
    private String registryValue;

    @Column(name = "update_time")
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
