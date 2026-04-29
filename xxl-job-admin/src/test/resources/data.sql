insert into xxl_job_group (id, app_name, title, address_type, address_list, update_time)
values (1, 'xxl-job-executor-sample', '通用执行器Sample', 0, null, current_timestamp);

insert into xxl_job_info (
    id, job_group, job_desc, add_time, update_time, author, alarm_email,
    schedule_type, schedule_conf, misfire_strategy,
    executor_route_strategy, executor_handler, executor_param, executor_block_strategy, executor_timeout, executor_fail_retry_count,
    glue_type, glue_source, glue_remark, glue_updatetime,
    child_jobid, trigger_status, trigger_last_time, trigger_next_time
) values (
    1, 1, '示例任务01', current_timestamp, current_timestamp, 'XXL', '',
    'NONE', '', 'DO_NOTHING',
    'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0,
    'BEAN', '', 'init', current_timestamp,
    '', 0, 0, 0
);

insert into xxl_job_user (id, username, password, token, role, permission)
values (1, 'admin', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', null, 1, null);

insert into xxl_job_lock (lock_name) values ('schedule_lock');

