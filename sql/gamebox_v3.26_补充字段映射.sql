-- 补充u2game平台缺失的字段映射配置
-- 执行此SQL文件以补充完整的字段映射规则

-- 推广链接相关 (promotion_link location)
INSERT INTO `gb_field_mappings` VALUES (52, 'u2game', 'game', 'downurl', 'download_url', 'string', 'promotion_link', NULL, NULL, '0', 200, '1', '', NOW(), '', NOW(), '推广下载链接（主链接）');
INSERT INTO `gb_field_mappings` VALUES (53, 'u2game', 'game', 'dwzdownurl', 'promote_url', 'string', 'promotion_link', NULL, NULL, '0', 201, '1', '', NOW(), '', NOW(), '短链推广链接');
INSERT INTO `gb_field_mappings` VALUES (54, 'u2game', 'game', 'qrcode', 'qrcode', 'string', 'promotion_link', NULL, NULL, '0', 202, '1', '', NOW(), '', NOW(), '二维码（base64格式）');
INSERT INTO `gb_field_mappings` VALUES (55, 'u2game', 'game', 'weburl', 'web_url', 'string', 'promotion_link', NULL, NULL, '0', 203, '1', '', NOW(), '', NOW(), '游戏详情页链接');
INSERT INTO `gb_field_mappings` VALUES (56, 'u2game', 'game', 'downurlmash', 'download_mash_url', 'string', 'promotion_link', NULL, NULL, '0', 204, '1', '', NOW(), '', NOW(), '马甲包下载链接');
INSERT INTO `gb_field_mappings` VALUES (57, 'u2game', 'game', 'dwzdownurlmash', 'promote_mash_url', 'string', 'promotion_link', NULL, NULL, '0', 205, '1', '', NOW(), '', NOW(), '马甲包短链链接');
INSERT INTO `gb_field_mappings` VALUES (58, 'u2game', 'game', 'weburl_qr', 'weburl_qrcode', 'string', 'promotion_link', NULL, NULL, '0', 206, '1', '', NOW(), '', NOW(), '详情页二维码');

-- 价格/充值相关字段 (main location - 这些是重要的业务字段)
INSERT INTO `gb_field_mappings` VALUES (59, 'u2game', 'game', 'firstpay', 'first_charge_domestic', 'string', 'main', NULL, NULL, '0', 10, '1', '', NOW(), '', NOW(), '国内首充金额');
INSERT INTO `gb_field_mappings` VALUES (60, 'u2game', 'game', 'otherpay', 'recharge_domestic', 'string', 'main', NULL, NULL, '0', 11, '1', '', NOW(), '', NOW(), '国内续充金额');
INSERT INTO `gb_field_mappings` VALUES (61, 'u2game', 'game', 'hw_firstpay', 'first_charge_overseas', 'string', 'main', NULL, NULL, '0', 12, '1', '', NOW(), '', NOW(), '海外首充金额');
INSERT INTO `gb_field_mappings` VALUES (62, 'u2game', 'game', 'hw_otherpay', 'recharge_overseas', 'string', 'main', NULL, NULL, '0', 13, '1', '', NOW(), '', NOW(), '海外续充金额');

-- 图片/视频资源 (ext location - 已存在video和photo，补充其他图片)
INSERT INTO `gb_field_mappings` VALUES (63, 'u2game', 'game', 'pic2', 'cover_image_2', 'string', 'ext', NULL, NULL, '0', 110, '1', '', NOW(), '', NOW(), '封面图2');
INSERT INTO `gb_field_mappings` VALUES (64, 'u2game', 'game', 'pic3', 'cover_image_3', 'string', 'ext', NULL, NULL, '0', 111, '1', '', NOW(), '', NOW(), '封面图3');
INSERT INTO `gb_field_mappings` VALUES (65, 'u2game', 'game', 'pic4', 'cover_image_4', 'string', 'ext', NULL, NULL, '0', 112, '1', '', NOW(), '', NOW(), '封面图4');
INSERT INTO `gb_field_mappings` VALUES (66, 'u2game', 'game', 'pic_xuanchuan', 'promo_image', 'string', 'ext', NULL, NULL, '0', 113, '1', '', NOW(), '', NOW(), '宣传图');

-- 盒子/内容相关 (ext location)
INSERT INTO `gb_field_mappings` VALUES (67, 'u2game', 'game', 'box_content', 'box_description', 'string', 'ext', NULL, NULL, '0', 114, '1', '', NOW(), '', NOW(), '盒子内容描述');
INSERT INTO `gb_field_mappings` VALUES (68, 'u2game', 'game', 'copy', 'copy_text', 'string', 'ext', NULL, NULL, '0', 115, '1', '', NOW(), '', NOW(), '复制文案（包含完整推广信息）');

-- 游戏特性标识 (ext location)
INSERT INTO `gb_field_mappings` VALUES (69, 'u2game', 'game', 'new', 'is_new', 'boolean', 'ext', NULL, NULL, '0', 116, '1', '', NOW(), '', NOW(), '是否新游（1=是 0=否）');
INSERT INTO `gb_field_mappings` VALUES (70, 'u2game', 'game', 'yuyue', 'is_reservation', 'boolean', 'ext', NULL, NULL, '0', 117, '1', '', NOW(), '', NOW(), '是否预约（1=是 0=否）');
INSERT INTO `gb_field_mappings` VALUES (71, 'u2game', 'game', 'h5', 'is_h5', 'boolean', 'ext', NULL, NULL, '0', 118, '1', '', NOW(), '', NOW(), '是否H5游戏（1=是 0=否）');
INSERT INTO `gb_field_mappings` VALUES (72, 'u2game', 'game', 'h5url', 'h5_url', 'string', 'ext', NULL, NULL, '0', 119, '1', '', NOW(), '', NOW(), 'H5游戏链接');
INSERT INTO `gb_field_mappings` VALUES (73, 'u2game', 'game', 'interchangeable_role', 'interchangeable_role', 'boolean', 'ext', NULL, NULL, '0', 120, '1', '', NOW(), '', NOW(), '是否支持角色互通');
INSERT INTO `gb_field_mappings` VALUES (74, 'u2game', 'game', 'version', 'app_version', 'int', 'ext', NULL, NULL, '0', 121, '1', '', NOW(), '', NOW(), 'APP版本号');
INSERT INTO `gb_field_mappings` VALUES (75, 'u2game', 'game', 'c_version', 'client_version', 'int', 'ext', NULL, NULL, '0', 122, '1', '', NOW(), '', NOW(), '客户端版本号');
INSERT INTO `gb_field_mappings` VALUES (76, 'u2game', 'game', 'userfc', 'user_fc_tag', 'int', 'ext', NULL, NULL, '0', 123, '1', '', NOW(), '', NOW(), '用户首充标签');

-- 推广控制/扩展标识 (ext location)
INSERT INTO `gb_field_mappings` VALUES (77, 'u2game', 'game', 'gn_extension', 'domestic_extension', 'boolean', 'ext', NULL, NULL, '0', 124, '1', '', NOW(), '', NOW(), '国内推广扩展（1=允许）');
INSERT INTO `gb_field_mappings` VALUES (78, 'u2game', 'game', 'hw_extension', 'overseas_extension', 'boolean', 'ext', NULL, NULL, '0', 125, '1', '', NOW(), '', NOW(), '海外推广扩展（1=允许）');
INSERT INTO `gb_field_mappings` VALUES (79, 'u2game', 'game', 'coupon_repeat', 'coupon_repeat', 'string', 'ext', NULL, NULL, '0', 126, '1', '', NOW(), '', NOW(), '优惠券重复领取规则');
INSERT INTO `gb_field_mappings` VALUES (80, 'u2game', 'game', 'game_source', 'game_source', 'string', 'ext', NULL, NULL, '0', 127, '1', '', NOW(), '', NOW(), '游戏来源标识');

-- 说明：
-- 1. target_location说明:
--    - main: 存入gb_games主表的核心字段
--    - ext: 存入gb_games_platform_ext表的platform_data JSON字段
--    - promotion_link: 存入gb_games_platform_ext表的promotion_links JSON数组
-- 2. 优先级order_num:
--    - 1-50: 主表字段
--    - 100-199: 扩展表普通字段
--    - 200+: 推广链接字段
-- 3. is_required: '1'=必填 '0'=可选
-- 4. status: '1'=启用 '0'=禁用
