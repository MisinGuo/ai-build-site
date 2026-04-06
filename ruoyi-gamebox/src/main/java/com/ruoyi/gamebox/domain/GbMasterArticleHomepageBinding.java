package com.ruoyi.gamebox.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主文章主页绑定对象 gb_master_article_homepage_binding
 * 
 * @author ruoyi
 * @date 2026-01-13
 */
public class GbMasterArticleHomepageBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 主文章ID */
    @Excel(name = "主文章ID")
    private Long masterArticleId;

    /** 游戏ID */
    @Excel(name = "游戏ID")
    private Long gameId;

    /** 游戏盒子ID */
    @Excel(name = "游戏盒子ID")
    private Long gameBoxId;

    /** 主文章标题（查询时关联显示） */
    private String title;

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

    public void setGameBoxId(Long gameBoxId) 
    {
        this.gameBoxId = gameBoxId;
    }

    public Long getGameBoxId() 
    {
        return gameBoxId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("masterArticleId", getMasterArticleId())
            .append("gameId", getGameId())
            .append("gameBoxId", getGameBoxId())
            .append("title", getTitle())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
