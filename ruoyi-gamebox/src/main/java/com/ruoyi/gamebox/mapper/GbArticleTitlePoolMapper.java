package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbArticleTitlePool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 文章标题池Mapper接口
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
@Mapper
public interface GbArticleTitlePoolMapper 
{
    /**
     * 查询文章标题池
     * 
     * @param id 文章标题池主键
     * @return 文章标题池
     */
    public GbArticleTitlePool selectGbArticleTitlePoolById(Long id);

    /**
     * 查询文章标题池列表
     * 
     * @param gbArticleTitlePool 文章标题池
     * @return 文章标题池集合
     */
    public List<GbArticleTitlePool> selectGbArticleTitlePoolList(GbArticleTitlePool gbArticleTitlePool);

    /**
     * 新增文章标题池
     * 
     * @param gbArticleTitlePool 文章标题池
     * @return 结果
     */
    public int insertGbArticleTitlePool(GbArticleTitlePool gbArticleTitlePool);

    /**
     * 修改文章标题池
     * 
     * @param gbArticleTitlePool 文章标题池
     * @return 结果
     */
    public int updateGbArticleTitlePool(GbArticleTitlePool gbArticleTitlePool);

    /**
     * 删除文章标题池
     * 
     * @param id 文章标题池主键
     * @return 结果
     */
    public int deleteGbArticleTitlePoolById(Long id);

    /**
     * 批量删除文章标题池
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbArticleTitlePoolByIds(Long[] ids);

    /**
     * 检查标题是否已存在
     * 
     * @param title 标题
     * @return 数量
     */
    public int checkTitleExists(String title);

    /**
     * 统计总标题数
     * 
     * @return 数量
     */
    public int countTotalTitles();

    /**
     * 统计已使用标题数
     * 
     * @return 数量
     */
    public int countUsedTitles();

    /**
     * 统计未使用标题数
     * 
     * @return 数量
     */
    public int countUnusedTitles();

    /**
     * 获取未使用的标题(按优先级排序)
     * 
     * @param limit 限制数量
     * @return 标题列表
     */
    public List<GbArticleTitlePool> selectUnusedTitles(int limit);

    /**
     * 标记标题为已使用
     * 
     * @param id 标题ID
     * @param articleId 文章ID
     * @return 结果
     */
    public int markTitleAsUsed(@Param("id") Long id, @Param("articleId") Long articleId);
}
