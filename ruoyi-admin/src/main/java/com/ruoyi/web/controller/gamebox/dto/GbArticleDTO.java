package com.ruoyi.web.controller.gamebox.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.gamebox.domain.GbArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章数据传输对象（包含关联关系）
 * 用于前端传递文章数据时，包含游戏和游戏盒子的ID数组
 * 
 * @author ruoyi
 * @date 2025-01-18
 */
public class GbArticleDTO extends GbArticle
{
    /** 关联的游戏盒子ID数组 */
    @JsonDeserialize(using = EmptyStringToListDeserializer.class)
    private List<Long> gameBoxIds = new ArrayList<>();
    
    /** 关联的游戏ID数组 */
    @JsonDeserialize(using = EmptyStringToListDeserializer.class)
    private List<Long> gameIds = new ArrayList<>();
    
    /** 关联的短剧ID数组 */
    @JsonDeserialize(using = EmptyStringToListDeserializer.class)
    private List<Long> dramaIds = new ArrayList<>();
    
    /** 标识内容是否从对象存储加载（数据库content字段已清理） */
    private Boolean contentLoadedFromStorage = false;

    public List<Long> getGameBoxIds()
    {
        return gameBoxIds == null ? new ArrayList<>() : gameBoxIds;
    }

    public void setGameBoxIds(List<Long> gameBoxIds)
    {
        this.gameBoxIds = gameBoxIds == null ? new ArrayList<>() : gameBoxIds;
    }

    public List<Long> getGameIds()
    {
        return gameIds == null ? new ArrayList<>() : gameIds;
    }

    public void setGameIds(List<Long> gameIds)
    {
        this.gameIds = gameIds == null ? new ArrayList<>() : gameIds;
    }
    
    public List<Long> getDramaIds()
    {
        return dramaIds == null ? new ArrayList<>() : dramaIds;
    }

    public void setDramaIds(List<Long> dramaIds)
    {
        this.dramaIds = dramaIds == null ? new ArrayList<>() : dramaIds;
    }
    
    public Boolean getContentLoadedFromStorage()
    {
        return contentLoadedFromStorage;
    }
    
    public void setContentLoadedFromStorage(Boolean contentLoadedFromStorage)
    {
        this.contentLoadedFromStorage = contentLoadedFromStorage;
    }
}
