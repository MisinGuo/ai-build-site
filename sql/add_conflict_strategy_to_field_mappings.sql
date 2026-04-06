-- 为字段映射表添加冲突策略字段
-- 对所有 target_location 均生效（category_relation 除外）
-- 默认策略因位置不同而有别：
--   main               → fill_empty（共享主表，保守；仅补全空字段）
--   relation           → overwrite （盒子专属，默认更新）
--   promotion_link     → overwrite （盒子专属，默认更新）
--   platform_data      → overwrite （盒子专属，默认更新）
--
-- 三种策略含义：
--   fill_empty  - 仅当目标字段为空时写入（适合主表共享字段，防止互相覆盖）
--   overwrite   - 每次导入都用新数据覆盖（适合平台专属字段，保持最新）
--   skip        - 首次导入后永远不再更新（适合手工维护、不希望被导入覆盖的字段）

ALTER TABLE `gb_box_field_mappings`
    ADD COLUMN `conflict_strategy` VARCHAR(20) DEFAULT 'fill_empty'
        COMMENT '字段冲突策略（main默认fill_empty；relation/promotion_link/platform_data默认overwrite）: fill_empty-补全空字段/overwrite-始终覆盖/skip-保持不变'
        AFTER `is_required`;
