package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSiteCategoryRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站-分类关联Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-27
 */
@Mapper
public interface GbSiteCategoryRelationMapper 
{
    /**
     * 查询网站-分类关联
     * 
     * @param id 主键
     * @return 网站-分类关联
     */
    public GbSiteCategoryRelation selectGbSiteCategoryRelationById(Long id);

    /**
     * 查询网站-分类关联列表
     * 
     * @param gbSiteCategoryRelation 网站-分类关联
     * @return 网站-分类关联集合
     */
    public List<GbSiteCategoryRelation> selectGbSiteCategoryRelationList(GbSiteCategoryRelation gbSiteCategoryRelation);

    /**
     * 查询网站的所有分类（带分类详情）
     * 
     * @param siteId 网站ID
     * @return 分类关联列表
     */
    public List<GbSiteCategoryRelation> selectCategoriesBySiteId(@Param("siteId") Long siteId);

    /**
     * 查询分类在哪些网站可用
     * 
     * @param categoryId 分类ID
     * @return 网站关联列表
     */
    public List<GbSiteCategoryRelation> selectSitesByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 检查关联是否存在
     * 
     * @param siteId 网站ID
     * @param categoryId 分类ID
     * @return 是否存在
     */
    public int checkRelationExists(@Param("siteId") Long siteId, @Param("categoryId") Long categoryId);
    
    /**
     * 检查排除关联是否存在
     * 
     * @param siteId 网站ID
     * @param categoryId 分类ID
     * @return 是否存在
     */
    public int checkExcludeRelationExists(@Param("siteId") Long siteId, @Param("categoryId") Long categoryId);

    /**
     * 查询所有全局分类的关联配置
     * 全局分类（site_id=0）的关联配置会被新创建的分类自动继承
     * 
     * @return 全局分类关联列表
     */
    public List<GbSiteCategoryRelation> selectGlobalCategoryRelations();

    /**
     * 新增网站-分类关联
     * 
     * @param gbSiteCategoryRelation 网站-分类关联
     * @return 结果
     */
    public int insertGbSiteCategoryRelation(GbSiteCategoryRelation gbSiteCategoryRelation);

    /**
     * 批量新增网站-分类关联
     * 
     * @param relations 关联列表
     * @return 结果
     */
    public int batchInsertGbSiteCategoryRelation(@Param("relations") List<GbSiteCategoryRelation> relations);

    /**
     * 修改网站-分类关联
     * 
     * @param gbSiteCategoryRelation 网站-分类关联
     * @return 结果
     */
    public int updateGbSiteCategoryRelation(GbSiteCategoryRelation gbSiteCategoryRelation);

    /**
     * 更新分类在网站的可见性
     * 
     * @param relation 关联信息（包含siteId, categoryId, isVisible）
     * @return 结果
     */
    public int updateVisibility(GbSiteCategoryRelation relation);

    /**
     * 删除网站-分类关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbSiteCategoryRelationById(Long id);

    /**
     * 删除网站的所有分类关联
     * 
     * @param siteId 网站ID
     * @return 结果
     */
    public int deleteGbSiteCategoryRelationsBySiteId(@Param("siteId") Long siteId);

    /**
     * 删除分类的所有网站关联
     * 
     * @param categoryId 分类ID
     * @return 结果
     */
    public int deleteGbSiteCategoryRelationsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 删除指定网站和分类的关联（支持按类型删除）
     * 
     * @param siteId 网站ID
     * @param categoryId 分类ID
     * @param relationType 关系类型（可选，为空则删除所有类型）
     * @return 结果
     */
    public int deleteGbSiteCategoryRelation(@Param("siteId") Long siteId, @Param("categoryId") Long categoryId, @Param("relationType") String relationType);

    /**
     * 批量删除网站-分类关联
     * 
     * @param ids 主键数组
     * @return 结果
     */
    public int deleteGbSiteCategoryRelationByIds(Long[] ids);
    
    /**
     * 批量查询已存在的关联
     * 
     * @param siteId 网站ID
     * @param categoryIds 分类ID列表
     * @return 已存在关联的分类ID列表
     */
    public List<Long> selectExistingRelations(@Param("siteId") Long siteId, @Param("categoryIds") List<Long> categoryIds);

    /**
     * 批量删除：对给定 categoryIds中指定类型的关联，保留 keepSiteIds 中的网站，删除其余
     *
     * @param categoryIds  分类ID列表
     * @param relationType 关系类型（include / exclude）
     * @param keepSiteIds  需保留的网站ID列表（为空则删除该类型所有关联）
     */
    public void deleteByCategoryIdsAndTypeExcluding(@Param("categoryIds") List<Long> categoryIds,
                                                     @Param("relationType") String relationType,
                                                     @Param("keepSiteIds") List<Long> keepSiteIds);
}
