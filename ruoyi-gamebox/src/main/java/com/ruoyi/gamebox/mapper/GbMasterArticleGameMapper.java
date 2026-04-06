package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGame;

/**
 * 主文章-游戏关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface GbMasterArticleGameMapper 
{
    /**
     * 查询主文章-游戏关联
     * 
     * @param id 主文章-游戏关联主键
     * @return 主文章-游戏关联
     */
    public GbMasterArticleGame selectGbMasterArticleGameById(Long id);

    /**
     * 查询主文章-游戏关联列表
     * 
     * @param gbMasterArticleGame 主文章-游戏关联
     * @return 主文章-游戏关联集合
     */
    public List<GbMasterArticleGame> selectGbMasterArticleGameList(GbMasterArticleGame gbMasterArticleGame);

    /**
     * 根据主文章ID查询游戏关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章-游戏关联集合
     */
    public List<GbMasterArticleGame> selectGbMasterArticleGameByMasterArticleId(Long masterArticleId);

    /**
     * 新增主文章-游戏关联
     * 
     * @param gbMasterArticleGame 主文章-游戏关联
     * @return 结果
     */
    public int insertGbMasterArticleGame(GbMasterArticleGame gbMasterArticleGame);

    /**
     * 修改主文章-游戏关联
     * 
     * @param gbMasterArticleGame 主文章-游戏关联
     * @return 结果
     */
    public int updateGbMasterArticleGame(GbMasterArticleGame gbMasterArticleGame);

    /**
     * 删除主文章-游戏关联
     * 
     * @param id 主文章-游戏关联主键
     * @return 结果
     */
    public int deleteGbMasterArticleGameById(Long id);

    /**
     * 批量删除主文章-游戏关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleGameByIds(Long[] ids);

    /**
     * 根据主文章ID删除关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteGbMasterArticleGameByMasterArticleId(Long masterArticleId);

    /**
     * 检查是否已存在关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 是否存在
     */
    public boolean checkAssociationExists(Long masterArticleId, Long gameId);

    /**
     * 批量插入关联
     * 
     * @param associations 关联列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGame(List<GbMasterArticleGame> associations);
}