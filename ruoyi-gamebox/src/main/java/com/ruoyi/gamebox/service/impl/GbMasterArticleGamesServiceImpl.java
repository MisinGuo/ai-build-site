package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.mapper.GbMasterArticleGamesMapper;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;
import com.ruoyi.gamebox.service.IGbMasterArticleGamesService;

/**
 * 主文章游戏关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2024
 */
@Service
public class GbMasterArticleGamesServiceImpl implements IGbMasterArticleGamesService
{
    @Autowired
    private GbMasterArticleGamesMapper gbMasterArticleGamesMapper;

    /**
     * 根据主文章ID查询关联的游戏列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏关联列表
     */
    @Override
    public List<GbMasterArticleGames> selectByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGamesMapper.selectByMasterArticleId(masterArticleId);
    }

    /**
     * 根据游戏ID查询关联的主文章列表
     * 
     * @param gameId 游戏ID
     * @return 文章关联列表
     */
    @Override
    public List<GbMasterArticleGames> selectByGameId(Long gameId)
    {
        return gbMasterArticleGamesMapper.selectByGameId(gameId);
    }

    /**
     * 新增主文章游戏关联
     * 
     * @param gbMasterArticleGames 关联信息
     * @return 结果
     */
    @Override
    public int insertGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames)
    {
        gbMasterArticleGames.setCreateTime(new Date());
        gbMasterArticleGames.setCreateBy(SecurityUtils.getUsername());
        return gbMasterArticleGamesMapper.insertGbMasterArticleGames(gbMasterArticleGames);
    }

    /**
     * 批量新增主文章游戏关联
     * 
     * @param list 关联信息列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertGbMasterArticleGames(List<GbMasterArticleGames> list)
    {
        if (list == null || list.isEmpty())
        {
            return 0;
        }
        
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        for (GbMasterArticleGames item : list)
        {
            item.setCreateTime(now);
            item.setCreateBy(username);
        }
        
        return gbMasterArticleGamesMapper.batchInsertGbMasterArticleGames(list);
    }

    /**
     * 修改主文章游戏关联
     * 
     * @param gbMasterArticleGames 关联信息
     * @return 结果
     */
    @Override
    public int updateGbMasterArticleGames(GbMasterArticleGames gbMasterArticleGames)
    {
        gbMasterArticleGames.setUpdateTime(new Date());
        gbMasterArticleGames.setUpdateBy(SecurityUtils.getUsername());
        return gbMasterArticleGamesMapper.updateGbMasterArticleGames(gbMasterArticleGames);
    }

    /**
     * 删除主文章游戏关联
     * 
     * @param id 关联ID
     * @return 结果
     */
    @Override
    public int deleteGbMasterArticleGamesById(Long id)
    {
        return gbMasterArticleGamesMapper.deleteGbMasterArticleGamesById(id);
    }

    /**
     * 删除主文章的所有游戏关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    @Override
    public int deleteByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGamesMapper.deleteByMasterArticleId(masterArticleId);
    }

    /**
     * 删除指定主文章和游戏的关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameId 游戏ID
     * @return 结果
     */
    @Override
    public int deleteByMasterArticleIdAndGameId(Long masterArticleId, Long gameId)
    {
        return gbMasterArticleGamesMapper.deleteByMasterArticleIdAndGameId(masterArticleId, gameId);
    }

    /**
     * 保存主文章的游戏关联（先删除旧关联，再插入新关联）
     * 
     * @param masterArticleId 主文章ID
     * @param gameIds 游戏ID列表
     * @param relationSource 关联来源
     * @param relationType 关联类型
     * @return 结果
     */
    @Override
    @Transactional
    public int saveGamesForMasterArticle(Long masterArticleId, List<Long> gameIds, String relationSource, String relationType)
    {
        // 删除旧关联
        gbMasterArticleGamesMapper.deleteByMasterArticleId(masterArticleId);
        
        // 如果新列表为空，直接返回
        if (gameIds == null || gameIds.isEmpty())
        {
            return 0;
        }
        
        // 构造新关联
        List<GbMasterArticleGames> list = new ArrayList<>();
        int displayOrder = 1;
        for (Long gameId : gameIds)
        {
            GbMasterArticleGames relation = new GbMasterArticleGames();
            relation.setMasterArticleId(masterArticleId);
            relation.setGameId(gameId);
            relation.setRelationSource(relationSource != null ? relationSource : "manual");
            relation.setRelationType(relationType != null ? relationType : "related");
            relation.setDisplayOrder(displayOrder++);
            list.add(relation);
        }
        
        // 批量插入
        return batchInsertGbMasterArticleGames(list);
    }
}
