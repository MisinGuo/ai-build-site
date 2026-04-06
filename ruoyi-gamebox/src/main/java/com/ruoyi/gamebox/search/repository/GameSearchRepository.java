package com.ruoyi.gamebox.search.repository;

import com.ruoyi.gamebox.search.document.GameDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

/**
 * 游戏搜索Repository
 * 
 * @author ruoyi
 */
@Repository
public interface GameSearchRepository extends ElasticsearchRepository<GameDocument, Long>
{
    /**
     * 根据名称搜索
     */
    List<GameDocument> findByNameContaining(String name);

    /**
     * 根据名称搜索（分页）
     */
    Page<GameDocument> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据分类查询
     */
    List<GameDocument> findByCategory(String category);

    /**
     * 根据分类查询（分页）
     */
    Page<GameDocument> findByCategory(String category, Pageable pageable);

    /**
     * 根据游戏类型查询
     */
    List<GameDocument> findByGameType(String gameType);

    /**
     * 根据游戏类型查询（分页）
     */
    Page<GameDocument> findByGameType(String gameType, Pageable pageable);

    /**
     * 统计指定状态的游戏数量
     */
    long countByStatus(String status);

    /**
     * 统计有效游戏数量（status='1' AND del_flag='0'）
     */
    long countByStatusAndDelFlag(String status, String delFlag);

    /**
     * 根据状态和网站ID查询
     */
    List<GameDocument> findByStatusAndSiteId(String status, Long siteId);

    /**
     * 根据开发商查询
     */
    List<GameDocument> findByDeveloper(String developer);
    /**
     * 在指定 ID 集合内搜索游戏名称（IK 分词）
     * 用于: DB 取可见 ID 集合 -> ES 内做关键词打分，分页 total 完全准确。
     *
     * 使用字段默认 search_analyzer，避免过严参数导致中文短词漏查。
     */
    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match\": {\"name\": {\"query\": \"?0\", \"boost\": 3}}}, {\"match\": {\"subtitle\": {\"query\": \"?0\", \"boost\": 2}}}, {\"match\": {\"description\": {\"query\": \"?0\"}}}], \"minimum_should_match\": 1}}], \"filter\": [{\"bool\": {\"should\": [{\"terms\": {\"id\": ?1}}, {\"ids\": {\"values\": ?2}}], \"minimum_should_match\": 1}}]}}")
    Page<GameDocument> findByNameMatchWithinIds(String keyword, Collection<Long> sourceIds, Collection<String> docIds, Pageable pageable);
}
