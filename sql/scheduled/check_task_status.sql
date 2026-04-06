-- 查询定时任务配置
SELECT 
    id,
    task_name,
    task_type,
    cron_expression,
    task_enabled,
    daily_quota,
    daily_generated,
    next_run_time,
    quartz_job_id,
    create_time
FROM gb_article_generation_tasks
ORDER BY create_time DESC
LIMIT 5;

-- 查询对应的Quartz作业
SELECT 
    job_id,
    job_name,
    job_group,
    invoke_target,
    cron_expression,
    status,
    create_time
FROM sys_job
WHERE job_group = 'ARTICLE_GENERATION'
ORDER BY create_time DESC;
