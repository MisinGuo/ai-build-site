package com.ruoyi.gamebox.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文章管理对象 gb_articles
 * 
 * @author ruoyi
 */
public class GbArticle extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文章ID */
    private Long id;

    /** 网站ID */
    @Excel(name = "网站ID")
    private Long siteId;

    /** 文章路径标识 */
    @Excel(name = "文章路径标识")
    private String slug;

    /** 文章语言 */
    @Excel(name = "文章语言")
    private String locale;

    /** 文章标题 */
    @Excel(name = "文章标题")
    private String title;

    /** 文章副标题 */
    @Excel(name = "文章副标题")
    private String subtitle;

    /** 分类ID */
    @Excel(name = "分类ID")
    private Long categoryId;

    /** 标签JSON数组 */
    @Excel(name = "标签")
    private String tags;

    /** 封面URL */
    @Excel(name = "封面URL")
    private String coverUrl;

    /** 文章摘要 */
    @Excel(name = "文章摘要")
    private String description;

    /** 文章内容 */
    private String content;

    /** content字段清理时间（清理后此字段有值） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "content清理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date contentClearedAt;

    /** content自动清理天数（发布后多少天自动清理，默认30天） */
    @Excel(name = "content自动清理天数")
    private Integer contentAutoClearDays;

    /** content备份存储路径（用于恢复） */
    @Excel(name = "content备份路径")
    private String contentBackupKey;

    /** 内容类型：html-富文本, markdown-Markdown */
    @Excel(name = "内容类型", readConverterExp = "html=富文本,markdown=Markdown")
    private String contentType;

    // 注释：source字段已从gb_articles表中移除，请使用主文章表的相关字段
    // /** 文章来源：manual-人工创建, ai-AI生成, import-导入 */
    // @Excel(name = "文章来源", readConverterExp = "manual=人工创建,ai=AI生成,import=导入")
    // private String source;

    /** AI生成任务ID */
    private Long generationId;

    /** 作者 */
    @Excel(name = "作者")
    private String author;

    /** 关键词 */
    @Excel(name = "关键词")
    private String keywords;

    /** 阅读数 */
    @Excel(name = "阅读数")
    private Long viewCount;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 收藏数 */
    @Excel(name = "收藏数")
    private Long favoriteCount;

    /** 评论数 */
    @Excel(name = "评论数")
    private Long commentCount;

    /** 分享数 */
    @Excel(name = "分享数")
    private Long shareCount;

    /** 是否置顶 */
    @Excel(name = "是否置顶", readConverterExp = "0=否,1=是")
    private String isTop;

    /** 是否热门 */
    @Excel(name = "是否热门", readConverterExp = "0=否,1=是")
    private String isHot;

    /** 是否推荐 */
    @Excel(name = "是否推荐", readConverterExp = "0=否,1=是")
    private String isRecommend;

    /** 是否允许评论 */
    @Excel(name = "是否允许评论", readConverterExp = "0=否,1=是")
    private String allowComment;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发布时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    // /** 是否已发布（已废弃，使用status字段：0=草稿，1=已发布，2=已下架） */
    // @Excel(name = "是否已发布", readConverterExp = "0=否,1=是")
    // private String isPublished;

    /** 主文章ID（关联到 gb_master_articles 表） */
    @Excel(name = "主文章ID")
    private Long masterArticleId;

    /** 主文章内容键（查询时从 gb_master_articles 获取，不对应数据库字段） */
    private String masterContentKey;

    /** 网站名称（查询时获取） */
    private String siteName;

    /** 该语言版本专用的存储配置ID */
    @Excel(name = "语言版本存储配置ID")
    private Long localeStorageConfigId;

    /** 该语言版本专用的资源存储配置ID */
    @Excel(name = "语言版本资源存储配置ID")
    private Long localeResourceStorageConfigId;

    /** 存储配置ID */
    @Excel(name = "存储配置ID")
    private Long storageConfigId;

    /** 存储键/路径 */
    @Excel(name = "存储键")
    private String storageKey;

    /** 完整访问URL */
    @Excel(name = "访问URL")
    private String storageUrl;

    /** 远程文件状态：0-未检查 1-正常 2-不存在 3-异常 */
    @Excel(name = "远程文件状态", readConverterExp = "0=未检查,1=正常,2=不存在,3=异常")
    private String remoteFileStatus;

    /** 最后同步检查时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后同步检查时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncCheckTime;

    /** 远程文件大小（字节） */
    @Excel(name = "远程文件大小")
    private Long remoteFileSize;

    /** 远程文件哈希值 */
    private String remoteFileHash;

    /** 存储路径规则 */
    @Excel(name = "存储路径规则")
    private String pathRule;

    /** SEO标题 */
    @Excel(name = "SEO标题")
    private String seoTitle;

    /** SEO关键词 */
    @Excel(name = "SEO关键词")
    private String seoKeywords;

    /** SEO描述 */
    @Excel(name = "SEO描述")
    private String seoDescription;

    /** 排序号 */
    @Excel(name = "排序号")
    private Integer sortOrder;

    /** 状态：0草稿 1已发布 2已下架 */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=已发布,2=已下架")
    private String status;

    /** 删除标志：0存在 2删除 */
    private String delFlag;
    
    /** 分类名称（关联查询字段） */
    private String categoryName;

    /** 分类图标（关联查询字段） */
    private String categoryIcon;
    
    /** 板块ID（查询时使用，从 category 表的 parent_id 获取） */
    private Long sectionId;
    
    /** 板块名称（查询时使用） */
    private String sectionName;
    
    /** 存储配置名称（关联查询字段） */
    private String storageConfigName;
    
    /** 存储分类ID（关联查询字段） */
    private Long storageCategoryId;
    
    /** 存储分类名称（关联查询字段） */
    private String storageCategoryName;
    
    /** 绑定类型（关联查询字段）：game-游戏主页, gamebox-游戏盒子主页 */
    private String bindType;
    
    /** 绑定目标ID（关联查询字段）：游戏ID或游戏盒子ID */
    private Long bindTargetId;
    
    /** 查询模式：creator-创建者, related-关联网站（仅用于查询，不存储） */
    private String queryMode;

    /** 是否包含默认配置（仅用于查询，不存储） */
    private Boolean includeDefault;

    /** 关联来源标识（仅用于查询，不存储）：own-自有，default-默认配置，shared-跨站共享 */
    private String relationSource;

    /** 统一的可见性状态（仅用于查询，不存储）：0-不可见，1-可见 */
    private String isVisible;

    /** 是否被排除（仅用于查询，不存储）：0-未排除，1-已排除（仅对默认配置有效） */
    private String isExcluded;
    
    /** 是否强制覆盖（请求参数，不存数据库） */
    private Boolean forceOverwrite;
    
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
    public void setSlug(String slug) 
    {
        this.slug = slug;
    }

    public String getSlug() 
    {
        return slug;
    }
    public void setLocale(String locale) 
    {
        this.locale = locale;
    }

    public String getLocale() 
    {
        return locale;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setSubtitle(String subtitle) 
    {
        this.subtitle = subtitle;
    }

    public String getSubtitle() 
    {
        return subtitle;
    }
    public void setCategoryId(Long categoryId) 
    {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() 
    {
        return categoryId;
    }
    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }
    public void setCoverUrl(String coverUrl) 
    {
        this.coverUrl = coverUrl;
    }

    public String getCoverUrl() 
    {
        return coverUrl;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }
    
    public void setContentClearedAt(Date contentClearedAt) 
    {
        this.contentClearedAt = contentClearedAt;
    }

    public Date getContentClearedAt() 
    {
        return contentClearedAt;
    }

    public void setContentAutoClearDays(Integer contentAutoClearDays) 
    {
        this.contentAutoClearDays = contentAutoClearDays;
    }

    public Integer getContentAutoClearDays() 
    {
        return contentAutoClearDays;
    }

    public void setContentBackupKey(String contentBackupKey) 
    {
        this.contentBackupKey = contentBackupKey;
    }

    public String getContentBackupKey() 
    {
        return contentBackupKey;
    }
    
    public void setContentType(String contentType) 
    {
        this.contentType = contentType;
    }

    public String getContentType() 
    {
        return contentType;
    }
    
    // 注释：source字段已从gb_articles表中移除
    // public void setSource(String source) 
    // {
    //     this.source = source;
    // }

    // public String getSource() 
    // {
    //     return source;
    // }
    
    public void setGenerationId(Long generationId) 
    {
        this.generationId = generationId;
    }

    public Long getGenerationId() 
    {
        return generationId;
    }
    public void setAuthor(String author) 
    {
        this.author = author;
    }

    public String getAuthor() 
    {
        return author;
    }
    public void setKeywords(String keywords) 
    {
        this.keywords = keywords;
    }

    public String getKeywords() 
    {
        return keywords;
    }
    public void setViewCount(Long viewCount) 
    {
        this.viewCount = viewCount;
    }

    public Long getViewCount() 
    {
        return viewCount;
    }
    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }
    public void setFavoriteCount(Long favoriteCount) 
    {
        this.favoriteCount = favoriteCount;
    }

    public Long getFavoriteCount() 
    {
        return favoriteCount;
    }
    public void setCommentCount(Long commentCount) 
    {
        this.commentCount = commentCount;
    }

    public Long getCommentCount() 
    {
        return commentCount;
    }
    public void setShareCount(Long shareCount) 
    {
        this.shareCount = shareCount;
    }

    public Long getShareCount() 
    {
        return shareCount;
    }
    public void setIsTop(String isTop) 
    {
        this.isTop = isTop;
    }

    public String getIsTop() 
    {
        return isTop;
    }
    public void setIsHot(String isHot) 
    {
        this.isHot = isHot;
    }

    public String getIsHot() 
    {
        return isHot;
    }
    public void setIsRecommend(String isRecommend) 
    {
        this.isRecommend = isRecommend;
    }

    public String getIsRecommend() 
    {
        return isRecommend;
    }
    public void setAllowComment(String allowComment) 
    {
        this.allowComment = allowComment;
    }

    public String getAllowComment() 
    {
        return allowComment;
    }
    public void setPublishTime(Date publishTime) 
    {
        this.publishTime = publishTime;
    }

    public Date getPublishTime() 
    {
        return publishTime;
    }
    
    public void setMasterArticleId(Long masterArticleId) 
    {
        this.masterArticleId = masterArticleId;
    }

    public Long getMasterArticleId() 
    {
        return masterArticleId;
    }
    
    public void setMasterContentKey(String masterContentKey) 
    {
        this.masterContentKey = masterContentKey;
    }

    public String getMasterContentKey() 
    {
        return masterContentKey;
    }

    public void setSiteName(String siteName) 
    {
        this.siteName = siteName;
    }

    public String getSiteName() 
    {
        return siteName;
    }

    public void setLocaleStorageConfigId(Long localeStorageConfigId) 
    {
        this.localeStorageConfigId = localeStorageConfigId;
    }

    public Long getLocaleStorageConfigId() 
    {
        return localeStorageConfigId;
    }

    public void setLocaleResourceStorageConfigId(Long localeResourceStorageConfigId) 
    {
        this.localeResourceStorageConfigId = localeResourceStorageConfigId;
    }

    public Long getLocaleResourceStorageConfigId() 
    {
        return localeResourceStorageConfigId;
    }

    // public void setIsPublished(String isPublished) 
    // {
    //     this.isPublished = isPublished;
    // }

    // public String getIsPublished() 
    // {
    //     return isPublished;
    // }
    public void setStorageConfigId(Long storageConfigId) 
    {
        this.storageConfigId = storageConfigId;
    }

    public Long getStorageConfigId() 
    {
        return storageConfigId;
    }
    public void setStorageKey(String storageKey) 
    {
        this.storageKey = storageKey;
    }

    public String getStorageKey() 
    {
        return storageKey;
    }
    public void setStorageUrl(String storageUrl) 
    {
        this.storageUrl = storageUrl;
    }

    public String getStorageUrl() 
    {
        return storageUrl;
    }

    public void setRemoteFileStatus(String remoteFileStatus) 
    {
        this.remoteFileStatus = remoteFileStatus;
    }

    public String getRemoteFileStatus() 
    {
        return remoteFileStatus;
    }

    public void setLastSyncCheckTime(Date lastSyncCheckTime) 
    {
        this.lastSyncCheckTime = lastSyncCheckTime;
    }

    public Date getLastSyncCheckTime() 
    {
        return lastSyncCheckTime;
    }

    public void setRemoteFileSize(Long remoteFileSize) 
    {
        this.remoteFileSize = remoteFileSize;
    }

    public Long getRemoteFileSize() 
    {
        return remoteFileSize;
    }

    public void setRemoteFileHash(String remoteFileHash) 
    {
        this.remoteFileHash = remoteFileHash;
    }

    public String getRemoteFileHash() 
    {
        return remoteFileHash;
    }
    
    public void setPathRule(String pathRule) 
    {
        this.pathRule = pathRule;
    }

    public String getPathRule() 
    {
        return pathRule;
    }
    public void setSeoTitle(String seoTitle) 
    {
        this.seoTitle = seoTitle;
    }

    public String getSeoTitle() 
    {
        return seoTitle;
    }
    public void setSeoKeywords(String seoKeywords) 
    {
        this.seoKeywords = seoKeywords;
    }

    public String getSeoKeywords() 
    {
        return seoKeywords;
    }
    public void setSeoDescription(String seoDescription) 
    {
        this.seoDescription = seoDescription;
    }

    public String getSeoDescription() 
    {
        return seoDescription;
    }
    public void setSortOrder(Integer sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() 
    {
        return sortOrder;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
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

    public void setStorageConfigName(String storageConfigName)
    {
        this.storageConfigName = storageConfigName;
    }

    public String getStorageConfigName()
    {
        return storageConfigName;
    }

    public void setStorageCategoryId(Long storageCategoryId)
    {
        this.storageCategoryId = storageCategoryId;
    }

    public Long getStorageCategoryId()
    {
        return storageCategoryId;
    }

    public void setStorageCategoryName(String storageCategoryName)
    {
        this.storageCategoryName = storageCategoryName;
    }

    public String getStorageCategoryName()
    {
        return storageCategoryName;
    }

    public void setQueryMode(String queryMode)
    {
        this.queryMode = queryMode;
    }

    public String getQueryMode()
    {
        return queryMode;
    }

    public void setIncludeDefault(Boolean includeDefault)
    {
        this.includeDefault = includeDefault;
    }

    public Boolean getIncludeDefault()
    {
        return includeDefault;
    }

    public void setRelationSource(String relationSource)
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource()
    {
        return relationSource;
    }

    public void setIsVisible(String isVisible)
    {
        this.isVisible = isVisible;
    }

    public String getIsVisible()
    {
        return isVisible;
    }

    public void setIsExcluded(String isExcluded)
    {
        this.isExcluded = isExcluded;
    }

    public String getIsExcluded()
    {
        return isExcluded;
    }

    public void setForceOverwrite(Boolean forceOverwrite)
    {
        this.forceOverwrite = forceOverwrite;
    }

    public Boolean getForceOverwrite()
    {
        return forceOverwrite;
    }

    public void setBindType(String bindType)
    {
        this.bindType = bindType;
    }

    public String getBindType()
    {
        return bindType;
    }

    public void setBindTargetId(Long bindTargetId)
    {
        this.bindTargetId = bindTargetId;
    }

    public Long getBindTargetId()
    {
        return bindTargetId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("siteId", getSiteId())
            .append("title", getTitle())
            .append("subtitle", getSubtitle())
            .append("categoryId", getCategoryId())
            .append("tags", getTags())
            .append("coverUrl", getCoverUrl())
            .append("description", getDescription())
            .append("content", getContent())
            .append("contentType", getContentType())
            // .append("source", getSource())  // source字段已移除
            .append("generationId", getGenerationId())
            .append("author", getAuthor())
            .append("viewCount", getViewCount())
            .append("likeCount", getLikeCount())
            .append("favoriteCount", getFavoriteCount())
            .append("commentCount", getCommentCount())
            .append("shareCount", getShareCount())
            .append("isTop", getIsTop())
            .append("isHot", getIsHot())
            .append("isRecommend", getIsRecommend())
            .append("allowComment", getAllowComment())
            // .append("publishTime", getPublishTime())
            .append("masterContentKey", getMasterContentKey())
            .append("siteName", getSiteName())
            .append("localeStorageConfigId", getLocaleStorageConfigId())
            .append("localeResourceStorageConfigId", getLocaleResourceStorageConfigId())
            .append("seoTitle", getSeoTitle())
            .append("seoKeywords", getSeoKeywords())
            .append("seoDescription", getSeoDescription())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
