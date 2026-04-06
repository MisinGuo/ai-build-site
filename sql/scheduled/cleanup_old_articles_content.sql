-- ====================================
-- 定时任务：清理旧文章content字段
-- 功能：自动清理发布超过30天的文章的content字段，节省数据库空间
-- 执行频率：每天凌晨2点自动执行
-- 创建日期：2025-12-23
-- ====================================

USE ruoyi_system;

-- ==========================================
-- 第一步：启用MySQL事件调度器
-- ==========================================
-- 检查事件调度器状态
SELECT @@event_scheduler;

-- 启用事件调度器（如果未启用）
SET GLOBAL event_scheduler = ON;

-- ==========================================
-- 第二步：创建清理存储过程
-- ==========================================
DELIMITER $$

DROP PROCEDURE IF EXISTS sp_cleanup_old_articles_content$$

CREATE PROCEDURE sp_cleanup_old_articles_content(IN cleanup_days INT)
BEGIN
    DECLARE cleaned_count INT DEFAULT 0;
    
    -- 清理超过保留期的已发布文章内容
    UPDATE gb_articles 
    SET content = NULL
    WHERE 
        is_published = '1'
        AND published_at IS NOT NULL
        AND DATEDIFF(NOW(), published_at) > cleanup_days
        AND content IS NOT NULL
        AND storage_key IS NOT NULL;
    
    SET cleaned_count = ROW_COUNT();
    
    -- 记录清理日志到临时表（可选）
    INSERT INTO sys_oper_log (title, business_type, method, request_method, operator_type, oper_name, oper_time, status)
    VALUES (
        '文章内容清理',
        9,  -- 其他操作
        'sp_cleanup_old_articles_content',
        'SCHEDULED',
        1,  -- 后台用户
        'system',
        NOW(),
        0   -- 成功
    );
    
    -- 返回清理结果
    SELECT CONCAT('已清理 ', cleaned_count, ' 篇文章的 content 字段') AS result;
    
    -- 优化表（回收空间）
    OPTIMIZE TABLE gb_articles;
END$$

DELIMITER ;

-- ==========================================
-- 第三步：创建定时任务事件
-- ==========================================
-- 删除已存在的事件（如果有）
DROP EVENT IF EXISTS evt_cleanup_old_articles;

-- 创建每天凌晨2点执行的定时任务
CREATE EVENT evt_cleanup_old_articles
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 2 HOUR)
ON COMPLETION PRESERVE
ENABLE
COMMENT '定时清理旧文章content字段，保留30天内的文章内容'
DO CALL sp_cleanup_old_articles_content(30);

-- ==========================================
-- 第四步：验证定时任务
-- ==========================================
-- 查看所有事件
SHOW EVENTS WHERE Db = 'ruoyi_system';

-- 查看事件详情
SHOW CREATE EVENT evt_cleanup_old_articles;

-- ==========================================
-- 手动执行命令（测试用）
-- ==========================================
-- 手动执行一次清理
-- CALL sp_cleanup_old_articles_content(30);

-- 查看清理效果
-- SELECT 
--     COUNT(*) as total_articles,
--     SUM(CASE WHEN content IS NOT NULL THEN 1 ELSE 0 END) as with_content,
--     SUM(CASE WHEN content IS NULL THEN 1 ELSE 0 END) as without_content,
--     ROUND(SUM(CASE WHEN content IS NULL THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) as cleanup_percentage
-- FROM gb_articles
-- WHERE is_published = '1';

-- ==========================================
-- 管理命令
-- ==========================================
-- 禁用定时任务
-- ALTER EVENT evt_cleanup_old_articles DISABLE;

-- 启用定时任务
-- ALTER EVENT evt_cleanup_old_articles ENABLE;

-- 删除定时任务
-- DROP EVENT IF EXISTS evt_cleanup_old_articles;

-- 修改清理天数（需要先删除再重新创建事件）
-- DROP EVENT IF EXISTS evt_cleanup_old_articles;
-- CREATE EVENT evt_cleanup_old_articles
-- ON SCHEDULE EVERY 1 DAY
-- STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 2 HOUR)
-- ON COMPLETION PRESERVE
-- ENABLE
-- DO CALL sp_cleanup_old_articles_content(7);  -- 改为7天

-- ==========================================
-- 注意事项
-- ==========================================
-- 1. 确保MySQL事件调度器已启用
-- 2. 清理前确认对象存储已正确保存文章内容
-- 3. 可以调整cleanup_days参数（建议7-30天）
-- 4. 定时任务会在每天凌晨2点自动执行
-- 5. 清理后的文章仍可编辑，但内容从对象存储加载
-- 6. 系统会自动记录清理日志到sys_oper_log表
