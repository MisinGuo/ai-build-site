package com.ruoyi.gamebox.search.vo;

import java.util.List;

/**
 * 搜索结果VO
 * 
 * @author ruoyi
 */
public class SearchResultVO
{
    /** 文章搜索结果 */
    private List<?> articles;

    /** 游戏搜索结果 */
    private List<?> games;

    /** 游戏盒子搜索结果 */
    private List<?> gameBoxes;

    /** 文章总数 */
    private long articleTotal;

    /** 游戏总数 */
    private long gameTotal;

    /** 游戏盒子总数 */
    private long gameBoxTotal;

    /** 搜索关键词 */
    private String keyword;

    /** 搜索耗时（毫秒） */
    private long took;

    public List<?> getArticles()
    {
        return articles;
    }

    public void setArticles(List<?> articles)
    {
        this.articles = articles;
    }

    public List<?> getGames()
    {
        return games;
    }

    public void setGames(List<?> games)
    {
        this.games = games;
    }

    public List<?> getGameBoxes()
    {
        return gameBoxes;
    }

    public void setGameBoxes(List<?> gameBoxes)
    {
        this.gameBoxes = gameBoxes;
    }

    public long getArticleTotal()
    {
        return articleTotal;
    }

    public void setArticleTotal(long articleTotal)
    {
        this.articleTotal = articleTotal;
    }

    public long getGameTotal()
    {
        return gameTotal;
    }

    public void setGameTotal(long gameTotal)
    {
        this.gameTotal = gameTotal;
    }

    public long getGameBoxTotal()
    {
        return gameBoxTotal;
    }

    public void setGameBoxTotal(long gameBoxTotal)
    {
        this.gameBoxTotal = gameBoxTotal;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public long getTook()
    {
        return took;
    }

    public void setTook(long took)
    {
        this.took = took;
    }
}
