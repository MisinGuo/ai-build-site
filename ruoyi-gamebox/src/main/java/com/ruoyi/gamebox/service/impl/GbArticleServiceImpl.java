package com.ruoyi.gamebox.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbArticleMapper;
import com.ruoyi.gamebox.mapper.GbMasterArticleMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbArticle;
import com.ruoyi.gamebox.domain.GbMasterArticle;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.domain.GbMasterArticleGames;
import com.ruoyi.gamebox.domain.GbMasterArticleGameBoxes;
import com.ruoyi.gamebox.domain.GbGame;
import com.ruoyi.gamebox.domain.GbGameBox;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.service.IGbArticleService;
import com.ruoyi.gamebox.service.IStorageFileService;
import com.ruoyi.gamebox.service.IGbStorageConfigService;
import com.ruoyi.gamebox.service.IGbMasterArticleGamesService;
import com.ruoyi.gamebox.service.IGbMasterArticleGameBoxesService;
import com.ruoyi.gamebox.service.IGbGameService;
import com.ruoyi.gamebox.service.IGbGameBoxService;
import com.ruoyi.gamebox.service.IGbSiteService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;
import com.ruoyi.gamebox.search.helper.ElasticsearchSyncHelper;

/**
 * 文章管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbArticleServiceImpl implements IGbArticleService 
{
    private static final Logger logger = LoggerFactory.getLogger(GbArticleServiceImpl.class);

    @Autowired
    private GbArticleMapper gbArticleMapper;
    
    @Autowired
    private GbMasterArticleMapper gbMasterArticleMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    private void injectPersonalSiteId(com.ruoyi.common.core.domain.BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
}
    
    @Autowired
    private IStorageFileService storageFileService;
    
    @Autowired
    private IGbStorageConfigService storageConfigService;

    @Autowired
    private IGbMasterArticleGamesService masterArticleGamesService;

    @Autowired
    private IGbMasterArticleGameBoxesService masterArticleGameBoxesService;

    @Autowired
    private IGbGameService gameService;

    @Autowired
    private IGbGameBoxService gameBoxService;

    @Autowired
    private IGbSiteService siteService;

    @Autowired(required = false)
    private ElasticsearchSyncHelper elasticsearchSyncHelper;

    /**
     * 查询文章
     * 
     * @param id 文章主键
     * @return 文章
     */
    @Override
    public GbArticle selectGbArticleById(Long id)
    {
        return gbArticleMapper.selectGbArticleById(id);
    }

    /**
     * 查询文章列表
     * 
     * @param gbArticle 文章
     * @return 文章
     */
    @Override
    public List<GbArticle> selectGbArticleList(GbArticle gbArticle)
    {
        relatedModeSiteValidator.validate(gbArticle.getQueryMode(), gbArticle.getSiteId());
        injectPersonalSiteId(gbArticle);
        return gbArticleMapper.selectGbArticleList(gbArticle);
    }

    @Override
    public List<GbArticle> selectAllGbArticlesForSync()
    {
        return gbArticleMapper.selectAllGbArticlesForSync();
    }

    /**
     * 新增文章
     * 
     * @param gbArticle 文章
     * @return 结果
     */
    @Override
    @Transactional
    public int insertGbArticle(GbArticle gbArticle)
    {
        gbArticle.setCreateTime(DateUtils.getNowDate());
        int result = gbArticleMapper.insertGbArticle(gbArticle);
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.syncArticle(gbArticle);
        }
        
        // 同步主文章状态（基于所有语言版本的统计）
        if (result > 0 && gbArticle.getMasterArticleId() != null) {
            updateMasterArticleStatus(gbArticle.getMasterArticleId());
        }
        
        return result;
    }
    
    /**
     * 修改文章
     * 
     * @param gbArticle 文章
     * @return 结果
     */
    @Override
    @Transactional
    public int updateGbArticle(GbArticle gbArticle)
    {
        gbArticle.setUpdateTime(DateUtils.getNowDate());
        int result = gbArticleMapper.updateGbArticle(gbArticle);
        
        // 同步到Elasticsearch
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.syncArticle(gbArticle);
        }
        
        // 同步主文章状态（基于所有语言版本的统计）
        if (result > 0 && gbArticle.getMasterArticleId() != null) {
            updateMasterArticleStatus(gbArticle.getMasterArticleId());
        }
        
        return result;
    }

    /**
     * 批量删除文章
     * 
     * @param ids 需要删除的文章主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbArticleByIds(Long[] ids)
    {
        // 1. 删除每个文章在存储配置中的物理文件
        for (Long id : ids) {
            GbArticle article = gbArticleMapper.selectGbArticleById(id);
            if (article != null && article.getStorageConfigId() != null && article.getStorageKey() != null) {
                try {
                    boolean deleted = storageFileService.deleteFile(article.getStorageConfigId(), article.getStorageKey());
                    if (deleted) {
                        logger.info("成功删除文章存储文件: articleId={}, configId={}, key={}", id, article.getStorageConfigId(), article.getStorageKey());
                    } else {
                        logger.warn("删除文章存储文件失败: articleId={}, configId={}, key={}", id, article.getStorageConfigId(), article.getStorageKey());
                    }
                } catch (Exception e) {
                    logger.error("删除文章存储文件时发生异常: articleId={}, configId={}, key={}, error={}", 
                        id, article.getStorageConfigId(), article.getStorageKey(), e.getMessage(), e);
                }
            }
        }
        
        // 2. 软删除文章记录（保留存储信息，便于恢复后再次发布）
        int result = gbArticleMapper.deleteGbArticleByIds(ids);
        
        // 3. 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteArticles(ids);
        }
        
        return result;
    }

    /**
     * 删除文章信息
     * 
     * @param id 文章主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteGbArticleById(Long id)
    {
        // 1. 删除文章在存储配置中的物理文件
        GbArticle article = gbArticleMapper.selectGbArticleById(id);
        if (article != null && article.getStorageConfigId() != null && article.getStorageKey() != null) {
            try {
                boolean deleted = storageFileService.deleteFile(article.getStorageConfigId(), article.getStorageKey());
                if (deleted) {
                    logger.info("成功删除文章存储文件: articleId={}, configId={}, key={}", id, article.getStorageConfigId(), article.getStorageKey());
                } else {
                    logger.warn("删除文章存储文件失败: articleId={}, configId={}, key={}", id, article.getStorageConfigId(), article.getStorageKey());
                }
            } catch (Exception e) {
                logger.error("删除文章存储文件时发生异常: articleId={}, configId={}, key={}, error={}", 
                    id, article.getStorageConfigId(), article.getStorageKey(), e.getMessage(), e);
            }
        }
        
        // 2. 软删除文章记录（保留存储信息，便于恢复后再次发布）
        int result = gbArticleMapper.deleteGbArticleById(id);
        
        // 3. 从Elasticsearch删除
        if (result > 0 && elasticsearchSyncHelper != null)
        {
            elasticsearchSyncHelper.deleteArticle(id);
        }
        
        return result;
    }

    /**
     * 通过游戏ID查询关联的文章列表
     * 
     * @param gameId 游戏ID
     * @param siteId 站点ID
     * @param locale 语言代码（可选）
     * @return 文章集合
     */
    @Override
    public List<GbArticle> selectArticlesByGameId(Long gameId, Long siteId, String locale)
    {
        return gbArticleMapper.selectArticlesByGameId(gameId, siteId, locale);
    }

    /**
     * 通过盒子ID查询关联的文章列表
     * 
     * @param boxId 盒子ID
     * @param siteId 站点ID
     * @param locale 语言代码（可选）
     * @return 文章集合
     */
    @Override
    public List<GbArticle> selectArticlesByBoxId(Long boxId, Long siteId, String locale)
    {
        return gbArticleMapper.selectArticlesByBoxId(boxId, siteId, locale);
    }

    /**
     * 根据主文章ID和语言代码获取文章
     * 
     * @param masterArticleId 主文章ID
     * @param locale 语言代码
     * @return 文章
     */
    @Override
    public GbArticle getArticleByMasterIdAndLocale(Long masterArticleId, String locale)
    {
        return gbArticleMapper.getArticleByMasterIdAndLocale(masterArticleId, locale);
    }
    
    /**
     * 发布文章到存储
     */
    @Override
    @Transactional
    public AjaxResult publishArticleToStorage(Long id) {
        GbArticle article = gbArticleMapper.selectGbArticleById(id);
        if (article == null) {
            return AjaxResult.error("文章不存在");
        }
        
        if (article.getStorageConfigId() == null) {
            return AjaxResult.error("请选择存储配置");
        }
        
        // 检查是否已经发布到存储
        if (article.getStorageKey() != null) {
            boolean fileExists = storageFileService.checkFileExists(
                article.getStorageConfigId(), 
                article.getStorageKey()
            );
            
            if (fileExists) {
                // 返回成功结果但包含冲突确认数据，与批量发布保持一致
                Map<String, Object> conflictData = new HashMap<>();
                conflictData.put("needConfirm", true);
                conflictData.put("existingFile", article.getStorageKey());
                conflictData.put("existingCount", 1);
                conflictData.put("totalCount", 1);
                conflictData.put("message", "文件已存在");
                
                return AjaxResult.success(conflictData);
            }
        }
        
        return publishToStorage(article, false);
    }
    
    /**
     * 强制发布文章到存储（覆盖已存在文件）
     */
    @Override
    @Transactional
    public AjaxResult publishArticleToStorageForce(Long id) {
        GbArticle article = gbArticleMapper.selectGbArticleById(id);
        if (article == null) {
            return AjaxResult.error("文章不存在");
        }
        
        if (article.getStorageConfigId() == null) {
            return AjaxResult.error("请选择存储配置");
        }
        
        return publishToStorage(article, true);
    }
    
    /**
     * 取消发布文章
     */
    @Override
    @Transactional
    public AjaxResult unpublishArticle(Long id) {
        GbArticle article = gbArticleMapper.selectGbArticleById(id);
        if (article == null) {
            return AjaxResult.error("文章不存在");
        }
        
        if (article.getStorageKey() == null) {
            return AjaxResult.error("文章尚未发布");
        }
        
        try {
            // 删除存储文件
            if (article.getStorageConfigId() != null) {
                storageFileService.deleteFile(
                    article.getStorageConfigId(), 
                    article.getStorageKey()
                );
            }
            
            // 更新文章状态 - 只更新发布相关字段
            gbArticleMapper.updateArticlePublishStatus(
                article.getId(),
                "0", // 草稿状态
                null, // 清除存储键
                null, // 清除存储URL
                article.getPathRule(), // 保持路径规则不变
                null  // 清除发布时间
            );
            
            // 更新远程文件状态为"不存在"
            GbArticle updateRemote = new GbArticle();
            updateRemote.setId(article.getId());
            updateRemote.setRemoteFileStatus("2"); // 文件不存在
            updateRemote.setLastSyncCheckTime(DateUtils.getNowDate());
            gbArticleMapper.updateGbArticle(updateRemote);
            
            // 同步主文章状态
            if (article.getMasterArticleId() != null) {
                updateMasterArticleStatus(article.getMasterArticleId());
            }
            
            return AjaxResult.success("取消发布成功");
            
        } catch (Exception e) {
            return AjaxResult.error("取消发布失败：" + e.getMessage());
        }
    }
    
    /**
     * 发布文章到存储的内部实现
     */
    private AjaxResult publishToStorage(GbArticle article, boolean forceOverride) {
        try {
            // 验证必填字段
            if (article.getStorageConfigId() == null) {
                return AjaxResult.error("请选择存储配置");
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
            
            // 检查是否已有存储路径
            String storageKey = article.getStorageKey();
            if (storageKey == null || storageKey.isEmpty()) {
                return AjaxResult.error("文章尚未设置存储路径，请先编辑并保存");
            }
            
            // 检查文件是否已存在（非强制覆盖时）
            if (!forceOverride) {
                boolean fileExists = storageFileService.checkFileExists(
                    article.getStorageConfigId(), 
                    storageKey
                );
                
                if (fileExists) {
                    return AjaxResult.error("文件已存在，如需覆盖请使用强制发布");
                }
            }
            
            // 替换模板变量（发布到远程的内容需要替换变量）
            String publishContent = replaceTemplateVariables(article.getContent(), article);
            
            // 发布文件到存储（使用替换后的内容）
            boolean success = storageFileService.updateTextFile(
                article.getStorageConfigId(), 
                storageKey, 
                publishContent
            );
            
            if (!success) {
                return AjaxResult.error("发布到存储失败");
            }
            
            // 更新数据库 - 只更新发布相关字段
            Date publishTime = new Date();
            String storageUrl = buildStorageUrl(config, storageKey);
            
            gbArticleMapper.updateArticlePublishStatus(
                article.getId(),
                "1", // 已发布
                storageKey,
                storageUrl,
                article.getPathRule(),
                publishTime
            );
            
            // 更新远程文件状态为"正常"
            GbArticle updateRemote = new GbArticle();
            updateRemote.setId(article.getId());
            updateRemote.setRemoteFileStatus("1"); // 文件正常
            updateRemote.setLastSyncCheckTime(publishTime);
            gbArticleMapper.updateGbArticle(updateRemote);
            
            // 同步主文章状态
            if (article.getMasterArticleId() != null) {
                updateMasterArticleStatus(article.getMasterArticleId());
            }
            
            // 同步到 Elasticsearch（如果启用）
            if (elasticsearchSyncHelper != null) {
                try {
                    elasticsearchSyncHelper.syncArticle(article);
                } catch (Exception e) {
                    // ES 同步失败不影响主流程
                    e.printStackTrace();
                }
            }
            
            return AjaxResult.success("发布成功");
            
        } catch (Exception e) {
            return AjaxResult.error("发布失败：" + e.getMessage());
        }
    }
    
    /**
     * 构建存储URL
     */
    private String buildStorageUrl(GbStorageConfig config, String storageKey) {
        if (config == null || storageKey == null) {
            return null;
        }
        
        // 根据存储类型构建URL
        String baseUrl = config.getCustomDomain();
        if (baseUrl == null || baseUrl.isEmpty()) {
            return null;
        }
        
        // 确保 baseUrl 以 / 结尾
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        // 确保 storageKey 不以 / 开头
        if (storageKey.startsWith("/")) {
            storageKey = storageKey.substring(1);
        }
        
        return baseUrl + storageKey;
    }
    
    /**
     * 更新主文章发布时间（根据语言版本状态）
     * 注意：主文章的 status 和 isPublished 字段已删除，状态由前端根据文章表动态计算
     */
    private void updateMasterArticleStatus(Long masterArticleId) {
        try {
            List<GbArticle> articles = gbArticleMapper.selectGbArticleList(
                new GbArticle() {{ setMasterArticleId(masterArticleId); }}
            );
            
            if (articles.isEmpty()) {
                return;
            }
            
            // 统计发布状态和获取最新发布时间
            boolean hasPublished = false;
            Date latestPublishTime = null;
            
            for (GbArticle article : articles) {
                if ("1".equals(article.getStatus())) {
                    hasPublished = true;
                    // 获取最新的发布时间
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
            
            gbMasterArticleMapper.updateGbMasterArticle(updateMaster);
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("更新主文章发布时间失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量发布语言版本文章（检测冲突）
     */
    @Override
    @Transactional
    public AjaxResult batchPublishArticles(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return AjaxResult.error("请选择要发布的文章");
        }
        
        List<Long> existingArticles = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        
        // 先检查哪些文章已发布或有问题
        for (Long articleId : articleIds) {
            GbArticle article = gbArticleMapper.selectGbArticleById(articleId);
            if (article == null) {
                errorMessages.add("文章ID " + articleId + ": 文章不存在");
                continue;
            }
            
            if (article.getStorageConfigId() == null) {
                errorMessages.add("文章ID " + articleId + ": 请选择存储配置");
                continue;
            }
            
            if (article.getStorageKey() == null || article.getStorageKey().isEmpty()) {
                errorMessages.add("文章ID " + articleId + ": 文章尚未设置存储路径");
                continue;
            }
            
            // 检查文件是否已存在
            boolean fileExists = storageFileService.checkFileExists(
                article.getStorageConfigId(), 
                article.getStorageKey()
            );
            
            if (fileExists) {
                existingArticles.add(articleId);
            }
        }
        
        // 如果有错误，直接返回
        if (!errorMessages.isEmpty()) {
            return AjaxResult.error("存在无法发布的文章", errorMessages);
        }
        
        // 如果有已存在的文章，返回确认信息（使用success状态，与单个发布保持一致）
        if (!existingArticles.isEmpty()) {
            Map<String, Object> confirmData = new HashMap<>();
            confirmData.put("needConfirm", true);
            confirmData.put("existingArticles", existingArticles);
            confirmData.put("totalCount", articleIds.size());
            confirmData.put("existingCount", existingArticles.size());
            confirmData.put("message", "部分文章已发布，是否强制覆盖？");
            
            return AjaxResult.success(confirmData);
        }
        
        // 执行批量发布
        return performBatchPublish(articleIds, false);
    }
    
    /**
     * 强制批量发布语言版本文章
     */
    @Override
    @Transactional
    public AjaxResult batchPublishArticlesForce(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return AjaxResult.error("请选择要发布的文章");
        }
        
        return performBatchPublish(articleIds, true);
    }
    
    /**
     * 执行批量发布的内部方法
     */
    private AjaxResult performBatchPublish(List<Long> articleIds, boolean forceOverride) {
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
        List<Long> successArticles = new ArrayList<>();
        List<Long> failedArticles = new ArrayList<>();
        
        for (Long articleId : articleIds) {
            try {
                // 查询文章信息用于错误提示
                GbArticle article = gbArticleMapper.selectGbArticleById(articleId);
                String articleInfo = article != null ? 
                    String.format("「%s」(%s)", article.getTitle(), article.getLocale()) : 
                    "ID " + articleId;
                
                AjaxResult result;
                if (forceOverride) {
                    result = publishArticleToStorageForce(articleId);
                } else {
                    result = publishArticleToStorage(articleId);
                }
                
                if (Objects.equals(200, result.get("code"))) {
                    successCount++;
                    successArticles.add(articleId);
                } else {
                    failCount++;
                    failedArticles.add(articleId);
                    failMessages.append(articleInfo)
                              .append(": ")
                              .append(result.get("msg"))
                              .append("; ");
                }
            } catch (Exception e) {
                // 查询文章信息用于错误提示
                GbArticle article = gbArticleMapper.selectGbArticleById(articleId);
                String articleInfo = article != null ? 
                    String.format("「%s」(%s)", article.getTitle(), article.getLocale()) : 
                    "ID " + articleId;
                    
                failCount++;
                failedArticles.add(articleId);
                failMessages.append(articleInfo)
                          .append(": ")
                          .append(e.getMessage())
                          .append("; ");
            }
        }
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("successArticles", successArticles);
        resultData.put("failedArticles", failedArticles);
        resultData.put("failMessages", failMessages.toString());
        
        // 批量操作统一返回成功，由前端根据 failCount 判断是否有失败
        return AjaxResult.success(resultData);
    }
    
    /**
     * 批量撤销发布语言版本文章
     */
    @Override
    @Transactional
    public AjaxResult batchUnpublishArticles(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return AjaxResult.error("请选择要撤销发布的文章");
        }
        
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
        List<Long> successArticles = new ArrayList<>();
        List<Long> failedArticles = new ArrayList<>();
        
        for (Long articleId : articleIds) {
            try {
                AjaxResult result = unpublishArticle(articleId);
                
                if (Objects.equals(200, result.get("code"))) {
                    successCount++;
                    successArticles.add(articleId);
                } else {
                    failCount++;
                    failedArticles.add(articleId);
                    failMessages.append("文章ID ")
                              .append(articleId)
                              .append(": ")
                              .append(result.get("msg"))
                              .append("; ");
                }
            } catch (Exception e) {
                failCount++;
                failedArticles.add(articleId);
                failMessages.append("文章ID ")
                          .append(articleId)
                          .append(": ")
                          .append(e.getMessage())
                          .append("; ");
            }
        }
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("successArticles", successArticles);
        resultData.put("failedArticles", failedArticles);
        resultData.put("failMessages", failMessages.toString());
        
        if (failCount > 0) {
            if (successCount > 0) {
                return AjaxResult.warn("批量撤销发布部分成功", resultData);
            } else {
                return AjaxResult.error("批量撤销发布失败", resultData);
            }
        } else {
            return AjaxResult.success("批量撤销发布成功", resultData);
        }
    }
    
    /**
     * 批量更新文章状态（只修改数据库状态）
     */
    @Override
    @Transactional
    public AjaxResult batchUpdateArticleStatus(List<Long> articleIds, String status) {
        if (articleIds == null || articleIds.isEmpty()) {
            return AjaxResult.error("请选择要更新的文章");
        }
        
        if (!"0".equals(status) && !"1".equals(status)) {
            return AjaxResult.error("无效的状态值");
        }
        
        int successCount = 0;
        int failCount = 0;
        StringBuilder failMessages = new StringBuilder();
        
        for (Long articleId : articleIds) {
            try {
                GbArticle article = gbArticleMapper.selectGbArticleById(articleId);
                if (article == null) {
                    failCount++;
                    failMessages.append("文章ID ").append(articleId).append(": 文章不存在; ");
                    continue;
                }
                
                GbArticle updateArticle = new GbArticle();
                updateArticle.setId(articleId);
                updateArticle.setStatus(status);
                
                if ("1".equals(status)) {
                    updateArticle.setPublishTime(DateUtils.getNowDate());
                } else {
                    updateArticle.setPublishTime(null);
                }
                
                int result = gbArticleMapper.updateGbArticle(updateArticle);
                if (result > 0) {
                    successCount++;
                    // 更新主文章状态
                    if (article.getMasterArticleId() != null) {
                        updateMasterArticleStatus(article.getMasterArticleId());
                    }
                } else {
                    failCount++;
                    failMessages.append("文章ID ").append(articleId).append(": 更新失败; ");
                }
            } catch (Exception e) {
                failCount++;
                failMessages.append("文章ID ").append(articleId).append(": ").append(e.getMessage()).append("; ");
            }
        }
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("failMessages", failMessages.toString());
        
        if (failCount > 0) {
            if (successCount > 0) {
                return AjaxResult.warn("批量更新状态部分成功", resultData);
            } else {
                return AjaxResult.error("批量更新状态失败", resultData);
            }
        } else {
            return AjaxResult.success("批量更新状态成功", resultData);
        }
    }
    
    /**
     * 检查文章远程文件状态
     */
    @Override
    public AjaxResult checkRemoteFileStatus(Long articleId) {
        GbArticle article = gbArticleMapper.selectGbArticleById(articleId);
        if (article == null) {
            return AjaxResult.error("文章不存在");
        }
        
        // 如果没有配置存储URL，标记为仅本地
        if (article.getStorageUrl() == null || article.getStorageUrl().isEmpty()) {
            GbArticle updateArticle = new GbArticle();
            updateArticle.setId(articleId);
            updateArticle.setRemoteFileStatus(null);
            updateArticle.setLastSyncCheckTime(DateUtils.getNowDate());
            gbArticleMapper.updateGbArticle(updateArticle);
            return AjaxResult.success("文章仅存储在本地");
        }
        
        String storageUrl = article.getStorageUrl();
        
        // 检查URL是否是完整的HTTP/HTTPS URL
        if (!storageUrl.startsWith("http://") && !storageUrl.startsWith("https://")) {
            // 不是完整URL，需要获取存储配置来构建完整URL
            if (article.getStorageConfigId() == null) {
                GbArticle updateArticle = new GbArticle();
                updateArticle.setId(articleId);
                updateArticle.setRemoteFileStatus("3"); // 异常：无法构建完整URL
                updateArticle.setLastSyncCheckTime(DateUtils.getNowDate());
                gbArticleMapper.updateGbArticle(updateArticle);
                return AjaxResult.error("无法检查远程文件：缺少存储配置");
            }
            
            GbStorageConfig config = storageConfigService.selectGbStorageConfigById(article.getStorageConfigId());
            if (config == null || config.getCustomDomain() == null || config.getCustomDomain().isEmpty()) {
                GbArticle updateArticle = new GbArticle();
                updateArticle.setId(articleId);
                updateArticle.setRemoteFileStatus("3"); // 异常：存储配置无效
                updateArticle.setLastSyncCheckTime(DateUtils.getNowDate());
                gbArticleMapper.updateGbArticle(updateArticle);
                return AjaxResult.error("无法检查远程文件：存储配置未设置访问域名");
            }
            
            // 构建完整URL
            storageUrl = buildStorageUrl(config, article.getStorageKey());
            if (storageUrl == null || storageUrl.isEmpty()) {
                GbArticle updateArticle = new GbArticle();
                updateArticle.setId(articleId);
                updateArticle.setRemoteFileStatus("3");
                updateArticle.setLastSyncCheckTime(DateUtils.getNowDate());
                gbArticleMapper.updateGbArticle(updateArticle);
                return AjaxResult.error("无法构建完整的存储URL");
            }
        }
        
        // 检查远程文件
        String remoteStatus = "0"; // 默认未检查
        Long fileSize = null;
        
        try {
            // 使用HEAD请求检查文件是否存在
            java.net.URL url = new java.net.URL(storageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                remoteStatus = "1"; // 正常
                fileSize = connection.getContentLengthLong();
                if (fileSize == 0) {
                    remoteStatus = "3"; // 文件大小为0，标记为异常
                }
            } else if (responseCode == 404) {
                remoteStatus = "2"; // 不存在
            } else {
                remoteStatus = "3"; // 其他错误
            }
            connection.disconnect();
        } catch (Exception e) {
            remoteStatus = "3"; // 访问异常
        }
        
        // 更新数据库
        GbArticle updateArticle = new GbArticle();
        updateArticle.setId(articleId);
        updateArticle.setRemoteFileStatus(remoteStatus);
        updateArticle.setLastSyncCheckTime(DateUtils.getNowDate());
        if (fileSize != null) {
            updateArticle.setRemoteFileSize(fileSize);
        }
        gbArticleMapper.updateGbArticle(updateArticle);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("remoteFileStatus", remoteStatus);
        result.put("lastSyncCheckTime", DateUtils.getNowDate());
        result.put("remoteFileSize", fileSize);
        result.put("storageUrl", storageUrl);
        
        String message = "未知状态";
        switch (remoteStatus) {
            case "1": message = "远程文件正常"; break;
            case "2": message = "远程文件不存在"; break;
            case "3": message = "远程文件异常"; break;
        }
        
        return AjaxResult.success(message, result);
    }
    
    /**
     * 批量检查文章远程文件状态
     */
    @Override
    public AjaxResult batchCheckRemoteFileStatus(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return AjaxResult.error("请选择要检查的文章");
        }
        
        int successCount = 0;
        int failCount = 0;
        Map<String, Integer> statusCount = new HashMap<>();
        statusCount.put("normal", 0);
        statusCount.put("missing", 0);
        statusCount.put("error", 0);
        statusCount.put("local", 0);
        
        for (Long articleId : articleIds) {
            try {
                AjaxResult result = checkRemoteFileStatus(articleId);
                if (result.get("code").equals(200)) {
                    successCount++;
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) result.get("data");
                    if (data != null) {
                        String status = (String) data.get("remoteFileStatus");
                        if ("1".equals(status)) statusCount.put("normal", statusCount.get("normal") + 1);
                        else if ("2".equals(status)) statusCount.put("missing", statusCount.get("missing") + 1);
                        else if ("3".equals(status)) statusCount.put("error", statusCount.get("error") + 1);
                    } else {
                        statusCount.put("local", statusCount.get("local") + 1);
                    }
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }
        
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("totalCount", articleIds.size());
        resultData.put("successCount", successCount);
        resultData.put("failCount", failCount);
        resultData.put("normalCount", statusCount.get("normal"));
        resultData.put("missingCount", statusCount.get("missing"));
        resultData.put("errorCount", statusCount.get("error"));
        resultData.put("localOnlyCount", statusCount.get("local"));
        
        return AjaxResult.success("批量检查完成", resultData);
    }

    /**
     * 根据文章ID获取所有可用的语言版本
     * 
     * @param id 文章ID
     * @return 语言代码列表
     */
    @Override
    public List<String> getArticleLocales(Long id)
    {
        // id 是 masterArticleId，直接查询所有语言版本
        List<String> locales = new ArrayList<>();
        List<String> relatedLocales = gbArticleMapper.selectArticleLocales(id);
        if (relatedLocales != null && !relatedLocales.isEmpty()) {
            locales.addAll(relatedLocales);
        }
        return locales;
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
