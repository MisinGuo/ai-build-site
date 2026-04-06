package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruoyi.gamebox.domain.*;
import com.ruoyi.gamebox.mapper.*;
import com.ruoyi.gamebox.service.AsyncRelationService;
import com.ruoyi.gamebox.service.ISiteContentRelationService;

/**
 * 异步关联处理服务实现
 * 
 * @author ruoyi
 * @date 2025-12-29
 */
@Service
public class AsyncRelationServiceImpl implements AsyncRelationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncRelationServiceImpl.class);
    
    @Autowired
    private GbBoxGameRelationMapper boxGameRelationMapper;
    
    @Autowired
    private GbSiteGameRelationMapper siteGameRelationMapper;
    
    @Autowired
    private GbGameMapper gameMapper;
    
    @Autowired
    private GbGameCategoryRelationMapper gameCategoryRelationMapper;
    
    @Autowired
    private ISiteContentRelationService siteContentRelationService;
    
    /**
     * 异步添加盒子中的游戏到网站
     * 流程：
     * 1. 查询盒子下所有游戏
     * 2. 收集所有游戏的分类（包括主分类和多分类）
     * 3. 批量关联所有分类到目标网站
     * 4. 批量关联所有游戏到目标网站
     */
    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void asyncAddBoxGamesToSite(Long siteId, Long boxId, String username) {
        try {
            logger.info("开始异步处理：将盒子{}的游戏添加到网站{}", boxId, siteId);
            
            // 1. 查询盒子下所有游戏
            List<GbBoxGameRelation> gameRelations = boxGameRelationMapper.selectGamesByBoxId(boxId);
            if (gameRelations.isEmpty()) {
                logger.info("盒子{}下没有游戏，跳过处理", boxId);
                return;
            }
            
            List<Long> gameIds = gameRelations.stream()
                .map(GbBoxGameRelation::getGameId)
                .collect(Collectors.toList());
            
            logger.info("盒子{}共有{}个游戏需要处理", boxId, gameIds.size());
            
            // 2. 批量收集所有游戏的分类ID（去重）
            // 注意：selectGamesByBoxId 已经通过 INNER JOIN 过滤了已删除的游戏
            Set<Long> allCategoryIds = new HashSet<>();
            
            for (Long gameId : gameIds) {
                GbGame game = gameMapper.selectGbGameById(gameId);
                if (game == null) {
                    logger.warn("游戏{}不存在（可能已被删除），跳过关联", gameId);
                    continue;
                }
                
                // 从关联表获取所有分类（包括主分类）
                List<GbGameCategoryRelation> categoryRelations = gameCategoryRelationMapper.selectCategoriesByGameId(gameId);
                for (GbGameCategoryRelation rel : categoryRelations) {
                    allCategoryIds.add(rel.getCategoryId());
                    logger.debug("收集游戏{}的多分类: {}", gameId, rel.getCategoryId());
                }
            }
            
            logger.info("盒子{}共有{}个有效游戏，收集到{}个需要关联的分类", boxId, gameIds.size(), allCategoryIds.size());
            
            // 3. 批量关联所有分类到目标网站（包括父分类）
            if (!allCategoryIds.isEmpty()) {
                logger.info("开始批量关联{}个分类到网站{}: {}", allCategoryIds.size(), siteId, allCategoryIds);
                siteContentRelationService.batchEnsureCategoriesAvailable(new ArrayList<>(allCategoryIds), siteId);
                logger.info("批量关联分类完成");
            } else {
                logger.warn("没有分类需要关联");
            }
            
            // 4. 批量添加游戏关联
            List<GbSiteGameRelation> gamesToAdd = new ArrayList<>();
            Date now = new Date();
            
            for (Long gameId : gameIds) {
                // 检查游戏关联是否已存在
                if (siteGameRelationMapper.checkRelationExists(siteId, gameId) > 0) {
                    logger.debug("游戏{}已关联到网站{}，跳过", gameId, siteId);
                    continue;
                }
                
                // 准备批量插入的数据
                GbSiteGameRelation gameRelation = new GbSiteGameRelation();
                gameRelation.setSiteId(siteId);
                gameRelation.setGameId(gameId);
                gameRelation.setRelationType("include");
                gameRelation.setIsVisible("1");
                gameRelation.setCreateBy(username);
                gameRelation.setCreateTime(now);
                gamesToAdd.add(gameRelation);
            }
            
            // 批量插入游戏关联
            if (!gamesToAdd.isEmpty()) {
                logger.info("开始批量插入{}个游戏关联", gamesToAdd.size());
                siteGameRelationMapper.batchInsertGbSiteGameRelation(gamesToAdd);
                logger.info("成功将{}个游戏添加到网站{}", gamesToAdd.size(), siteId);
            } else {
                logger.info("所有游戏都已关联，无需添加");
            }
            
            logger.info("异步任务完成：盒子{}的游戏已全部添加到网站{}", boxId, siteId);
            
        } catch (Exception e) {
            logger.error("异步添加盒子{}的游戏到网站{}失败", boxId, siteId, e);
        }
    }
}
