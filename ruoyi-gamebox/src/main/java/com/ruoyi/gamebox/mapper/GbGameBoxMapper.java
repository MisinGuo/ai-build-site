package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbGameBox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏盒子Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbGameBoxMapper
{
    /**
     * 查询游戏盒子
     * 
     * @param id 游戏盒子主键
     * @return 游戏盒子
     */
    public GbGameBox selectGbGameBoxById(Long id);

    /**
     * 查询游戏盒子列表
     * 
     * @param gbGameBox 游戏盒子
     * @return 游戏盒子集合
     */
    public List<GbGameBox> selectGbGameBoxList(GbGameBox gbGameBox);

    /**
     * 查询所有游戏盒子（用于ES全量重建，不受站点权限过滤）
     */
    public List<GbGameBox> selectAllGbGameBoxesForSync();

    /**
     * 新增游戏盒子
     * 
     * @param gbGameBox 游戏盒子
     * @return 结果
     */
    public int insertGbGameBox(GbGameBox gbGameBox);

    /**
     * 修改游戏盒子
     * 
     * @param gbGameBox 游戏盒子
     * @return 结果
     */
    public int updateGbGameBox(GbGameBox gbGameBox);

    /**
     * 删除游戏盒子
     * 
     * @param id 游戏盒子主键
     * @return 结果
     */
    public int deleteGbGameBoxById(Long id);

    /**
     * 批量删除游戏盒子
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbGameBoxByIds(Long[] ids);

    /**
     * 通过分类ID查询盒子列表（支持siteId过滤和排序）
     * 
     * @param categoryId 分类ID
     * @param siteId 站点ID
     * @param orderBy 排序方式：hot-热门, new-最新, rating-评分
     * @return 盒子集合
     */
    public List<GbGameBox> selectBoxesByCategoryId(Long categoryId, Long siteId, String orderBy);

    /**
     * 通过站点ID查询盒子列表（包含siteId=0的全局数据）
     * 
     * @param siteId 站点ID
     * @param categoryId 分类ID（可选）
     * @return 盒子集合
     */
    public List<GbGameBox> selectBoxesBySiteId(@Param("siteId") Long siteId, @Param("categoryId") Long categoryId);

    /**
     * 通过游戏ID查询关联的盒子列表
     * 
     * @param gameId 游戏ID
     * @param siteId 站点ID
     * @return 盒子集合
     */
    public List<GbGameBox> selectBoxesByGameId(Long gameId, Long siteId);

    /**
     * 通过文章ID查询关联的盒子列表
     * 
     * @param articleId 文章ID
     * @param siteId 站点ID
     * @return 盒子集合
     */
    public List<GbGameBox> selectBoxesByArticleId(Long articleId, Long siteId);
    
    /**
     * 级联更新盒子下游戏的siteId
     * 只更新仅属于该盒子的游戏（避免影响属于多个盒子的游戏）
     * 
     * @param boxId 盒子ID
     * @param newSiteId 新的网站ID
     * @return 更新的游戏数量
     */
    public int updateGamesSiteIdByBoxId(@Param("boxId") Long boxId, @Param("newSiteId") Long newSiteId);
}
