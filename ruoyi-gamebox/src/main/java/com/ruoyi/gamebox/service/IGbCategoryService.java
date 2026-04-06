package com.ruoyi.gamebox.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.gamebox.domain.GbCategory;

/**
 * 分类管理Service接口
 * 
 * @author ruoyi
 */
public interface IGbCategoryService 
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
     * 只返回对指定网站可见的分类
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
     * 批量删除分类
     * 
     * @param ids 需要删除的分类主键集合
     * @return 结果
     */
    public int deleteGbCategoryByIds(Long[] ids);

    /**
     * 删除分类信息
     * 
     * @param id 分类主键
     * @return 结果
     */
    public int deleteGbCategoryById(Long id);

    /**
     * 获取分类类型选项列表
     * 
     * @return 分类类型选项列表
     */
    public List<Map<String, Object>> getCategoryTypeOptions();

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
     * 查询文章板块列表
     * 
     * @param siteId 网站ID，null表示查询所有板块
     * @return 板块列表
     */
    public List<GbCategory> selectArticleSections(Long siteId);

    /**
     * 通过板块ID查询分类列表
     * 
     * @param sectionId 板块ID
     * @return 分类集合
     */
    public List<GbCategory> selectCategoriesBySection(Long sectionId);
}
