package com.ruoyi.gamebox.search.repository;

import com.ruoyi.gamebox.search.document.GameBoxDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 游戏盒子搜索Repository
 * 
 * @author ruoyi
 */
@Repository
public interface GameBoxSearchRepository extends ElasticsearchRepository<GameBoxDocument, Long>
{
    /**
     * 根据名称搜索
     */
    List<GameBoxDocument> findByNameContaining(String name);

    /**
     * 根据名称搜索（分页）
     */
    Page<GameBoxDocument> findByNameContaining(String name, Pageable pageable);

    /**
     * 根据分类ID查询
     */
    List<GameBoxDocument> findByCategoryId(Long categoryId);

    /**
     * 根据分类ID查询（分页）
     */
    Page<GameBoxDocument> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 统计指定状态的盒子数量
     */
    long countByStatus(String status);

    /**
     * 统计有效盒子数量（status='1' AND del_flag='0'）
     */
    long countByStatusAndDelFlag(String status, String delFlag);

    /**
     * 根据状态和网站ID查询
     */
    List<GameBoxDocument> findByStatusAndSiteId(String status, Long siteId);
}
