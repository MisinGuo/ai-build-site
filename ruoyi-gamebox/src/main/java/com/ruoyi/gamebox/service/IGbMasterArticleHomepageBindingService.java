package com.ruoyi.gamebox.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.gamebox.domain.GbMasterArticleHomepageBinding;

/**
 * 主文章主页绑定Service接口
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public interface IGbMasterArticleHomepageBindingService 
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
    public GbMasterArticleHomepageBinding selectByMasterArticleId(Long masterArticleId);

    /**
     * 根据游戏ID查询主页绑定
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectByGameId(Long gameId);

    /**
     * 根据游戏盒子ID查询主页绑定
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定
     */
    public GbMasterArticleHomepageBinding selectByGameBoxId(Long gameBoxId);

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
     * 删除主文章主页绑定信息
     * 
     * @param id 主文章主页绑定主键
     * @return 结果
     */
    public int deleteGbMasterArticleHomepageBindingById(Long id);

    /**
     * 绑定主文章到游戏主页
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @param force 是否强制绑定（覆盖已有绑定）
     * @return 结果
     */
    public AjaxResult bindToGame(Long masterArticleId, Long gameId, boolean force);

    /**
     * 绑定主文章到游戏盒子主页
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @param force 是否强制绑定（覆盖已有绑定）
     * @return 结果
     */
    public AjaxResult bindToGameBox(Long masterArticleId, Long gameBoxId, boolean force);

    /**
     * 解绑主文章的游戏主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 结果
     */
    public int unbindFromGame(Long masterArticleId, Long gameId);

    /**
     * 解绑主文章的游戏盒子主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    public int unbindFromGameBox(Long masterArticleId, Long gameBoxId);

    /**
     * 根据游戏ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    public GbMasterArticleHomepageBinding selectByGameIdWithTitle(Long gameId);

    /**
     * 根据游戏盒子ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    public GbMasterArticleHomepageBinding selectByGameBoxIdWithTitle(Long gameBoxId);
}
