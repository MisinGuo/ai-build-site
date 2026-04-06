-- ============================================================================
-- 字段映射功能迁移至盒子管理 - 数据库变更
-- 版本: v3.31
-- 日期: 2026-01-XX
-- 说明: 在 gb_field_mappings 表中添加 box_id 字段，实现字段映射配置与盒子的关联
-- ============================================================================

USE `ry-vue`;

-- ----------------------------------------------------------------------------
-- 1. 添加 box_id 列
-- ----------------------------------------------------------------------------
ALTER TABLE `gb_field_mappings` 
ADD COLUMN `box_id` bigint NULL COMMENT '关联的盒子ID，NULL表示通用配置（基于平台+资源类型的默认配置）' 
AFTER `id`;

-- ----------------------------------------------------------------------------
-- 2. 添加索引以提升查询性能
-- ----------------------------------------------------------------------------
CREATE INDEX `idx_box_id` ON `gb_field_mappings`(`box_id`);

-- 组合索引：平台+资源类型（用于通用配置查询）
CREATE INDEX `idx_platform_resource_type` ON `gb_field_mappings`(`platform`, `resource_type`);

-- ----------------------------------------------------------------------------
-- 3. 添加外键约束（可选 - 根据实际需求决定是否启用）
-- ----------------------------------------------------------------------------
-- 注意：如果启用外键约束，删除盒子时会自动删除关联的字段映射配置
-- 如果不希望级联删除，可以注释掉下面的语句

ALTER TABLE `gb_field_mappings`
ADD CONSTRAINT `fk_field_mapping_box` 
FOREIGN KEY (`box_id`) REFERENCES `gb_game_boxes`(`id`) 
ON DELETE CASCADE  -- 删除盒子时级联删除字段映射
ON UPDATE CASCADE; -- 更新盒子ID时级联更新

-- ----------------------------------------------------------------------------
-- 4. 数据迁移（将现有的字段映射关联到盒子）
-- ----------------------------------------------------------------------------
-- 策略：为每个平台的每个盒子创建独立的字段映射配置副本
-- 这样每个盒子都有自己的配置，互不影响

-- 步骤1: 查看当前的字段映射和盒子分布情况
-- SELECT platform, COUNT(*) as mapping_count FROM gb_field_mappings WHERE box_id IS NULL GROUP BY platform;
-- SELECT platform, COUNT(*) as box_count FROM gb_game_boxes GROUP BY platform;

-- 步骤2: 为每个盒子复制对应平台的字段映射配置
-- 注意：这会为每个盒子创建独立的配置副本

-- 创建临时存储过程来执行迁移
DELIMITER $$

DROP PROCEDURE IF EXISTS migrate_field_mappings_to_boxes$$

CREATE PROCEDURE migrate_field_mappings_to_boxes()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_box_id BIGINT;
    DECLARE v_platform VARCHAR(50);
    DECLARE v_mapping_id BIGINT;
    DECLARE v_source_field VARCHAR(200);
    DECLARE v_target_field VARCHAR(100);
    DECLARE v_field_type VARCHAR(20);
    DECLARE v_target_location VARCHAR(50);
    DECLARE v_transform_expression TEXT;
    DECLARE v_value_mapping TEXT;
    DECLARE v_default_value VARCHAR(255);
    DECLARE v_is_required CHAR(1);
    DECLARE v_sort_order INT;
    DECLARE v_status CHAR(1);
    DECLARE v_remark VARCHAR(500);
    DECLARE v_resource_type VARCHAR(20);
    
    -- 游标：遍历所有盒子
    DECLARE box_cursor CURSOR FOR 
        SELECT id, platform 
        FROM gb_game_boxes 
        WHERE platform IS NOT NULL AND platform != '';
    
    -- 游标：遍历指定平台的字段映射
    DECLARE mapping_cursor CURSOR FOR
        SELECT id, platform, resource_type, source_field, target_field, field_type, 
               target_location, transform_expression, value_mapping, default_value, 
               is_required, sort_order, status, remark
        FROM gb_field_mappings
        WHERE platform = v_platform 
          AND resource_type = 'game'  -- 只迁移游戏相关的映射
          AND box_id IS NULL;  -- 只迁移未关联的通用配置
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始迁移
    OPEN box_cursor;
    
    box_loop: LOOP
        FETCH box_cursor INTO v_box_id, v_platform;
        IF done THEN
            LEAVE box_loop;
        END IF;
        
        -- 检查该盒子是否已有映射配置
        IF EXISTS (SELECT 1 FROM gb_field_mappings WHERE box_id = v_box_id) THEN
            -- 该盒子已有配置，跳过
            ITERATE box_loop;
        END IF;
        
        -- 为该盒子复制对应平台的映射配置
        SET done = FALSE;
        
        BEGIN
            DECLARE mapping_done INT DEFAULT FALSE;
            DECLARE CONTINUE HANDLER FOR NOT FOUND SET mapping_done = TRUE;
            
            OPEN mapping_cursor;
            
            mapping_loop: LOOP
                FETCH mapping_cursor INTO v_mapping_id, v_platform, v_resource_type,
                    v_source_field, v_target_field, v_field_type, v_target_location,
                    v_transform_expression, v_value_mapping, v_default_value,
                    v_is_required, v_sort_order, v_status, v_remark;
                
                IF mapping_done THEN
                    LEAVE mapping_loop;
                END IF;
                
                -- 为该盒子插入一条新的映射配置（复制）
                INSERT INTO gb_field_mappings (
                    box_id, platform, resource_type, source_field, target_field, 
                    field_type, target_location, transform_expression, value_mapping, 
                    default_value, is_required, sort_order, status, remark, 
                    create_by, create_time
                ) VALUES (
                    v_box_id, v_platform, v_resource_type, v_source_field, v_target_field,
                    v_field_type, v_target_location, v_transform_expression, v_value_mapping,
                    v_default_value, v_is_required, v_sort_order, v_status, v_remark,
                    'system', NOW()
                );
                
            END LOOP mapping_loop;
            
            CLOSE mapping_cursor;
        END;
        
        SET done = FALSE;
        
    END LOOP box_loop;
    
    CLOSE box_cursor;
    
    -- 输出迁移结果
    SELECT 
        '数据迁移完成' as message,
        COUNT(DISTINCT box_id) as migrated_boxes,
        COUNT(*) as total_mappings
    FROM gb_field_mappings 
    WHERE box_id IS NOT NULL;
    
END$$

DELIMITER ;

-- 执行迁移
CALL migrate_field_mappings_to_boxes();

-- 清理临时存储过程
DROP PROCEDURE IF EXISTS migrate_field_mappings_to_boxes;

-- 步骤3: 验证迁移结果
SELECT 
    b.id as box_id,
    b.name as box_name,
    b.platform,
    COUNT(fm.id) as mapping_count
FROM gb_game_boxes b
LEFT JOIN gb_field_mappings fm ON b.id = fm.box_id
WHERE b.platform IS NOT NULL
GROUP BY b.id, b.name, b.platform
ORDER BY b.platform, b.id;

-- 步骤4: 查看未迁移的通用配置（可选保留作为模板）
SELECT 
    platform,
    resource_type,
    COUNT(*) as config_count
FROM gb_field_mappings
WHERE box_id IS NULL
GROUP BY platform, resource_type;

-- 可选：如果要删除旧的通用配置（建议先备份）
-- DELETE FROM gb_field_mappings WHERE box_id IS NULL;

-- ----------------------------------------------------------------------------
-- 5. 验证数据完整性
-- ----------------------------------------------------------------------------
-- 查询已关联盒子的字段映射数量
-- SELECT 
--     b.id AS box_id, 
--     b.name AS box_name, 
--     b.platform,
--     COUNT(fm.id) AS mapping_count
-- FROM gb_game_boxes b
-- LEFT JOIN gb_field_mappings fm ON b.id = fm.box_id
-- GROUP BY b.id, b.name, b.platform
-- ORDER BY b.id;

-- 查询未关联盒子的字段映射（通用配置）
-- SELECT 
--     id,
--     platform,
--     resource_type,
--     source_field,
--     target_field
-- FROM gb_field_mappings
-- WHERE box_id IS NULL
-- ORDER BY platform, resource_type, sort_order;

-- ----------------------------------------------------------------------------
-- 6. 回滚脚本（如果出现问题，可执行以下语句恢复）
-- ----------------------------------------------------------------------------
-- 删除外键约束
-- ALTER TABLE `gb_field_mappings` DROP FOREIGN KEY `fk_field_mapping_box`;

-- 删除索引
-- DROP INDEX `idx_box_id` ON `gb_field_mappings`;
-- DROP INDEX `idx_platform_resource_type` ON `gb_field_mappings`;

-- 删除列
-- ALTER TABLE `gb_field_mappings` DROP COLUMN `box_id`;

-- ============================================================================
-- 变更说明
-- ============================================================================
-- 
-- 新的字段映射配置查询逻辑：
-- 1. 优先级1：查询指定盒子的专属配置（box_id = ?）
-- 2. 优先级2：查询通用配置（box_id IS NULL AND platform = ? AND resource_type = ?）
--
-- 这种设计的优点：
-- - 最小化数据库变更，不影响现有表结构
-- - 保留独立配置的灵活性（通用配置仍可使用）
-- - 支持盒子级别的精细化配置
-- - 向后兼容：现有数据（box_id=NULL）仍可正常使用
--
-- 使用场景：
-- - 场景1：每个盒子有独立的字段映射规则 → 创建盒子时同时配置字段映射
-- - 场景2：多个盒子共享相同规则 → 使用通用配置（box_id=NULL）
-- - 场景3：大部分使用通用规则，个别盒子有特殊规则 → 混合使用
--
-- ============================================================================
