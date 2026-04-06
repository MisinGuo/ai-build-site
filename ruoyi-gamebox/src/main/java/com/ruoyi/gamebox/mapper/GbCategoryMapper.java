package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 分类管理Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbCategoryMapper 
{
    /**
     * 查询分类
     * 
     * @param id 分类主键
     * @return 分类
     */
    public GbCategory selectGbCategoryById(Long id);

    /**
     * 查询分类列表
     * 
     * @param gbCategory 分类
     * @return 分类集合
     */
    public List<GbCategory> selectGbCategoryList(GbCategory gbCategory);

    /**
     * 查询可见的分类列表（用于编辑器选择）
     * 只返回对指定网站可见的分类：
     * 1. 自有分类（启用状态）
     * 2. 默认配置（未被排除）
     * 3. 其他网站分类（关联且可见）
     * 
     * @param gbCategory 分类查询条件
     * @return 可见的分类集合
     */
    public List<GbCategory> selectVisibleGbCategoryList(GbCategory gbCategory);

    /**
     * 新增分类
     * 
     * @param gbCategory 分类
     * @return 结果
     */
    public int insertGbCategory(GbCategory gbCategory);

    /**
     * 修改分类
     * 
     * @param gbCategory 分类
     * @return 结果
     */
    public int updateGbCategory(GbCategory gbCategory);

    /**
     * 删除分类
     * 
     * @param id 分类主键
     * @return 结果
     */
    public int deleteGbCategoryById(Long id);

    /**
     * 批量删除分类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbCategoryByIds(Long[] ids);

    /**
     * 通过盒子ID查询关联的所有分类
     * 
     * @param boxId 盒子ID
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesByBoxId(Long boxId);

    /**
     * 通过游戏ID查询关联的所有分类
     * 
     * @param gameId 游戏ID
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesByGameId(Long gameId);

    /**
     * 通过文章ID查询关联的所有分类
     * 
     * @param articleId 文章ID
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesByArticleId(Long articleId);

    /**
     * 通过网站ID查询可见的分类（使用网站-分类关联表）
     * 
     * @param gbCategory 分类查询参数（包含siteId, categoryType, parentId）
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesBySiteId(GbCategory gbCategory);
    
    /**
     * 批量查询分类
     * 
     * @param ids 分类ID列表
     * @return 分类集合
     */
    public List<GbCategory> selectGbCategoryByIds(@Param("ids") List<Long> ids);

    /**
     * 查询文章板块列表（is_section='1' 且 parent_id=0）
     * 
     * @param siteId 网站ID，null表示查询所有板块
     * @return 板块列表
     */
    public List<GbCategory> selectArticleSections(@Param("siteId") Long siteId, @Param("personalSiteId") Long personalSiteId);

    /**
     * 通过板块ID查询分类列表（parent_id=sectionId）
     * 
     * @param sectionId 板块ID
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesBySection(@Param("sectionId") Long sectionId);
}
