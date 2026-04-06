package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 主文章-游戏盒子关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Mapper
public interface GbMasterArticleGameBoxesMapper 
{
    /**
     * 查询主文章的游戏盒子关联列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏盒子关联列表
     */
    public List<GbMasterArticleGameBoxes> selectByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 根据游戏盒子ID查询关联的主文章列表
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章关联列表
     */
    public List<GbMasterArticleGameBoxes> selectByGameBoxId(@Param("gameBoxId") Long gameBoxId);

    /**
     * 新增主文章-游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联对象
     * @return 结果
     */
    public int insertGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes);

    /**
     * 批量新增主文章-游戏盒子关联
     * 
     * @param list 关联对象列表
     * @return 结果
     */
    public int batchInsertGbMasterArticleGameBoxes(List<GbMasterArticleGameBoxes> list);

    /**
     * 修改主文章-游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联对象
     * @return 结果
     */
    public int updateGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes);

    /**
     * 删除主文章-游戏盒子关联
     * 
     * @param id 主键
     * @return 结果
     */
    public int deleteGbMasterArticleGameBoxesById(@Param("id") Long id);

    /**
     * 删除主文章的所有游戏盒子关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 删除主文章的指定游戏盒子关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    public int deleteByMasterArticleIdAndGameBoxId(@Param("masterArticleId") Long masterArticleId, @Param("gameBoxId") Long gameBoxId);
}
