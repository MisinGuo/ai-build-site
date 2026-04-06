-- ====================================
-- 数据库优化脚本：分级存储策略
-- 策略：旧文章清理 content 节省空间，编辑时从对象存储加载已发布内容
-- 特点：所有文章都可以编辑，但内容来源不同
--      - 近期文章：从数据库 content 字段加载（支持模板变量修改）
--      - 旧文章：从对象存储加载已发布内容（模板变量已替换，不可修改变量关联）
-- 日期：2025-12-23
-- ====================================

USE ruoyi_system;

-- ==========================================
-- 配置说明
-- ==========================================
-- CLEANUP_DAYS: 文章发布后多少天清理 content 字段
-- 建议值：7-30 天（根据存储成本和编辑需求调整）
-- 清理后文章仍可编辑，但从对象存储加载内容
-- ==========================================

SET @CLEANUP_DAYS = 30;  -- 发布 30 天后清理数据库 content

-- 第一步：清理旧文章的 content 字段
-- 说明：清理已发布且超过保留期的文章 content，节省数据库空间
-- 编辑时将从对象存储自动加载已发布内容
UPDATE gb_articles 
SET content = NULL
WHERE 
    is_published = '1'  -- 已发布
    AND published_at IS NOT NULL
    AND DATEDIFF(NOW(), published_at) > @CLEANUP_DAYS  -- 超过保留期
    AND content IS NOT NULL  -- 有内容的才清理
    AND storage_key IS NOT NULL;  -- 确保对象存储有备份


-- 第三步：创建定时清理存储过程（可选）
-- 建议配置定时任务每天执行一次
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
    
    -- 记录清理日志
    SELECT CONCAT('已清理 ', cleaned_count, ' 篇文章的 content 字段') AS result;
    
    -- 优化表（回收空间）
    OPTIMIZE TABLE gb_articles;
END$$

DELIMITER ;

-- 第四步：查看清理效果
SELECT 
-- 第四步：查看清理效果
SELECT 
    COUNT(*) as total_articles,
    SUM(CASE WHEN content IS NOT NULL THEN 1 ELSE 0 END) as with_content,
    SUM(CASE WHEN content IS NULL THEN 1 ELSE 0 END) as without_content,
    ROUND(SUM(CASE WHEN content IS NULL THEN 1 ELSE 0 END) / COUNT(*) * 100, 2) as cleanup_percentage
FROM gb_articles
WHERE is_published = '1';

-- ==========================================
-- 使用说明
-- ==========================================
-- 1. 手动执行清理：
--    CALL sp_cleanup_old_articles_content(30);
--
-- 2. 配置定时任务（MySQL Event Scheduler）：
--    -- 启用事件调度器
--    SET GLOBAL event_scheduler = ON;
--    
--    -- 创建每天凌晨 2 点执行的定时任务
--    CREATE EVENT IF NOT EXISTS evt_cleanup_old_articles
--    ON SCHEDULE EVERY 1 DAY
--    STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 2 HOUR)
--    DO CALL sp_cleanup_old_articles_content(30);
--
-- 3. 查看数据库存储状态：
--    SELECT 
--      COUNT(*) as total,
--      SUM(CASE WHEN content IS NOT NULL THEN 1 ELSE 0 END) as in_db,
--      SUM(CASE WHEN content IS NULL THEN 1 ELSE 0 END) as in_storage_only
--    FROM gb_articles WHERE is_published = '1';
--
-- 4. 调整保留天数：
--    -- 修改 @CLEANUP_DAYS 变量后重新执行清理步骤
-- ==========================================

-- 完成说明：
-- 1. ✅ content 字段保留，但旧文章的内容已清理
-- 2. ✅ 近期文章（30天内）保留 content，支持模板变量编辑
-- 3. ✅ 旧文章 content 置为 NULL，编辑时从对象存储加载（模板变量已替换）
-- 4. ✅ 所有文章都可以编辑，只是内容来源不同
-- 5. ✅ 可通过定时任务持续清理旧数据，数据库容量持续优化

COMMIT;
