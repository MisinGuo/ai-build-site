package com.ruoyi.gamebox.search.repository;

import com.ruoyi.gamebox.search.document.ArticleDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

/**
 * 文章搜索Repository
 * 
 * @author ruoyi
 */
@Repository
public interface ArticleSearchRepository extends ElasticsearchRepository<ArticleDocument, Long>
{
    /**
     * 根据标题搜索
     */
    List<ArticleDocument> findByTitleContaining(String title);

    /**
     * 根据标题搜索（分页）
     */
    Page<ArticleDocument> findByTitleContaining(String title, Pageable pageable);

    /**
     * 根据分类ID查询
     */
    List<ArticleDocument> findByCategoryId(Long categoryId);

    /**
     * 根据分类ID查询（分页）
     */
    Page<ArticleDocument> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 统计指定状态的文章数量
     */
    long countByStatus(String status);

    /**
     * 统计有效文章数量（status='1' AND del_flag='0'）
     */
    long countByStatusAndDelFlag(String status, String delFlag);

    /**
     * 根据状态和网站ID查询
     */
    List<ArticleDocument> findByStatusAndSiteId(String status, Long siteId);

    /**
     * 根据作者查询
     */
    List<ArticleDocument> findByAuthor(String author);
    
    /**
     * 根据标题或内容搜索（分页）- 使用IK分词器的match查询
     */
    @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?1\"}}]}}")
    Page<ArticleDocument> findByTitleOrContentMatch(String title, String content, Pageable pageable);

    /**
     * 在指定 ID 集合内搜索标题或内容（IK 分词）
     * 用于: DB 取可见 ID 集合 -> ES 内做关键词打分，分页 total 完全准确。
     *
     * 这里使用字段默认 search_analyzer，避免过严参数导致中文短词漏查。
     */
    @Query("{\"bool\": {\"must\": [{\"bool\": {\"should\": [{\"match\": {\"title\": {\"query\": \"?0\", \"boost\": 2}}}, {\"match\": {\"content\": {\"query\": \"?1\"}}}], \"minimum_should_match\": 1}}], \"filter\": [{\"bool\": {\"should\": [{\"terms\": {\"id\": ?2}}, {\"ids\": {\"values\": ?3}}], \"minimum_should_match\": 1}}]}}")
    Page<ArticleDocument> findByTitleOrContentMatchWithinIds(String title, String content, Collection<Long> sourceIds, Collection<String> docIds, Pageable pageable);
}
