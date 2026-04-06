package com.ruoyi.gamebox.search.service;

import com.ruoyi.gamebox.search.vo.SearchResultVO;
import org.springframework.data.domain.Page;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 统一搜索服务接口
 * 
 * @author ruoyi
 */
public interface ISearchService
{
    /**
     * 全局搜索（搜索文章、游戏、游戏盒子）
     * 
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 搜索结果
     */
    SearchResultVO globalSearch(String keyword, int pageNum, int pageSize);

    /**
     * 搜索文章
     * 
     * @param keyword 关键词
     * @param categoryId 分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 文章列表
     */
    Page<?> searchArticles(String keyword, Long categoryId, int pageNum, int pageSize);

    /**
     * 搜索游戏
     * 
     * @param keyword 关键词
     * @param category 分类（可选）
     * @param gameType 游戏类型（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 游戏列表
     */
    Page<?> searchGames(String keyword, String category, String gameType, int pageNum, int pageSize);

    /**
     * 搜索游戏盒子
     * 
     * @param keyword 关键词
     * @param categoryId 分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 游戏盒子列表
     */
    Page<?> searchGameBoxes(String keyword, Long categoryId, int pageNum, int pageSize);

    /**
     * 获取热门搜索词（基于搜索次数统计）
     * 
     * @param limit 返回数量
     * @return 热门搜索词列表
     */
    List<String> getHotSearchKeywords(int limit);

    /**
     * 搜索建议（自动补全）
     * 
     * @param prefix 输入前缀
     * @param limit 返回数量
     * @return 建议词列表
     */
    List<String> getSuggestions(String prefix, int limit);

    /**
     * 获取搜索统计信息
     * 
     * @return 统计数据
     */
    Map<String, Object> getSearchStatistics();

    /**
     * 在指定可见 ID 集合内搜索游戏（IK 分词）
     * <p>由调用方通过 DB related-mode 查出该站可见的游戏 ID，传入后 ES 仅在此集合内做全文搜索，
     * 分页 total 完全精确。</p>
     *
     * @param keyword 搜索关键词
     * @param visibleIds 该站可见的游戏 ID 集合（由 DB related-mode 查出）
     * @param pageNum 页码（1-based）
     * @param pageSize 每页数量
     * @return 搜索结果页
     */
    Page<?> searchGamesWithinIds(String keyword, Collection<Long> visibleIds, int pageNum, int pageSize);

    /**
     * 在指定可见 ID 集合内搜索文章（IK 分词）
     * <p>由调用方通过 DB related-mode 查出该站可见的文章 ID，传入后 ES 仅在此集合内做全文搜索，
     * 分页 total 完全精确。</p>
     *
     * @param keyword 搜索关键词
     * @param visibleIds 该站可见的文章 ID 集合（由 DB related-mode 查出）
     * @param locale 语言代码，可选（ES 文档有 locale 字段时进一步过滤）
     * @param pageNum 页码（1-based）
     * @param pageSize 每页数量
     * @return 搜索结果页
     */
    Page<?> searchArticlesWithinIds(String keyword, Collection<Long> visibleIds, String locale, int pageNum, int pageSize);
}
