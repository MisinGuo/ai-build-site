package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;

/**
 * 主文章游戏关联Service接口
 * 
 * @author ruoyi
 * @date 2024
 */
public interface IGbMasterArticleGamesService
{
    /**
     * 根据主文章ID查询关联的游戏列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏关联列表
     */
    public List<GbMasterArticleGames> selectByMasterArticleId(Long masterArticleId);

    /**
     * 根据游戏ID查询关联的主文章列表
     * 
     * @param gameId 游戏ID
     * @return 文章关联列表
     */
    public List<GbMasterArticleGames> selectByGameId(Long gameId);

    /**
     * 新增主文章游戏关联
     * 
     * @param gbMasterArticleGames 关联信息
     * @return 结果
     */
    public int insertGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames);

    /**
     * 批量新增主文章游戏关联
     * 
     * @param list 关联信息列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGames(List<GbMasterArticleGames> list);

    /**
     * 修改主文章游戏关联
     * 
     * @param gbMasterArticleGames 关联信息
     * @return 结果
     */
    public int updateGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames);

    /**
     * 删除主文章游戏关联
     * 
     * @param id 关联ID
     * @return 结果
     */
    public int deleteGbMasterArticleGamesById(Long id);

    /**
     * 删除主文章的所有游戏关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteByMasterArticleId(Long masterArticleId);

    /**
     * 删除指定主文章和游戏的关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteByMasterArticleIdAndGameId(Long masterArticleId, Long gameId);

    /**
     * 保存主文章的游戏关联（先删除旧关联，再插入新关联）
     * 
     * @param masterArticleId 主文章ID
     * @param gameIds 游戏ID列表
     * @param relationSource 关联来源
     * @param relationType 关联类型
     * @return 结果
     */
    public int saveGamesForMasterArticle(Long masterArticleId, List<Long> gameIds, String relationSource, String relationType);
}
