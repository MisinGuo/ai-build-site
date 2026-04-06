package com.ruoyi.gamebox.domain.dto;

import javax.validation.constraints.NotNull;

/**
 * 主页绑定DTO
 * 
 * @author ruoyi
 * @date 2026-01-15
 */
public class HomepageBindingDTO
{
    /** 主文章ID */
    @NotNull(message = "主文章ID不能为空")
    private Long masterArticleId;
    
    /** 游戏ID */
    private Long gameId;
    
    /** 游戏盒子ID */
    private Long gameBoxId;
    
    /** 是否强制绑定（覆盖已有绑定） */
    private Boolean force;

    public HomepageBindingDTO() {
        this.force = false;
    }

    public Long getMasterArticleId() {
        return masterArticleId;
    }

    public void setMasterArticleId(Long masterArticleId) {
        this.masterArticleId = masterArticleId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public Long getGameBoxId() {
        return gameBoxId;
    }

    public void setGameBoxId(Long gameBoxId) {
        this.gameBoxId = gameBoxId;
    }

    public Boolean getForce() {
        return force;
    }

    public void setForce(Boolean force) {
        this.force = force;
    }

    @Override
    public String toString() {
        return "HomepageBindingDTO{" +
                "masterArticleId=" + masterArticleId +
                ", gameId=" + gameId +
                ", gameBoxId=" + gameBoxId +
                ", force=" + force +
                '}';
    }
}
