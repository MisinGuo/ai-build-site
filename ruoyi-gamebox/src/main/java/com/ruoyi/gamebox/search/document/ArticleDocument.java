package com.ruoyi.gamebox.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 文章搜索文档
 * 
 * @author ruoyi
 */
@Document(indexName = "gb_articles")
@Setting(shards = 1, replicas = 0)
public class ArticleDocument
{
    /** 文章ID */
    @Id
    private Long id;

    /** 网站ID */
    @Field(type = FieldType.Long)
    private Long siteId;

    /** 文章标题 - 支持中文分词 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /** 文章副标题 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String subtitle;

    /** 分类ID */
    @Field(type = FieldType.Long)
    private Long categoryId;

    /** 分类名称 */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /** 标签 */
    @Field(type = FieldType.Keyword)
    private String[] tags;

    /** 封面URL */
    @Field(type = FieldType.Keyword)
    private String coverUrl;

    /** 文章摘要 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;

    /** 文章内容 - 核心搜索字段 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /** 内容类型 */
    @Field(type = FieldType.Keyword)
    private String contentType;

    /** 文章来源 */
    @Field(type = FieldType.Keyword)
    private String source;

    /** 作者 */
    @Field(type = FieldType.Keyword)
    private String author;

    /** 关键词 */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String keywords;

    /** 阅读数 */
    @Field(type = FieldType.Long)
    private Long viewCount;

    /** 点赞数 */
    @Field(type = FieldType.Long)
    private Long likeCount;

    /** 主文章ID（多语言版本共享同一 masterArticleId，用于 URL 构建） */
    @Field(type = FieldType.Long)
    private Long masterArticleId;

    /** 板块名称（用于前端路由分类） */
    @Field(type = FieldType.Keyword)
    private String sectionName;

    /** 是否置顶 */
    @Field(type = FieldType.Keyword)
    private String isTop;

    /** 发布状态 */
    @Field(type = FieldType.Keyword)
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @Field(type = FieldType.Keyword)
    private String delFlag;

    /** 发布时间 */
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishTime;

    /** 创建时间 */
    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getSiteId()
    {
        return siteId;
    }

    public void setSiteId(Long siteId)
    {
        this.siteId = siteId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public Long getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Long categoryId)
    {
        this.categoryId = categoryId;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String[] getTags()
    {
        return tags;
    }

    public void setTags(String[] tags)
    {
        this.tags = tags;
    }

    public String getCoverUrl()
    {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl)
    {
        this.coverUrl = coverUrl;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    public Long getLikeCount()
    {
        return likeCount;
    }

    public void setLikeCount(Long likeCount)
    {
        this.likeCount = likeCount;
    }

    public String getIsTop()
    {
        return isTop;
    }

    public void setIsTop(String isTop)
    {
        this.isTop = isTop;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Date getPublishTime()
    {
        return publishTime;
    }

    public void setPublishTime(Date publishTime)
    {
        this.publishTime = publishTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Long getMasterArticleId()
    {
        return masterArticleId;
    }

    public void setMasterArticleId(Long masterArticleId)
    {
        this.masterArticleId = masterArticleId;
    }

    public String getSectionName()
    {
        return sectionName;
    }

    public void setSectionName(String sectionName)
    {
        this.sectionName = sectionName;
    }
}
