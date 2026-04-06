package com.ruoyi.gamebox.search.helper;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.mapper.GbBoxGameRelationMapper;
import com.ruoyi.gamebox.search.document.ArticleDocument;
import com.ruoyi.gamebox.search.document.GameBoxDocument;
import com.ruoyi.gamebox.search.document.GameDocument;
import com.ruoyi.gamebox.search.repository.ArticleSearchRepository;
import com.ruoyi.gamebox.search.repository.GameBoxSearchRepository;
import com.ruoyi.gamebox.search.repository.GameSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Elasticsearch数据同步助手
 * 
 * @author ruoyi
 */
@Component
public class ElasticsearchSyncHelper
{
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchSyncHelper.class);

    @Autowired
    private ArticleSearchRepository articleSearchRepository;

    @Autowired
    private GameSearchRepository gameSearchRepository;

    @Autowired
    private GameBoxSearchRepository gameBoxSearchRepository;

    @Autowired
    private GbBoxGameRelationMapper boxGameRelationMapper;

    /**
     * 同步文章到ES
     */
    @Async
    public void syncArticle(GbArticle article)
    {
        try
        {
            ArticleDocument document = new ArticleDocument();
            BeanUtils.copyProperties(article, document);
            
            // 处理标签JSON数组
            if (StringUtils.hasText(article.getTags()))
            {
                try
                {
                    JSONArray tagsArray = JSON.parseArray(article.getTags());
                    document.setTags(tagsArray.toArray(new String[0]));
                }
                catch (Exception e)
                {
                    log.warn("解析文章标签失败: {}", article.getId(), e);
                }
            }
            
            articleSearchRepository.save(document);
            log.info("文章同步到ES成功: {}", article.getId());
        }
        catch (Exception e)
        {
            log.error("文章同步到ES失败: {}", article.getId(), e);
        }
    }

    /**
     * 批量同步文章到ES
     */
    @Async
    public void syncArticles(Iterable<GbArticle> articles)
    {
        articles.forEach(this::syncArticle);
    }

    /**
     * 删除ES中的文章
     */
    @Async
    public void deleteArticle(Long id)
    {
        try
        {
            articleSearchRepository.deleteById(id);
            log.info("从ES删除文章成功: {}", id);
        }
        catch (Exception e)
        {
            log.error("从ES删除文章失败: {}", id, e);
        }
    }

    /**
     * 批量删除ES中的文章
     */
    @Async
    public void deleteArticles(Long[] ids)
    {
        for (Long id : ids)
        {
            deleteArticle(id);
        }
    }

    /**
     * 同步游戏到ES
     */
    @Async
    public void syncGame(GbGame game)
    {
        try
        {
            GameDocument document = new GameDocument();
            BeanUtils.copyProperties(game, document);

            // 填充盒子信息
            try {
                java.util.List<GbBoxGameRelation> boxRelations = boxGameRelationMapper.selectBoxSummaryByGameIds(
                        java.util.Collections.singletonList(game.getId()));
                if (boxRelations != null && !boxRelations.isEmpty()) {
                    java.util.List<GameDocument.GameBoxInfo> boxes = boxRelations.stream()
                            .map(r -> new GameDocument.GameBoxInfo(r.getBoxId(), r.getBoxName(), r.getBoxLogoUrl()))
                            .collect(java.util.stream.Collectors.toList());
                    document.setBoxes(boxes);
                }
            } catch (Exception e) {
                log.warn("填充游戏盒子信息失败 [gameId={}]: {}", game.getId(), e.getMessage());
            }

            gameSearchRepository.save(document);
            log.info("游戏同步到ES成功: {}", game.getId());
        }
        catch (Exception e)
        {
            log.error("游戏同步到ES失败: {}", game.getId(), e);
        }
    }

    /**
     * 批量同步游戏到ES
     */
    @Async
    public void syncGames(Iterable<GbGame> games)
    {
        int totalCount = 0;
        int successCount = 0;
        int failCount = 0;
        long startTime = System.currentTimeMillis();
        
        try
        {
            // 统计总数
            for (GbGame game : games) {
                totalCount++;
            }
            
            log.info("[ES批量同步] 开始批量同步 {} 个游戏到Elasticsearch", totalCount);

            // 批量查询所有游戏的盒子关系
            java.util.Map<Long, java.util.List<GameDocument.GameBoxInfo>> boxMap = new java.util.HashMap<>();
            try {
                java.util.List<Long> gameIds = new java.util.ArrayList<>();
                for (GbGame g : games) {
                    if (g != null && g.getId() != null) {
                        gameIds.add(g.getId());
                    }
                }
                if (!gameIds.isEmpty()) {
                    // 分批查询，避免 IN 从句过长导致超时或解析错误
                    int batchSize = 100;
                    for (int i = 0; i < gameIds.size(); i += batchSize) {
                        java.util.List<Long> subList = gameIds.subList(i, Math.min(i + batchSize, gameIds.size()));
                        java.util.List<GbBoxGameRelation> allBoxRelations = boxGameRelationMapper.selectBoxSummaryByGameIds(subList);
                        if (allBoxRelations != null) {
                            for (GbBoxGameRelation rel : allBoxRelations) {
                                boxMap.computeIfAbsent(rel.getGameId(), k -> new java.util.ArrayList<>())
                                        .add(new GameDocument.GameBoxInfo(rel.getBoxId(), rel.getBoxName(), rel.getBoxLogoUrl()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("[ES批量同步] 批量查询盒子关系失败: {}", e.getMessage());
            }

            // 转换为GameDocument列表
            java.util.List<GameDocument> documents = new java.util.ArrayList<>();
            for (GbGame game : games) {
                try {
                    GameDocument document = new GameDocument();
                    BeanUtils.copyProperties(game, document);
                    // 填充盒子信息
                    java.util.List<GameDocument.GameBoxInfo> boxes = boxMap.get(game.getId());
                    if (boxes != null && !boxes.isEmpty()) {
                        document.setBoxes(boxes);
                    }
                    documents.add(document);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    log.error("[ES批量同步] 转换游戏文档失败 [gameId={}]: {}", 
                             game != null ? game.getId() : "null", e.getMessage());
                }
            }
            
            // 批量保存到ES（Spring Data ES的saveAll是批量操作）
            if (!documents.isEmpty()) {
                long saveStartTime = System.currentTimeMillis();
                gameSearchRepository.saveAll(documents);
                long saveTime = System.currentTimeMillis() - saveStartTime;
                
                long totalTime = System.currentTimeMillis() - startTime;
                log.info("[ES批量同步] 批量同步完成 - 总数: {}, 成功: {}, 失败: {}, 保存耗时: {}ms, 总耗时: {}ms", 
                         totalCount, successCount, failCount, saveTime, totalTime);
            } else {
                log.warn("[ES批量同步] 没有有效文档需要同步到ES");
            }
        }
        catch (Exception e)
        {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("[ES批量同步] 批量同步游戏到ES失败 - 总数: {}, 已转换: {}, 失败: {}, 耗时: {}ms", 
                     totalCount, successCount, failCount, totalTime, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 删除ES中的游戏
     */
    @Async
    public void deleteGame(Long id)
    {
        try
        {
            gameSearchRepository.deleteById(id);
            log.info("从ES删除游戏成功: {}", id);
        }
        catch (Exception e)
        {
            log.error("从ES删除游戏失败: {}", id, e);
        }
    }

    /**
     * 批量删除ES中的游戏
     */
    @Async
    public void deleteGames(Long[] ids)
    {
        for (Long id : ids)
        {
            deleteGame(id);
        }
    }

    /**
     * 同步游戏盒子到ES
     */
    public void syncGameBox(GbGameBox gameBox)
    {
        try
        {
            GameBoxDocument document = new GameBoxDocument();
            BeanUtils.copyProperties(gameBox, document);
            
            // 处理特色功能JSON数组
            if (StringUtils.hasText(gameBox.getFeatures()))
            {
                try
                {
                    JSONArray featuresArray = JSON.parseArray(gameBox.getFeatures());
                    document.setFeatures(featuresArray.toArray(new String[0]));
                }
                catch (Exception e)
                {
                    log.warn("解析游戏盒子特色功能失败: {}", gameBox.getId(), e);
                }
            }
            
            gameBoxSearchRepository.save(document);
            log.info("游戏盒子同步到ES成功: {}", gameBox.getId());
        }
        catch (Exception e)
        {
            log.error("游戏盒子同步到ES失败: {}", gameBox.getId(), e);
        }
    }

    /**
     * 批量同步游戏盒子到ES
     */
    public void syncGameBoxes(Iterable<GbGameBox> gameBoxes)
    {
        gameBoxes.forEach(this::syncGameBox);
    }

    /**
     * 删除ES中的游戏盒子
     */
    public void deleteGameBox(Long id)
    {
        try
        {
            gameBoxSearchRepository.deleteById(id);
            log.info("从ES删除游戏盒子成功: {}", id);
        }
        catch (Exception e)
        {
            log.error("从ES删除游戏盒子失败: {}", id, e);
        }
    }

    /**
     * 批量删除ES中的游戏盒子
     */
    public void deleteGameBoxes(Long[] ids)
    {
        for (Long id : ids)
        {
            deleteGameBox(id);
        }
    }

    /**
     * 清空所有ES索引数据
     */
    public void clearAllIndexes()
    {
        try
        {
            articleSearchRepository.deleteAll();
            log.info("清空文章ES索引成功");
        }
        catch (Exception e)
        {
            log.error("清空文章ES索引失败", e);
        }

        try
        {
            gameSearchRepository.deleteAll();
            log.info("清空游戏ES索引成功");
        }
        catch (Exception e)
        {
            log.error("清空游戏ES索引失败", e);
        }

        try
        {
            gameBoxSearchRepository.deleteAll();
            log.info("清空游戏盒子ES索引成功");
        }
        catch (Exception e)
        {
            log.error("清空游戏盒子ES索引失败", e);
        }
    }
}
