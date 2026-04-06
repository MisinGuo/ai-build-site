package com.ruoyi.gamebox.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation;

/**
 * 网站-主文章关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Mapper
public interface GbSiteMasterArticleRelationMapper 
{
    /**
     * 查询关联
     */
    public GbSiteMasterArticleRelation selectById(Long id);

    /**
     * 查询关联列表
     */
    public List<GbSiteMasterArticleRelation> selectList(GbSiteMasterArticleRelation relation);

    /**
     * 根据网站ID和主文章ID查询关联
     */
    public GbSiteMasterArticleRelation selectBySiteAndMasterArticle(
        @Param("siteId") Long siteId, 
        @Param("masterArticleId") Long masterArticleId,
        @Param("relationType") String relationType
    );

    /**
     * 查询主文章被哪些网站排除
     */
    public List<Long> selectExcludedSiteIdsByMasterArticle(@Param("masterArticleId") Long masterArticleId);

    /**
     * 新增关联
     */
    public int insert(GbSiteMasterArticleRelation relation);

    /**
     * 批量新增关联
     */
    public int batchInsert(@Param("list") List<GbSiteMasterArticleRelation> list);

    /**
     * 更新关联
     */
    public int update(GbSiteMasterArticleRelation relation);

    /**
     * 删除关联
     */
    public int deleteById(Long id);

    /**
     * 删除指定关联
     */
    public int deleteBySiteAndMasterArticle(
        @Param("siteId") Long siteId, 
        @Param("masterArticleId") Long masterArticleId,
        @Param("relationType") String relationType
    );

    /**
     * 删除网站的所有排除关联
     */
    public int deleteExclusionsBySite(@Param("siteId") Long siteId);

    /**
     * 删除主文章的所有排除关联
     */
    public int deleteExclusionsByMasterArticle(@Param("masterArticleId") Long masterArticleId);
}
