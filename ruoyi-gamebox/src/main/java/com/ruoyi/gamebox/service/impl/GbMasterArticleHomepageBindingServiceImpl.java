package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.gamebox.domain.GbMasterArticleHomepageBinding;
import com.ruoyi.gamebox.mapper.GbMasterArticleHomepageBindingMapper;
import com.ruoyi.gamebox.service.IGbMasterArticleHomepageBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 主文章主页绑定Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
@Service
public class GbMasterArticleHomepageBindingServiceImpl implements IGbMasterArticleHomepageBindingService 
{
    @Autowired
    private GbMasterArticleHomepageBindingMapper gbMasterArticleHomepageBindingMapper;

    /**
     * 查询主文章主页绑定
     * 
     * @param id 主文章主页绑定主键
     * @return 主文章主页绑定
     */
    @Override
    public GbMasterArticleHomepageBinding selectGbMasterArticleHomepageBindingById(Long id)
    {
        return gbMasterArticleHomepageBindingMapper.selectGbMasterArticleHomepageBindingById(id);
    }

    /**
     * 根据主文章ID查询主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @return 主文章主页绑定
     */
    @Override
    public GbMasterArticleHomepageBinding selectByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleHomepageBindingMapper.selectByMasterArticleId(masterArticleId);
    }

    /**
     * 根据游戏ID查询主页绑定
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定
     */
    @Override
    public GbMasterArticleHomepageBinding selectByGameId(Long gameId)
    {
        return gbMasterArticleHomepageBindingMapper.selectByGameId(gameId);
    }

    /**
     * 根据游戏盒子ID查询主页绑定
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定
     */
    @Override
    public GbMasterArticleHomepageBinding selectByGameBoxId(Long gameBoxId)
    {
        return gbMasterArticleHomepageBindingMapper.selectByGameBoxId(gameBoxId);
    }

    /**
     * 新增主文章主页绑定
     * 
     * @param gbMasterArticleHomepageBinding 主文章主页绑定
     * @return 结果
     */
    @Override
    public int insertGbMasterArticleHomepageBinding(GbMasterArticleHomepageBinding gbMasterArticleHomepageBinding)
    {
        gbMasterArticleHomepageBinding.setCreateTime(DateUtils.getNowDate());
        return gbMasterArticleHomepageBindingMapper.insertGbMasterArticleHomepageBinding(gbMasterArticleHomepageBinding);
    }

    /**
     * 修改主文章主页绑定
     * 
     * @param gbMasterArticleHomepageBinding 主文章主页绑定
     * @return 结果
     */
    @Override
    public int updateGbMasterArticleHomepageBinding(GbMasterArticleHomepageBinding gbMasterArticleHomepageBinding)
    {
        gbMasterArticleHomepageBinding.setUpdateTime(DateUtils.getNowDate());
        return gbMasterArticleHomepageBindingMapper.updateGbMasterArticleHomepageBinding(gbMasterArticleHomepageBinding);
    }

    /**
     * 删除主文章主页绑定信息
     * 
     * @param id 主文章主页绑定主键
     * @return 结果
     */
    @Override
    public int deleteGbMasterArticleHomepageBindingById(Long id)
    {
        return gbMasterArticleHomepageBindingMapper.deleteGbMasterArticleHomepageBindingById(id);
    }

    /**
     * 绑定主文章到游戏主页
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @param force 是否强制绑定（覆盖已有绑定）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult bindToGame(Long masterArticleId, Long gameId, boolean force)
    {
        // 检查该游戏是否已绑定其他主文章（这是需要用户确认的关键检测）
        GbMasterArticleHomepageBinding existingByGame = gbMasterArticleHomepageBindingMapper.selectByGameId(gameId);
        if (existingByGame != null) {
            // 如果绑定的是同一个文章，无需提示
            if (!existingByGame.getMasterArticleId().equals(masterArticleId)) {
                if (!force) {
                    // 返回需要确认的响应，不抛异常
                    return AjaxResult.success()
                        .put("needConfirm", true)
                        .put("message", "该游戏主页已绑定其他主文章，是否要强制覆盖？");
                }
            }
            // 删除原有绑定
            gbMasterArticleHomepageBindingMapper.deleteByGameId(gameId);
        }
        
        // 检查该主文章是否已绑定到其他主页，如果有则直接覆盖（无需确认）
        GbMasterArticleHomepageBinding existingByMasterArticle = gbMasterArticleHomepageBindingMapper.selectByMasterArticleId(masterArticleId);
        if (existingByMasterArticle != null) {
            // 删除原有绑定
            gbMasterArticleHomepageBindingMapper.deleteByMasterArticleId(masterArticleId);
        }

        // 创建新绑定
        GbMasterArticleHomepageBinding binding = new GbMasterArticleHomepageBinding();
        binding.setMasterArticleId(masterArticleId);
        binding.setGameId(gameId);
        int result = insertGbMasterArticleHomepageBinding(binding);
        
        return result > 0 ? AjaxResult.success("绑定主页成功") : AjaxResult.error("绑定主页失败");
    }

    /**
     * 绑定主文章到游戏盒子主页
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @param force 是否强制绑定（覆盖已有绑定）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult bindToGameBox(Long masterArticleId, Long gameBoxId, boolean force)
    {
        // 检查该游戏盒子是否已绑定其他主文章（这是需要用户确认的关键检测）
        GbMasterArticleHomepageBinding existingByGameBox = gbMasterArticleHomepageBindingMapper.selectByGameBoxId(gameBoxId);
        if (existingByGameBox != null) {
            // 如果绑定的是同一个文章，无需提示
            if (!existingByGameBox.getMasterArticleId().equals(masterArticleId)) {
                if (!force) {
                    // 返回需要确认的响应，不抛异常
                    return AjaxResult.success()
                        .put("needConfirm", true)
                        .put("message", "该游戏盒子主页已绑定其他主文章，是否要强制覆盖？");
                }
            }
            // 删除原有绑定
            gbMasterArticleHomepageBindingMapper.deleteByGameBoxId(gameBoxId);
        }
        
        // 检查该主文章是否已绑定到其他主页，如果有则直接覆盖（无需确认）
        GbMasterArticleHomepageBinding existingByMasterArticle = gbMasterArticleHomepageBindingMapper.selectByMasterArticleId(masterArticleId);
        if (existingByMasterArticle != null) {
            // 删除原有绑定
            gbMasterArticleHomepageBindingMapper.deleteByMasterArticleId(masterArticleId);
        }

        // 创建新绑定
        GbMasterArticleHomepageBinding binding = new GbMasterArticleHomepageBinding();
        binding.setMasterArticleId(masterArticleId);
        binding.setGameBoxId(gameBoxId);
        int result = insertGbMasterArticleHomepageBinding(binding);
        
        return result > 0 ? AjaxResult.success("绑定主页成功") : AjaxResult.error("绑定主页失败");
    }

    /**
     * 解绑主文章的游戏主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindFromGame(Long masterArticleId, Long gameId)
    {
        GbMasterArticleHomepageBinding binding = gbMasterArticleHomepageBindingMapper.selectByMasterArticleId(masterArticleId);
        if (binding == null) {
            throw new ServiceException("该主文章未绑定任何主页");
        }
        if (binding.getGameId() == null || !binding.getGameId().equals(gameId)) {
            throw new ServiceException("该主文章未绑定到指定游戏");
        }
        return gbMasterArticleHomepageBindingMapper.deleteByMasterArticleId(masterArticleId);
    }

    /**
     * 解绑主文章的游戏盒子主页绑定
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unbindFromGameBox(Long masterArticleId, Long gameBoxId)
    {
        GbMasterArticleHomepageBinding binding = gbMasterArticleHomepageBindingMapper.selectByMasterArticleId(masterArticleId);
        if (binding == null) {
            throw new ServiceException("该主文章未绑定任何主页");
        }
        if (binding.getGameBoxId() == null || !binding.getGameBoxId().equals(gameBoxId)) {
            throw new ServiceException("该主文章未绑定到指定游戏盒子");
        }
        return gbMasterArticleHomepageBindingMapper.deleteByMasterArticleId(masterArticleId);
    }

    /**
     * 根据游戏ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameId 游戏ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    @Override
    public GbMasterArticleHomepageBinding selectByGameIdWithTitle(Long gameId)
    {
        return gbMasterArticleHomepageBindingMapper.selectByGameIdWithTitle(gameId);
    }

    /**
     * 根据游戏盒子ID查询主页绑定（包含主文章标题信息）
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 主文章主页绑定（包含主文章标题信息）
     */
    @Override
    public GbMasterArticleHomepageBinding selectByGameBoxIdWithTitle(Long gameBoxId)
    {
        return gbMasterArticleHomepageBindingMapper.selectByGameBoxIdWithTitle(gameBoxId);
    }
}
