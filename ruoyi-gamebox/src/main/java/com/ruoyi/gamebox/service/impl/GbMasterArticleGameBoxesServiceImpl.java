package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.mapper.GbMasterArticleGameBoxesMapper;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxesService;

/**
 * 主文章游戏盒子关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2024
 */
@Service
public class GbMasterArticleGameBoxesServiceImpl implements IGbMasterArticleGameBoxesService
{
    @Autowired
    private GbMasterArticleGameBoxesMapper gbMasterArticleGameBoxesMapper;

    /**
     * 根据主文章ID查询关联的游戏盒子列表
     * 
     * @param masterArticleId 主文章ID
     * @return 游戏盒子关联列表
     */
    @Override
    public List<GbMasterArticleGameBoxes> selectByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGameBoxesMapper.selectByMasterArticleId(masterArticleId);
    }

    /**
     * 根据游戏盒子ID查询关联的主文章列表
     * 
     * @param gameBoxId 游戏盒子ID
     * @return 文章关联列表
     */
    @Override
    public List<GbMasterArticleGameBoxes> selectByGameBoxId(Long gameBoxId)
    {
        return gbMasterArticleGameBoxesMapper.selectByGameBoxId(gameBoxId);
    }

    /**
     * 新增主文章游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联信息
     * @return 结果
     */
    @Override
    public int insertGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes)
    {
        gbMasterArticleGameBoxes.setCreateTime(new Date());
        gbMasterArticleGameBoxes.setCreateBy(SecurityUtils.getUsername());
        return gbMasterArticleGameBoxesMapper.insertGbMasterArticleGameBoxes(gbMasterArticleGameBoxes);
    }

    /**
     * 批量新增主文章游戏盒子关联
     * 
     * @param list 关联信息列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertGbMasterArticleGameBoxes(List<GbMasterArticleGameBoxes> list)
    {
        if (list == null || list.isEmpty())
        {
            return 0;
        }
        
        String username = SecurityUtils.getUsername();
        Date now = new Date();
        for (GbMasterArticleGameBoxes item : list)
        {
            item.setCreateTime(now);
            item.setCreateBy(username);
        }
        
        return gbMasterArticleGameBoxesMapper.batchInsertGbMasterArticleGameBoxes(list);
    }

    /**
     * 修改主文章游戏盒子关联
     * 
     * @param gbMasterArticleGameBoxes 关联信息
     * @return 结果
     */
    @Override
    public int updateGbMasterArticleGameBoxes(GbMasterArticleGameBoxes gbMasterArticleGameBoxes)
    {
        gbMasterArticleGameBoxes.setUpdateTime(new Date());
        gbMasterArticleGameBoxes.setUpdateBy(SecurityUtils.getUsername());
        return gbMasterArticleGameBoxesMapper.updateGbMasterArticleGameBoxes(gbMasterArticleGameBoxes);
    }

    /**
     * 删除主文章游戏盒子关联
     * 
     * @param id 关联ID
     * @return 结果
     */
    @Override
    public int deleteGbMasterArticleGameBoxesById(Long id)
    {
        return gbMasterArticleGameBoxesMapper.deleteGbMasterArticleGameBoxesById(id);
    }

    /**
     * 删除主文章的所有游戏盒子关联
     * 
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    @Override
    public int deleteByMasterArticleId(Long masterArticleId)
    {
        return gbMasterArticleGameBoxesMapper.deleteByMasterArticleId(masterArticleId);
    }

    /**
     * 删除指定主文章和游戏盒子的关联
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxId 游戏盒子ID
     * @return 结果
     */
    @Override
    public int deleteByMasterArticleIdAndGameBoxId(Long masterArticleId, Long gameBoxId)
    {
        return gbMasterArticleGameBoxesMapper.deleteByMasterArticleIdAndGameBoxId(masterArticleId, gameBoxId);
    }

    /**
     * 保存主文章的游戏盒子关联（先删除旧关联，再插入新关联）
     * 
     * @param masterArticleId 主文章ID
     * @param gameBoxIds 游戏盒子ID列表
     * @param relationSource 关联来源
     * @param relationType 关联类型
     * @return 结果
     */
    @Override
    @Transactional
    public int saveGameBoxesForMasterArticle(Long masterArticleId, List<Long> gameBoxIds, String relationSource, String relationType)
    {
        // 删除旧关联
        gbMasterArticleGameBoxesMapper.deleteByMasterArticleId(masterArticleId);
        
        // 如果新列表为空，直接返回
        if (gameBoxIds == null || gameBoxIds.isEmpty())
        {
            return 0;
        }
        
        // 构造新关联
        List<GbMasterArticleGameBoxes> list = new ArrayList<>();
        int displayOrder = 1;
        for (Long gameBoxId : gameBoxIds)
        {
            GbMasterArticleGameBoxes relation = new GbMasterArticleGameBoxes();
            relation.setMasterArticleId(masterArticleId);
            relation.setGameBoxId(gameBoxId);
            relation.setRelationSource(relationSource != null ? relationSource : "manual");
            relation.setRelationType(relationType != null ? relationType : "related");
            relation.setDisplayOrder(displayOrder++);
            list.add(relation);
        }
        
        // 批量插入
        return batchInsertGbMasterArticleGameBoxes(list);
    }
}
