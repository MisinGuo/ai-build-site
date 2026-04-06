package com.ruoyi.gamebox.mapper;

import com.ruoyi.gamebox.domain.GbMasterArticleHomepageBinding;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 主文章主页绑定Mapper接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Mapper
public interface GbMasterArticleHomepageBindingMapper 
{
    /**
     * 查询主文章主页绑定
     * 
     * @param id 主文章主页绑定主键
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectGbMasterArticleHomepageBindingById(Long id);

    /**
     * 根据主文章ID查询主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 根据游戏ID查询主页绑定
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectByGameId(@Param("gameId") Long gameId);

    /**
     * 根据游戏盒子ID查询主页绑定
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectByGameBoxId(@Param("gameBoxId") Long gameBoxId);

    /**
     * 新增主文章主页绑定
     * 
     * @param gbMasterArticleHomepageBinding 主文章主页绑定
     * @return 结果
     */
    public int insertGbMasterArticleHomepageBinding(GbMasterArticleHomepageBinding gbMasterArticleHomepageBinding);

    /**
     * 修改主文章主页绑定
     * 
     * @param gbMasterArticleHomepageBinding 主文章主页绑定
     * @return 结果
     */
    public int updateGbMasterArticleHomepageBinding(GbMasterArticleHomepageBinding gbMasterArticleHomepageBinding);

    /**
     * 删除主文章主页绑定
     * 
     * @param id 主文章主页绑定主键
     * @return 结果
     */
    public int deleteGbMasterArticleHomepageBindingById(Long id);

    /**
     * 根据主文章ID删除主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int deleteByMasterArticleId(@Param("masterArticleId") Long masterArticleId);

    /**
     * 根据游戏ID删除主页绑定
     * 
     * @param gameId 游戏ID
     * @return 结果
     */
    public int deleteByGameId(@Param("gameId") Long gameId);

    /**
     * 根据游戏盒子ID删除主页绑定
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    public int deleteByGameBoxId(@Param("gameBoxId") Long gameBoxId);

    /**
     * 根据游戏ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    public GbMasterArticleHomepageBinding selectByGameIdWithTitle(@Param("gameId") Long gameId);

    /**
     * 根据游戏盒子ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    public GbMasterArticleHomepageBinding selectByGameBoxIdWithTitle(@Param("gameBoxId") Long gameBoxId);
}
