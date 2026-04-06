package com.ruoyi.gamebox.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主文章内容对象 gb_master_articles
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public class GbMasterArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主文章ID */
    private Long id;

    /** 所属网站ID */
    @Excel(name = "所属网站ID")
    private Long siteId;

    /** 网站名称（查询时关联显示） */
    private String siteName;

    /** 默认语言（从网站配置动态获取，不存储到数据库） */
    @Excel(name = "默认语言")
    private String defaultLocale;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 分类名称（查询时关联显示） */
    private String categoryName;

    /** 分类图标（查询时关联显示） */
    private String categoryIcon;

    /** 板块ID（查询时关联显示） */
    private Long sectionId;

    /** 板块名称（查询时关联显示） */
    private String sectionName;

    /** 是否AI生成：0-否 1-是 */
    @Excel(name = "是否AI生成", readConverterExp = "0=否,1=是")
    private String isAiGenerated;

    /** 使用的提示词模板ID */
    @Excel(name = "提示词模板ID")
    private Long promptTemplateId;

    /** 生成次数 */
    @Excel(name = "生成次数")
    private Integer generationCount;

    /** 最后一次生成记录ID */
    @Excel(name = "最后一次生成记录ID")
    private Long lastGenerationId;

    /** 是否置顶：0-否 1-是 */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private String isTop;

    /** 是否推荐：0-否 1-是 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isRecommend;

    /** 排序 */
    @Excel(name = "排序")
    private Integer sortOrder;

    /** 删除标志 */
    private String delFlag;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /** 关联的语言版本列表 */
    private List<GbArticle> languageVersions;
    
    /** 关联的游戏盒子 */
    private List<GbMasterArticleGameBox> gameBoxRelations;
    
    /** 关联的游戏 */
    private List<GbMasterArticleGame> gameRelations;
    
    /** 关联的剧集 */
    private List<GbMasterArticleDrama> dramaRelations;
    
    /** 存储配置 */
    private List<GbMultilangStorageConfig> storageConfigs;

    /** 语言版本数量 */
    private Integer languageVersionCount;

    /** 用于批量操作的ID数组 */
    private Long[] ids;
    
    // ====== 动态计算的状态字段（不映射到数据库） ======
    /** 主文章发布状态：0-无发布版本(所有版本都是草稿) 1-有发布版本(至少有一个版本已发布) 2-全部已发布 */
    private String publishStatus;
    
    /** 主文章状态描述（用于前端展示）*/
    private String statusText;
    
    /** 关联的网站数量（动态计算，不映射到数据库） */
    private Integer relatedSitesCount;
    
    /** 排除的网站数量（动态计算，不映射到数据库） */
    private Integer excludedSitesCount;
    
    // ====== 以下字段来自默认语言版本的文章，用于列表显示 ======
    /** 默认语言版本的文章标题 */
    private String title;
    
    /** 默认语言版本的封面URL */
    private String coverUrl;
    
    /** 默认语言版本的作者 */
    private String author;
    
    /** 默认语言版本的slug */
    private String slug;
    
    /** 默认语言版本的存储配置ID */
    private Long storageConfigId;
    
    /** 默认语言版本的存储URL */
    private String storageUrl;
    
    /** 默认语言版本的浏览量 */
    private Integer viewCount;

    /** 当前显示的语言版本（来自关联的文章表） */
    private String locale;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setSiteId(Long siteId) 
    {
        this.siteId = siteId;
    }

    public Long getSiteId() 
    {
        return siteId;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setDefaultLocale(String defaultLocale) 
    {
        this.defaultLocale = defaultLocale;
    }

    public String getDefaultLocale() 
    {
        return defaultLocale;
    }

    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }

    public void setCategoryName(String categoryName) 
    {
        this.categoryName = categoryName;
    }

    public String getCategoryName() 
    {
        return categoryName;
    }

    public void setCategoryIcon(String categoryIcon) 
    {
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryIcon() 
    {
        return categoryIcon;
    }

    public void setSectionId(Long sectionId) 
    {
        this.sectionId = sectionId;
    }

    public Long getSectionId() 
    {
        return sectionId;
    }

    public void setSectionName(String sectionName) 
    {
        this.sectionName = sectionName;
    }

    public String getSectionName() 
    {
        return sectionName;
    }

    public void setIsAiGenerated(String isAiGenerated) 
    {
        this.isAiGenerated = isAiGenerated;
    }

    public String getIsAiGenerated() 
    {
        return isAiGenerated;
    }

    public void setPromptTemplateId(Long promptTemplateId) 
    {
        this.promptTemplateId = promptTemplateId;
    }

    public Long getPromptTemplateId() 
    {
        return promptTemplateId;
    }

    public void setGenerationCount(Integer generationCount) 
    {
        this.generationCount = generationCount;
    }

    public Integer getGenerationCount() 
    {
        return generationCount;
    }

    public void setLastGenerationId(Long lastGenerationId) 
    {
        this.lastGenerationId = lastGenerationId;
    }

    public Long getLastGenerationId() 
    {
        return lastGenerationId;
    }

    public void setIsTop(String isTop) 
    {
        this.isTop = isTop;
    }

    public String getIsTop() 
    {
        return isTop;
    }

    public void setIsRecommend(String isRecommend) 
    {
        this.isRecommend = isRecommend;
    }

    public String getIsRecommend() 
    {
        return isRecommend;
    }

    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }

    public List<GbArticle> getLanguageVersions() {
        return languageVersions;
    }

    public void setLanguageVersions(List<GbArticle> languageVersions) {
        this.languageVersions = languageVersions;
    }

    public List<GbMasterArticleGameBox> getGameBoxRelations() {
        return gameBoxRelations;
    }

    public void setGameBoxRelations(List<GbMasterArticleGameBox> gameBoxRelations) {
        this.gameBoxRelations = gameBoxRelations;
    }

    public List<GbMasterArticleGame> getGameRelations() {
        return gameRelations;
    }

    public void setGameRelations(List<GbMasterArticleGame> gameRelations) {
        this.gameRelations = gameRelations;
    }

    public List<GbMasterArticleDrama> getDramaRelations() {
        return dramaRelations;
    }

    public void setDramaRelations(List<GbMasterArticleDrama> dramaRelations) {
        this.dramaRelations = dramaRelations;
    }

    public List<GbMultilangStorageConfig> getStorageConfigs() {
        return storageConfigs;
    }

    public void setStorageConfigs(List<GbMultilangStorageConfig> storageConfigs) {
        this.storageConfigs = storageConfigs;
    }

    public Integer getLanguageVersionCount() {
        return languageVersionCount;
    }

    public void setLanguageVersionCount(Integer languageVersionCount) {
        this.languageVersionCount = languageVersionCount;
    }
    
    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getStorageConfigId() {
        return storageConfigId;
    }

    public void setStorageConfigId(Long storageConfigId) {
        this.storageConfigId = storageConfigId;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    // ====== 查询相关字段（仅用于查询，不存储） ======
    
    /** 查询模式：creator-创建者, related-关联网站（仅用于查询，不存储） */
    private String queryMode;

    /** 是否包含默认配置（仅用于查询，不存储） */
    private Boolean includeDefault;

    /** 统一的可见性状态（仅用于查询，不存储）：0-不可见，1-可见 */
    private String isVisible;

    /** 关联来源标识（仅用于查询，不存储）：own-自有，default-默认配置，shared-跨站共享 */
    private String relationSource;

    /** 是否被排除（仅用于查询，不存储）：0-未排除，1-已排除（仅对默认配置有效） */
    private String isExcluded;

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    public Boolean getIncludeDefault() {
        return includeDefault;
    }

    public void setIncludeDefault(Boolean includeDefault) {
        this.includeDefault = includeDefault;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }

    public String getRelationSource() {
        return relationSource;
    }

    public void setRelationSource(String relationSource) {
        this.relationSource = relationSource;
    }

    public String getIsExcluded() {
        return isExcluded;
    }

    public void setIsExcluded(String isExcluded) {
        this.isExcluded = isExcluded;
    }

    public Integer getRelatedSitesCount() {
        return relatedSitesCount;
    }

    public void setRelatedSitesCount(Integer relatedSitesCount) {
        this.relatedSitesCount = relatedSitesCount;
    }

    public Integer getExcludedSitesCount() {
        return excludedSitesCount;
    }

    public void setExcludedSitesCount(Integer excludedSitesCount) {
        this.excludedSitesCount = excludedSitesCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("siteName", getSiteName())
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("isAiGenerated", getIsAiGenerated())
            .append("promptTemplateId", getPromptTemplateId())
            .append("generationCount", getGenerationCount())
            .append("lastGenerationId", getLastGenerationId())
            .append("isTop", getIsTop())
            .append("isRecommend", getIsRecommend())
            .append("sortOrder", getSortOrder())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("publishTime", getPublishTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long[] getIds() {
        return ids;
    }

    public void setIds(Long[] ids) {
        this.ids = ids;
    }
}