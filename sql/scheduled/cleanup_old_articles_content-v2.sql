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



-- =============================================================================
-- 第六步：创建管理视图
-- =============================================================================

CREATE OR REPLACE VIEW `v_ai_game_relations_pending` AS
SELECT ag.id, ag.article_id, a.title AS article_title, ag.game_id, g.name AS game_name,
       ag.confidence_score, ag.ai_reasoning, ag.create_time
FROM gb_article_games ag
LEFT JOIN gb_articles a ON ag.article_id = a.id
LEFT JOIN gb_games g ON ag.game_id = g.id
WHERE ag.relation_source = 'ai' AND ag.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_gamebox_relations_pending` AS
SELECT agb.id, agb.article_id, a.title AS article_title, agb.game_box_id, gb.name AS gamebox_name,
       agb.confidence_score, agb.ai_reasoning, agb.create_time
FROM gb_article_game_boxes agb
LEFT JOIN gb_articles a ON agb.article_id = a.id
LEFT JOIN gb_game_boxes gb ON agb.game_box_id = gb.id
WHERE agb.relation_source = 'ai' AND agb.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_drama_relations_pending` AS
SELECT ad.id, ad.article_id, a.title AS article_title, ad.drama_id, d.name AS drama_name,
       ad.confidence_score, ad.ai_reasoning, ad.create_time
FROM gb_article_dramas ad
LEFT JOIN gb_articles a ON ad.article_id = a.id
LEFT JOIN gb_dramas d ON ad.drama_id = d.id
WHERE ad.relation_source = 'ai' AND ad.review_status = '0';

CREATE OR REPLACE VIEW `v_ai_relations_statistics` AS
SELECT 'game' AS product_type, COUNT(*) AS total,
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END) AS pending,
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END) AS approved,
       AVG(confidence_score) AS avg_confidence
FROM gb_article_games WHERE relation_source='ai'
UNION ALL
SELECT 'gamebox', COUNT(*), 
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END),
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END),
       AVG(confidence_score)
FROM gb_article_game_boxes WHERE relation_source='ai'
UNION ALL
SELECT 'drama', COUNT(*),
       SUM(CASE WHEN review_status='0' THEN 1 ELSE 0 END),
       SUM(CASE WHEN review_status='1' THEN 1 ELSE 0 END),
       AVG(confidence_score)
FROM gb_article_dramas WHERE relation_source='ai';

SELECT '✓ 管理视图创建完成' AS status;