package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;

/**
 * 主文章游戏盒子关联Service接口
 * 
 * @author ruoyi
 * @date 2024
 */
public interface IGbMasterArticleGameBoxesService
{
    /**
     * 根据主文章ID查询关联的游戏盒子列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏盒子关联列表
     */
    public List<GbMasterArticleGameBoxes> selectByMasterArticleId(Long masterArticleId);

    /**
     * 根据游戏盒子ID查询关联的主文章列表
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 文章关联列表
     */
    public List<GbMasterArticleGameBoxes> selectByGameBoxId(Long gameBoxId);

    /**
     * 新增主文章游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联信息
     * @return 结果
     */
    public int insertGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes);

    /**
     * 批量新增主文章游戏盒子关联
     * 
     * @param list 关联信息列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGameBoxes(List<GbMasterArticleGameBoxes> list);

    /**
     * 修改主文章游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联信息
     * @return 结果
     */
    public int updateGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes);

    /**
     * 删除主文章游戏盒子关联
     * 
     * @param id 关联ID
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxesById(Long id);

    /**
     * 删除主文章的所有游戏盒子关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteByMasterArticleId(Long masterArticleId);

    /**
     * 删除指定主文章和游戏盒子的关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    public int deleteByMasterArticleIdAndGameBoxId(Long masterArticleId, Long gameBoxId);

    /**
     * 保存主文章的游戏盒子关联（先删除旧关联，再插入新关联）
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxIds 游戏盒子ID列表
     * @param relationSource 关联来源
     * @param relationType 关联类型
     * @return 结果
     */
    public int saveGameBoxesForMasterArticle(Long masterArticleId, List<Long> gameBoxIds, String relationSource, String relationType);
}
