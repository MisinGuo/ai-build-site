package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 主文章-游戏关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Mapper
public interface GbMasterArticleGamesMapper 
{
    /**
     * 查询主文章的游戏关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏关联列表
     */
    public List<GbMasterArticleGames> selectByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 根据游戏ID查询关联的主文章列表
     * 
     * @param gameId 游戏ID
     * @return 主文章关联列表
     */
    public List<GbMasterArticleGames> selectByGameId(@Param("gameId") Long gameId);

    /**
     * 新增主文章-游戏关联
     * 
     * @param gbMasterArticleGames 关联对象
     * @return 结果
     */
    public int insertGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames);

    /**
     * 批量新增主文章-游戏关联
     * 
     * @param list 关联对象列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGames(List<GbMasterArticleGames> list);

    /**
     * 修改主文章-游戏关联
     * 
     * @param gbMasterArticleGames 关联对象
     * @return 结果
     */
    public int updateGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames);

    /**
     * 删除主文章-游戏关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbMasterArticleGamesById(@Param("id") Long id);

    /**
     * 删除主文章的所有游戏关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 删除主文章的指定游戏关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteByMasterArticleIdAndGameId(@Param("masterArticleId") Long masterArticleId, @Param("gameId") Long gameId);
}
