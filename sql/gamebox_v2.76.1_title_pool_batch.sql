-- ===================================================================
-- 标题池批次管理功能
-- 版本：v2.76.1
-- 日期：2026-01-01
-- 说明：为标题池添加批次管理，支持按网站和日期自动生成批次
-- ===================================================================

-- 1. 创建标题池批次管理表
CREATE TABLE IF NOT EXISTS `gb_title_pool_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `site_id` bigint NOT NULL DEFAULT 0 COMMENT '所属网站ID(0表示全局默认)',
  `batch_code` varchar(100) NOT NULL COMMENT '批次号(格式：SITE_{siteId}_YYYYMMDD_序号)',
  `batch_name` varchar(200) DEFAULT NULL COMMENT '批次名称',
  `import_date` date NOT NULL COMMENT '导入日期',
  `import_source` varchar(100) DEFAULT NULL COMMENT '导入来源(json/excel/manual)',
  `title_count` int DEFAULT 0 COMMENT '标题数量',
  `category_id` bigint DEFAULT NULL COMMENT '关联分类ID',
  `status` char(1) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_batch_code` (`batch_code`) USING BTREE,
  KEY `idx_site_id` (`site_id`) USING BTREE,
  KEY `idx_import_date` (`import_date`) USING BTREE,
  KEY `idx_site_date` (`site_id`, `import_date`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标题池批次管理表';

-- 2. 为现有数据创建默认批次（如果import_batch字段不为空）
-- 先聚合数据
DROP TEMPORARY TABLE IF EXISTS temp_batch_data;
CREATE TEMPORARY TABLE temp_batch_data AS
SELECT 
  COALESCE(t.site_id, 0) as site_id,
  t.import_batch,
  DATE(MIN(t.create_time)) as import_date,
  COUNT(*) as title_count,
  MIN(t.create_time) as create_time
FROM gb_article_title_pool t
WHERE t.import_batch IS NOT NULL 
  AND t.import_batch != ''
  AND t.del_flag = '0'
GROUP BY COALESCE(t.site_id, 0), t.import_batch
ORDER BY site_id, import_date, create_time;

-- 生成批次号并插入
SET @row_num = 0;
SET @prev_site = NULL;
SET @prev_date = NULL;

INSERT INTO `gb_title_pool_batch` (
  `site_id`, 
  `batch_code`, 
  `batch_name`,
  `import_date`, 
  `import_source`, 
  `title_count`,
  `status`,
  `create_time`,
  `create_by`
)
SELECT 
  site_id,
  CONCAT('SITE_', site_id, '_', DATE_FORMAT(import_date, '%Y%m%d'), '_', 
         LPAD(@row_num := IF(@prev_site = site_id AND @prev_date = import_date, @row_num + 1, 1), 3, '0')) as batch_code,
  CONCAT('历史导入-', import_batch) as batch_name,
  import_date,
  'historical' as import_source,
  title_count,
  '1' as status,
  create_time,
  'system' as create_by
FROM (
  SELECT 
    *,
    @prev_site := site_id as prev_site_dummy,
    @prev_date := import_date as prev_date_dummy
  FROM temp_batch_data
  ORDER BY site_id, import_date, create_time
) t;

-- 3. 更新现有标题池数据的批次号（关联到新创建的批次）
UPDATE gb_article_title_pool t
INNER JOIN gb_title_pool_batch b 
  ON COALESCE(t.site_id, 0) = b.site_id 
  AND b.batch_name COLLATE utf8mb4_unicode_ci = CONCAT('历史导入-', t.import_batch) COLLATE utf8mb4_unicode_ci
SET t.import_batch = b.batch_code
WHERE t.import_batch IS NOT NULL 
  AND t.import_batch != ''
  AND t.del_flag = '0';

-- 4. 验证批次表数据
SELECT 
  id,
  site_id,
  batch_code,
  batch_name,
  import_date,
  import_source,
  title_count,
  status,
  create_time
FROM gb_title_pool_batch
ORDER BY site_id, import_date DESC, id DESC
LIMIT 20;

-- 5. 验证标题池数据的批次号更新情况
SELECT 
  site_id,
  import_batch,
  COUNT(*) as title_count
FROM gb_article_title_pool
WHERE del_flag = '0'
  AND import_batch IS NOT NULL
GROUP BY site_id, import_batch
ORDER BY site_id, import_batch
LIMIT 20;

-- ===================================================================
-- 完成
-- ===================================================================
