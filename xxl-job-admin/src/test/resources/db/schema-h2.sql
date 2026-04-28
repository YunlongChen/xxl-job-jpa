create table xxl_job_group (
    id int auto_increment primary key,
    app_name varchar(64) not null,
    title varchar(64) not null,
    address_type tinyint not null default 0,
    address_list clob,
    update_time timestamp
);

create table xxl_job_registry (
    id bigint auto_increment primary key,
    registry_group varchar(50) not null,
    registry_key varchar(255) not null,
    registry_value varchar(255) not null,
    update_time timestamp,
    constraint i_g_k_v unique (registry_group, registry_key, registry_value)
);

create table xxl_job_info (
    id int auto_increment primary key,
    job_group int not null,
    job_desc varchar(255) not null,
    add_time timestamp,
    update_time timestamp,
    author varchar(64),
    alarm_email varchar(255),
    schedule_type varchar(50) not null,
    schedule_conf varchar(128),
    misfire_strategy varchar(50) not null,
    executor_route_strategy varchar(50),
    executor_handler varchar(255),
    executor_param clob,
    executor_block_strategy varchar(50),
    executor_timeout int not null default 0,
    executor_fail_retry_count int not null default 0,
    glue_type varchar(50) not null,
    glue_source clob,
    glue_remark varchar(128),
    glue_updatetime timestamp,
    child_jobid varchar(255),
    trigger_status tinyint not null default 0,
    trigger_last_time bigint not null default 0,
    trigger_next_time bigint not null default 0
);

create table xxl_job_logglue (
    id int auto_increment primary key,
    job_id int not null,
    glue_type varchar(50),
    glue_source clob,
    glue_remark varchar(128) not null,
    add_time timestamp,
    update_time timestamp
);

create table xxl_job_log (
    id bigint auto_increment primary key,
    job_group int not null,
    job_id int not null,
    executor_address varchar(255),
    executor_handler varchar(255),
    executor_param clob,
    executor_sharding_param varchar(20),
    executor_fail_retry_count int not null default 0,
    trigger_time timestamp,
    trigger_code int not null default 0,
    trigger_msg clob,
    handle_time timestamp,
    handle_code int not null default 0,
    handle_msg clob,
    alarm_status tinyint not null default 0
);

create index I_trigger_time on xxl_job_log (trigger_time);
create index I_handle_code on xxl_job_log (handle_code);
create index I_jobgroup on xxl_job_log (job_group);
create index I_jobid on xxl_job_log (job_id);

create table xxl_job_log_report (
    id int auto_increment primary key,
    trigger_day timestamp,
    running_count int not null default 0,
    suc_count int not null default 0,
    fail_count int not null default 0,
    update_time timestamp,
    constraint i_trigger_day unique (trigger_day)
);

create table xxl_job_lock (
    lock_name varchar(50) primary key
);

create table xxl_job_user (
    id int auto_increment primary key,
    username varchar(50) not null,
    password varchar(100) not null,
    token varchar(100),
    role tinyint not null,
    permission varchar(255),
    constraint i_username unique (username)
);

