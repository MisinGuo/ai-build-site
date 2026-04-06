package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbGame;

/**
 * 游戏Service接口
 * 
 * @author ruoyi
 */
public interface IGbGameService
{
    /**
     * 查询游戏
     * 
     * @param id 游戏主键
     * @return 游戏
     */
    public GbGame selectGbGameById(Long id);

    /**
     * 查询游戏列表
     * 
     * @param gbGame 游戏
     * @return 游戏集合
     */
    public List<GbGame> selectGbGameList(GbGame gbGame);

    /**
     * 查询符合筛选条件的所有游戏ID（不分页，用于跨页全选）
     */
    public List<Long> selectGbGameIds(GbGame gbGame);

    /**
     * 查询所有游戏（用于ES全量重建，不受站点权限过滤）
     */
    public List<GbGame> selectAllGbGamesForSync();

    /**
     * 新增游戏
     * 
     * @param gbGame 游戏
     * @return 结果
     */
    public int insertGbGame(GbGame gbGame);

    /**
     * 批量新增游戏
     * 
     * @param games 游戏列表
     * @return 游戏ID列表
     */
    public List<Long> batchInsertGames(List<GbGame> games);

    /**
     * 批量新增游戏（可控是否立即同步 ES）
     *
     * @param games      游戏列表
     * @param skipEsSync true 时跳过立即 ES 同步，由调用方在合适时机触发
     * @return 游戏ID列表
     */
    public List<Long> batchInsertGames(List<GbGame> games, boolean skipEsSync);

    /**
     * 修改游戏
     * 
     * @param gbGame 游戏
     * @return 结果
     */
    public int updateGbGame(GbGame gbGame);

    /**
     * 批量删除游戏
     * 
     * @param ids 需要删除的游戏主键集合
     * @return 结果
     */
    public int deleteGbGameByIds(Long[] ids);

    /**
     * 删除游戏信息
     * 
     * @param id 游戏主键
     * @return 结果
     */
    public int deleteGbGameById(Long id);

    /**
     * 按游戏名称+副标题+游戏类型+站点ID精准查询（用于导入判重）
     * subtitle/gameType 为 null 时匹配字段 IS NULL 的记录
     *
     * @param siteId   站点ID
     * @param name     游戏名称（精确匹配）
     * @param subtitle 游戏副标题/版本说明（精确匹配，null 表示无副标题）
     * @param gameType 游戏类型（精确匹配，null 表示无类型）
     * @return 游戏，不存在时返回 null
     */
    GbGame selectGbGameByNameAndSubtitle(Long siteId, String name, String subtitle, String gameType);

    /**
     * 批量按名称预加载游戏（用于导入判重，避免 N+1 查询）。
     * 仅按 site_id + name IN (...) 过滤，Java 层再按 subtitle/gameType 精确匹配。
     */
    List<GbGame> selectGbGamesByNamesForDedup(Long siteId, List<String> names);

    /**
     * 按主键 ID 列表批量查询游戏（用于 ES 导入同步前补查复用游戏对象）
     */
    List<GbGame> selectGbGameByIds(List<Long> ids);

    /**
     * 通过站点ID查询游戏列表（包含siteId=0的全局数据）
     * 
     * @param siteId 站点ID
     * @param categoryId 分类ID（可选）
     * @return 游戏集合
     */
    public List<GbGame> selectGamesBySiteId(Long siteId, Long categoryId);

    /**
     * 通过盒子ID查询关联的游戏列表
     * 
     * @param boxId 盒子ID
     * @param siteId 站点ID
     * @return 游戏集合
     */
    public List<GbGame> selectGamesByBoxId(Long boxId, Long siteId);

    /**
     * 通过文章ID查询关联的游戏列表
     * 
     * @param articleId 文章ID
     * @param siteId 站点ID
     * @return 游戏集合
     */
    public List<GbGame> selectGamesByArticleId(Long articleId, Long siteId);
}
