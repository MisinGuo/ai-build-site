-- =====================================================
-- 文章数据迁移到主文章架构
-- 版本: v3.07
-- 日期: 2026-01-13
-- 说明: 将现有的独立文章迁移到主文章+多语言文章架构
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 步骤1: 为每个没有主文章ID的文章创建主文章记录
-- =====================================================

-- 为每个没有 master_article_id 的文章创建对应的主文章
INSERT INTO gb_master_articles (
    site_id,
    default_locale,
    category_id,
    is_ai_generated,
    prompt_template_id,
    generation_count,
    last_generation_id,
    is_published,
    is_top,
    is_recommend,
    sort_order,
    status,
    del_flag,
    create_by,
    create_time,
    update_by,
    update_time,
    publish_time,
    remark
)
SELECT 
    a.site_id,
    COALESCE(a.locale, 'zh-CN') as default_locale,
    a.category_id,
    a.is_ai_generated,
    a.prompt_template_id,
    a.generation_count,
    a.last_generation_id,
    a.is_published,
    a.is_top,
    a.is_recommend,
    a.sort_order,
    a.status,
    a.del_flag,
    a.create_by,
    a.create_time,
    a.update_by,
    a.update_time,
    a.publish_time,
    CONCAT('从文章ID=', a.id, '迁移而来') as remark
FROM gb_articles a
WHERE a.master_article_id IS NULL
  AND a.del_flag = '0';

-- =====================================================
-- 步骤2: 更新文章的 master_article_id 字段
-- =====================================================

-- 创建临时映射表，记录文章ID和新创建的主文章ID的对应关系
CREATE TEMPORARY TABLE IF NOT EXISTS temp_article_master_mapping (
    article_id BIGINT,
    master_article_id BIGINT,
    PRIMARY KEY (article_id)
);

-- 填充映射表：通过创建时间、网站ID、分类ID等关键信息匹配
INSERT INTO temp_article_master_mapping (article_id, master_article_id)
SELECT 
    a.id as article_id,
    m.id as master_article_id
FROM gb_articles a
INNER JOIN gb_master_articles m ON (
    a.site_id = m.site_id
    AND COALESCE(a.category_id, 0) = COALESCE(m.category_id, 0)
    AND ABS(TIMESTAMPDIFF(SECOND, a.create_time, m.create_time)) < 5  -- 创建时间相差5秒内
    AND m.remark LIKE CONCAT('%从文章ID=', a.id, '迁移而来%')
)
WHERE a.master_article_id IS NULL
  AND a.del_flag = '0';

-- 更新文章的 master_article_id
UPDATE gb_articles a
INNER JOIN temp_article_master_mapping m ON a.id = m.article_id
SET a.master_article_id = m.master_article_id
WHERE a.master_article_id IS NULL;

-- =====================================================
-- 步骤3: 迁移游戏绑定关系（从文章级别到主文章级别）
-- =====================================================

-- 迁移游戏绑定
INSERT INTO gb_master_article_game_binding (
    master_article_id,
    game_id,
    is_active,
    create_by,
    create_time,
    update_by,
    update_time
)
SELECT DISTINCT
    m.master_article_id,
    ag.game_id,
    '1' as is_active,
    ag.create_by,
    ag.create_time,
    ag.update_by,
    ag.update_time
FROM gb_article_games ag
INNER JOIN temp_article_master_mapping m ON ag.article_id = m.article_id
WHERE ag.del_flag = '0'
  AND ag._deprecated = '1'
  AND NOT EXISTS (
    SELECT 1 FROM gb_master_article_game_binding magb
    WHERE magb.master_article_id = m.master_article_id
      AND magb.game_id = ag.game_id
  );

-- =====================================================
-- 步骤4: 迁移游戏盒子绑定关系
-- =====================================================

-- 迁移游戏盒子绑定
INSERT INTO gb_master_article_gamebox_binding (
    master_article_id,
    game_box_id,
    is_active,
    create_by,
    create_time,
    update_by,
    update_time
)
SELECT DISTINCT
    m.master_article_id,
    agb.game_box_id,
    '1' as is_active,
    agb.create_by,
    agb.create_time,
    agb.update_by,
    agb.update_time
FROM gb_article_game_boxes agb
INNER JOIN temp_article_master_mapping m ON agb.article_id = m.article_id
WHERE agb.del_flag = '0'
  AND agb._deprecated = '1'
  AND NOT EXISTS (
    SELECT 1 FROM gb_master_article_gamebox_binding magbb
    WHERE magbb.master_article_id = m.master_article_id
      AND magbb.game_box_id = agb.game_box_id
  );

-- =====================================================
-- 步骤5: 清理和验证
-- =====================================================

-- 显示迁移统计信息
SELECT 
    '迁移完成统计' as info,
    (SELECT COUNT(*) FROM gb_master_articles WHERE remark LIKE '%从文章ID=%') as '新创建的主文章数',
    (SELECT COUNT(*) FROM gb_articles WHERE master_article_id IS NOT NULL) as '已关联主文章的文章数',
    (SELECT COUNT(*) FROM gb_articles WHERE master_article_id IS NULL AND del_flag = '0') as '未关联主文章的文章数',
    (SELECT COUNT(*) FROM gb_master_article_game_binding) as '主文章-游戏绑定数',
    (SELECT COUNT(*) FROM gb_master_article_gamebox_binding) as '主文章-游戏盒子绑定数';

-- 显示需要手动处理的记录
SELECT 
    a.id,
    a.title,
    a.locale,
    a.site_id,
    a.status,
    '未能自动关联主文章，请手动检查' as note
FROM gb_articles a
WHERE a.master_article_id IS NULL
  AND a.del_flag = '0'
ORDER BY a.id;

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS temp_article_master_mapping;

-- =====================================================
-- 步骤6: 更新主文章表的备注字段（清理迁移标记）
-- =====================================================

-- 可选：清理迁移标记，使 remark 字段更干净
-- UPDATE gb_master_articles 
-- SET remark = NULL 
-- WHERE remark LIKE '%从文章ID=%';

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 迁移完成提示
-- =====================================================
-- 
-- 迁移步骤说明：
-- 1. ✅ 为每个独立文章创建了对应的主文章记录
-- 2. ✅ 建立了文章到主文章的关联关系
-- 3. ✅ 迁移了游戏绑定关系到主文章级别
-- 4. ✅ 迁移了游戏盒子绑定关系到主文章级别
-- 
-- 后续操作：
-- 1. 检查上方显示的"未能自动关联主文章"的记录
-- 2. 手动为这些记录创建或关联主文章
-- 3. 如果需要，可以取消注释步骤6的SQL来清理迁移标记
-- 4. 验证前端显示是否正常
-- 
-- 注意事项：
-- - 旧的文章绑定关系（gb_article_games, gb_article_game_boxes）未删除
-- - 如需删除，请在确认迁移成功后手动执行
-- - 主文章的 remark 字段包含了迁移来源信息，便于追溯
-- =====================================================
