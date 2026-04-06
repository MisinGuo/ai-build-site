package com.ruoyi.gamebox.domain.dto;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticle;
import com.ruoyi.gamebox.domain.GbArticle;

/**
 * 主文章发布DTO
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public class GbMasterArticlePublishDTO
{
    /** 主文章ID（用于更新操作） */
    private Long masterArticleId;
    
    /** 语言版本文章ID（用于更新操作） */
    private Long articleId;
    
    /** 主文章信息 */
    private GbMasterArticle masterArticle;
    
    /** 语言版本文章信息 */
    private GbArticle article;
    
    /** 游戏盒子ID列表 */
    private List<Long> gameBoxIds;
    
    /** 游戏ID列表 */
    private List<Long> gameIds;
    
    /** 影视作品ID列表 */
    private List<Long> dramaIds;
    
    /** 是否强制覆盖已存在文件 */
    private Boolean forceOverwrite;

    public Long getMasterArticleId() {
        return masterArticleId;
    }

    public void setMasterArticleId(Long masterArticleId) {
        this.masterArticleId = masterArticleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public GbMasterArticle getMasterArticle() {
        return masterArticle;
    }

    public void setMasterArticle(GbMasterArticle masterArticle) {
        this.masterArticle = masterArticle;
    }

    public GbArticle getArticle() {
        return article;
    }

    public void setArticle(GbArticle article) {
        this.article = article;
    }

    public List<Long> getGameBoxIds() {
        return gameBoxIds;
    }

    public void setGameBoxIds(List<Long> gameBoxIds) {
        this.gameBoxIds = gameBoxIds;
    }

    public List<Long> getGameIds() {
        return gameIds;
    }

    public void setGameIds(List<Long> gameIds) {
        this.gameIds = gameIds;
    }

    public List<Long> getDramaIds() {
        return dramaIds;
    }

    public void setDramaIds(List<Long> dramaIds) {
        this.dramaIds = dramaIds;
    }

    public Boolean getForceOverwrite() {
        return forceOverwrite;
    }

    public void setForceOverwrite(Boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    @Override
    public String toString() {
        return "GbMasterArticlePublishDTO{" +
                "masterArticle=" + masterArticle +
                ", article=" + article +
                ", gameBoxIds=" + gameBoxIds +
                ", gameIds=" + gameIds +
                ", dramaIds=" + dramaIds +
                ", forceOverwrite=" + forceOverwrite +
                '}';
    }
}