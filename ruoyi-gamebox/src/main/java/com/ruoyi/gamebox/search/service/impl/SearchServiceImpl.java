package com.ruoyi.gamebox.search.service.impl;

import com.ruoyi.gamebox.search.document.ArticleDocument;
import com.ruoyi.gamebox.search.document.GameDocument;
import com.ruoyi.gamebox.search.document.GameBoxDocument;
import com.ruoyi.gamebox.search.repository.ArticleSearchRepository;
import com.ruoyi.gamebox.search.repository.GameSearchRepository;
import com.ruoyi.gamebox.search.repository.GameBoxSearchRepository;
import com.ruoyi.gamebox.search.service.ISearchService;
import com.ruoyi.gamebox.search.vo.SearchResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 * 
 * @author ruoyi
 */
@Service
public class SearchServiceImpl implements ISearchService
{
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private ArticleSearchRepository articleSearchRepository;

    @Autowired
    private GameSearchRepository gameSearchRepository;

    @Autowired
    private GameBoxSearchRepository gameBoxSearchRepository;

    /**
     * 全局搜索
     */
    @Override
    public SearchResultVO globalSearch(String keyword, int pageNum, int pageSize)
    {
        long startTime = System.currentTimeMillis();
        
        SearchResultVO result = new SearchResultVO();
        result.setKeyword(keyword);
        
        if (!StringUtils.hasText(keyword))
        {
            result.setArticles(Collections.emptyList());
            result.setGames(Collections.emptyList());
            result.setGameBoxes(Collections.emptyList());
            result.setTook(System.currentTimeMillis() - startTime);
            return result;
        }
        
        // 搜索文章（取前5条）- 使用match查询（IK分词器）
        Page<ArticleDocument> articlePage = articleSearchRepository.findByTitleOrContentMatch(
            keyword, keyword, PageRequest.of(0, 5));
        result.setArticles(articlePage.getContent());
        result.setArticleTotal(articlePage.getTotalElements());
        
        // 搜索游戏（取前5条）
        Page<GameDocument> gamePage = gameSearchRepository.findByNameContaining(keyword, PageRequest.of(0, 5));
        result.setGames(gamePage.getContent());
        result.setGameTotal(gamePage.getTotalElements());
        
        // 搜索游戏盒子（取前5条）
        Page<GameBoxDocument> gameBoxPage = gameBoxSearchRepository.findByNameContaining(keyword, PageRequest.of(0, 5));
        result.setGameBoxes(gameBoxPage.getContent());
        result.setGameBoxTotal(gameBoxPage.getTotalElements());
        
        result.setTook(System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * 搜索文章
     */
    @Override
    public Page<?> searchArticles(String keyword, Long categoryId, int pageNum, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        if (categoryId != null && StringUtils.hasText(keyword))
        {
            // 有分类和关键词，使用match查询（IK分词器）
            return articleSearchRepository.findByTitleOrContentMatch(keyword, keyword, pageable);
        }
        else if (categoryId != null)
        {
            return articleSearchRepository.findByCategoryId(categoryId, pageable);
        }
        else if (StringUtils.hasText(keyword))
        {
            return articleSearchRepository.findByTitleOrContentMatch(keyword, keyword, pageable);
        }
        else
        {
            return articleSearchRepository.findAll(pageable);
        }
    }

    /**
     * 搜索游戏
     */
    @Override
    public Page<?> searchGames(String keyword, String category, String gameType, int pageNum, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        if (StringUtils.hasText(gameType) && StringUtils.hasText(keyword))
        {
            // 暂时用简单查询
            return gameSearchRepository.findByNameContaining(keyword, pageable);
        }
        else if (StringUtils.hasText(category))
        {
            return gameSearchRepository.findByCategory(category, pageable);
        }
        else if (StringUtils.hasText(gameType))
        {
            return gameSearchRepository.findByGameType(gameType, pageable);
        }
        else if (StringUtils.hasText(keyword))
        {
            return gameSearchRepository.findByNameContaining(keyword, pageable);
        }
        else
        {
            return gameSearchRepository.findAll(pageable);
        }
    }

    /**
     * 搜索游戏盒子
     */
    @Override
    public Page<?> searchGameBoxes(String keyword, Long categoryId, int pageNum, int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        
        if (categoryId != null && StringUtils.hasText(keyword))
        {
            return gameBoxSearchRepository.findByNameContaining(keyword, pageable);
        }
        else if (categoryId != null)
        {
            return gameBoxSearchRepository.findByCategoryId(categoryId, pageable);
        }
        else if (StringUtils.hasText(keyword))
        {
            return gameBoxSearchRepository.findByNameContaining(keyword, pageable);
        }
        else
        {
            return gameBoxSearchRepository.findAll(pageable);
        }
    }

    /**
     * 获取热门搜索词
     */
    @Override
    public List<String> getHotSearchKeywords(int limit)
    {
        // TODO: 实现基于搜索日志的热词统计
        // 暂时返回示例数据
        List<String> hotKeywords = Arrays.asList(
                "传奇", "三国", "仙侠", "卡牌", "策略",
                "折扣游戏", "BT游戏", "新游", "热门游戏"
        );
        return hotKeywords.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 搜索建议
     */
    @Override
    public List<String> getSuggestions(String prefix, int limit)
    {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2)
        {
            return Collections.emptyList();
        }
        
        List<String> suggestions = new ArrayList<>();
        
        // 从文章标题中获取建议
        Page<ArticleDocument> articles = articleSearchRepository.findByTitleContaining(prefix, PageRequest.of(0, limit));
        articles.getContent().forEach(article -> {
            if (suggestions.size() < limit && StringUtils.hasText(article.getTitle()))
            {
                suggestions.add(article.getTitle());
            }
        });
        
        // 从游戏名称中获取建议
        if (suggestions.size() < limit)
        {
            Page<GameDocument> games = gameSearchRepository.findByNameContaining(prefix, PageRequest.of(0, limit - suggestions.size()));
            games.getContent().forEach(game -> {
                if (suggestions.size() < limit && StringUtils.hasText(game.getName()))
                {
                    suggestions.add(game.getName());
                }
            });
        }
        
        return suggestions;
    }

    /**
     * 获取搜索统计信息
     */
    @Override
    public Map<String, Object> getSearchStatistics()
    {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 统计status='1'且del_flag='0'的有效数据
            long articleCount = articleSearchRepository.countByStatusAndDelFlag("1", "0");
            long gameCount = gameSearchRepository.countByStatusAndDelFlag("1", "0");
            long gameBoxCount = gameBoxSearchRepository.countByStatusAndDelFlag("1", "0");
            
            statistics.put("articleCount", articleCount);
            statistics.put("gameCount", gameCount);
            statistics.put("gameBoxCount", gameBoxCount);
            statistics.put("totalCount", articleCount + gameCount + gameBoxCount);
        } catch (Exception e) {
            // ES索引可能缺少delFlag字段，抛出异常让调用方降级到数据库查询
            throw new RuntimeException("ES索引缺少delFlag字段，请重新同步索引数据", e);
        }
        
        return statistics;
    }

    /**
     * 在指定可见 ID 集合内搜索游戏（IK 分词）
     * 由调用方通过 DB related-mode 查出该站可见的游戏 ID ，
     * ES 仅在此集合内实施全文搜索，分页 total 完全准确。
     */
    @Override
    public Page<?> searchGamesWithinIds(String keyword, java.util.Collection<Long> visibleIds, int pageNum, int pageSize)
    {
        if (!StringUtils.hasText(keyword) || visibleIds == null || visibleIds.isEmpty())
        {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(pageNum - 1, pageSize), 0);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        List<String> idValues = visibleIds.stream().map(String::valueOf).collect(Collectors.toList());
        Page<?> page = gameSearchRepository.findByNameMatchWithinIds(keyword, visibleIds, idValues, pageable);
        log.info("ES游戏检索: keyword='{}', visibleIds={}, resultTotal={}", keyword, visibleIds.size(), page.getTotalElements());
        return page;
    }

    /**
     * 在指定可见 ID 集合内搜索文章（IK 分词）
     * 由调用方通过 DB related-mode 查出该站可见的文章 ID ，
     * ES 仅在此集合内实施全文搜索，分页 total 完全准确。
     * locale 参数目前仅作记录，文章索引内已包含 locale 信息，可由调用方在 DB 阶段过滤。
     */
    @Override
    public Page<?> searchArticlesWithinIds(String keyword, java.util.Collection<Long> visibleIds, String locale, int pageNum, int pageSize)
    {
        if (!StringUtils.hasText(keyword) || visibleIds == null || visibleIds.isEmpty())
        {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(pageNum - 1, pageSize), 0);
        }
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        List<String> idValues = visibleIds.stream().map(String::valueOf).collect(Collectors.toList());
        Page<?> page = articleSearchRepository.findByTitleOrContentMatchWithinIds(keyword, keyword, visibleIds, idValues, pageable);
        log.info("ES文章检索: keyword='{}', locale='{}', visibleIds={}, resultTotal={}", keyword, locale, visibleIds.size(), page.getTotalElements());
        return page;
    }
}
