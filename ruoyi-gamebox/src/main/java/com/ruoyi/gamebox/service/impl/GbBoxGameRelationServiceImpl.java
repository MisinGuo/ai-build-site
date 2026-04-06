package com.ruoyi.gamebox.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbBoxGameRelationMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbBoxGameRelation;
import com.ruoyi.gamebox.service.IGbBoxGameRelationService;

/**
 * 游戏盒子-游戏关联Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbBoxGameRelationServiceImpl implements IGbBoxGameRelationService
{
    @Autowired
    private GbBoxGameRelationMapper gbBoxGameRelationMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    /**
     * 查询游戏盒子-游戏关联
     * 
     * @param id 游戏盒子-游戏关联主键
     * @return 游戏盒子-游戏关联
     */
    @Override
    public GbBoxGameRelation selectGbBoxGameRelationById(Long id)
    {
        return gbBoxGameRelationMapper.selectGbBoxGameRelationById(id);
    }

    /**
     * 查询游戏盒子-游戏关联列表
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 游戏盒子-游戏关联
     */
    @Override
    public List<GbBoxGameRelation> selectGbBoxGameRelationList(GbBoxGameRelation gbBoxGameRelation)
    {
        return gbBoxGameRelationMapper.selectGbBoxGameRelationList(gbBoxGameRelation);
    }

    /**
     * 根据盒子ID查询关联的游戏列表
     * 
     * @param boxId 盒子ID
     * @return 游戏关联列表
     */
    @Override
    public List<GbBoxGameRelation> selectGamesByBoxId(Long boxId)
    {
        return gbBoxGameRelationMapper.selectGamesByBoxId(boxId);
    }

    /**
     * 根据游戏ID查询关联的盒子列表
     * 
     * @param gameId 游戏ID
     * @return 盒子关联列表
     */
    @Override
    public List<GbBoxGameRelation> selectBoxesByGameId(Long gameId)
    {
        return gbBoxGameRelationMapper.selectBoxesByGameId(gameId);
    }

    @Override
    public List<GbBoxGameRelation> selectBoxesByGameIdWithSite(Long gameId, Long siteId)
    {
        Long personalSiteId = null;
        try {
            personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
        } catch (Exception ignored) {}
        if (personalSiteId == null) personalSiteId = 0L;
        return gbBoxGameRelationMapper.selectBoxesByGameIdWithSite(gameId, siteId, personalSiteId);
    }

    /**
     * 新增游戏盒子-游戏关联
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 结果
     */
    @Override
    public int insertGbBoxGameRelation(GbBoxGameRelation gbBoxGameRelation)
    {
        gbBoxGameRelation.setCreateTime(DateUtils.getNowDate());
        return gbBoxGameRelationMapper.insertGbBoxGameRelation(gbBoxGameRelation);
    }

    /**
     * 修改游戏盒子-游戏关联
     * 
     * @param gbBoxGameRelation 游戏盒子-游戏关联
     * @return 结果
     */
    @Override
    public int updateGbBoxGameRelation(GbBoxGameRelation gbBoxGameRelation)
    {
        return gbBoxGameRelationMapper.updateGbBoxGameRelation(gbBoxGameRelation);
    }

    /**
     * 批量删除游戏盒子-游戏关联
     * 
     * @param ids 需要删除的游戏盒子-游戏关联主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxGameRelationByIds(Long[] ids)
    {
        return gbBoxGameRelationMapper.deleteGbBoxGameRelationByIds(ids);
    }

    /**
     * 删除游戏盒子-游戏关联信息
     * 
     * @param id 游戏盒子-游戏关联主键
     * @return 结果
     */
    @Override
    public int deleteGbBoxGameRelationById(Long id)
    {
        return gbBoxGameRelationMapper.deleteGbBoxGameRelationById(id);
    }

    /**
     * 批量添加游戏到盒子
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int batchAddGamesToBox(Long boxId, Long[] gameIds)
    {
        int count = 0;
        for (Long gameId : gameIds)
        {
            // 检查是否已存在
            GbBoxGameRelation existing = gbBoxGameRelationMapper.checkBoxGameRelation(boxId, gameId);
            if (existing == null)
            {
                GbBoxGameRelation relation = new GbBoxGameRelation();
                relation.setBoxId(boxId);
                relation.setGameId(gameId);
                relation.setSortOrder(0);
                relation.setCreateTime(DateUtils.getNowDate());
                count += gbBoxGameRelationMapper.insertGbBoxGameRelation(relation);
            }
        }
        return count;
    }

    /**
     * 批量插入盒子-游戏关联关系（支持关联字段数据）
     * 
     * @param relations 关联关系列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertRelations(List<GbBoxGameRelation> relations)
    {
        if (relations == null || relations.isEmpty()) return 0;
        Date now = DateUtils.getNowDate();
        for (GbBoxGameRelation r : relations) {
            if (r.getCreateTime() == null) r.setCreateTime(now);
        }
        // 分批插入，避免单 SQL 过大（platform_data / promotion_links 含大 JSON）
        final int BATCH_SIZE = 50;
        int total = 0;
        for (int i = 0; i < relations.size(); i += BATCH_SIZE) {
            List<GbBoxGameRelation> batch = relations.subList(i, Math.min(i + BATCH_SIZE, relations.size()));
            total += gbBoxGameRelationMapper.batchInsertRelations(batch);
        }
        return total;
    }

    /**
     * 批量从盒子移除游戏
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID数组
     * @return 结果
     */
    @Override
    @Transactional
    public int batchRemoveGamesFromBox(Long boxId, Long[] gameIds)
    {
        int count = 0;
        for (Long gameId : gameIds)
        {
            GbBoxGameRelation existing = gbBoxGameRelationMapper.checkBoxGameRelation(boxId, gameId);
            if (existing != null)
            {
                count += gbBoxGameRelationMapper.deleteGbBoxGameRelationById(existing.getId());
            }
        }
        return count;
    }

    /**
     * 根据盒子ID和游戏ID查询关联关系
     * 
     * @param boxId 盒子ID
     * @param gameId 游戏ID
     * @return 关联关系
     */
    @Override
    public GbBoxGameRelation selectRelationByBoxIdAndGameId(Long boxId, Long gameId)
    {
        return gbBoxGameRelationMapper.checkBoxGameRelation(boxId, gameId);
    }

    /**
     * 批量查询盒子和游戏的关联关系
     * 
     * @param boxId 盒子ID
     * @param gameIds 游戏ID列表
     * @return 关联关系列表
     */
    @Override
    public List<GbBoxGameRelation> selectRelationsByBoxIdAndGameIds(Long boxId, List<Long> gameIds)
    {
        if (gameIds == null || gameIds.isEmpty()) {
            return new ArrayList<>();
        }
        return gbBoxGameRelationMapper.selectRelationsByBoxIdAndGameIds(boxId, gameIds);
    }

    /**
     * 批量更新关联记录（优化版：分批处理）
     * 
     * @param relations 关联记录列表
     * @return 更新的记录数
     */
    @Override
    @Transactional
    public int batchUpdateRelations(List<GbBoxGameRelation> relations)
    {
        if (relations == null || relations.isEmpty()) {
            return 0;
        }
        // 分批执行，避免 platform_data / promotion_links 大 JSON 导致单条 SQL 过大
        final int BATCH_SIZE = 50;
        int total = 0;
        for (int i = 0; i < relations.size(); i += BATCH_SIZE) {
            List<GbBoxGameRelation> batch = relations.subList(i, Math.min(i + BATCH_SIZE, relations.size()));
            total += gbBoxGameRelationMapper.batchUpdateRelations(batch);
        }
        return total;
    }

    /**
     * 根据盒子ID和游戏ID删除关联
     * 
     * @param boxId 盒子ID
     * @param gameId 游戏ID
     * @return 删除的记录数
     */
    @Override
    public int deleteByBoxIdAndGameId(Long boxId, Long gameId)
    {
        return gbBoxGameRelationMapper.deleteByBoxIdAndGameId(boxId, gameId);
    }
}
