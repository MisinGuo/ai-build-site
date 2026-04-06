-- ================================
-- 游戏盒子系统 v3.29 补丁
-- 功能: 添加u2game平台完整字段映射配置
-- 日期: 2026-01-26
-- 说明: 将u2game的40+字段映射到游戏表和扩展表
-- ================================

-- 删除现有的u2game映射（如果存在）
DELETE FROM `gb_field_mappings` WHERE platform = 'u2game' AND resource_type = 'game';

-- ================================
-- 主表字段映射（存储到 gb_games 表）
-- ================================

-- 1. 游戏名称
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'gamename', 'name', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏名称');

-- 2. 图标 (pic1)
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'pic1', 'icon_url', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏图标');

-- 3. 封面图 (pic2作为主封面)
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'pic2', 'cover_url', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏封面');

-- 4. 视频
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'video', 'video_url', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏视频');

-- 5. 游戏描述
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'excerpt', 'description', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏描述');

-- 6. 游戏类型
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'gametype', 'game_type', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏类型');

-- 7. 设备类型 (0=android, 1=ios, 2=both) -> (1=android, 2=ios, 3=both)
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'device_type', 'device_support', 'integer', 'main', NULL, 'if(value==0) return 1; if(value==1) return 2; if(value==2) return 3; return 3;', '1', 0, NULL, NULL, NOW(), NULL, NULL, '设备支持');

-- 8. 折扣标签
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'welfare', 'discount_label', 'string', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '折扣标签');

-- 9. 首充折扣（国内）
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'firstpay', 'first_charge_discount', 'decimal', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '首充折扣');

-- 10. 续充折扣（国内）
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'otherpay', 'recharge_discount', 'decimal', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '续充折扣');

-- 11. 上线时间
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'addtime', 'launch_time', 'datetime', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '上线时间');

-- 12. 是否新游
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'new', 'is_new', 'integer', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '是否新游');

-- 13. 更新时间
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'updatetime', 'update_time', 'datetime', 'main', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '更新时间');

-- 14. 截图数组 (photo数组转JSON)
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'photo', 'screenshots', 'json', 'main', NULL, 'return JSON.stringify(value.map(item => item.url));', '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏截图');

-- ================================
-- 扩展表字段映射（存储到 gb_games_platform_ext.platform_data）
-- ================================

-- 15. pic3 额外封面
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'pic3', 'pic3', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '额外封面3');

-- 16. pic4 额外封面
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'pic4', 'pic4', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '额外封面4');

-- 17. 海外首充折扣
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'hw_firstpay', 'hw_firstpay', 'decimal', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '海外首充折扣');

-- 18. 海外续充折扣
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'hw_otherpay', 'hw_otherpay', 'decimal', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '海外续充折扣');

-- 19. 盒子内容（推广文案）
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'box_content', 'box_content', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '盒子推广内容');

-- 20. 推广备注
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'text_cps', 'text_cps', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '推广备注');

-- 21. 复制文本
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'copy', 'copy_text', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '复制文本');

-- 22. 版本类型 (0=official, 1=discount, 2=bt)
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'edition', 'edition', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '版本类型');

-- 23. H5链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'h5url', 'h5url', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'H5链接');

-- 24. 是否H5游戏
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'h5', 'is_h5', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '是否H5');

-- 25. 角色互通
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'interchangeable_role', 'interchangeable_role', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '角色互通');

-- 26. 是否预约
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'yuyue', 'yuyue', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '是否预约');

-- 27. 国内推广
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'gn_extension', 'gn_extension', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '国内推广');

-- 28. 海外推广
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'hw_extension', 'hw_extension', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '海外推广');

-- 29. CPS/CPA信息
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'cps_cpa', 'cps_cpa', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'CPS/CPA');

-- 30. 海外CPS/CPA
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'hw_cps_cpa', 'hw_cps_cpa', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '海外CPS/CPA');

-- 31. 优惠券重复
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'coupon_repeat', 'coupon_repeat', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '优惠券重复');

-- 32. 版本号
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'version', 'version', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '版本号');

-- 33. C端版本
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'c_version', 'c_version', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'C端版本');

-- 34. 游戏来源
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'game_source', 'game_source', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '游戏来源');

-- 35. 宣传图
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'pic_xuanchuan', 'pic_xuanchuan', 'string', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '宣传图');

-- 36. 严格模式
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'strict', 'strict', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '严格模式');

-- 37. 用户首充
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'userfc', 'userfc', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '用户首充');

-- 38. CPS比例
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'cps_ratio', 'cps_ratio', 'decimal', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'CPS比例');

-- 39. 海外比例
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'hw_ratio', 'hw_ratio', 'decimal', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '海外比例');

-- 40. u2game游戏ID（保存原始ID）
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'id', 'platform_game_id', 'integer', 'ext', NULL, NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '平台游戏ID');

-- ================================
-- 推广链接映射（存储到 gb_games_platform_ext.promotion_links）
-- ================================

-- 41. 下载链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'downurl', 'download_url', 'string', 'promotion_link', 'download', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '下载链接');

-- 42. 短链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'dwzdownurl', 'short_download_url', 'string', 'promotion_link', 'download', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '短链接');

-- 43. Mash下载链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'downurlmash', 'mash_download_url', 'string', 'promotion_link', 'download', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'Mash下载链接');

-- 44. Mash短链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'dwzdownurlmash', 'mash_short_url', 'string', 'promotion_link', 'download', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, 'Mash短链接');

-- 45. 二维码
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'qrcode', 'qrcode', 'string', 'promotion_link', 'qrcode', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '下载二维码');

-- 46. 网页链接
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'weburl', 'web_url', 'string', 'promotion_link', 'web', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '网页链接');

-- 47. 网页二维码
INSERT INTO `gb_field_mappings` VALUES (NULL, 'u2game', 'game', 'weburl_qr', 'web_qrcode', 'string', 'promotion_link', 'qrcode', NULL, '1', 0, NULL, NULL, NOW(), NULL, NULL, '网页二维码');

-- ================================
-- 验证映射配置
-- ================================
SELECT 
    COUNT(*) as total_mappings,
    SUM(CASE WHEN target_location = 'main' THEN 1 ELSE 0 END) as main_fields,
    SUM(CASE WHEN target_location = 'ext' THEN 1 ELSE 0 END) as ext_fields,
    SUM(CASE WHEN target_location = 'promotion_link' THEN 1 ELSE 0 END) as promotion_links
FROM gb_field_mappings 
WHERE platform = 'u2game' AND resource_type = 'game';

-- ================================
-- 说明:
-- 1. 主表字段(14个): 存储核心游戏信息到 gb_games 表
-- 2. 扩展字段(26个): 存储平台特定数据到 gb_games_platform_ext.platform_data (JSON)
-- 3. 推广链接(7个): 存储推广链接到 gb_games_platform_ext.promotion_links (JSON)
-- 4. 总计47个字段映射，覆盖u2game所有重要字段
-- 5. device_type做了转换: u2game(0/1/2) -> 系统(1/2/3)
-- 6. photo数组转换为JSON字符串数组
-- 7. 所有映射状态为'1'(启用)
-- ================================
