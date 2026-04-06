package com.ruoyi.web.controller.gamebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.domain.GbDrama;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.mapper.GbGameCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbBoxGameRelationMapper;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbCategoryService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbGameCategoryRelationService;
import com.ruoyi.gamebox.service.ITranslationService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.search.service.ISearchService;
import com.ruoyi.gamebox.search.document.GameDocument;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * 公开API接口Controller (精简版)
 * 供Next-web前端项目调用，不需要登录认证
 * 
 * @author ruoyi
 */
@Api(tags = "公开API接口")
@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublicApiController extends BaseController
{
    @Autowired
    private IGbArticleService articleService;
    
    @Autowired
    private IGbCategoryService categoryService;
    
    @Autowired
    private IGbGameBoxService gameBoxService;
    
    @Autowired
    private IGbGameService gameService;

    @Autowired
    private IGbGameCategoryRelationService gameCategoryRelationService;
    
    @Autowired
    private ITranslationService translationService;
    
    @Autowired
    private IGbSiteService siteService;

    @Autowired
    private GbGameCategoryRelationMapper gameCategoryRelationMapper;

    @Autowired
    private GbBoxGameRelationMapper boxGameRelationMapper;
    
    @Autowired(required = false)
    private ISearchService searchService;

    @Value("${gamebox.public.api.key:}")
    private String publicApiKey;

    private static final String[] ALLOWED_CATEGORY_TYPES = {"game_box", "game", "article"};

    @Value("${gamebox.public.api.key.enabled:false}")
    private Boolean apiKeyEnabled;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /** 统计数据 Redis 缓存时长（秒） */
    private static final long STATISTICS_CACHE_TTL_SECONDS = 300L;

    /** 可见 ID 集合缓存 TTL（秒），与统计缓存联动失效 */
    private static final long VISIBLE_IDS_CACHE_TTL_SECONDS = 300L;

    /** 站点信息缓存 TTL（秒） */
    private static final long SITE_CACHE_TTL_SECONDS = 300L;

    /** 首页数据缓存 TTL（秒） - 5 分钟，平衡性能与数据新鲜度 */
    private static final long HOME_CACHE_TTL_SECONDS = 300L;

    /**
     * 获取站点信息（优先 Redis 5 分钟缓存，miss 则查 DB）
     */
    private GbSite getCachedSite(Long siteId) {
        String cacheKey = "public:site:" + siteId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof GbSite) {
                return (GbSite) cached;
            }
        } catch (Exception e) {
            logger.warn("读取站点缓存失败", e);
        }
        GbSite site = siteService.selectGbSiteById(siteId);
        if (site != null) {
            try {
                redisTemplate.opsForValue().set(cacheKey, site, SITE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.warn("写入站点缓存失败", e);
            }
        }
        return site;
    }

    /**
     * 获取站点可见游戏 ID 集合（优先 Redis， miss 则查 DB related-mode）
     */
    @SuppressWarnings("unchecked")
    private Collection<Long> getVisibleGameIds(Long siteId) {
        String cacheKey = "public:visible_game_ids:" + siteId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof Collection) {
                return (Collection<Long>) cached;
            }
        } catch (Exception e) {
            logger.warn("读取可见游戏 ID 缓存失败", e);
        }
        GbGame q = new GbGame();
        q.setSiteId(siteId);
        q.setQueryMode("related");
        q.setStatus("1");
        try {
            Long resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            if (resolvedPersonalSiteId != null) {
                q.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
        } catch (Exception ignored) {}
        List<Long> ids = gameService.selectGbGameList(q).stream()
                .filter(g -> !"0".equals(g.getIsVisible()))
                .map(GbGame::getId)
                .collect(Collectors.toList());
        try {
            redisTemplate.opsForValue().set(cacheKey, ids, VISIBLE_IDS_CACHE_TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("写入可见游戏 ID 缓存失败", e);
        }
        return ids;
    }

    /**
     * 获取站点可见文章 ID 集合（优先 Redis， miss 则查 DB related-mode）
     */
    @SuppressWarnings("unchecked")
    private Collection<Long> getVisibleArticleIds(Long siteId, String locale) {
        String localeKey = StringUtils.isNotEmpty(locale) ? locale : "all";
        String cacheKey = "public:visible_article_ids:" + siteId + ":" + localeKey;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof Collection) {
                return (Collection<Long>) cached;
            }
        } catch (Exception e) {
            logger.warn("读取可见文章 ID 缓存失败", e);
        }
        GbArticle q = new GbArticle();
        q.setSiteId(siteId);
        q.setQueryMode("related");
        q.setStatus("1");
        q.setDelFlag("0");
        if (StringUtils.isNotEmpty(locale)) {
            q.setLocale(locale);
        }
        List<Long> ids = articleService.selectGbArticleList(q).stream()
                .map(GbArticle::getId)
                .collect(Collectors.toList());
        try {
            redisTemplate.opsForValue().set(cacheKey, ids, VISIBLE_IDS_CACHE_TTL_SECONDS, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("写入可见文章 ID 缓存失败", e);
        }
        return ids;
    }

    /**
     * 获取站点可见盒子 ID 集合（优先 Redis， miss 则查 DB related-mode）
     */
    @SuppressWarnings("unchecked")
    private Collection<Long> getVisibleBoxIds(Long siteId) {
        String cacheKey = "public:visible_box_ids:" + siteId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof Collection) {
                return (Collection<Long>) cached;
            }
        } catch (Exception e) {
            logger.warn("读取可见盒子 ID 缓存失败", e);
        }
        GbGameBox q = new GbGameBox();
        q.setSiteId(siteId);
        q.setQueryMode("related");
        q.setStatus("1");
        List<Long> ids = gameBoxService.selectGbGameBoxList(q).stream()
                .filter(b -> !"0".equals(b.getIsVisible()))
                .map(GbGameBox::getId)
                .collect(Collectors.toList());
        try {
            redisTemplate.opsForValue().set(cacheKey, ids, VISIBLE_IDS_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("写入可见盒子 ID 缓存失败", e);
        }
        return ids;
    }

    private Long getSiteIdFromHeader(@RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
                                     @RequestParam(value = "siteId", required = false) Long siteIdParam)
    {
        if (siteIdHeader != null && !siteIdHeader.isEmpty()) {
            try {
                return Long.parseLong(siteIdHeader);
            } catch (NumberFormatException e) {
                logger.error("Invalid X-Site-Id header: {}", siteIdHeader);
            }
        }
        return siteIdParam;
    }

    private boolean validateApiKey(@RequestHeader(value = "X-Api-Key", required = false) String apiKey)
    {
        if (!apiKeyEnabled || StringUtils.isEmpty(publicApiKey)) {
            return true;
        }
        return publicApiKey.equals(apiKey);
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取统计数据（优先读 Redis 缓存，未命中则查 DB 并写入缓存）
     * 缓存键：public:statistics:{siteId}:{locale}
     * TTL：{@link #STATISTICS_CACHE_TTL_SECONDS} 秒
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrCacheStatistics(Long siteId, String locale) {
        String localeKey = StringUtils.isNotEmpty(locale) ? locale : "all";
        String cacheKey = "public:statistics:" + siteId + ":" + localeKey;

        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof Map) {
                return (Map<String, Object>) cached;
            }
        } catch (Exception e) {
            logger.warn("读取统计缓存失败，降级到 DB 查询", e);
        }

        // 复用可见 ID 缓存（与搜索功能共享 Redis key，避免重复查询 DB）
        long boxCount     = getVisibleBoxIds(siteId).size();
        long gameCount    = getVisibleGameIds(siteId).size();
        long articleCount = getVisibleArticleIds(siteId, locale).size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("boxCount", boxCount);
        stats.put("gameCount", gameCount);
        stats.put("articleCount", articleCount);
        stats.put("totalSavings", 50000000L);

        try {
            redisTemplate.opsForValue().set(cacheKey, stats, STATISTICS_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("写入统计缓存失败", e);
        }

        return stats;
    }

    /**
     * 验证网站是否支持指定的语言
     * @param siteId 站点ID
     * @param locale 语言代码
     * @return 错误信息,如果为null则表示验证通过
     */
    private String validateLocale(Long siteId, String locale) {
        if (StringUtils.isEmpty(locale)) {
            return null; // 空locale表示使用默认语言,不需要验证
        }
        
        if (siteId == null) {
            return null; // 无站点绑定（全局共享资源），跳过语言验证
        }
        
        try {
            GbSite site = getCachedSite(siteId);
            if (site == null) {
                return null; // 站点不存在时不拒绝请求，降级为不验证
            }
            
            // 检查是否为默认语言
            if (locale.equals(site.getDefaultLocale())) {
                return null;
            }
            
            // 检查是否在支持的语言列表中
            String supportedLocalesJson = site.getSupportedLocales();
            if (StringUtils.isEmpty(supportedLocalesJson)) {
                return "站点未配置支持的语言";
            }
            
            List<String> supportedLocales = objectMapper.readValue(
                supportedLocalesJson, 
                new TypeReference<List<String>>(){}
            );
            
            if (!supportedLocales.contains(locale)) {
                return String.format("不支持的语言代码: %s, 支持的语言: %s", 
                    locale, String.join(", ", supportedLocales));
            }
            
            return null;
        } catch (Exception e) {
            logger.error("验证语言代码失败", e);
            return "验证语言代码失败: " + e.getMessage();
        }
    }

    /**
     * 应用翻译到游戏对象
     */
    private void applyGameTranslation(GbGame game, String locale) {
        if (StringUtils.isEmpty(locale)) {
            return; // 空locale表示使用默认语言,不需要翻译
        }
        
        Map<String, String> translations = translationService.getEntityTranslations("game", game.getId(), locale);
        if (translations != null && !translations.isEmpty()) {
            if (translations.containsKey("name")) game.setName(translations.get("name"));
            if (translations.containsKey("subtitle")) game.setSubtitle(translations.get("subtitle"));
            if (translations.containsKey("shortName")) game.setShortName(translations.get("shortName"));
            if (translations.containsKey("description")) game.setDescription(translations.get("description"));
            if (translations.containsKey("promotionDesc")) game.setPromotionDesc(translations.get("promotionDesc"));
            if (translations.containsKey("discountLabel")) game.setDiscountLabel(translations.get("discountLabel"));
        }
    }

    /**
     * 批量应用翻译到游戏列表
     */
    private void applyGamesTranslation(List<GbGame> games, String locale) {
        if (StringUtils.isEmpty(locale) || games == null || games.isEmpty()) {
            return;
        }
        
        List<Long> gameIds = games.stream().map(GbGame::getId).collect(Collectors.toList());
        Map<Long, Map<String, String>> batchTranslations = translationService.getBatchEntityTranslations("game", gameIds, locale);
        
        if (batchTranslations != null && !batchTranslations.isEmpty()) {
            for (GbGame game : games) {
                Map<String, String> translations = batchTranslations.get(game.getId());
                if (translations != null && !translations.isEmpty()) {
                    if (translations.containsKey("name")) game.setName(translations.get("name"));
                    if (translations.containsKey("subtitle")) game.setSubtitle(translations.get("subtitle"));
                    if (translations.containsKey("shortName")) game.setShortName(translations.get("shortName"));
                    if (translations.containsKey("description")) game.setDescription(translations.get("description"));
                    if (translations.containsKey("promotionDesc")) game.setPromotionDesc(translations.get("promotionDesc"));
                    if (translations.containsKey("discountLabel")) game.setDiscountLabel(translations.get("discountLabel"));
                }
            }
        }
    }

    private Long toLongId(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Map<String, Object> toCategoryPayload(GbGameCategoryRelation relation) {
        Map<String, Object> category = new HashMap<>();
        category.put("id", relation.getCategoryId());
        category.put("categoryId", relation.getCategoryId());
        category.put("name", relation.getCategoryName());
        category.put("categoryName", relation.getCategoryName());
        category.put("slug", relation.getCategorySlug());
        category.put("icon", relation.getCategoryIcon());
        category.put("categoryIcon", relation.getCategoryIcon());
        category.put("isPrimary", relation.getIsPrimary());
        category.put("sortOrder", relation.getSortOrder());
        return category;
    }

    private void applyMultiCategoriesToRows(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        List<Long> gameIds = rows.stream()
                .map(row -> toLongId(row.get("id")))
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (gameIds.isEmpty()) {
            return;
        }

        List<GbGameCategoryRelation> relations = gameCategoryRelationMapper.selectCategoriesByGameIds(gameIds);
        if (relations == null || relations.isEmpty()) {
            return;
        }

        Map<Long, List<GbGameCategoryRelation>> relationMap = relations.stream()
                .collect(Collectors.groupingBy(GbGameCategoryRelation::getGameId, java.util.LinkedHashMap::new, Collectors.toList()));

        for (Map<String, Object> row : rows) {
            Long gameId = toLongId(row.get("id"));
            if (gameId == null) {
                continue;
            }

            List<GbGameCategoryRelation> gameRelations = relationMap.get(gameId);
            if (gameRelations == null || gameRelations.isEmpty()) {
                continue;
            }

            List<Map<String, Object>> categories = gameRelations.stream()
                    .map(this::toCategoryPayload)
                    .collect(Collectors.toList());
            row.put("categories", categories);

            GbGameCategoryRelation primary = gameRelations.stream()
                    .filter(r -> "1".equals(r.getIsPrimary()))
                    .findFirst()
                    .orElse(gameRelations.get(0));

            if (StringUtils.isEmpty((String) row.get("category"))) {
                row.put("category", primary.getCategoryName());
            }
            if (StringUtils.isEmpty((String) row.get("categoryName"))) {
                row.put("categoryName", primary.getCategoryName());
            }
            if (StringUtils.isEmpty((String) row.get("categoryIcon"))) {
                row.put("categoryIcon", primary.getCategoryIcon());
            }

            row.put("primaryCategory", toCategoryPayload(primary));
        }
    }

    private void applyMultiCategoriesToGames(List<GbGame> games) {
        if (games == null || games.isEmpty()) {
            return;
        }

        List<Long> gameIds = games.stream()
                .map(GbGame::getId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (gameIds.isEmpty()) {
            return;
        }

        List<GbGameCategoryRelation> relations = gameCategoryRelationMapper.selectCategoriesByGameIds(gameIds);
        if (relations == null || relations.isEmpty()) {
            return;
        }

        Map<Long, List<GbGameCategoryRelation>> relationMap = relations.stream()
                .collect(Collectors.groupingBy(GbGameCategoryRelation::getGameId, java.util.LinkedHashMap::new, Collectors.toList()));

        for (GbGame game : games) {
            if (game == null || game.getId() == null) {
                continue;
            }

            List<GbGameCategoryRelation> gameRelations = relationMap.get(game.getId());
            if (gameRelations == null || gameRelations.isEmpty()) {
                continue;
            }

            game.setCategories(gameRelations);

            GbGameCategoryRelation primary = gameRelations.stream()
                    .filter(r -> "1".equals(r.getIsPrimary()))
                    .findFirst()
                    .orElse(gameRelations.get(0));

            if (StringUtils.isEmpty(game.getCategoryName())) {
                game.setCategoryName(primary.getCategoryName());
            }
            if (StringUtils.isEmpty(game.getCategoryIcon())) {
                game.setCategoryIcon(primary.getCategoryIcon());
            }
        }
    }

    /**
     * 批量为游戏列表附加盒子信息（DB降级路径使用）
     */
    private void applyBoxInfoToGames(List<GbGame> games) {
        if (games == null || games.isEmpty()) {
            return;
        }

        List<Long> gameIds = games.stream()
                .map(GbGame::getId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (gameIds.isEmpty()) {
            return;
        }

        try {
            List<GbBoxGameRelation> relations = boxGameRelationMapper.selectBoxSummaryByGameIds(gameIds);
            if (relations == null || relations.isEmpty()) {
                return;
            }

            Map<Long, List<Map<String, Object>>> boxMap = new HashMap<>();
            for (GbBoxGameRelation rel : relations) {
                Map<String, Object> box = new HashMap<>();
                box.put("boxId", rel.getBoxId());
                box.put("boxName", rel.getBoxName());
                box.put("logoUrl", rel.getBoxLogoUrl());
                boxMap.computeIfAbsent(rel.getGameId(), k -> new ArrayList<>()).add(box);
            }

            for (GbGame game : games) {
                if (game.getId() != null && boxMap.containsKey(game.getId())) {
                    game.setBoxes(boxMap.get(game.getId()));
                }
            }
        } catch (Exception e) {
            logger.warn("批量查询游戏盒子信息失败", e);
        }
    }

    /**
     * 应用翻译到盒子对象
     */
    private void applyBoxTranslation(GbGameBox box, String locale) {
        if (StringUtils.isEmpty(locale)) {
            return;
        }
        
        Map<String, String> translations = translationService.getEntityTranslations("box", box.getId(), locale);
        if (translations != null && !translations.isEmpty()) {
            if (translations.containsKey("name")) box.setName(translations.get("name"));
            if (translations.containsKey("description")) box.setDescription(translations.get("description"));
        }
    }

    /**
     * 批量应用翻译到盒子列表
     */
    private void applyBoxesTranslation(List<GbGameBox> boxes, String locale) {
        if (StringUtils.isEmpty(locale) || boxes == null || boxes.isEmpty()) {
            return;
        }
        
        List<Long> boxIds = boxes.stream().map(GbGameBox::getId).collect(Collectors.toList());
        Map<Long, Map<String, String>> batchTranslations = translationService.getBatchEntityTranslations("box", boxIds, locale);
        
        if (batchTranslations != null && !batchTranslations.isEmpty()) {
            for (GbGameBox box : boxes) {
                Map<String, String> translations = batchTranslations.get(box.getId());
                if (translations != null && !translations.isEmpty()) {
                    if (translations.containsKey("name")) box.setName(translations.get("name"));
                    if (translations.containsKey("description")) box.setDescription(translations.get("description"));
                }
            }
        }
    }

    /**
     * 应用翻译到分类对象
     */
    private void applyCategoryTranslation(GbCategory category, String locale) {
        if (StringUtils.isEmpty(locale)) {
            return;
        }
        
        Map<String, String> translations = translationService.getEntityTranslations("category", category.getId(), locale);
        if (translations != null && !translations.isEmpty()) {
            if (translations.containsKey("name")) category.setName(translations.get("name"));
            if (translations.containsKey("description")) category.setDescription(translations.get("description"));
        }
    }

    /**
     * 批量应用翻译到分类列表
     */
    private void applyCategoriesTranslation(List<GbCategory> categories, String locale) {
        if (StringUtils.isEmpty(locale) || categories == null || categories.isEmpty()) {
            return;
        }
        
        List<Long> categoryIds = categories.stream().map(GbCategory::getId).collect(Collectors.toList());
        Map<Long, Map<String, String>> batchTranslations = translationService.getBatchEntityTranslations("category", categoryIds, locale);
        
        if (batchTranslations != null && !batchTranslations.isEmpty()) {
            for (GbCategory category : categories) {
                Map<String, String> translations = batchTranslations.get(category.getId());
                if (translations != null && !translations.isEmpty()) {
                    if (translations.containsKey("name")) category.setName(translations.get("name"));
                    if (translations.containsKey("description")) category.setDescription(translations.get("description"));
                }
            }
        }
    }

    /**
     * 应用翻译到短剧对象
     */
    private void applyDramaTranslation(GbDrama drama, String locale) {
        if (StringUtils.isEmpty(locale)) {
            return;
        }
        
        Map<String, String> translations = translationService.getEntityTranslations("drama", drama.getId(), locale);
        if (translations != null && !translations.isEmpty()) {
            if (translations.containsKey("name")) drama.setName(translations.get("name"));
            if (translations.containsKey("subtitle")) drama.setSubtitle(translations.get("subtitle"));
            if (translations.containsKey("description")) drama.setDescription(translations.get("description"));
            if (translations.containsKey("content")) drama.setContent(translations.get("content"));
            if (translations.containsKey("director")) drama.setDirector(translations.get("director"));
            if (translations.containsKey("actors")) drama.setActors(translations.get("actors"));
            if (translations.containsKey("producer")) drama.setProducer(translations.get("producer"));
        }
    }

    /**
     * 批量应用翻译到短剧列表
     */
    private void applyDramasTranslation(List<GbDrama> dramas, String locale) {
        if (StringUtils.isEmpty(locale) || dramas == null || dramas.isEmpty()) {
            return;
        }
        
        List<Long> dramaIds = dramas.stream().map(GbDrama::getId).collect(Collectors.toList());
        Map<Long, Map<String, String>> batchTranslations = translationService.getBatchEntityTranslations("drama", dramaIds, locale);
        
        if (batchTranslations != null && !batchTranslations.isEmpty()) {
            for (GbDrama drama : dramas) {
                Map<String, String> translations = batchTranslations.get(drama.getId());
                if (translations != null && !translations.isEmpty()) {
                    if (translations.containsKey("name")) drama.setName(translations.get("name"));
                    if (translations.containsKey("subtitle")) drama.setSubtitle(translations.get("subtitle"));
                    if (translations.containsKey("description")) drama.setDescription(translations.get("description"));
                    if (translations.containsKey("content")) drama.setContent(translations.get("content"));
                    if (translations.containsKey("director")) drama.setDirector(translations.get("director"));
                    if (translations.containsKey("actors")) drama.setActors(translations.get("actors"));
                    if (translations.containsKey("producer")) drama.setProducer(translations.get("producer"));
                }
            }
        }
    }

    // ==================== 文章接口 ====================

    /**
     * 获取文章列表（支持按配置的 section slug 精确过滤）
     */
    @ApiOperation("获取文章列表")
    @GetMapping("/articles")
    public TableDataInfo getArticleList(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("板块slug（站点配置使用）") @RequestParam(value = "section", required = false) String sectionSlug,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return getDataTable(new ArrayList<>());
        }

        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }

        GbArticle query = new GbArticle();
        query.setQueryMode("related");
        query.setSiteId(siteId);
        query.setLocale(locale);
        query.setStatus("1");
        query.setDelFlag("0");

        if (StringUtils.isNotEmpty(sectionSlug)) {
            GbCategory matchedCategory = findArticleCategoryBySlug(siteId, sectionSlug);
            if (matchedCategory == null) {
                return getDataTable(new ArrayList<>());
            }

            if (matchedCategory.isSectionCategory()) {
                query.setSectionId(matchedCategory.getId());
            } else {
                query.setCategoryId(matchedCategory.getId());
            }
        }

        startPage();
        List<GbArticle> list = articleService.selectGbArticleList(query);
        return getDataTable(list);
    }

    private GbCategory findArticleCategoryBySlug(Long siteId, String slug)
    {
        if (siteId == null || StringUtils.isEmpty(slug)) {
            return null;
        }

        GbCategory query = new GbCategory();
        query.setSiteId(siteId);
        query.setCategoryType("article");
        query.setStatus("1");

        List<GbCategory> categories = categoryService.selectGbCategoryList(query);

        GbCategory sectionMatch = categories.stream()
                .filter(category -> category.getSlug() != null && category.getSlug().equalsIgnoreCase(slug))
                .filter(GbCategory::isSectionCategory)
                .findFirst()
                .orElse(null);
        if (sectionMatch != null) {
            return sectionMatch;
        }

        return categories.stream()
                .filter(category -> category.getSlug() != null && category.getSlug().equalsIgnoreCase(slug))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取文章详情
     */
    @ApiOperation("获取文章详情")
    @GetMapping("/articles/{id}")
    public AjaxResult getArticleDetail(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("文章ID") @PathVariable("id") Long id,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return error("站点ID不能为空");
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            return error(localeError);
        }
        
        // id 是 masterArticleId，直接按 masterArticleId + locale 查找
        GbArticle article = articleService.getArticleByMasterIdAndLocale(id, locale);

        if (article == null || !"1".equals(article.getStatus()) || "1".equals(article.getDelFlag())) {
            return error("文章不存在或未发布");
        }

        return success(article);
    }

    /**
     * 获取文章可用的语言版本
     */
    @ApiOperation("获取文章可用的语言版本")
    @GetMapping("/articles/{id}/locales")
    public AjaxResult getArticleLocales(
            @ApiParam("文章ID") @PathVariable("id") Long id)
    {
        List<String> locales = articleService.getArticleLocales(id);
        return success(locales);
    }

    /**
     * 搜索文章
     */
    @ApiOperation("搜索文章")
    @GetMapping("/articles/search")
    public TableDataInfo searchArticles(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("搜索关键词") @RequestParam("keyword") String keyword,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null || StringUtils.isEmpty(keyword)) {
            return getDataTable(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }
        
        // 搜索文章: ES 主路 + DB 降级
        // 1. DB related-mode 查该站可见文章 ID（Redis 缓存 5 分钟）
        // 2. ES 在可见 ID 集内做 IK 分词搜索 + 相关性排序
        // 3. ES 不可用时降级为 DB LIKE
        if (searchService != null) {
            try {
                Collection<Long> visibleIds = getVisibleArticleIds(siteId, locale);
                    if (visibleIds.isEmpty()) {
                        logger.warn("文章搜索可见 ID 集为空，降级至 DB LIKE 查询, siteId={}, locale={}", siteId, locale);
                    } else {
                        org.springframework.data.domain.Page<?> page =
                                searchService.searchArticlesWithinIds(keyword, visibleIds, locale, pageNum, pageSize);
                        if (page.hasContent() || page.getTotalElements() > 0) {
                            TableDataInfo dataTable = new TableDataInfo();
                            dataTable.setCode(HttpStatus.SUCCESS);
                            dataTable.setMsg("查询成功");
                            dataTable.setTotal(page.getTotalElements());
                            dataTable.setRows(page.getContent());
                            return dataTable;
                        }
                        logger.warn("文章 ES 搜索无结果，降级至 DB LIKE 查询, siteId={}, locale={}, keyword={}", siteId, locale, keyword);
                }
            } catch (Exception e) {
                logger.warn("文章 ES 搜索失败，降级至 DB LIKE 查询", e);
            }
        }
        // 降级: DB LIKE 查询
        GbArticle query = new GbArticle();
        query.setQueryMode("related");
        query.setSiteId(siteId);
        query.setTitle(keyword);
        query.setStatus("1");
        query.setDelFlag("0");
        if (StringUtils.isNotEmpty(locale)) {
            query.setLocale(locale);
        }
        startPage();
        List<GbArticle> list = articleService.selectGbArticleList(query);
        return getDataTable(list);
    }

    // ==================== 分类接口 ====================

    /**
     * 获取分类列表
     */
    @ApiOperation("获取分类列表")
    @GetMapping("/categories")
    public AjaxResult getCategoryList(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("分类类型") @RequestParam(value = "categoryType", required = false) String categoryType,
            @ApiParam("父级分类ID") @RequestParam(value = "parentId", required = false) Long parentId,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return success(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return success(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            return error(localeError);
        }
        
        Long resolvedPersonalSiteId = null;
        try {
            resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
        } catch (Exception ignored) {
        }

        GbCategory query = new GbCategory();
        query.setSiteId(siteId);
        query.setCategoryType(categoryType);
        query.setParentId(parentId);
        query.setStatus("1");
        query.setQueryMode("related");
        if (resolvedPersonalSiteId != null) {
            query.getParams().put("personalSiteId", resolvedPersonalSiteId);
        }

        List<GbCategory> list = categoryService.selectGbCategoryList(query);
        list = list.stream()
                .filter(category -> Arrays.asList(ALLOWED_CATEGORY_TYPES).contains(category.getCategoryType()))
                .collect(Collectors.toList());
        
        applyCategoriesTranslation(list, locale);
        return success(list);
    }

    /**
     * 获取分类下的游戏列表
     */
    @ApiOperation("获取分类下的游戏列表")
    @GetMapping("/categories/{categoryId}/games")
    public TableDataInfo getCategoryGames(
            @ApiParam("分类ID") @PathVariable("categoryId") Long categoryId,
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("游戏类型") @RequestParam(value = "gameType", required = false) String gameType,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("排序方式") @RequestParam(value = "orderBy", required = false, defaultValue = "hot") String orderBy,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return getDataTable(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }

        List<Long> gameIds = gameCategoryRelationService.selectGameIdsByCategoryId(categoryId);
        if (gameIds.isEmpty()) {
            return getDataTable(new ArrayList<>());
        }

        GbGame query = new GbGame();
        query.setSiteId(siteId);
        query.setGameType(gameType);
        query.setStatus("1");
        query.setQueryMode("related");
        // 注入 personalSiteId（用于包含默认配置）
        try {
            Long resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            if (resolvedPersonalSiteId != null) {
                query.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
        } catch (Exception ignored) {}
        
        // 先不分页，查询所有符合条件的游戏
        List<GbGame> allGames = gameService.selectGbGameList(query);
        
        // 过滤出属于该分类的游戏
        List<GbGame> categoryGames = allGames.stream()
                .filter(game -> gameIds.contains(game.getId()))
                .collect(Collectors.toList());

        // 排序
        switch (orderBy) {
            case "new":
                categoryGames.sort((a, b) -> {
                    if (b.getCreateTime() == null) return -1;
                    if (a.getCreateTime() == null) return 1;
                    return b.getCreateTime().compareTo(a.getCreateTime());
                });
                break;
            case "rating":
                categoryGames.sort((a, b) -> {
                    if (b.getRating() == null) return -1;
                    if (a.getRating() == null) return 1;
                    return b.getRating().compareTo(a.getRating());
                });
                break;
            case "hot":
            default:
                categoryGames.sort((a, b) -> {
                    int aCount = a.getDownloadCount() != null ? a.getDownloadCount() : 0;
                    int bCount = b.getDownloadCount() != null ? b.getDownloadCount() : 0;
                    return Integer.compare(bCount, aCount);
                });
                break;
        }
        
        // 应用翻译
        applyGamesTranslation(categoryGames, locale);
        
        // 手动分页（在过滤和排序之后）
        int total = categoryGames.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<GbGame> pagedGames = start < total ? categoryGames.subList(start, end) : new ArrayList<>();
        
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(HttpStatus.SUCCESS);
        dataTable.setMsg("查询成功");
        dataTable.setRows(pagedGames);
        dataTable.setTotal(total);
        
        return dataTable;
    }

    // ==================== 盒子接口 ====================

    /**
     * 获取盒子列表
     */
    @ApiOperation("获取游戏盒子列表")
    @GetMapping("/boxes")
    public TableDataInfo getBoxList(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("分类ID") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return getDataTable(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }
        
        // 解析当前站点对应的用户默认个人站点ID（用于将默认配置包含进查询）
        Long resolvedPersonalSiteId = null;
        try {
            resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
        } catch (Exception ignored) {}

        startPage();
        // 构造查询对象以使用关联模式并注入 resolvedPersonalSiteId（如果有）
        GbGameBox query = new GbGameBox();
        query.setSiteId(siteId);
        query.setCategoryId(categoryId);
        query.setQueryMode("related");
        if (resolvedPersonalSiteId != null) {
            query.getParams().put("personalSiteId", resolvedPersonalSiteId);
        }

        List<GbGameBox> list = gameBoxService.selectGbGameBoxList(query);
        applyBoxesTranslation(list, locale);
        return getDataTable(list);
    }

    /**
     * 获取盒子详情
     */
    @ApiOperation("获取游戏盒子详情")
    @GetMapping("/boxes/{id}")
    public AjaxResult getBoxDetail(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("游戏盒子ID") @PathVariable("id") Long id,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        GbGameBox gameBox = gameBoxService.selectGbGameBoxById(id);
        if (gameBox == null || !"1".equals(gameBox.getStatus())) {
            return error("盒子不存在或未发布");
        }
        
        String localeError = validateLocale(gameBox.getSiteId(), locale);
        if (localeError != null) {
            return error(localeError);
        }
        
        applyBoxTranslation(gameBox, locale);
        return success(gameBox);
    }

    /**
     * 获取盒子下的游戏列表
     * 使用与 /games 一致的关联模式查询，再按 boxIdsFilter 过滤，保证可见性规则一致
     */
    @ApiOperation("获取盒子下的游戏列表")
    @GetMapping("/boxes/{id}/games")
    public TableDataInfo getBoxGames(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("游戏盒子ID") @PathVariable("id") Long id,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return getDataTable(new ArrayList<>());
        }

        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }

        GbGameBox gameBox = gameBoxService.selectGbGameBoxById(id);
        if (gameBox == null || !"1".equals(gameBox.getStatus())) {
            return getDataTable(new ArrayList<>());
        }

        GbGame query = new GbGame();
        query.setSiteId(siteId);
        query.setQueryMode("related");
        query.setStatus("1");
        query.setBoxIdsFilter(String.valueOf(id));

        // 注入 personalSiteId（用于包含默认配置）
        try {
            Long resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            if (resolvedPersonalSiteId != null) {
                query.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
        } catch (Exception ignored) {}

        startPage();
        List<GbGame> games = gameService.selectGbGameList(query);
        // 与 /games 接口保持一致，仅返回最终可见游戏
        games = games.stream()
                .filter(game -> !"0".equals(game.getIsVisible()))
                .collect(java.util.stream.Collectors.toList());
        applyGamesTranslation(games, locale);
        return getDataTable(games);
    }

    // ==================== 游戏接口 ====================

    /**
     * 获取游戏列表
     * 使用关联模式查询：返回站点的所有可见游戏（自有+默认配置+跨站共享）
     */
    @ApiOperation("获取游戏列表")
    @GetMapping("/games")
    public TableDataInfo getGameList(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("分类ID") @RequestParam(value = "categoryId", required = false) Long categoryId,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("仅查询无分类游戏") @RequestParam(value = "uncategorized", required = false) Boolean uncategorized,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return getDataTable(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }
        
        // 使用关联模式查询，与管理端保持一致
        // 返回：自有游戏 + 默认配置游戏（未排除） + 跨站共享游戏（已include）
        GbGame query = new GbGame();
        query.setSiteId(siteId);
        query.setCategoryId(categoryId);
        query.setQueryMode("related"); // 关联模式
        query.setStatus("1"); // 只返回启用的游戏
        if (Boolean.TRUE.equals(uncategorized)) {
            query.setUncategorizedOnly(true);
        }
        // 注入 personalSiteId（用于包含默认配置）
        try {
            Long resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            if (resolvedPersonalSiteId != null) {
                query.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
        } catch (Exception ignored) {}
        
        // 对于大批量请求（如sitemap），不使用分页
        if (pageSize >= 10000) {
            // 不调用startPage()，直接查询所有数据
            List<GbGame> list = gameService.selectGbGameList(query);
            
            // 过滤掉不可见的游戏
            list = list.stream()
                    .filter(game -> !"0".equals(game.getIsVisible()))
                    .collect(java.util.stream.Collectors.toList());
            
            applyGamesTranslation(list, locale);
            
            // 手动构造分页结果
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(200);
            dataTable.setMsg("查询成功");
            dataTable.setTotal(list.size());
            dataTable.setRows(list);
            return dataTable;
        } else {
            // 正常分页查询
            startPage();
            List<GbGame> list = gameService.selectGbGameList(query);
            
            // 过滤掉不可见的游戏
            list = list.stream()
                    .filter(game -> !"0".equals(game.getIsVisible()))
                    .collect(java.util.stream.Collectors.toList());
            
            applyGamesTranslation(list, locale);
            return getDataTable(list);
        }
    }

    /**
     * 获取游戏详情
     */
    @ApiOperation("获取游戏详情")
    @GetMapping("/games/{id}")
    public AjaxResult getGameDetail(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("游戏ID") @PathVariable("id") Long id,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        GbGame game = gameService.selectGbGameById(id);
        if (game == null || !"1".equals(game.getStatus())) {
            return error("游戏不存在或未发布");
        }
        
        String localeError = validateLocale(game.getSiteId(), locale);
        if (localeError != null) {
            return error(localeError);
        }
        
        applyGameTranslation(game, locale);
        return success(game);
    }

    /**
     * 获取游戏所在的所有盒子（含推广链接等关联数据）
     */
    @ApiOperation("获取游戏关联的盒子列表")
    @GetMapping("/games/{id}/boxes")
    public AjaxResult getGameBoxRelations(
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("游戏ID") @PathVariable("id") Long id)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        try {
            List<GbBoxGameRelation> relations = boxGameRelationMapper.selectBoxesByGameId(id);
            if (relations == null) {
                relations = new ArrayList<>();
            }
            return success(relations);
        } catch (Exception e) {
            logger.error("获取游戏盒子关联失败 [gameId={}]", id, e);
            return success(new ArrayList<>());
        }
    }

    /**
     * 搜索游戏
     */
    @ApiOperation("搜索游戏")
    @GetMapping("/games/search")
    public TableDataInfo searchGames(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("搜索关键词") @RequestParam("keyword") String keyword,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("页码") @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @ApiParam("每页数量") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        if (!validateApiKey(apiKey)) {
            return getDataTable(new ArrayList<>());
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null || StringUtils.isEmpty(keyword)) {
            return getDataTable(new ArrayList<>());
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            TableDataInfo dataTable = new TableDataInfo();
            dataTable.setCode(400);
            dataTable.setMsg(localeError);
            return dataTable;
        }
        
        // 搜索游戏: ES 主路 + DB 降级
        // 1. DB related-mode 查询该站可见游戏 ID（Redis 缓存 5 分钟）
        // 2. ES 在可见 ID 集内做 IK 分词搜索 + 相关性排序
        // 3. ES 不可用时降级为 DB LIKE
        if (searchService != null) {
            try {
                Collection<Long> visibleIds = getVisibleGameIds(siteId);
                    if (visibleIds.isEmpty()) {
                        logger.warn("游戏搜索可见 ID 集为空，降级至 DB LIKE 查询, siteId={}", siteId);
                    } else {
                        org.springframework.data.domain.Page<?> page =
                                searchService.searchGamesWithinIds(keyword, visibleIds, pageNum, pageSize);
                        if (page.hasContent() || page.getTotalElements() > 0) {
                            List<?> content = page.getContent();
                            List<Object> normalizedRows = new ArrayList<>();
                            for (Object item : content) {
                                if (item instanceof GameDocument) {
                                    GameDocument doc = (GameDocument) item;
                                    Map<String, Object> row = new HashMap<>();
                                    row.put("id", doc.getId());
                                    row.put("siteId", doc.getSiteId());
                                    row.put("name", doc.getName());
                                    row.put("subtitle", doc.getSubtitle());
                                    row.put("shortName", doc.getShortName());
                                    row.put("gameType", doc.getGameType());
                                    row.put("iconUrl", doc.getIconUrl());
                                    row.put("coverUrl", doc.getCoverUrl());
                                    row.put("description", doc.getDescription());
                                    row.put("promotionDesc", doc.getPromotionDesc());
                                    row.put("developer", doc.getDeveloper());
                                    row.put("publisher", doc.getPublisher());
                                    row.put("deviceSupport", doc.getDeviceSupport());
                                    row.put("rating", doc.getRating());
                                    row.put("status", doc.getStatus());
                                    row.put("delFlag", doc.getDelFlag());
                                    row.put("createTime", doc.getCreateTime());

                                    row.put("category", doc.getCategory());
                                    row.put("categoryName", doc.getCategory());
                                    row.put("categoryIcon", null);

                                    // 添加盒子信息
                                    if (doc.getBoxes() != null && !doc.getBoxes().isEmpty()) {
                                        List<Map<String, Object>> boxesList = doc.getBoxes().stream().map(b -> {
                                            Map<String, Object> boxMap = new HashMap<>();
                                            boxMap.put("boxId", b.getBoxId());
                                            boxMap.put("boxName", b.getBoxName());
                                            boxMap.put("logoUrl", b.getLogoUrl());
                                            return boxMap;
                                        }).collect(Collectors.toList());
                                        row.put("boxes", boxesList);
                                    }

                                    normalizedRows.add(row);
                                } else {
                                    normalizedRows.add(item);
                                }
                            }

                            List<Map<String, Object>> gameRows = normalizedRows.stream()
                                    .filter(Map.class::isInstance)
                                    .map(item -> (Map<String, Object>) item)
                                    .collect(Collectors.toList());
                            applyMultiCategoriesToRows(gameRows);

                            TableDataInfo dataTable = new TableDataInfo();
                            dataTable.setCode(HttpStatus.SUCCESS);
                            dataTable.setMsg("查询成功");
                            dataTable.setTotal(page.getTotalElements());
                            dataTable.setRows(normalizedRows);
                            return dataTable;
                        }
                        logger.warn("游戏 ES 搜索无结果，降级至 DB LIKE 查询, siteId={}, keyword={}", siteId, keyword);
                }
            } catch (Exception e) {
                logger.warn("游戏 ES 搜索失败，降级至 DB LIKE 查询", e);
            }
        }
        // 降级: DB LIKE 查询
        GbGame query = new GbGame();
        query.setSiteId(siteId);
        query.setName(keyword);
        query.setQueryMode("related");
        query.setStatus("1");
        try {
            Long resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            if (resolvedPersonalSiteId != null) {
                query.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
        } catch (Exception ignored) {}
        startPage();
        List<GbGame> list = gameService.selectGbGameList(query);
        list = list.stream().filter(g -> !"0".equals(g.getIsVisible())).collect(Collectors.toList());
        applyGamesTranslation(list, locale);
        applyMultiCategoriesToGames(list);
        applyBoxInfoToGames(list);
        return getDataTable(list);
    }

    // ==================== 统计与首页接口 ====================

    /**
     * 健康检查
     */
    @ApiOperation("健康检查")
    @GetMapping("/health")
    public AjaxResult health()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "ok");
        result.put("timestamp", System.currentTimeMillis());
        result.put("version", "1.0.0");
        return success(result);
    }

    /**
     * 获取统计数据
     */
    @ApiOperation("获取统计数据")
    @GetMapping("/statistics")
    public AjaxResult getStatistics(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return error("站点ID不能为空");
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            return error(localeError);
        }

        Map<String, Object> statistics = new HashMap<>();
        
        try {
            return success(getOrCacheStatistics(siteId, locale));
        } catch (Exception e) {
            logger.error("获取统计数据失败", e);
            statistics.put("boxCount", 50L);
            statistics.put("gameCount", 100000L);
            statistics.put("articleCount", 200L);
            statistics.put("totalSavings", 50000000L);
            return success(statistics);
        }
    }

    /**
     * 获取网站配置信息（支持多语言翻译和CDN缓存）
     */
    @ApiOperation("获取网站配置信息")
    @GetMapping("/site-config")
    public AjaxResult getSiteConfig(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return error("站点ID不能为空");
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            return error(localeError);
        }

        try {
            GbSite site = getCachedSite(siteId);
            if (site == null) {
                return error("站点不存在");
            }
            
            Map<String, Object> siteConfig = new HashMap<>();
            
            // 基本信息
            siteConfig.put("id", site.getId());
            siteConfig.put("name", site.getName());
            siteConfig.put("code", site.getCode());
            siteConfig.put("domain", site.getDomain());
            siteConfig.put("logoUrl", site.getLogoUrl());
            siteConfig.put("faviconUrl", site.getFaviconUrl());
            siteConfig.put("defaultLocale", site.getDefaultLocale());
            siteConfig.put("supportedLocales", site.getSupportedLocales());
            
            // 获取当前语言的翻译
            String effectiveLocale = StringUtils.isNotEmpty(locale) ? locale : site.getDefaultLocale();
            Map<String, String> translations = translationService.getEntityTranslations("site", siteId, effectiveLocale);
            
            // 优先使用翻译,如果没有翻译则使用原始值
            siteConfig.put("description", translations.getOrDefault("description", site.getDescription()));
            siteConfig.put("seoTitle", translations.getOrDefault("seoTitle", site.getSeoTitle()));
            siteConfig.put("seoKeywords", translations.getOrDefault("seoKeywords", site.getSeoKeywords()));
            siteConfig.put("seoDescription", translations.getOrDefault("seoDescription", site.getSeoDescription()));
            
            return success(siteConfig);
            
        } catch (Exception e) {
            logger.error("获取网站配置失败", e);
            return error("获取网站配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取首页数据
     */
    @ApiOperation("获取首页数据")
    @GetMapping("/home")
    public AjaxResult getHomeData(
            @RequestHeader(value = "X-Site-Id", required = false) String siteIdHeader,
            @RequestHeader(value = "X-Api-Key", required = false) String apiKey,
            @ApiParam("站点ID") @RequestParam(value = "siteId", required = false) Long siteIdParam,
            @ApiParam("语言代码") @RequestParam(value = "locale", required = false) String locale,
            @ApiParam("攻略文章数量") @RequestParam(value = "strategyCount", defaultValue = "6") Integer strategyCount,
            @ApiParam("热门游戏数量") @RequestParam(value = "hotGamesCount", defaultValue = "6") Integer hotGamesCount)
    {
        if (!validateApiKey(apiKey)) {
            return error("API密钥无效");
        }

        Long siteId = getSiteIdFromHeader(siteIdHeader, siteIdParam);
        if (siteId == null) {
            return error("站点ID不能为空");
        }
        
        String localeError = validateLocale(siteId, locale);
        if (localeError != null) {
            return error(localeError);
        }

        String localeKey = StringUtils.isNotEmpty(locale) ? locale : "default";
        String homeCacheKey = "public:home:" + siteId + ":" + localeKey + ":" + strategyCount + ":" + hotGamesCount;
        try {
            Object cached = redisTemplate.opsForValue().get(homeCacheKey);
            if (cached instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> cachedHomeData = (Map<String, Object>) cached;
                return success(cachedHomeData);
            }
        } catch (Exception e) {
            logger.warn("读取首页缓存失败", e);
        }

        Map<String, Object> homeData = new HashMap<>();
        
        try {
            // 解析当前站点对应的用户默认个人站点ID（如果存在）
            Long resolvedPersonalSiteId = null;
            try {
                resolvedPersonalSiteId = siteService.resolvePersonalSiteIdForSite(siteId);
            } catch (Exception ignored) {
            }
            // 1. 统计数据 - 使用关联模式（related），Redis 缓存 5 分钟
            Map<String, Object> statistics = getOrCacheStatistics(siteId, locale);
            homeData.put("statistics", statistics);
            
            // 2. 攻略文章 - 复用getStrategyList的查询逻辑
            // 注意：文章必须设置了分类（categoryId）才能返回到前端，未设置分类的文章不会出现在列表中
            List<GbArticle> strategyArticles = new ArrayList<>();
            
            // 1. 查找攻略板块（使用关联模式，以便找到个人默认站点下的分类）
            GbCategory sectionQuery = new GbCategory();
            sectionQuery.setSiteId(siteId);
            sectionQuery.setCategoryType("article");
            sectionQuery.setStatus("1");
            sectionQuery.setQueryMode("related");
            if (resolvedPersonalSiteId != null) {
                sectionQuery.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
            List<GbCategory> sections = categoryService.selectGbCategoryList(sectionQuery);
            
            GbCategory strategySection = sections.stream()
                .filter(cat -> cat.getParentId() == 0 && 
                              ("guide-section".equals(cat.getSlug()) || "攻略板块".equals(cat.getName()) ||
                               (cat.getName() != null && cat.getName().contains("攻略"))))
                .findFirst()
                .orElse(null);
            
            if (strategySection == null) {
                // 如果没有找到攻略板块，尝试直接查找攻略分类
                GbCategory strategyCategory = sections.stream()
                    .filter(cat -> "guide".equals(cat.getSlug()) || "strategy".equals(cat.getSlug()) ||
                                  (cat.getName() != null && cat.getName().contains("攻略")))
                    .findFirst()
                    .orElse(null);
                
                if (strategyCategory != null) {
                    // 直接使用这个分类查询文章，SQL 层限制数量
                    GbArticle query = new GbArticle();
                    query.setQueryMode("related"); // 使用关联模式，基于主文章表查询
                    query.setSiteId(siteId);
                    query.setCategoryId(strategyCategory.getId());
                    query.setLocale(locale);
                    query.setStatus("1");
                    query.setDelFlag("0");
                    // 对于匿名公开接口：尝试注入该站点对应用户的个人默认站点ID
                    if (resolvedPersonalSiteId != null) {
                        query.getParams().put("personalSiteId", resolvedPersonalSiteId);
                    }
                    
                    PageHelper.startPage(1, strategyCount);
                    strategyArticles = articleService.selectGbArticleList(query);
                }
            } else {
                // 利用 sectionId 在 SQL 中过滤（mapper: c.parent_id = #{sectionId}），
                // 无需额外查子分类列表，也无需内存过滤全量文章
                GbArticle query = new GbArticle();
                query.setQueryMode("related"); // 使用关联模式，基于主文章表查询
                query.setSiteId(siteId);
                query.setSectionId(strategySection.getId()); // SQL 过滤：c.parent_id = strategySection.getId()
                query.setLocale(locale);
                query.setStatus("1");
                query.setDelFlag("0");
                // 对于匿名公开接口：尝试注入该站点对应用户的个人默认站点ID
                if (resolvedPersonalSiteId != null) {
                    query.getParams().put("personalSiteId", resolvedPersonalSiteId);
                }
                
                PageHelper.startPage(1, strategyCount);
                strategyArticles = articleService.selectGbArticleList(query);
            }
            
            homeData.put("strategyArticles", strategyArticles.stream()
                .limit(strategyCount)
                .collect(Collectors.toList()));
            
            // 3. 热门游戏 - 使用关联模式，与 /games 接口保持一致
            GbGame hotGameQuery = new GbGame();
            hotGameQuery.setSiteId(siteId);
            hotGameQuery.setQueryMode("related"); // 关联模式：自有 + 默认 + 跨站共享
            hotGameQuery.setStatus("1");
            // 对于匿名公开接口：尝试注入该站点对应用户的个人默认站点ID
            if (resolvedPersonalSiteId != null) {
                hotGameQuery.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
            List<GbGame> allGames = gameService.selectGbGameList(hotGameQuery);
            
            List<GbGame> hotGames = allGames.stream()
                .filter(game -> !"0".equals(game.getIsVisible())) // 过滤不可见游戏
                .sorted((g1, g2) -> {
                    Long d1 = g1.getDownloadCount() != null ? g1.getDownloadCount() : 0L;
                    Long d2 = g2.getDownloadCount() != null ? g2.getDownloadCount() : 0L;
                    return d2.compareTo(d1);
                })
                .limit(hotGamesCount)
                .collect(Collectors.toList());
            
            applyGamesTranslation(hotGames, locale);
            homeData.put("hotGames", hotGames);

            // 4. 游戏分类 - 供首页分类导航使用
            GbCategory catQuery = new GbCategory();
            catQuery.setSiteId(siteId);
            catQuery.setCategoryType("game");
            catQuery.setStatus("1");
            catQuery.setQueryMode("related");
            if (resolvedPersonalSiteId != null) {
                catQuery.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
            List<GbCategory> homeCategories = categoryService.selectGbCategoryList(catQuery);
            applyCategoriesTranslation(homeCategories, locale);
            homeData.put("categories", homeCategories);

            // 5. 盒子礼包 - 供首页礼包区使用（取前4条）
            GbGameBox boxQuery = new GbGameBox();
            boxQuery.setSiteId(siteId);
            boxQuery.setQueryMode("related");
            if (resolvedPersonalSiteId != null) {
                boxQuery.getParams().put("personalSiteId", resolvedPersonalSiteId);
            }
            PageHelper.startPage(1, 4);
            List<GbGameBox> homeBoxes = gameBoxService.selectGbGameBoxList(boxQuery);
            applyBoxesTranslation(homeBoxes, locale);
            homeData.put("boxes", homeBoxes);

            try {
                redisTemplate.opsForValue().set(homeCacheKey, homeData, HOME_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.warn("写入首页缓存失败", e);
            }
            
            return success(homeData);
            
        } catch (Exception e) {
            logger.error("获取首页数据失败", e);
            return error("获取首页数据失败: " + e.getMessage());
        }
    }
}
