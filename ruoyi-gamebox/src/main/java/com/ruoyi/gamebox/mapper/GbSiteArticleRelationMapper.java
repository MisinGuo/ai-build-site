package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteArticleRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-文章关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Mapper
public interface GbSiteArticleRelationMapper 
{
    /**
     * 查询网站-文章关联
     * 
     * @param id 主键
     * @return 网站-文章关联
     */
    public GbSiteArticleRelation selectGbSiteArticleRelationById(Long id);

    /**
     * 查询网站-文章关联列表
     * 
     * @param gbSiteArticleRelation 网站-文章关联
     * @return 网站-文章关联集合
     */
    public List<GbSiteArticleRelation> selectGbSiteArticleRelationList(GbSiteArticleRelation gbSiteArticleRelation);

    /**
     * 查询网站的所有文章（带文章详情）
     * 
     * @param siteId 网站ID
     * @return 文章关联列表
     */
    public List<GbSiteArticleRelation> selectArticlesBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询文章在哪些网站可用
     * 
     * @param articleId 文章ID
     * @return 网站关联列表
     */
    public List<GbSiteArticleRelation> selectSitesByArticleId(@Param("articleId") Long articleId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("articleId") Long articleId);

    /**
     * 新增网站-文章关联
     * 
     * @param gbSiteArticleRelation 网站-文章关联
     * @return 结果
     */
    public int insertGbSiteArticleRelation(GbSiteArticleRelation gbSiteArticleRelation);

    /**
     * 批量新增网站-文章关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteArticleRelation(@Param("relations") List<GbSiteArticleRelation> relations);

    /**
     * 修改网站-文章关联
     * 
     * @param gbSiteArticleRelation 网站-文章关联
     * @return 结果
     */
    public int updateGbSiteArticleRelation(GbSiteArticleRelation gbSiteArticleRelation);

    /**
     * 更新文章在指定网站的可见性
     *
     * @param siteId    网站ID
     * @param articleId 文章ID
     * @param isVisible 可见性（'1'可见/'0'隐藏）
     * @return 结果
     */
    public int updateArticleVisibility(@Param("siteId") Long siteId,
                                       @Param("articleId") Long articleId,
                                       @Param("isVisible") String isVisible);

    /**
     * 删除网站-文章关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbSiteArticleRelationById(Long id);

    /**
     * 删除网站的所有文章关联
     * 
     * @param siteId 网站ID
     * @return 结果
     */
    public int deleteGbSiteArticleRelationsBySiteId(@Param("siteId") Long siteId);

    /**
     * 删除文章的所有网站关联
     * 
     * @param articleId 文章ID
     * @return 结果
     */
    public int deleteGbSiteArticleRelationsByArticleId(@Param("articleId") Long articleId);

    /**
     * 删除指定网站和文章的关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteArticleRelation(@Param("siteId") Long siteId, @Param("articleId") Long articleId, @Param("relationType") String relationType);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("articleId") Long articleId);

    /**
     * 批量删除网站-文章关联
     * 
     * @param ids 主键数组
     * @return 结果
     */
    public int deleteGbSiteArticleRelationByIds(Long[] ids);

    /**
     * 增加浏览量
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    public int incrementViewCount(@Param("siteId") Long siteId, @Param("articleId") Long articleId);

    /**
     * 删除排除关联
     * 
     * @param siteId 网站ID
     * @param articleId 文章ID
     * @return 结果
     */
    public int deleteExcludeRelation(@Param("siteId") Long siteId, @Param("articleId") Long articleId);

    /**
     * 查询被排除的网站ID列表
     * 
     * @param articleId 文章ID
     * @return 网站ID列表
     */
    public List<Long> selectExcludedSiteIds(@Param("articleId") Long articleId);

    /**
     * 批量删除排除关联
     * 
     * @param articleId 文章ID
     * @param siteIds 网站ID列表
     * @return 结果
     */
    public int batchDeleteExcludeRelations(@Param("articleId") Long articleId, @Param("siteIds") List<Long> siteIds);

    /**
     * 批量插入排除关联
     * 
     * @param articleId 文章ID
     * @param siteIds 网站ID列表
     * @param createBy 创建者
     * @return 结果
     */
    public int batchInsertExcludeRelations(@Param("articleId") Long articleId, @Param("siteIds") List<Long> siteIds, @Param("createBy") String createBy);

    /**
     * 批量删除：对给定 articleIds+relationType，删除不在 keepSiteIds 中的网站关联
     */
    public void deleteByArticleIdsAndTypeExcluding(@Param("articleIds") List<Long> articleIds,
                                                    @Param("relationType") String relationType,
                                                    @Param("keepSiteIds") List<Long> keepSiteIds);
}
