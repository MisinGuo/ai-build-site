-- =============================================
-- 分类系统初始化数据
-- =============================================
-- 版本：v1.1.0
-- 日期：2025-12-23
-- 说明：初始化各种类型的分类数据
-- ⚠️ 重要：此脚本基于 gamebox_init.sql 中的实际表结构
-- =============================================

-- 清空现有分类数据（可选，生产环境慎用）
-- TRUNCATE TABLE gb_categories;

-- =============================================
-- 1. 游戏分类 (game)
-- =============================================

-- 一级游戏分类
INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'game', '仙侠', 'xianxia', '⚔️', '仙侠修真类游戏', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '传奇', 'chuanqi', '🛡️', '传奇经典类游戏', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '魔幻', 'mohuan', '🧙', '魔幻题材游戏', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '二次元', 'erciyuan', '🎨', '二次元动漫风格游戏', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '武侠', 'wuxia', '🥋', '武侠江湖类游戏', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '三国', 'sanguo', '🏛️', '三国题材游戏', 6, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '西游', 'xiyou', '🐒', '西游记题材游戏', 7, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '卡牌', 'kapai', '🎴', '卡牌策略游戏', 8, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '策略', 'celue', '🎯', '策略战争游戏', 9, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', 'RPG', 'rpg', '🎮', '角色扮演游戏', 10, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '挂机', 'guaji', '⏰', '放置挂机游戏', 11, '1', '0', 'admin', NOW()),
(NULL, 0, 'game', '竞技', 'jingji', '🏆', '竞技对战游戏', 12, '1', '0', 'admin', NOW());

-- 二级游戏分类（仙侠子分类示例）
INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) 
SELECT NULL, id, 'game', '修仙', 'xiuxian', '✨', '修仙飞升类', 1, '1', '0', 'admin', NOW()
FROM gb_categories WHERE category_type = 'game' AND slug = 'xianxia' AND del_flag = '0';

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT NULL, id, 'game', '仙侠动作', 'xianxia-action', '⚡', '动作战斗类仙侠', 2, '1', '0', 'admin', NOW()
FROM gb_categories WHERE category_type = 'game' AND slug = 'xianxia' AND del_flag = '0';

-- 二级游戏分类（传奇子分类示例）
INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT NULL, id, 'game', '三职业', 'sanzhi', '👥', '战法道三职业', 1, '1', '0', 'admin', NOW()
FROM gb_categories WHERE category_type = 'game' AND slug = 'chuanqi' AND del_flag = '0';

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time)
SELECT NULL, id, 'game', '单职业', 'danzhi', '👤', '单职业传奇', 2, '1', '0', 'admin', NOW()
FROM gb_categories WHERE category_type = 'game' AND slug = 'chuanqi' AND del_flag = '0';

-- =============================================
-- 2. 短剧分类 (drama)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'drama', '都市', 'urban', '🏙️', '都市现代题材', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '古装', 'costume', '👘', '古装历史题材', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '悬疑', 'mystery', '🔍', '悬疑推理题材', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '甜宠', 'sweet', '💕', '甜宠爱情题材', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '复仇', 'revenge', '⚔️', '复仇反击题材', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '玄幻', 'xuanhuan', '🌟', '玄幻奇幻题材', 6, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '霸总', 'ceo', '💼', '霸道总裁题材', 7, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '武侠', 'martial', '🥋', '武侠江湖题材', 8, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '宫斗', 'palace', '👑', '宫廷斗争题材', 9, '1', '0', 'admin', NOW()),
(NULL, 0, 'drama', '重生', 'rebirth', '🔄', '重生逆袭题材', 10, '1', '0', 'admin', NOW());

-- =============================================
-- 3. 文章分类 (article)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'article', '攻略', 'guide', '📖', '游戏攻略教程', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '新闻', 'news', '📰', '游戏新闻资讯', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '评测', 'review', '⭐', '游戏评测推荐', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '活动', 'event', '🎉', '游戏活动公告', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '礼包', 'gift', '🎁', '礼包码发放', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '开服', 'server', '🚀', '开服表预告', 6, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '充值', 'recharge', '💰', '充值福利相关', 7, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '福利', 'welfare', '🎊', '福利活动', 8, '1', '0', 'admin', NOW()),
(NULL, 0, 'article', '更新', 'update', '🔄', '版本更新日志', 9, '1', '0', 'admin', NOW());

-- =============================================
-- 4. 网站分类 (website)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'website', '官方网站', 'official', '🏢', '官方主站', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'website', '推广网站', 'promotion', '📢', '推广营销站点', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'website', '社区论坛', 'community', '💬', '玩家社区论坛', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'website', '下载站', 'download', '📥', '游戏下载站', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'website', '资讯站', 'news-site', '📰', '新闻资讯站', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'website', '工具站', 'tool', '🔧', '辅助工具站', 6, '1', '0', 'admin', NOW());

-- =============================================
-- 5. 游戏盒子分类 (gamebox)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'gamebox', '折扣盒子', 'discount-box', '💰', '提供游戏折扣充值', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'gamebox', 'BT盒子', 'bt-box', '⚡', 'BT变态版游戏', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'gamebox', '官服盒子', 'official-box', '🏢', '官方正版游戏', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'gamebox', '首充盒子', 'first-charge-box', '🎁', '首充返利盒子', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'gamebox', '公益盒子', 'welfare-box', '❤️', '公益服盒子', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'gamebox', '满V盒子', 'full-vip-box', '👑', '上线满V盒子', 6, '1', '0', 'admin', NOW());

-- =============================================
-- 6. 文档分类 (document)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'document', '入门教程', 'tutorial', '📚', '新手入门教程', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'document', 'API文档', 'api-doc', '🔌', '接口文档说明', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'document', '常见问题', 'faq', '❓', 'FAQ常见问题', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'document', '开发指南', 'dev-guide', '💻', '开发者指南', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'document', '部署文档', 'deploy', '🚀', '部署运维文档', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'document', '更新日志', 'changelog', '📝', '版本更新记录', 6, '1', '0', 'admin', NOW());

-- =============================================
-- 7. 存储配置分类 (storage)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'storage', '云存储', 'cloud-storage', '☁️', '云存储服务配置', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', '本地存储', 'local-storage', '💾', '本地存储配置', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', '对象存储', 'object-storage', '📦', '对象存储服务', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', '图片存储', 'image-storage', '🖼️', '图片专用存储', 4, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', '文件存储', 'file-storage', '📁', '文件存储服务', 5, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', '视频存储', 'video-storage', '🎬', '视频存储服务', 6, '1', '0', 'admin', NOW()),
(NULL, 0, 'storage', 'CDN加速', 'cdn', '🚀', 'CDN加速服务', 7, '1', '0', 'admin', NOW());

-- =============================================
-- 8. 其他分类 (other)
-- =============================================

INSERT INTO gb_categories (site_id, parent_id, category_type, name, slug, icon, description, sort_order, status, del_flag, create_by, create_time) VALUES
(NULL, 0, 'other', '未分类', 'uncategorized', '📦', '暂未分类内容', 1, '1', '0', 'admin', NOW()),
(NULL, 0, 'other', '推荐', 'recommend', '⭐', '推荐内容', 2, '1', '0', 'admin', NOW()),
(NULL, 0, 'other', '热门', 'hot', '🔥', '热门内容', 3, '1', '0', 'admin', NOW()),
(NULL, 0, 'other', '精选', 'featured', '💎', '精选优质内容', 4, '1', '0', 'admin', NOW());

-- =============================================
-- 查询验证
-- =============================================

-- 统计各类型分类数量
SELECT 
    category_type,
    COUNT(*) as count,
    COUNT(CASE WHEN parent_id = 0 THEN 1 END) as parent_count,
    COUNT(CASE WHEN parent_id != 0 THEN 1 END) as child_count
FROM gb_categories
GROUP BY category_type
ORDER BY 
    FIELD(category_type, 'game', 'drama', 'article', 'website', 'gamebox', 'document', 'storage', 'other');

-- 查看分类树结构（游戏分类示例）
SELECT 
    CASE 
        WHEN parent_id = 0 THEN name
        ELSE CONCAT('  └─ ', name)
    END as category_tree,
    slug,
    icon,
    sort_order
FROM gb_categories
WHERE category_type = 'game'
ORDER BY parent_id, sort_order;

COMMIT;
