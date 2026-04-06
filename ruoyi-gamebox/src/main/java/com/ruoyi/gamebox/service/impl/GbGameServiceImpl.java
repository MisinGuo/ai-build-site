package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbGameMapper;
import com.ruoyi.gamebox.mapper.GbGameCategoryRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameCategoryRelation;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;

/**
 * 游戏Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbGameServiceImpl implements IGbGameService
{
    private static final Logger log = LoggerFactory.getLogger(GbGameServiceImpl.class);
    
    @Autowired
    private GbGameMapper gbGameMapper;
    
    @Autowired
    private GbGameCategoryRelationMapper gameCategoryRelationMapper;

    @Autowired(required = false)
    private ElasticsearchSyncHelper elasticsearchSyncHelper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
    }

    /**
     * 查询游戏
     * 
     * @param id 游戏主键
     * @return 游戏
     */
    @Override
    public GbGame selectGbGameById(Long id)
    {
        return gbGameMapper.selectGbGameById(id);
    }

    /**
     * 查询游戏列表
     * 
     * @param gbGame 游戏
     * @return 游戏
     */
    @Override
    public List<GbGame> selectGbGameList(GbGame gbGame)
    {
        // 分页上下文来自Controller.startPage()，但下面两个前置步骤会触发数据库查询。
        // 若不保护，分页会被前置查询消耗，导致主列表查询返回全量数据。
        Page<?> page = PageMethod.getLocalPage();
        if (page != null) {
            PageMethod.clearPage();
        }

        relatedModeSiteValidator.validate(gbGame.getQueryMode(), gbGame.getSiteId());
        injectPersonalSiteId(gbGame);

        if (page != null) {
            PageMethod.startPage(page.getPageNum(), page.getPageSize(), page.isCount());
            if (StringUtils.isNotEmpty(page.getOrderBy())) {
                PageMethod.orderBy(page.getOrderBy());
            }
        }

        List<GbGame> games = gbGameMapper.selectGbGameList(gbGame);
        if (games.isEmpty()) {
            return games;
        }
        // 批量加载所有游戏的分类关联，避免N+1查询
        List<Long> gameIds = games.stream().map(GbGame::getId).collect(Collectors.toList());
        List<GbGameCategoryRelation> allCategories = gameCategoryRelationMapper.selectCategoriesByGameIds(gameIds);
        Map<Long, List<GbGameCategoryRelation>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(GbGameCategoryRelation::getGameId));
        for (GbGame game : games) {
            game.setCategories(categoryMap.getOrDefault(game.getId(), new ArrayList<>()));
        }
        return games;
    }

    @Override
    public List<Long> selectGbGameIds(GbGame gbGame)
    {
        return gbGameMapper.selectGbGameIds(gbGame);
    }

    @Override
    public List<GbGame> selectAllGbGamesForSync()
    {
        return gbGameMapper.selectAllGbGamesForSync();
    }

    /**
     * 新增游戏
     * 
     * @param gbGame 游戏
     * @return 结果
     */
    @Override
    public int insertGbGame(GbGame gbGame)
    {
        gbGame.setCreateTime(DateUtils.getNowDate());
        int result = gbGameMapper.insertGbGame(gbGame);
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            // 补全数据库默认值，防止 ES 文档中 status/delFlag 为 null
            if (gbGame.getStatus() == null) { gbGame.setStatus("0"); }
            if (gbGame.getDelFlag() == null) { gbGame.setDelFlag("0"); }
            elasticsearchSyncHelper.syncGame(gbGame);
        }
        
        return result;
    }

    /**
     * 批量新增游戏（优化版：分批处理，避免超时）
     * 
     * @param games 游戏列表
     * @return 游戏ID列表
     */
    @Override
    @Transactional
    public List<Long> batchInsertGames(List<GbGame> games)
    {
        return batchInsertGames(games, false);
    }

    /**
     * 批量新增游戏（可控是否立即同步 ES）
     *
     * @param games      游戏列表
     * @param skipEsSync true 时跳过立即 ES 同步，由调用方在事务提交后触发
     * @return 游戏ID列表
     */
    @Override
    @Transactional
    public List<Long> batchInsertGames(List<GbGame> games, boolean skipEsSync)
    {
        if (games == null || games.isEmpty()) {
            log.warn("[批量导入] 游戏列表为空，跳过导入");
            return new ArrayList<>();
        }
        
        log.info("[批量导入] 开始批量插入 {} 个游戏", games.size());
        long startTime = System.currentTimeMillis();
        
        Date now = DateUtils.getNowDate();
        // 设置创建时间，并补全 ES 同步所需的默认值（DB 默认值不会回写到 Java 对象）
        for (GbGame game : games) {
            game.setCreateTime(now);
            if (game.getStatus() == null) { game.setStatus("0"); }
            if (game.getDelFlag() == null) { game.setDelFlag("0"); }
        }
        
        // 分批处理，每批最多100条，避免单次SQL过大
        final int BATCH_SIZE = 100;
        List<Long> gameIds = new ArrayList<>();
        int totalGames = games.size();
        int processedCount = 0;
        
        for (int i = 0; i < totalGames; i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, totalGames);
            List<GbGame> batch = games.subList(i, end);
            
            log.debug("[批量导入] 处理第 {}/{} 批，游戏数: {}", (i / BATCH_SIZE + 1), (totalGames + BATCH_SIZE - 1) / BATCH_SIZE, batch.size());
            
            // 批量插入当前批次
            int result = gbGameMapper.batchInsertGames(batch);
            processedCount += result;
            
            // 收集当前批次插入后的游戏ID
            for (GbGame game : batch) {
                if (game.getId() != null) {
                    gameIds.add(game.getId());
                }
            }
        }
        
        long dbTime = System.currentTimeMillis() - startTime;
        log.info("[批量导入] 成功插入 {} 个游戏，耗时 {}ms，平均 {}ms/条", 
                 gameIds.size(), dbTime, gameIds.isEmpty() ? 0 : dbTime / gameIds.size());
        
        // 批量同步到Elasticsearch（异步执行，避免阻塞主流程和ES压力过大）
        // skipEsSync=true 时由调用方（如导入服务）在事务提交后统一触发，避免盒子关联尚未提交时同步
        if (!skipEsSync && processedCount > 0 && elasticsearchSyncHelper != null && !games.isEmpty()) {
            final List<GbGame> gamesToSync = new ArrayList<>(games);
            final int syncCount = gamesToSync.size();
            log.info("[批量导入] 启动ES异步同步线程，准备同步 {} 个游戏", syncCount);
            
            // 使用新线程异步执行ES批量同步
            new Thread(() -> {
                try {
                    log.info("[ES同步] 开始异步批量同步 {} 个游戏到Elasticsearch", syncCount);
                    long esStartTime = System.currentTimeMillis();
                    
                    elasticsearchSyncHelper.syncGames(gamesToSync);
                    
                    long esTime = System.currentTimeMillis() - esStartTime;
                    log.info("[ES同步] 异步批量同步完成，耗时 {}ms", esTime);
                } catch (Exception e) {
                    log.error("[ES同步] 批量同步游戏到Elasticsearch失败，游戏数量: {}", syncCount, e);
                }
            }, "ES-Batch-Sync-Thread").start();
        } else if (skipEsSync) {
            log.debug("[批量导入] skipEsSync=true，跳过立即 ES 同步，由调用方在事务提交后触发");
        } else {
            log.debug("[批量导入] 跳过ES同步 (result={}, helper={}, games.size={})", 
                     processedCount, elasticsearchSyncHelper != null, games.size());
        }
        
        return gameIds;
    }

    /**
     * 修改游戏
     * 
     * @param gbGame 游戏
     * @return 结果
     */
    @Override
    public List<GbGame> selectGbGameByIds(List<Long> ids)
    {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return gbGameMapper.selectGbGameByIds(ids);
    }

    @Override
    public int updateGbGame(GbGame gbGame)
    {
        gbGame.setUpdateTime(DateUtils.getNowDate());
        int result = gbGameMapper.updateGbGame(gbGame);
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.syncGame(gbGame);
        }
        
        return result;
    }

    /**
     * 批量删除游戏
     * 
     * @param ids 需要删除的游戏主键
     * @return 结果
     */
    @Override
    public int deleteGbGameByIds(Long[] ids)
    {
        int result = gbGameMapper.deleteGbGameByIds(ids);
        
        // 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteGames(ids);
        }
        
        return result;
    }

    /**
     * 删除游戏信息
     * 
     * @param id 游戏主键
     * @return 结果
     */
    @Override
    public int deleteGbGameById(Long id)
    {
        int result = gbGameMapper.deleteGbGameById(id);
        
        // 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteGame(id);
        }
        
        return result;
    }
    /**
     * 按游戏名称+副标题+游戏类型+站点ID精准查询（用于导入判重）
     */
    @Override
    public GbGame selectGbGameByNameAndSubtitle(Long siteId, String name, String subtitle, String gameType)
    {
        return gbGameMapper.selectGbGameByNameAndSubtitle(siteId, name, subtitle, gameType);
    }

    /**
     * 批量按名称预加载游戏（导入判重专用，避免 N+1 查询）
     */
    @Override
    public List<GbGame> selectGbGamesByNamesForDedup(Long siteId, List<String> names)
    {
        if (names == null || names.isEmpty()) return java.util.Collections.emptyList();
        return gbGameMapper.selectGbGamesByNamesForDedup(siteId, names);
    }

    /**
     * 通过站点ID查询游戏列表（包含siteId=0的全局数据）
     * 
     * @param siteId 站点ID
     * @param categoryId 分类ID（可选）
     * @return 游戏集合
     */
    @Override
    public List<GbGame> selectGamesBySiteId(Long siteId, Long categoryId)
    {
        return gbGameMapper.selectGamesBySiteId(siteId, categoryId);
    }

    /**
     * 通过盒子ID查询关联的游戏列表
     * 
     * @param boxId 盒子ID
     * @param siteId 站点ID
     * @return 游戏集合
     */
    @Override
    public List<GbGame> selectGamesByBoxId(Long boxId, Long siteId)
    {
        return gbGameMapper.selectGamesByBoxId(boxId, siteId);
    }

    /**
     * 通过文章ID查询关联的游戏列表
     * 
     * @param articleId 文章ID
     * @param siteId 站点ID
     * @return 游戏集合
     */
    @Override
    public List<GbGame> selectGamesByArticleId(Long articleId, Long siteId)
    {
        return gbGameMapper.selectGamesByArticleId(articleId, siteId);
    }}
