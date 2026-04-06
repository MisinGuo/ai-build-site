package com.ruoyi.web.controller.gamebox;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Date;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbGameChangeLog;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;
import com.ruoyi.gamebox.service.IGbBoxGameRelationService;
import com.ruoyi.gamebox.service.ITranslationService;
import com.ruoyi.gamebox.service.IGbMasterArticleHomepageBindingService;
import com.ruoyi.gamebox.domain.GbMasterArticleHomepageBinding;
import com.ruoyi.gamebox.service.IFieldMappingApplyService;
import com.ruoyi.gamebox.service.IGbGameImportService;
import com.ruoyi.gamebox.mapper.GbGameChangeLogMapper;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/game")
public class GbGameController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(GbGameController.class);
    
    @Autowired
    private IGbGameService gbGameService;
    
    @Autowired
    private IGbArticleService gbArticleService;
    
    @Autowired
    private IGbGameCategoryRelationService gameCategoryRelationService;
    
    @Autowired
    private IGbBoxGameRelationService boxGameRelationService;
    
    @Autowired
    private ITranslationService translationService;
    
    @Autowired
    private IGbMasterArticleHomepageBindingService masterArticleHomepageBindingService;
    
    @Autowired
    private IFieldMappingApplyService fieldMappingApplyService;

    @Autowired
    private IGbGameImportService gbGameImportService;

    @Autowired
    private GbGameChangeLogMapper gameChangeLogMapper;

    private static final String TABLE_GAMES = "gb_games";
    private static final String TABLE_GAME_CATEGORIES = "gb_game_category_relations";

    /**
     * 查询游戏列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:list')")
    @GetMapping("/list")
    public TableDataInfo list(GbGame gbGame)
    {
        startPage();
        List<GbGame> list = gbGameService.selectGbGameList(gbGame);
        return getDataTable(list);
    }

    /**
     * 查询符合筛选条件的所有游戏ID（不分页，用于跨页全选）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:list')")
    @GetMapping("/ids")
    public AjaxResult listIds(GbGame gbGame)
    {
        List<Long> ids = gbGameService.selectGbGameIds(gbGame);
        return success(ids);
    }

    /**
     * 查询游戏列表（支持多语言）
     */
    @GetMapping("/list/multilang")
    public TableDataInfo listWithTranslations(GbGame gbGame, HttpServletRequest request)
    {
        // 获取语言参数
        String locale = request.getParameter("locale");
        if (locale == null || locale.trim().isEmpty()) {
            locale = request.getHeader("Accept-Language");
        }
        if (locale == null || locale.trim().isEmpty()) {
            locale = "zh-CN"; // 默认语言
        }
        
        startPage();
        List<GbGame> list = gbGameService.selectGbGameList(gbGame);
        
        // 如果是默认语言，直接返回原始数据
        if ("zh-CN".equals(locale)) {
            return getDataTable(list);
        }
        
        // 批量获取翻译数据
        List<Long> gameIds = list.stream().map(GbGame::getId).collect(Collectors.toList());
        Map<Long, Map<String, String>> translations = translationService.getBatchEntityTranslations("game", gameIds, locale);
        
        // 应用翻译
        List<Map<String, Object>> translatedList = list.stream().map(game -> {
            Map<String, Object> gameMap = new HashMap<>();
            // 复制原始游戏数据
            gameMap.put("id", game.getId());
            gameMap.put("siteId", game.getSiteId());
            gameMap.put("categoryName", game.getCategoryName());
            gameMap.put("gameType", game.getGameType());
            gameMap.put("iconUrl", game.getIconUrl());
            gameMap.put("coverUrl", game.getCoverUrl());
            gameMap.put("downloadUrl", game.getDownloadUrl());
            gameMap.put("status", game.getStatus());
            gameMap.put("createTime", game.getCreateTime());
            gameMap.put("updateTime", game.getUpdateTime());
            
            // 应用翻译（如果存在）
            Map<String, String> gameTranslations = translations.get(game.getId());
            if (gameTranslations != null) {
                gameMap.put("name", gameTranslations.getOrDefault("name", game.getName()));
                gameMap.put("description", gameTranslations.getOrDefault("description", game.getDescription()));
                gameMap.put("subtitle", gameTranslations.getOrDefault("subtitle", game.getSubtitle()));
            } else {
                // 使用原始数据
                gameMap.put("name", game.getName());
                gameMap.put("description", game.getDescription());
                gameMap.put("subtitle", game.getSubtitle());
            }
            
            return gameMap;
        }).collect(Collectors.toList());
        
        return getDataTable(translatedList);
    }

    /**
     * 导出游戏列表
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:export')")
    @Log(title = "游戏管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(GbGame gbGame)
    {
        List<GbGame> list = gbGameService.selectGbGameList(gbGame);
        ExcelUtil<GbGame> util = new ExcelUtil<GbGame>(GbGame.class);
        util.exportExcel(list, "游戏数据");
    }

    /**
     * 获取游戏详细信息
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbGameService.selectGbGameById(id));
    }

    /**
     * 新增游戏
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:add')")
    @Log(title = "游戏管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody GbGame gbGame)
    {
        gbGame.setCreateBy(getUsername());
        int result = gbGameService.insertGbGame(gbGame);
        if (result > 0) {
            // 返回新增游戏的ID，供前端保存分类关联使用
            return success(gbGame.getId());
        }
        return error();
    }

    /**
     * 批量新增游戏
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:add')")
    @Log(title = "游戏管理", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@Validated @RequestBody List<GbGame> games)
    {
        if (games == null || games.isEmpty()) {
            log.warn("[批量导入API] 请求参数为空，返回错误");
            return error("游戏列表不能为空");
        }
        
        log.info("[批量导入API] 收到批量导入请求，游戏数量: {}", games.size());
        long startTime = System.currentTimeMillis();
        
        String username = getUsername();
        log.debug("[批量导入API] 操作用户: {}", username);
        
        // 设置创建者
        for (GbGame game : games) {
            game.setCreateBy(username);
        }
        
        // 批量插入并返回游戏ID列表
        List<Long> gameIds = gbGameService.batchInsertGames(games);
        
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("[批量导入API] 批量导入完成，请求游戏数: {}，成功创建ID数: {}，总耗时: {}ms", 
                 games.size(), gameIds.size(), totalTime);
        
        return success(gameIds);
    }

    /**
     * 修改游戏
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:edit')")
    @Log(title = "游戏管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody GbGame gbGame)
    {
        GbGame before = gbGameService.selectGbGameById(gbGame.getId());
        gbGame.setUpdateBy(getUsername());
        int rows = gbGameService.updateGbGame(gbGame);
        if (rows > 0) {
            GbGame after = gbGameService.selectGbGameById(gbGame.getId());
            recordGameUpdateChangeLog(before, after);
        }
        return toAjax(rows);
    }
    
    /**
     * 更新游戏状态
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:edit')")
    @Log(title = "游戏状态", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public AjaxResult updateStatus(@RequestBody Map<String, Object> params)
    {
        Long id = Long.parseLong(params.get("id").toString());
        String status = params.get("status").toString();

        GbGame before = gbGameService.selectGbGameById(id);
        
        GbGame gbGame = new GbGame();
        gbGame.setId(id);
        gbGame.setStatus(status);
        gbGame.setUpdateBy(getUsername());

        int rows = gbGameService.updateGbGame(gbGame);
        if (rows > 0) {
            GbGame after = gbGameService.selectGbGameById(id);
            recordGameUpdateChangeLog(before, after);
        }

        return toAjax(rows);
    }

    private void recordGameUpdateChangeLog(GbGame before, GbGame after) {
        if (before == null || after == null || after.getId() == null) {
            return;
        }

        String beforeSnapshot = JSON.toJSONString(before);
        String afterSnapshot = JSON.toJSONString(after);
        if (beforeSnapshot.equals(afterSnapshot)) {
            return;
        }

        GbGameChangeLog logEntry = new GbGameChangeLog();
        logEntry.setBatchNo("MANUAL-" + System.currentTimeMillis());
        logEntry.setGameId(after.getId());
        logEntry.setGameName(after.getName());
        logEntry.setChangeType("UPDATE");
        logEntry.setTargetTable(TABLE_GAMES);
        logEntry.setTargetId(after.getId());
        logEntry.setBeforeSnapshot(beforeSnapshot);
        logEntry.setAfterSnapshot(afterSnapshot);
        logEntry.setCreateTime(new Date());
        logEntry.setCreateBy(getUsername());
        gameChangeLogMapper.insert(logEntry);
    }

    /**
     * 删除游戏
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:remove')")
    @Log(title = "游戏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gbGameService.deleteGbGameByIds(ids));
    }
    
    /**
     * 获取游戏的分类列表
     */
    @GetMapping("/{id}/categories")
    public AjaxResult getGameCategories(@PathVariable("id") Long id)
    {
        try {
            List<GbGameCategoryRelation> categories = gameCategoryRelationService.selectCategoriesByGameId(id);
            return success(categories);
        } catch (Exception e) {
            logger.error("获取游戏分类列表失败", e);
            return error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存游戏的分类关联
     */
    @PostMapping("/{id}/categories")
    public AjaxResult saveGameCategories(@PathVariable("id") Long id, @RequestBody List<Map<String, Object>> categories)
    {
        try {
            String beforeSnapshot = buildCategorySnapshot(id);
            // 转换为 GbGameCategoryRelation 列表
            List<GbGameCategoryRelation> relations = categories.stream().map(cat -> {
                GbGameCategoryRelation relation = new GbGameCategoryRelation();
                relation.setGameId(id);
                relation.setCategoryId(Long.valueOf(cat.get("categoryId").toString()));
                relation.setIsPrimary(cat.get("isPrimary") != null ? cat.get("isPrimary").toString() : "0");
                // sortOrder 可以根据列表顺序自动生成
                return relation;
            }).collect(Collectors.toList());
            
            // 使用 service 保存（会先删除旧关联再插入新关联）
            gameCategoryRelationService.saveGameCategoryRelations(id, relations);
            recordCategoryChangeLog(id, beforeSnapshot);
            return success();
        } catch (Exception e) {
            logger.error("保存游戏分类关联失败", e);
            return error("保存分类关联失败: " + e.getMessage());
        }
    }

    /**
     * 追加游戏的分类关联（合并而不是覆盖）
     */
    @PostMapping("/{id}/categories/append")
    public AjaxResult appendGameCategories(@PathVariable("id") Long id, @RequestBody List<Map<String, Object>> categories)
    {
        try {
            String beforeSnapshot = buildCategorySnapshot(id);
            // 转换为 GbGameCategoryRelation 列表
            List<GbGameCategoryRelation> relations = categories.stream().map(cat -> {
                GbGameCategoryRelation relation = new GbGameCategoryRelation();
                relation.setGameId(id);
                relation.setCategoryId(Long.valueOf(cat.get("categoryId").toString()));
                relation.setIsPrimary(cat.get("isPrimary") != null ? cat.get("isPrimary").toString() : "0");
                return relation;
            }).collect(Collectors.toList());
            
            // 使用 service 追加分类（不会删除旧分类，只追加新的）
            int count = gameCategoryRelationService.appendGameCategoryRelations(id, relations);
            recordCategoryChangeLog(id, beforeSnapshot);
            return success("成功追加 " + count + " 个分类关联");
        } catch (Exception e) {
            logger.error("追加游戏分类关联失败", e);
            return error("追加分类关联失败: " + e.getMessage());
        }
    }

    /**
     * 批量保存多个游戏的分类关联
     * 请求数据格式: [{gameId: 1, categories: [{categoryId: 1, isPrimary: '1'}]}]
     */
    @PostMapping("/batch/categories")
    public AjaxResult batchSaveGameCategories(@RequestBody List<Map<String, Object>> gameCategoryMappings)
    {
        try {
            Map<Long, String> beforeSnapshots = new HashMap<>();
            // 汇总所有需要删除的游戏ID
            List<Long> gameIds = gameCategoryMappings.stream()
                .map(mapping -> Long.valueOf(mapping.get("gameId").toString()))
                .collect(Collectors.toList());

            for (Long gameId : gameIds) {
                beforeSnapshots.put(gameId, buildCategorySnapshot(gameId));
            }
            
            // 批量删除旧的分类关联
            gameCategoryRelationService.batchDeleteByGameIds(gameIds);
            
            // 汇总所有需要插入的关联记录
            List<GbGameCategoryRelation> allRelations = new ArrayList<>();
            for (Map<String, Object> mapping : gameCategoryMappings) {
                Long gameId = Long.valueOf(mapping.get("gameId").toString());
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> categories = (List<Map<String, Object>>) mapping.get("categories");
                
                for (int i = 0; i < categories.size(); i++) {
                    Map<String, Object> cat = categories.get(i);
                    GbGameCategoryRelation relation = new GbGameCategoryRelation();
                    relation.setGameId(gameId);
                    relation.setCategoryId(Long.valueOf(cat.get("categoryId").toString()));
                    relation.setIsPrimary(cat.get("isPrimary") != null ? cat.get("isPrimary").toString() : "0");
                    relation.setSortOrder(i);
                    allRelations.add(relation);
                }
            }
            
            // 批量插入所有关联记录
            int count = 0;
            if (!allRelations.isEmpty()) {
                count = gameCategoryRelationService.batchInsertRelations(allRelations);
            }

            for (Long gameId : gameIds) {
                recordCategoryChangeLog(gameId, beforeSnapshots.get(gameId));
            }
            
            return success("成功保存 " + count + " 个分类关联");
        } catch (Exception e) {
            logger.error("批量保存游戏分类关联失败", e);
            return error("批量保存分类关联失败: " + e.getMessage());
        }
    }

    /**
     * 批量追加多个游戏的分类关联
     * 请求数据格式: [{gameId: 1, categories: [{categoryId: 1, isPrimary: '1'}]}]
     */
    @PostMapping("/batch/categories/append")
    public AjaxResult batchAppendGameCategories(@RequestBody List<Map<String, Object>> gameCategoryMappings)
    {
        try {
            Map<Long, String> beforeSnapshots = new HashMap<>();
            // 汇总所有需要追加的关联记录
            List<GbGameCategoryRelation> allRelations = new ArrayList<>();
            for (Map<String, Object> mapping : gameCategoryMappings) {
                Long gameId = Long.valueOf(mapping.get("gameId").toString());
                if (!beforeSnapshots.containsKey(gameId)) {
                    beforeSnapshots.put(gameId, buildCategorySnapshot(gameId));
                }
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> categories = (List<Map<String, Object>>) mapping.get("categories");
                
                for (Map<String, Object> cat : categories) {
                    GbGameCategoryRelation relation = new GbGameCategoryRelation();
                    relation.setGameId(gameId);
                    relation.setCategoryId(Long.valueOf(cat.get("categoryId").toString()));
                    relation.setIsPrimary(cat.get("isPrimary") != null ? cat.get("isPrimary").toString() : "0");
                    allRelations.add(relation);
                }
            }
            
            // 批量追加（去重）
            int count = 0;
            if (!allRelations.isEmpty()) {
                count = gameCategoryRelationService.batchAppendRelations(allRelations);
            }

            for (Long gameId : beforeSnapshots.keySet()) {
                recordCategoryChangeLog(gameId, beforeSnapshots.get(gameId));
            }
            
            return success("成功追加 " + count + " 个分类关联");
        } catch (Exception e) {
            logger.error("批量追加游戏分类关联失败", e);
            return error("批量追加分类关联失败: " + e.getMessage());
        }
    }

    private String buildCategorySnapshot(Long gameId) {
        List<GbGameCategoryRelation> categories = gameCategoryRelationService.selectCategoriesByGameId(gameId);
        List<Map<String, Object>> normalized = new ArrayList<>();
        for (GbGameCategoryRelation relation : categories) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("categoryId", relation.getCategoryId());
            row.put("categoryName", relation.getCategoryName());
            row.put("categorySlug", relation.getCategorySlug());
            row.put("categoryIcon", relation.getCategoryIcon());
            row.put("sortOrder", relation.getSortOrder());
            row.put("isPrimary", relation.getIsPrimary());
            normalized.add(row);
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("categories", normalized);
        return JSON.toJSONString(payload);
    }

    private void recordCategoryChangeLog(Long gameId, String beforeSnapshot) {
        GbGame game = gbGameService.selectGbGameById(gameId);
        String afterSnapshot = buildCategorySnapshot(gameId);
        if (beforeSnapshot != null && beforeSnapshot.equals(afterSnapshot)) {
            return;
        }
        GbGameChangeLog logEntry = new GbGameChangeLog();
        logEntry.setBatchNo("MANUAL-" + System.currentTimeMillis());
        logEntry.setGameId(gameId);
        logEntry.setGameName(game == null ? "" : game.getName());
        logEntry.setChangeType("UPDATE");
        logEntry.setTargetTable(TABLE_GAME_CATEGORIES);
        logEntry.setTargetId(gameId);
        logEntry.setBeforeSnapshot(beforeSnapshot);
        logEntry.setAfterSnapshot(afterSnapshot);
        logEntry.setCreateTime(new Date());
        logEntry.setCreateBy(getUsername());
        gameChangeLogMapper.insert(logEntry);
    }
    
    /**
     * 获取游戏的主页绑定
     */
    @GetMapping("/{id}/homepage")
    public AjaxResult getGameHomepage(@PathVariable("id") Long id)
    {
        try {
            GbMasterArticleHomepageBinding binding = masterArticleHomepageBindingService.selectByGameIdWithTitle(id);
            return success(binding);
        } catch (Exception e) {
            logger.error("获取游戏主页绑定失败", e);
            return error("获取主页绑定失败: " + e.getMessage());
        }
    }
    
    /**
     * 绑定主文章到游戏主页
     */
    @PostMapping("/{id}/homepage")
    public AjaxResult bindGameHomepage(@PathVariable("id") Long id, @RequestBody Map<String, Object> params)
    {
        try {
            Long masterArticleId = Long.valueOf(params.get("masterArticleId").toString());
            boolean force = params.containsKey("force") && Boolean.parseBoolean(params.get("force").toString());
            
            AjaxResult result = masterArticleHomepageBindingService.bindToGame(masterArticleId, id, force);
            return result;
        } catch (Exception e) {
            logger.error("绑定游戏主页失败", e);
            return error("绑定主页失败: " + e.getMessage());
        }
    }
    
    /**
     * 解绑游戏主页
     */
    @DeleteMapping("/{id}/homepage")
    public AjaxResult unbindGameHomepage(@PathVariable("id") Long id, @RequestBody Map<String, Object> params)
    {
        try {
            Long masterArticleId = Long.valueOf(params.get("masterArticleId").toString());
            
            int result = masterArticleHomepageBindingService.unbindFromGame(masterArticleId, id);
            return result > 0 ? success("解绑主页成功") : error("解绑主页失败");
        } catch (Exception e) {
            logger.error("解绑游戏主页失败", e);
            return error("解绑主页失败: " + e.getMessage());
        }
    }
    
    /**
     * 多平台游戏数据导入（使用字段映射配置）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:game:add')")
    @Log(title = "多平台游戏导入", businessType = BusinessType.IMPORT)
    @PostMapping("/import/platform")
    public AjaxResult importFromPlatform(@RequestBody Map<String, Object> params)
    {
        try {
            // 获取参数
            Long boxId = params.get("boxId") != null ? Long.valueOf(params.get("boxId").toString()) : null;
            Long siteId = params.get("siteId") != null ? Long.valueOf(params.get("siteId").toString()) : null;
            String jsonData = params.get("jsonData") != null ? params.get("jsonData").toString() : null;
            Boolean preview = params.get("preview") instanceof Boolean ? (Boolean) params.get("preview") : false;
            Boolean updateExisting = params.get("updateExisting") instanceof Boolean ? (Boolean) params.get("updateExisting") : false;
            
            if (boxId == null) {
                return error("盒子ID不能为空");
            }
            if (siteId == null) {
                return error("网站ID不能为空");
            }
            if (jsonData == null || jsonData.trim().isEmpty()) {
                return error("导入数据不能为空");
            }
            
            // 解析原始 JSON 字符串，提取游戏列表（与原子工具路径逻辑一致）
            List<Map<String, Object>> gameDataList;
            try {
                gameDataList = extractGameListFromJson(jsonData.trim());
            } catch (Exception e) {
                return error("JSON 解析失败: " + e.getMessage());
            }
            
            if (gameDataList.isEmpty()) {
                return error("未找到可导入的游戏数据");
            }
            
            log.info("接收到 {} 条游戏数据，准备应用字段映射", gameDataList.size());
            
            // 预览模式：只返回转换后的数据，不保存到数据库
            if (preview) {
                List<Map<String, Object>> previewData = new ArrayList<>();
                List<String> errorList = new ArrayList<>();

                // 批量应用字段映射：只查询一次数据库，避免 N+1
                List<Map<String, Object>> mappedDataList;
                try {
                    mappedDataList = fieldMappingApplyService.batchApplyBoxFieldMappings(gameDataList, boxId, "game");
                } catch (Exception e) {
                    return error("字段映射加载失败: " + e.getMessage());
                }

                if (mappedDataList == null || mappedDataList.isEmpty()) {
                    return error("字段映射配置为空或转换失败，请检查字段映射配置");
                }

                // 遍历映射结果，构建预览数据（mappedDataList 与 gameDataList 等长、顺序一致）
                for (int i = 0; i < gameDataList.size(); i++) {
                    Map<String, Object> gameData = gameDataList.get(i);
                    Map<String, Object> mappedData = i < mappedDataList.size() ? mappedDataList.get(i) : null;
                    String gameName = gameData.get("gamename") != null ? gameData.get("gamename").toString() : "未知游戏";
                    try {
                        if (mappedData != null && !mappedData.isEmpty()) {
                            Map<String, Object> mainFields = (Map<String, Object>) mappedData.get("mainFields");
                            Map<String, Object> extensionFields = (Map<String, Object>) mappedData.get("extensionFields");
                            Map<String, Object> promotionLinks = (Map<String, Object>) mappedData.get("promotionLinks");
                            Map<String, Object> platformData = (Map<String, Object>) mappedData.get("platformData");
                            Map<String, Object> relationFields = (Map<String, Object>) mappedData.get("relationFields");
                            Map<String, Object> categoryRelationFields = (Map<String, Object>) mappedData.get("categoryRelationFields");

                            // 构建预览数据：合并所有字段便于前端展示
                            Map<String, Object> previewItem = new HashMap<>();

                            // 1. 主表字段
                            if (mainFields != null) {
                                previewItem.putAll(mainFields);
                            }

                            // 2. 扩展字段（添加前缀标识）
                            if (extensionFields != null && !extensionFields.isEmpty()) {
                                extensionFields.forEach((key, value) -> {
                                    previewItem.put("ext_" + key, value);
                                });
                            }

                            // 3. 推广链接信息（统计数量）
                            int promotionLinksCount = 0;
                            if (promotionLinks != null && !promotionLinks.isEmpty()) {
                                promotionLinksCount = promotionLinks.size();
                                previewItem.put("promotionLinks", promotionLinks);
                            }
                            previewItem.put("promotionLinksCount", promotionLinksCount);

                            // 4. 分类关联字段（从 categoryRelationFields 获取 category_id，统一包装为 List）
                            if (categoryRelationFields != null && categoryRelationFields.containsKey("category_id")) {
                                Object catId = categoryRelationFields.get("category_id");
                                if (catId instanceof List) {
                                    previewItem.put("category_id", catId);
                                } else if (catId != null) {
                                    List<Object> catList = new ArrayList<>();
                                    catList.add(catId);
                                    previewItem.put("category_id", catList);
                                }
                            }

                            // 5. 关联表字段信息（统计数量）
                            int relationFieldsCount = 0;
                            if (relationFields != null && !relationFields.isEmpty()) {
                                relationFieldsCount = relationFields.size();
                                previewItem.put("relationFields", relationFields);
                            }
                            previewItem.put("relationFieldsCount", relationFieldsCount);

                            // 6. 平台原始数据的关键字段（用于调试）
                            previewItem.put("_originalName", gameData.get("gamename"));
                            previewItem.put("_boxId", boxId);

                            // 7. 保存平台数据用于后续导入
                            if (platformData != null && !platformData.isEmpty()) {
                                previewItem.put("platformData", platformData);
                            }

                            previewData.add(previewItem);
                        } else {
                            errorList.add(gameName + ": 字段映射配置为空或转换失败");
                        }
                    } catch (Exception e) {
                        errorList.add(gameName + ": " + e.getMessage());
                        log.error("预览数据转换失败: {}", gameName, e);
                    }
                }
                
                Map<String, Object> result = new HashMap<>();
                result.put("previewData", previewData);
                result.put("totalCount", gameDataList.size());
                result.put("successCount", previewData.size());
                result.put("errorCount", errorList.size());
                if (!errorList.isEmpty()) {
                    result.put("errorList", errorList);
                }
                
                return AjaxResult.success("数据解析成功").put("data", result);
            }
            
            // 正式导入模式：委托给共享导入服务（分类策略从字段映射配置读取）
            Map<String, Object> importResult = gbGameImportService.importGamesWithFieldMapping(
                gameDataList, boxId, siteId, updateExisting);
            
            // 为前端兼容性补充 errorCount 字段（服务内部使用 failureCount）
            importResult.put("errorCount", importResult.getOrDefault("failureCount", 0));
            
            String message = String.format("导入完成！成功: %d 条，失败: %d 条",
                ((Number) importResult.getOrDefault("successCount", 0)).intValue(),
                ((Number) importResult.getOrDefault("failureCount", 0)).intValue());
            
            return AjaxResult.success(message).put("data", importResult);
            
        } catch (Exception e) {
            log.error("多平台游戏导入失败", e);
            return error("导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 从 JSON 字符串中提取游戏列表。
     * 支持三种格式：根节点为数组、根节点为对象时自动探测第一个数组字段。
     * 与 ImportBoxGamesToolExecutor.extractGameList 逻辑保持一致。
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractGameListFromJson(String jsonData) {
        Object parsed = JSON.parse(jsonData);
        if (parsed instanceof JSONArray) {
            return (List<Map<String, Object>>) (List<?>) ((JSONArray) parsed).toJavaList(Map.class);
        }
        if (parsed instanceof List) {
            return (List<Map<String, Object>>) parsed;
        }
        if (parsed instanceof JSONObject || parsed instanceof Map) {
            Map<?, ?> obj = (Map<?, ?>) parsed;
            for (Object val : obj.values()) {
                if (val instanceof JSONArray) {
                    List<Map<String, Object>> list =
                        (List<Map<String, Object>>) (List<?>) ((JSONArray) val).toJavaList(Map.class);
                    if (!list.isEmpty()) {
                        log.info("自动探测到游戏数据数组，大小={}", list.size());
                        return list;
                    }
                }
            }
            throw new IllegalArgumentException("JSON 根节点为对象，但未找到数组字段，请检查数据格式");
        }
        throw new IllegalArgumentException("数据格式错误，JSON 既不是数组也不是对象");
    }

    /**
     * 获取支持的平台列表
     */
    @GetMapping("/import/platforms")
    public AjaxResult getSupportedPlatforms()
    {
        List<String> platforms = fieldMappingApplyService.getSupportedPlatforms();
        return success(platforms);
    }
    

}
