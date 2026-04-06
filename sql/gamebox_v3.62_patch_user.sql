-- =====================================================
-- 存量用户个人站点修复脚本
-- 说明：为所有没有个人站点的用户创建专属个人默认站点
-- 适用：在引入 gb_user_site_relation 之后注册但未正确
--       创建个人站点的用户，以及历史存量用户
-- =====================================================

-- 查看哪些用户缺少个人站点（执行前先确认）
SELECT u.user_id, u.user_name
FROM sys_user u
WHERE u.del_flag = '0'
  AND u.user_id NOT IN (
      SELECT r.user_id
      FROM gb_user_site_relation r
      WHERE r.is_default = 1
  );

-- =====================================================
-- 修复：为每个缺少个人站点的用户逐一插入
-- （MySQL 8.0+ 可直接使用以下批量语句）
-- =====================================================

-- Step 1: 批量创建缺失的个人站点
INSERT INTO gb_sites (name, code, domain, site_type, is_personal, sort_order, status, del_flag, default_locale, create_by, create_time)
SELECT
    CONCAT(u.user_name, '的个人默认站点'),
    CONCAT('_personal_', u.user_id),
    CONCAT('_personal_', u.user_id),
    'personal',
    1,
    -1,
    '1',
    '0',
    'zh-CN',
    u.user_name,
    NOW()
FROM sys_user u
WHERE u.del_flag = '0'
  AND u.user_id NOT IN (
      SELECT r.user_id
      FROM gb_user_site_relation r
      WHERE r.is_default = 1
  )
  -- 避免 code/domain 重复（已创建过的跳过）
  AND CONCAT('_personal_', u.user_id) NOT IN (
      SELECT code FROM gb_sites WHERE del_flag = '0'
  );

-- Step 2: 建立用户-个人站点关联（is_default=1）
INSERT IGNORE INTO gb_user_site_relation (user_id, site_id, is_default)
SELECT u.user_id, s.id, 1
FROM sys_user u
INNER JOIN gb_sites s ON s.code = CONCAT('_personal_', u.user_id)
WHERE u.del_flag = '0'
  AND s.del_flag = '0'
  AND s.is_personal = 1
  -- 仅处理还没有 is_default=1 记录的用户
  AND u.user_id NOT IN (
      SELECT r.user_id
      FROM gb_user_site_relation r
      WHERE r.is_default = 1
  );

-- 验证结果
SELECT u.user_id, u.user_name, s.id AS personal_site_id, s.name AS personal_site_name
FROM sys_user u
INNER JOIN gb_user_site_relation r ON r.user_id = u.user_id AND r.is_default = 1
INNER JOIN gb_sites s ON s.id = r.site_id
WHERE u.del_flag = '0'
ORDER BY u.user_id;