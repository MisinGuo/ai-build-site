package com.ruoyi.web.controller.gamebox.dto;

import java.util.List;

/**
 * 主文章关联操作DTO
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public class GbMasterArticleAssociationDTO 
{
    /** 主文章ID */
    private Long masterArticleId;

    /** GameBox ID列表 */
    private List<Long> gameBoxIds;

    /** 游戏ID列表 */
    private List<Long> gameIds;

    /** 剧集ID列表 */
    private List<Long> dramaIds;

    /** 关联来源 */
    private String relationSource;

    /** 关联类型 */
    private String relationType;

    // Getters and Setters

    public Long getMasterArticleId() {
        return masterArticleId;
    }

    public void setMasterArticleId(Long masterArticleId) {
        this.masterArticleId = masterArticleId;
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

    public String getRelationSource() {
        return relationSource;
    }

    public void setRelationSource(String relationSource) {
        this.relationSource = relationSource;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return "GbMasterArticleAssociationDTO{" +
                "masterArticleId=" + masterArticleId +
                ", gameBoxIds=" + gameBoxIds +
                ", gameIds=" + gameIds +
                ", dramaIds=" + dramaIds +
                ", relationSource='" + relationSource + '\'' +
                ", relationType='" + relationType + '\'' +
                '}';
    }
}