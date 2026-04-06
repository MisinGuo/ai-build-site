package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主文章-游戏绑定对象 gb_master_article_game_binding
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public class GbMasterArticleGameBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 绑定ID */
    private Long id;

    /** 主文章ID */
    private Long masterArticleId;

    /** 游戏ID */
    private Long gameId;

    /** 是否启用：0-否 1-是 */
    private String isActive;

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

    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("masterArticleId", getMasterArticleId())
            .append("gameId", getGameId())
            .append("isActive", getIsActive())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
