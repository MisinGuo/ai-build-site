package com.ruoyi.gamebox.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主文章-游戏关联对象 gb_master_article_games
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public class GbMasterArticleGame extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 主文章内容ID */
    @Excel(name = "主文章内容ID")
    private Long masterArticleId;

    /** 游戏ID */
    @Excel(name = "游戏ID")
    private Long gameId;

    /** 关联来源 */
    @Excel(name = "关联来源", readConverterExp = "manual=人工手动,ai=AI自动,import=批量导入,sync=同步")
    private String relationSource;

    /** 关联类型 */
    @Excel(name = "关联类型", readConverterExp = "primary=主要关联,secondary=次要关联,related=相关推荐,mention=提及")
    private String relationType;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer displayOrder;

    /** AI关联置信度 */
    @Excel(name = "AI关联置信度")
    private BigDecimal confidenceScore;

    /** AI平台ID */
    @Excel(name = "AI平台ID")
    private Long aiPlatformId;

    /** AI关联推理说明 */
    @Excel(name = "AI关联推理说明")
    private String aiReasoning;

    /** AI关联元数据 */
    @Excel(name = "AI关联元数据")
    private String aiMetadata;

    /** 审核状态 */
    @Excel(name = "审核状态", readConverterExp = "0=待审核,1=已通过,2=已拒绝")
    private String reviewStatus;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewedBy;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewedAt;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String reviewNotes;

    /** 是否推荐展示 */
    @Excel(name = "是否推荐展示", readConverterExp = "0=否,1=是")
    private String isFeatured;

    /** 点击次数 */
    @Excel(name = "点击次数")
    private Integer clickCount;

    /** 最后使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后使用时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastUsedAt;

    /** 删除标志 */
    private String delFlag;

    /** 游戏名称 */
    private String gameName;

    // getters and setters
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setMasterArticleId(Long masterArticleId) 
    {
        this.masterArticleId = masterArticleId;
    }

    public Long getMasterArticleId() 
    {
        return masterArticleId;
    }

    public void setGameId(Long gameId) 
    {
        this.gameId = gameId;
    }

    public Long getGameId() 
    {
        return gameId;
    }

    public void setRelationSource(String relationSource) 
    {
        this.relationSource = relationSource;
    }

    public String getRelationSource() 
    {
        return relationSource;
    }

    public void setRelationType(String relationType) 
    {
        this.relationType = relationType;
    }

    public String getRelationType() 
    {
        return relationType;
    }

    public void setDisplayOrder(Integer displayOrder) 
    {
        this.displayOrder = displayOrder;
    }

    public Integer getDisplayOrder() 
    {
        return displayOrder;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) 
    {
        this.confidenceScore = confidenceScore;
    }

    public BigDecimal getConfidenceScore() 
    {
        return confidenceScore;
    }

    public void setAiPlatformId(Long aiPlatformId) 
    {
        this.aiPlatformId = aiPlatformId;
    }

    public Long getAiPlatformId() 
    {
        return aiPlatformId;
    }

    public void setAiReasoning(String aiReasoning) 
    {
        this.aiReasoning = aiReasoning;
    }

    public String getAiReasoning() 
    {
        return aiReasoning;
    }

    public void setAiMetadata(String aiMetadata) 
    {
        this.aiMetadata = aiMetadata;
    }

    public String getAiMetadata() 
    {
        return aiMetadata;
    }

    public void setReviewStatus(String reviewStatus) 
    {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewStatus() 
    {
        return reviewStatus;
    }

    public void setReviewedBy(String reviewedBy) 
    {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewedBy() 
    {
        return reviewedBy;
    }

    public void setReviewedAt(Date reviewedAt) 
    {
        this.reviewedAt = reviewedAt;
    }

    public Date getReviewedAt() 
    {
        return reviewedAt;
    }

    public void setReviewNotes(String reviewNotes) 
    {
        this.reviewNotes = reviewNotes;
    }

    public String getReviewNotes() 
    {
        return reviewNotes;
    }

    public void setIsFeatured(String isFeatured) 
    {
        this.isFeatured = isFeatured;
    }

    public String getIsFeatured() 
    {
        return isFeatured;
    }

    public void setClickCount(Integer clickCount) 
    {
        this.clickCount = clickCount;
    }

    public Integer getClickCount() 
    {
        return clickCount;
    }

    public void setLastUsedAt(Date lastUsedAt) 
    {
        this.lastUsedAt = lastUsedAt;
    }

    public Date getLastUsedAt() 
    {
        return lastUsedAt;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("masterArticleId", getMasterArticleId())
            .append("gameId", getGameId())
            .append("relationSource", getRelationSource())
            .append("relationType", getRelationType())
            .append("displayOrder", getDisplayOrder())
            .append("confidenceScore", getConfidenceScore())
            .append("aiPlatformId", getAiPlatformId())
            .append("aiReasoning", getAiReasoning())
            .append("aiMetadata", getAiMetadata())
            .append("reviewStatus", getReviewStatus())
            .append("reviewedBy", getReviewedBy())
            .append("reviewedAt", getReviewedAt())
            .append("reviewNotes", getReviewNotes())
            .append("isFeatured", getIsFeatured())
            .append("clickCount", getClickCount())
            .append("lastUsedAt", getLastUsedAt())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}