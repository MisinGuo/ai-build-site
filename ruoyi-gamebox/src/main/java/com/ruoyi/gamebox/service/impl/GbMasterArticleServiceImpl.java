package com.ruoyi.gamebox.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.gamebox.domain.GbMasterArticle;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBox;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.domain.dto.GbMasterArticlePublishDTO;
import com.ruoyi.gamebox.mapper.GbMasterArticleMapper;
import com.ruoyi.gamebox.mapper.GbArticleMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.mapper.GbSiteMasterArticleRelationMapper;
import com.ruoyi.gamebox.service.IGbMasterArticleService;
import com.ruoyi.gamebox.service.IGbMultilangStorageConfigService;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxService;
import com.ruoyi.gamebox.service.IGbMasterArticleGamesService;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxesService;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IGbStorageConfigService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.service.IStorageFileService;

/**
 * 主文章内容Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
@Service
public class GbMasterArticleServiceImpl implements IGbMasterArticleService 
{
    private static final Logger logger = LoggerFactory.getLogger(GbMasterArticleServiceImpl.class);

    @Autowired
    private GbMasterArticleMapper gbMasterArticleMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    private void injectPersonalSiteId(com.ruoyi.common.core.domain.BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}

    /**
     * 判断站点是否为个人站点（即原来的"默认配置"）
     */
    private boolean isPersonalSite(Long siteId) {
        if (siteId == null) return false;
        GbSite site = gbSiteMapper.selectGbSiteById(siteId);
        return site != null && Integer.valueOf(1).equals(site.getIsPersonal());
    }

    @Autowired
    private GbArticleMapper gbArticleMapper;

    @Autowired
    private IGbMultilangStorageConfigService multilangStorageConfigService;

    @Autowired
    private IGbMasterArticleGameBoxService masterArticleGameBoxService;

    @Autowired
    private IGbMasterArticleGamesService masterArticleGamesService;

    @Autowired
    private IGbMasterArticleGameBoxesService masterArticleGameBoxesService;

    @Autowired
    private GbSiteMasterArticleRelationMapper siteMasterArticleRelationMapper;

    @Autowired
    private GbMasterArticleMapper masterArticleMapper;

    @Autowired
    private IGbArticleService gbArticleService;

    @Autowired
    private IGbStorageConfigService storageConfigService;

    @Autowired
    private IStorageFileService storageFileService;

    @Autowired
    private IGbSiteService siteService;

    @Autowired
    private IGbGameService gameService;

    @Autowired
    private IGbGameBoxService gameBoxService;

    /**
     * 查询主文章内容
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    @Override
    public GbMasterArticle selectGbMasterArticleById(Long id)
    {
        GbMasterArticle masterArticle = gbMasterArticleMapper.selectGbMasterArticleById(id);
        if (masterArticle != null) {
            // 动态设置默认语言
            setDefaultLocale(masterArticle);
        }
        return masterArticle;
    }

    /**
     * 查询主文章内容列表
     * 
     * @param gbMasterArticle 主文章内容
     * @return 主文章内容
     */
    @Override
    public List<GbMasterArticle> selectGbMasterArticleList(GbMasterArticle gbMasterArticle)
    {
        injectPersonalSiteId(gbMasterArticle);
        List<GbMasterArticle> list = gbMasterArticleMapper.selectGbMasterArticleList(gbMasterArticle);
        // 为列表中的每个主文章动态设置默认语言和计算发布状态
        for (GbMasterArticle masterArticle : list) {
            setDefaultLocale(masterArticle);
            // 计算并设置发布状态
            calculatePublishStatus(masterArticle);
        }
        return list;
    }

    /**
     * 新增主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    @Override
    @Transactional
    public int insertGbMasterArticle(GbMasterArticle gbMasterArticle)
    {
        // 设置创建时间和创建人
        gbMasterArticle.setCreateTime(DateUtils.getNowDate());
        gbMasterArticle.setCreateBy(SecurityUtils.getUsername());
        gbMasterArticle.setDelFlag("0");
        
        // 设置默认值
        if (StringUtils.isNull(gbMasterArticle.getIsAiGenerated())) {
            gbMasterArticle.setIsAiGenerated("0"); // 默认非AI生成
        }
        // status 和 isPublished 由 updateMasterArticleStatus 统计控制，不手动设置
        // 初始值在数据库中有默认值 '0'
        if (StringUtils.isNull(gbMasterArticle.getIsTop())) {
            gbMasterArticle.setIsTop("0"); // 默认不置顶
        }
        if (StringUtils.isNull(gbMasterArticle.getIsRecommend())) {
            gbMasterArticle.setIsRecommend("0"); // 默认不推荐
        }
        if (StringUtils.isNull(gbMasterArticle.getSortOrder())) {
            gbMasterArticle.setSortOrder(0); // 默认排序
        }
        if (StringUtils.isNull(gbMasterArticle.getGenerationCount())) {
            gbMasterArticle.setGenerationCount(0); // 默认生成次数
        }
        if (StringUtils.isNull(gbMasterArticle.getSiteId())) {
            gbMasterArticle.setSiteId(1L); // 默认网站ID
        }

        return gbMasterArticleMapper.insertGbMasterArticle(gbMasterArticle);
    }

    /**
     * 修改主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    @Override
    @Transactional
    public int updateGbMasterArticle(GbMasterArticle gbMasterArticle)
    {
        gbMasterArticle.setUpdateTime(DateUtils.getNowDate());
        gbMasterArticle.setUpdateBy(SecurityUtils.getUsername());
        return gbMasterArticleMapper.updateGbMasterArticle(gbMasterArticle);
    }

    /**
     * 批量删除主文章内容
     * 
     * @param ids 需要删除的主文章内容主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMasterArticleByIds(Long[] ids)
    {
        // 删除主文章时同时删除相关的关联数据
        for (Long id : ids) {
            // 1. 获取该主文章关联的所有文章（各语言版本）
            List<GbArticle> articles = gbArticleMapper.selectArticlesByMasterArticleId(id);
            
            // 2. 删除每个文章在存储配置中的物理文件
            for (GbArticle article : articles) {
                if (article.getStorageConfigId() != null && article.getStorageKey() != null) {
                    try {
                        boolean deleted = storageFileService.deleteFile(article.getStorageConfigId(), article.getStorageKey());
                        if (deleted) {
                            logger.info("成功删除文章存储文件: configId={}, key={}", article.getStorageConfigId(), article.getStorageKey());
                        } else {
                            logger.warn("删除文章存储文件失败: configId={}, key={}", article.getStorageConfigId(), article.getStorageKey());
                        }
                    } catch (Exception e) {
                        logger.error("删除文章存储文件时发生异常: configId={}, key={}, error={}", 
                            article.getStorageConfigId(), article.getStorageKey(), e.getMessage(), e);
                    }
                }
            }
            
            // 3. 软删除所有关联的文章（标记为已删除）
            gbArticleMapper.softDeleteByMasterArticleId(id);
            
            // 4. 删除GameBox关联
            masterArticleGameBoxService.deleteByMasterArticleId(id);
            
            // 5. 删除Games关联
            masterArticleGamesService.deleteByMasterArticleId(id);
            
            // 6. 删除GameBoxes关联
            masterArticleGameBoxesService.deleteByMasterArticleId(id);
        }
        
        // 7. 软删除主文章（标记为已删除）
        int result = gbMasterArticleMapper.deleteGbMasterArticleByIds(ids);
        
        // 注意：isPublished 和 status 字段已删除，不需要更新状态
        
        return result;
    }

    /**
     * 删除主文章内容信息
     * 
     * @param id 主文章内容主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbMasterArticleById(Long id)
    {
        // 1. 获取该主文章关联的所有文章（各语言版本）
        List<GbArticle> articles = gbArticleMapper.selectArticlesByMasterArticleId(id);
        
        // 2. 删除每个文章在存储配置中的物理文件
        for (GbArticle article : articles) {
            if (article.getStorageConfigId() != null && article.getStorageKey() != null) {
                try {
                    boolean deleted = storageFileService.deleteFile(article.getStorageConfigId(), article.getStorageKey());
                    if (deleted) {
                        logger.info("成功删除文章存储文件: configId={}, key={}", article.getStorageConfigId(), article.getStorageKey());
                    } else {
                        logger.warn("删除文章存储文件失败: configId={}, key={}", article.getStorageConfigId(), article.getStorageKey());
                    }
                } catch (Exception e) {
                    logger.error("删除文章存储文件时发生异常: configId={}, key={}, error={}", 
                        article.getStorageConfigId(), article.getStorageKey(), e.getMessage(), e);
                }
            }
        }
        
        // 3. 软删除所有关联的文章（标记为已删除）
        gbArticleMapper.softDeleteByMasterArticleId(id);
        
        // 4. 删除关联数据
        masterArticleGameBoxService.deleteByMasterArticleId(id);
        masterArticleGamesService.deleteByMasterArticleId(id);
        masterArticleGameBoxesService.deleteByMasterArticleId(id);
        
        // 5. 软删除主文章（标记为已删除）
        int result = gbMasterArticleMapper.deleteGbMasterArticleById(id);
        
        // 注意：isPublished 和 status 字段已删除，不需要更新状态
        
        return result;
    }

    // 注释：content_key字段已从表中移除
    /**
     * 根据内容标识查询主文章
     * 
     * @param contentKey 内容标识
     * @return 主文章内容
     */
    // @Override
    // public GbMasterArticle selectGbMasterArticleByContentKey(String contentKey)
    // {
    //     return gbMasterArticleMapper.selectGbMasterArticleByContentKey(contentKey);
    // }

    /**
     * 查询主文章详情（包含所有关联信息）
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    @Override
    public GbMasterArticle selectGbMasterArticleWithAssociations(Long id)
    {
        return gbMasterArticleMapper.selectGbMasterArticleWithAssociations(id);
    }

    // 注释：content_key字段已从表中移除
    /**
     * 检查内容标识是否已存在
     * 
     * @param contentKey 内容标识
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    // @Override
    // public boolean checkContentKeyExists(String contentKey, Long excludeId)
    // {
    //     return gbMasterArticleMapper.checkContentKeyExists(contentKey, excludeId);
    // }

    /**
     * 获取主文章统计信息
     * 
     * @param id 主文章ID
     * @return 统计信息
     */
    @Override
    public GbMasterArticle getMasterArticleStatistics(Long id)
    {
        GbMasterArticle masterArticle = gbMasterArticleMapper.selectGbMasterArticleById(id);
        if (masterArticle != null) {
            // 获取语言版本数量
            int languageCount = gbMasterArticleMapper.selectLanguageVersionCount(id);
            masterArticle.setLanguageVersionCount(languageCount);
        }
        return masterArticle;
    }

    /**
     * 为主文章创建多语言版本
     * 
     * @param masterArticleId 主文章ID
     * @param languageCodes 语言代码列表
     * @return 创建成功的数量
     */
    @Override
    @Transactional
    public int createLanguageVersions(Long masterArticleId, List<String> languageCodes)
    {
        GbMasterArticle masterArticle = gbMasterArticleMapper.selectGbMasterArticleById(masterArticleId);
        if (masterArticle == null) {
            return 0;
        }

        int successCount = 0;
        String currentUser = SecurityUtils.getUsername();
        Date currentTime = DateUtils.getNowDate();

        for (String langCode : languageCodes) {
            // 检查是否已存在该语言版本
            GbArticle existingArticle = new GbArticle();
            existingArticle.setMasterArticleId(masterArticleId);
            existingArticle.setLocale(langCode);
            existingArticle.setDelFlag("0");
            
            // 这里需要实现检查方法，暂时跳过重复检查
            // if (!checkLanguageVersionExists(masterArticleId, langCode)) {
                // 创建语言版本文章
                GbArticle languageVersion = new GbArticle();
                languageVersion.setMasterArticleId(masterArticleId); // 使用新的关联字段
                languageVersion.setTitle("Article #" + masterArticleId + " (" + langCode.toUpperCase() + ")");
                languageVersion.setLocale(langCode);
                languageVersion.setCategoryId(masterArticle.getCategoryId());
                // 设置基本内容字段，具体内容将通过AI生成或手动填写
                languageVersion.setContent(""); // 初始为空，待后续填充
                languageVersion.setStatus("0"); // 草稿状态
                languageVersion.setCreateBy(currentUser);
                languageVersion.setCreateTime(currentTime);
                languageVersion.setDelFlag("0");

                try {
                    int result = gbArticleMapper.insertGbArticle(languageVersion);
                    if (result > 0) {
                        successCount++;
                    }
                } catch (Exception e) {
                    // 记录日志但继续处理其他语言版本
                    continue;
                }
            // }
        }

        // 更新主文章的语言数量
        if (successCount > 0) {
            int totalLanguages = gbMasterArticleMapper.selectLanguageVersionCount(masterArticleId);
            GbMasterArticle updateMaster = new GbMasterArticle();
            updateMaster.setId(masterArticleId);
            updateMaster.setLanguageVersionCount(totalLanguages);
            updateMaster.setUpdateTime(currentTime);
            updateMaster.setUpdateBy(currentUser);
            gbMasterArticleMapper.updateGbMasterArticle(updateMaster);
        }

        return successCount;
    }

    /**
     * 同步主文章的关联关系到语言版本
     * 
     * @param masterArticleId 主文章ID
     * @return 同步结果
     */
    @Override
    @Transactional
    public boolean syncAssociationsToLanguageVersions(Long masterArticleId)
    {
        try {
            // 获取所有语言版本
            GbArticle queryArticle = new GbArticle();
            queryArticle.setMasterArticleId(masterArticleId);
            queryArticle.setDelFlag("0");
            List<GbArticle> languageVersions = gbArticleMapper.selectGbArticleList(queryArticle);

            if (languageVersions.isEmpty()) {
                return true; // 没有语言版本，认为同步成功
            }

            // 获取主文章的GameBox关联
            List<GbMasterArticleGameBox> gameBoxAssociations = masterArticleGameBoxService.selectGbMasterArticleGameBoxByMasterArticleId(masterArticleId);
            
            // 为每个语言版本同步关联
            // 主文章关联已经处理完成，语言版本会继承主文章的关联
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 批量更新主文章状态
     * 
     * @param ids 主文章ID列表
     * @param status 状态
     * @param operatorBy 操作人
     * @return 更新数量
     */
    // 注释：主文章表已删除 status 字段，批量更新状态功能已废弃
    // @Override
    // @Transactional
    // public int batchUpdateStatus(Long[] ids, String status, String operatorBy)
    // {
    //     int updateCount = 0;
    //     Date currentTime = DateUtils.getNowDate();
    //
    //     for (Long id : ids) {
    //         GbMasterArticle updateMaster = new GbMasterArticle();
    //         updateMaster.setId(id);
    //         // status 由 updateMasterArticleStatus 统计控制，不手动设置
    //         updateMaster.setUpdateBy(operatorBy);
    //         updateMaster.setUpdateTime(currentTime);
    //         
    //         int result = gbMasterArticleMapper.updateGbMasterArticle(updateMaster);
    //         if (result > 0) {
    //             updateCount++;
    //         }
    //     }
    //
    //     return updateCount;
    // }

    /**
     * 根据类别统计主文章数量
     * 
     * @param categoryId 类别ID
     * @return 数量
     */
    @Override
    public int countByCategoryId(Long categoryId)
    {
        GbMasterArticle queryMaster = new GbMasterArticle();
        queryMaster.setCategoryId(categoryId);
        queryMaster.setDelFlag("0");
        List<GbMasterArticle> list = gbMasterArticleMapper.selectGbMasterArticleList(queryMaster);
        return list != null ? list.size() : 0;
    }



    /**
     * 保存主文章为草稿
     * 
     * @param publishDTO 发布数据传输对象
     * @return 保存结果
     */
    @Override
    @Transactional
    public AjaxResult saveMasterArticleDraft(GbMasterArticlePublishDTO publishDTO)
    {
        try {
            // 1. 先创建主文章记录获取ID
            GbMasterArticle masterArticle = publishDTO.getMasterArticle();
            masterArticle.setCreateBy(SecurityUtils.getUsername());
            masterArticle.setCreateTime(DateUtils.getNowDate());
            masterArticle.setDelFlag("0");
            // isPublished 和 publishTime 由 updateMasterArticleStatus 统计控制
            
            int masterResult = insertGbMasterArticle(masterArticle);
            if (masterResult <= 0) {
                return AjaxResult.error("创建主文章失败");
            }
            
            // 2. 设置文章的主文章ID关联
            GbArticle article = publishDTO.getArticle();
            article.setMasterArticleId(masterArticle.getId());
            article.setCreateBy(SecurityUtils.getUsername());
            article.setCreateTime(DateUtils.getNowDate());
            article.setStatus("0"); // 草稿状态
            article.setPublishTime(null);
            
            // 3. 保存文章记录（不含关联关系，因为关联已移至主文章层）
            article.setDelFlag("0");
            int articleResult = gbArticleMapper.insertGbArticle(article);
            
            if (articleResult <= 0) {
                throw new RuntimeException("创建语言版本失败");
            }
            
            // 4. 保存主文章的游戏和游戏盒子关联
            if (publishDTO.getGameIds() != null && !publishDTO.getGameIds().isEmpty()) {
                masterArticleGamesService.saveGamesForMasterArticle(
                    masterArticle.getId(), 
                    publishDTO.getGameIds(), 
                    "manual", 
                    "related"
                );
            }
            
            if (publishDTO.getGameBoxIds() != null && !publishDTO.getGameBoxIds().isEmpty()) {
                masterArticleGameBoxesService.saveGameBoxesForMasterArticle(
                    masterArticle.getId(), 
                    publishDTO.getGameBoxIds(), 
                    "manual", 
                    "related"
                );
            }
            
            return AjaxResult.success("保存草稿成功", masterArticle.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("保存主文章草稿失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新主文章草稿
     * 
     * @param publishDTO 发布数据传输对象
     * @return 更新结果
     */
    @Override
    @Transactional
    public AjaxResult updateMasterArticleDraft(GbMasterArticlePublishDTO publishDTO)
    {
        try {
            // 1. 先更新文章记录，这会触发 updateMasterArticleStatus 统计所有语言版本状态
            GbArticle article = publishDTO.getArticle();
            article.setId(publishDTO.getArticleId());
            article.setMasterArticleId(publishDTO.getMasterArticleId());
            article.setUpdateBy(SecurityUtils.getUsername());
            article.setUpdateTime(DateUtils.getNowDate());
            article.setStatus("0"); // 保持草稿状态
            // 不更新publishTime，保持原有值
            
            int articleResult = gbArticleMapper.updateGbArticle(article);
            if (articleResult <= 0) {
                throw new RuntimeException("更新语言版本失败");
            }
            
            // 2. 更新主文章记录（不包含 status 和 isPublished，这些由 updateMasterArticleStatus 统计）
            GbMasterArticle masterArticle = publishDTO.getMasterArticle();
            masterArticle.setId(publishDTO.getMasterArticleId());
            masterArticle.setUpdateBy(SecurityUtils.getUsername());
            masterArticle.setUpdateTime(DateUtils.getNowDate());
            // 移除强制设置 isPublished，让 updateMasterArticleStatus 根据所有语言版本统计
            // 不设置 status 和 isPublished，保持 updateMasterArticleStatus 统计的值
            // 不更新publishTime，保持原有值或 updateMasterArticleStatus 统计的值
            
            // 只更新允许用户编辑的字段
            GbMasterArticle updateMaster = new GbMasterArticle();
            updateMaster.setId(publishDTO.getMasterArticleId());
            updateMaster.setSiteId(masterArticle.getSiteId());
            updateMaster.setCategoryId(masterArticle.getCategoryId());
            updateMaster.setDefaultLocale(masterArticle.getDefaultLocale());
            updateMaster.setIsTop(masterArticle.getIsTop());
            updateMaster.setIsRecommend(masterArticle.getIsRecommend());
            updateMaster.setSortOrder(masterArticle.getSortOrder());
            updateMaster.setRemark(masterArticle.getRemark());
            updateMaster.setUpdateBy(SecurityUtils.getUsername());
            updateMaster.setUpdateTime(DateUtils.getNowDate());
            // 不设置 status、isPublished、publishTime，让它们保持为 null
            // 这样 MyBatis 的动态 SQL 就不会更新这些字段，保留 updateMasterArticleStatus 统计的值
            
            int masterResult = updateGbMasterArticle(updateMaster);
            if (masterResult <= 0) {
                return AjaxResult.error("更新主文章失败");
            }
            
            // 3. 更新文章记录已经完成，updateMasterArticleStatus 已被调用
            
            // 4. 更新主文章的游戏和游戏盒子关联
            if (publishDTO.getGameIds() != null) {
                masterArticleGamesService.saveGamesForMasterArticle(
                    publishDTO.getMasterArticleId(), 
                    publishDTO.getGameIds(), 
                    "manual", 
                    "related"
                );
            }
            
            if (publishDTO.getGameBoxIds() != null) {
                masterArticleGameBoxesService.saveGameBoxesForMasterArticle(
                    publishDTO.getMasterArticleId(), 
                    publishDTO.getGameBoxIds(), 
                    "manual", 
                    "related"
                );
            }
            
            return AjaxResult.success("更新草稿成功", publishDTO.getMasterArticleId());
            
        } catch (Exception e) {
            throw new RuntimeException("更新主文章草稿失败: " + e.getMessage(), e);
        }
    }



    /**
     * 处理存储发布（复用原有的存储逻辑）
     * 
     * @param article 文章对象
     * @param forceOverwrite 是否强制覆盖
     * @return 处理结果
     */
    private AjaxResult handleStoragePublish(GbArticle article, Boolean forceOverwrite) {
        try {
            // 验证必填字段
            if (article.getStorageConfigId() == null) {
                return AjaxResult.error("请选择存储配置");
            }
            if (article.getStorageKey() == null || article.getStorageKey().isEmpty()) {
                return AjaxResult.error("存储路径不能为空");
            }
            if (article.getContent() == null || article.getContent().isEmpty()) {
                return AjaxResult.error("文章内容不能为空");
            }
            
            // 获取存储配置
            GbStorageConfig config = storageConfigService.selectGbStorageConfigById(article.getStorageConfigId());
            if (config == null) {
                return AjaxResult.error("存储配置不存在");
            }
            
            // 验证存储配置的自定义域名
            if (config.getCustomDomain() == null || config.getCustomDomain().isEmpty()) {
                return AjaxResult.error("存储配置未设置访问域名，无法发布。请先在存储配置中设置自定义域名");
            }
            
            if (forceOverwrite == null) {
                forceOverwrite = false;
            }
            
            // 检查文件是否已存在（非强制覆盖时）
            if (!forceOverwrite) {
                boolean fileExists = storageFileService.checkFileExists(
                    article.getStorageConfigId(), 
                    article.getStorageKey()
                );
                
                if (fileExists) {
                    // 文件已存在，返回特殊状态让前端确认
                    AjaxResult result = AjaxResult.success();
                    result.put("needConfirm", true);
                    result.put("existingFile", article.getStorageKey());
                    return result;
                }
            }
            
            // 替换模板变量（发布到远程的内容需要替换变量）
            String publishContent = replaceTemplateVariables(article.getContent(), article);
            
            // 发布文件到存储（使用替换后的内容）
            boolean success = storageFileService.updateTextFile(
                article.getStorageConfigId(), 
                article.getStorageKey(), 
                publishContent
            );
            
            if (!success) {
                return AjaxResult.error("发布到存储失败");
            }
            
            // 构建访问URL
            String storageUrl = buildStorageUrl(config, article.getStorageKey());
            article.setStorageUrl(storageUrl);
            
            return AjaxResult.success("存储发布成功");
            
        } catch (Exception e) {
            return AjaxResult.error("存储发布失败: " + e.getMessage());
        }
    }

    /**
     * 判断存储操作是否成功
     */
    private boolean isStorageSuccess(AjaxResult result) {
        return result != null && 
               (result.get("code") == null || result.get("code").equals(200)) &&
               (result.get("needConfirm") == null || !(Boolean) result.get("needConfirm"));
    }

    /**
     * 构建存储访问URL
     */
    private String buildStorageUrl(GbStorageConfig config, String storageKey) {
        String baseUrl = "";
        
        // 优先使用自定义域名
        if (config.getCustomDomain() != null && !config.getCustomDomain().isEmpty()) {
            baseUrl = config.getCustomDomain();
        } else {
            // 根据存储类型构建默认URL
            switch (config.getStorageType()) {
                case "github":
                    baseUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s",
                        config.getGithubOwner(),
                        config.getGithubRepo(),
                        config.getGithubBranch() != null ? config.getGithubBranch() : "main"
                    );
                    break;
                case "oss":
                    baseUrl = String.format("https://%s.%s", config.getOssBucket(), config.getOssEndpoint());
                    break;
                case "r2":
                    if (config.getR2PublicUrl() != null && !config.getR2PublicUrl().isEmpty()) {
                        baseUrl = config.getR2PublicUrl();
                    } else {
                        baseUrl = String.format("https://%s.%s.r2.cloudflarestorage.com", config.getR2Bucket(), config.getR2AccountId());
                    }
                    break;
                default:
                    baseUrl = "";
                    break;
            }
        }
        
        // 确保以 '/' 结尾
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        // 移除 storageKey 开头的 '/'
        String path = storageKey.startsWith("/") ? storageKey.substring(1) : storageKey;
        
        return baseUrl + path;
    }

    /**
     * 获取主文章编辑数据（包含默认语言版本内容）
     * 
     * @param masterArticleId 主文章ID
     * @return 编辑数据
     */
    @Override
    public AjaxResult getMasterArticleEditData(Long masterArticleId) {
        try {
            // 1. 获取主文章信息（不包含复杂关联，避免属性映射问题）
            GbMasterArticle masterArticle = selectGbMasterArticleById(masterArticleId);
            if (masterArticle == null) {
                return AjaxResult.error("主文章不存在");
            }

            // 2. 从网站配置获取默认语言
            GbSite site = siteService.selectGbSiteById(masterArticle.getSiteId());
            String defaultLocale = "zh-CN"; // 兜底默认值
            if (site != null && site.getDefaultLocale() != null) {
                defaultLocale = site.getDefaultLocale();
            }
            
            GbArticle defaultArticle = gbArticleService.getArticleByMasterIdAndLocale(
                masterArticleId, 
                defaultLocale
            );

            // 3. 如果默认语言版本不存在，创建一个空的文章记录
            if (defaultArticle == null) {
                defaultArticle = new GbArticle();
                defaultArticle.setMasterArticleId(masterArticleId);
                defaultArticle.setLocale(defaultLocale);
                defaultArticle.setSiteId(masterArticle.getSiteId());
                defaultArticle.setCategoryId(masterArticle.getCategoryId());
                defaultArticle.setStatus("0"); // 草稿状态
                defaultArticle.setIsTop(masterArticle.getIsTop());
                defaultArticle.setIsRecommend(masterArticle.getIsRecommend());
                defaultArticle.setSortOrder(masterArticle.getSortOrder());
            }
            
            // 4. 获取游戏关联
            List<Long> gameIds = new ArrayList<>();
            List<com.ruoyi.gamebox.domain.GbMasterArticleGames> gameRelations = 
                masterArticleGamesService.selectByMasterArticleId(masterArticleId);
            if (gameRelations != null) {
                for (com.ruoyi.gamebox.domain.GbMasterArticleGames relation : gameRelations) {
                    gameIds.add(relation.getGameId());
                }
            }
            
            // 5. 获取游戏盒子关联
            List<Long> gameBoxIds = new ArrayList<>();
            List<com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes> gameBoxRelations = 
                masterArticleGameBoxesService.selectByMasterArticleId(masterArticleId);
            if (gameBoxRelations != null) {
                for (com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes relation : gameBoxRelations) {
                    gameBoxIds.add(relation.getGameBoxId());
                }
            }

            // 6. 构建编辑数据
            Map<String, Object> editData = new HashMap<>();
            editData.put("masterArticle", masterArticle);
            editData.put("article", defaultArticle);
            editData.put("masterArticleId", masterArticleId);
            editData.put("articleId", defaultArticle.getId());
            editData.put("gameIds", gameIds);
            editData.put("gameBoxIds", gameBoxIds);

            return AjaxResult.success("获取编辑数据成功", editData);

        } catch (Exception e) {
            logger.error("获取主文章编辑数据失败", e);
            return AjaxResult.error("获取编辑数据失败：" + e.getMessage());
        }
    }

    // ==================== 排除管理 ====================

    /**
     * 排除默认主文章（对某个网站不可见）
     * 
     * @param siteId 网站ID
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    @Override
    @Transactional
    public int excludeDefaultMasterArticle(Long siteId, Long masterArticleId) {
        // 1. 检查是否为个人站点的默认配置
        GbMasterArticle masterArticle = gbMasterArticleMapper.selectGbMasterArticleById(masterArticleId);
        if (masterArticle == null || !isPersonalSite(masterArticle.getSiteId())) {
            logger.warn("主文章{}不是默认配置，无法排除", masterArticleId);
            return 0;
        }
        
        // 2. 检查是否已存在排除记录
        com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation existing = 
            siteMasterArticleRelationMapper.selectBySiteAndMasterArticle(siteId, masterArticleId, "exclude");
        if (existing != null) {
            logger.info("主文章{}已被网站{}排除", masterArticleId, siteId);
            return 0;
        }
        
        // 3. 创建排除关联
        com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation relation = 
            new com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation();
        relation.setSiteId(siteId);
        relation.setMasterArticleId(masterArticleId);
        relation.setRelationType("exclude");
        relation.setIsVisible("0");
        relation.setIsTop("0");
        relation.setIsRecommend("0");
        relation.setSortOrder(0);
        relation.setCreateBy(SecurityUtils.getUsername());
        relation.setCreateTime(new Date());
        
        return siteMasterArticleRelationMapper.insert(relation);
    }

    /**
     * 恢复默认主文章（取消排除）
     * 
     * @param siteId 网站ID
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    @Override
    public int restoreDefaultMasterArticle(Long siteId, Long masterArticleId) {
        return siteMasterArticleRelationMapper.deleteBySiteAndMasterArticle(siteId, masterArticleId, "exclude");
    }

    /**
     * 获取主文章被哪些网站排除
     * 
     * @param masterArticleId 主文章ID
     * @return 被排除的网站ID列表
     */
    @Override
    public List<Long> getExcludedSitesByMasterArticle(Long masterArticleId) {
        return siteMasterArticleRelationMapper.selectExcludedSiteIdsByMasterArticle(masterArticleId);
    }

    /**
     * 批量管理主文章的排除网站
     * 
     * @param masterArticleId 主文章ID
     * @param excludedSiteIds 要排除的网站ID列表
     * @return 操作数量（新增+删除）
     */
    @Override
    @Transactional
    public int manageDefaultMasterArticleExclusions(Long masterArticleId, List<Long> excludedSiteIds) {
        // 1. 查询当前已排除的网站
        List<Long> currentExcluded = siteMasterArticleRelationMapper.selectExcludedSiteIdsByMasterArticle(masterArticleId);
        
        // 2. 计算需要新增的排除关联
        List<Long> toAdd = excludedSiteIds.stream()
            .filter(id -> !currentExcluded.contains(id))
            .collect(Collectors.toList());
        
        // 3. 计算需要删除的排除关联
        List<Long> toRemove = currentExcluded.stream()
            .filter(id -> !excludedSiteIds.contains(id))
            .collect(Collectors.toList());
        
        int operationCount = 0;
        
        // 4. 批量新增排除关联
        if (!toAdd.isEmpty()) {
            List<com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation> relations = new ArrayList<>();
            String username = SecurityUtils.getUsername();
            Date now = new Date();
            
            for (Long siteId : toAdd) {
                com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation relation = 
                    new com.ruoyi.gamebox.domain.GbSiteMasterArticleRelation();
                relation.setSiteId(siteId);
                relation.setMasterArticleId(masterArticleId);
                relation.setRelationType("exclude");
                relation.setIsVisible("0");
                relation.setIsTop("0");
                relation.setIsRecommend("0");
                relation.setSortOrder(0);
                relation.setCreateBy(username);
                relation.setCreateTime(now);
                relations.add(relation);
            }
            
            siteMasterArticleRelationMapper.batchInsert(relations);
            operationCount += toAdd.size();
        }
        
        // 5. 批量删除排除关联
        if (!toRemove.isEmpty()) {
            for (Long siteId : toRemove) {
                siteMasterArticleRelationMapper.deleteBySiteAndMasterArticle(siteId, masterArticleId, "exclude");
                operationCount++;
            }
        }
        
        return operationCount;
    }

    /**
     * 更新主文章发布时间，基于其语言版本的发布情况
     * 注意：主文章的 status 和 isPublished 字段已删除，状态由前端根据文章表动态计算
     * 
     * @param masterArticleId 主文章ID
     */
    private void updateMasterArticleStatus(Long masterArticleId) {
        try {
            // 查询该主文章的所有语言版本
            java.util.List<GbArticle> articles = gbArticleMapper.selectGbArticleList(new GbArticle() {{
                setMasterArticleId(masterArticleId);
                setDelFlag("0");
            }});
            
            if (articles == null || articles.isEmpty()) {
                return;
            }
            
            // 统计发布状态和获取最新发布时间
            Date latestPublishTime = null;
            
            for (GbArticle article : articles) {
                if ("1".equals(article.getStatus())) {
                    if (article.getPublishTime() != null) {
                        if (latestPublishTime == null || article.getPublishTime().after(latestPublishTime)) {
                            latestPublishTime = article.getPublishTime();
                        }
                    }
                }
            }
            
            // 只更新主文章的发布时间
            GbMasterArticle updateMaster = new GbMasterArticle();
            updateMaster.setId(masterArticleId);
            updateMaster.setPublishTime(latestPublishTime); // 有已发布版本用最新发布时间，否则为null
            updateMaster.setUpdateBy(SecurityUtils.getUsername());
            updateMaster.setUpdateTime(DateUtils.getNowDate());
            
            updateGbMasterArticle(updateMaster);
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("更新主文章发布时间失败: " + e.getMessage());
        }
    }

    /**
     * 计算主文章的发布状态（基于语言版本）
     * 
     * @param masterArticle 主文章对象
     */
    private void calculatePublishStatus(GbMasterArticle masterArticle) {
        if (masterArticle == null || masterArticle.getId() == null) {
            return;
        }
        
        try {
            // 查询该主文章的所有语言版本
            List<GbArticle> articles = gbArticleMapper.selectGbArticleList(new GbArticle() {{
                setMasterArticleId(masterArticle.getId());
                setDelFlag("0");
            }});
            
            if (articles == null || articles.isEmpty()) {
                masterArticle.setPublishStatus("0"); // 无发布版本
                masterArticle.setStatusText("未发布");
                return;
            }
            
            // 统计发布状态
            int totalCount = articles.size();
            int publishedCount = 0;
            
            for (GbArticle article : articles) {
                if ("1".equals(article.getStatus())) {
                    publishedCount++;
                }
            }
            
            // 设置发布状态
            if (publishedCount == 0) {
                masterArticle.setPublishStatus("0"); // 无发布版本
                masterArticle.setStatusText("未发布");
            } else if (publishedCount == totalCount) {
                masterArticle.setPublishStatus("2"); // 全部已发布
                masterArticle.setStatusText("已发布");
            } else {
                masterArticle.setPublishStatus("1"); // 有发布版本
                masterArticle.setStatusText("部分发布");
            }
        } catch (Exception e) {
            // 计算失败时设置默认值
            masterArticle.setPublishStatus("0");
            masterArticle.setStatusText("未知");
        }
    }

    /**
     * 为主文章动态设置默认语言（从网站配置获取）
     * 
     * @param masterArticle 主文章对象
     */
    private void setDefaultLocale(GbMasterArticle masterArticle) {
        if (masterArticle != null && masterArticle.getSiteId() != null) {
            try {
                GbSite site = siteService.selectGbSiteById(masterArticle.getSiteId());
                if (site != null && site.getDefaultLocale() != null) {
                    masterArticle.setDefaultLocale(site.getDefaultLocale());
                } else {
                    // 兜底默认值
                    masterArticle.setDefaultLocale("zh-CN");
                }
            } catch (Exception e) {
                // 如果获取失败，使用默认值
                masterArticle.setDefaultLocale("zh-CN");
            }
        }
    }

    /**
     * 替换文章内容中的模板变量
     * 支持格式: {{SITE.name}}, {{GAMEBOX-1.name}}, {{GAME-1.name}}
     * 
     * @param content 原始内容（带模板变量）
     * @param article 文章对象（用于获取site信息）
     * @return 替换后的内容
     */
    private String replaceTemplateVariables(String content, GbArticle article) {
        if (content == null || content.isEmpty()) {
            return content;
        }

        try {
            // 构建变量上下文
            Map<String, Object> context = buildTemplateContext(article);
            
            // 使用Pattern和Matcher替换所有 {{XXX.yyy}} 或 {{XXX-n.yyy}} 格式的变量
            Pattern pattern = Pattern.compile("\\{\\{([a-zA-Z0-9_.-]+)\\}\\}");
            Matcher matcher = pattern.matcher(content);
            StringBuffer result = new StringBuffer();
            
            while (matcher.find()) {
                String path = matcher.group(1);
                Object value = resolveVariablePath(path, context);
                // 只有基本类型（字符串、数字、布尔值）才替换，对象类型保留原样
                if (value != null && !(value instanceof Map) && !(value instanceof List)) {
                    matcher.appendReplacement(result, Matcher.quoteReplacement(String.valueOf(value)));
                } else {
                    // 未找到变量或者是对象类型，保留原变量语法
                    matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
                }
            }
            matcher.appendTail(result);
            
            return result.toString();
        } catch (Exception e) {
            logger.error("替换模板变量失败: " + e.getMessage(), e);
            return content; // 替换失败时返回原内容
        }
    }

    /**
     * 构建模板变量上下文
     * 
     * @param article 文章对象
     * @return 变量上下文Map
     */
    private Map<String, Object> buildTemplateContext(GbArticle article) {
        Map<String, Object> context = new HashMap<>();

        try {
            // 1. 添加网站配置
            if (article.getSiteId() != null) {
                GbSite site = siteService.selectGbSiteById(article.getSiteId());
                if (site != null) {
                    Map<String, Object> siteData = new HashMap<>();
                    siteData.put("name", site.getName());
                    siteData.put("domain", site.getDomain());
                    siteData.put("code", site.getCode());
                    context.put("SITE", siteData);
                }
            }

            // 2. 添加游戏盒子数据
            if (article.getMasterArticleId() != null) {
                List<GbMasterArticleGameBoxes> gameBoxRelations = masterArticleGameBoxesService.selectByMasterArticleId(article.getMasterArticleId());
                if (gameBoxRelations != null && !gameBoxRelations.isEmpty()) {
                    for (int i = 0; i < gameBoxRelations.size(); i++) {
                        Long gameBoxId = gameBoxRelations.get(i).getGameBoxId();
                        GbGameBox gameBox = gameBoxService.selectGbGameBoxById(gameBoxId);
                        if (gameBox != null) {
                            Map<String, Object> gameBoxData = new HashMap<>();
                            gameBoxData.put("name", gameBox.getName());
                            gameBoxData.put("logoUrl", gameBox.getLogoUrl());
                            gameBoxData.put("downloadUrl", gameBox.getDownloadUrl());
                            gameBoxData.put("description", gameBox.getDescription());
                            context.put("GAMEBOX-" + (i + 1), gameBoxData);
                        }
                    }
                }
            }

            // 3. 添加游戏数据
            if (article.getMasterArticleId() != null) {
                List<GbMasterArticleGames> gameRelations = masterArticleGamesService.selectByMasterArticleId(article.getMasterArticleId());
                if (gameRelations != null && !gameRelations.isEmpty()) {
                    for (int i = 0; i < gameRelations.size(); i++) {
                        Long gameId = gameRelations.get(i).getGameId();
                        GbGame game = gameService.selectGbGameById(gameId);
                        if (game != null) {
                            Map<String, Object> gameData = new HashMap<>();
                            gameData.put("name", game.getName());
                            gameData.put("downloadUrl", game.getDownloadUrl());
                            gameData.put("androidUrl", game.getAndroidUrl());
                            gameData.put("iosUrl", game.getIosUrl());
                            gameData.put("coverUrl", game.getCoverUrl());
                            gameData.put("description", game.getDescription());
                            context.put("GAME-" + (i + 1), gameData);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("构建模板上下文失败: " + e.getMessage(), e);
        }

        return context;
    }

    /**
     * 解析变量路径（支持点号分隔）
     * 
     * @param path 变量路径，如 "SITE.name" 或 "GAMEBOX-1.logoUrl"
     * @param context 变量上下文
     * @return 解析后的值，找不到返回null
     */
    @SuppressWarnings("unchecked")
    private Object resolveVariablePath(String path, Map<String, Object> context) {
        String[] parts = path.split("\\.");
        Object current = context;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
            } else {
                return null;
            }
        }

        return current;
    }

}