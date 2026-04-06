package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSite;

/**
 * 网站Service接口
 * 
 * @author ruoyi
 */
public interface IGbSiteService
{
    /**
     * 查询网站
     * 
     * @param id 网站主键
     * @return 网站
     */
    public GbSite selectGbSiteById(Long id);

    /**
     * 查询网站列表
     * 
     * @param gbSite 网站
     * @return 网站集合
     */
    public List<GbSite> selectGbSiteList(GbSite gbSite);
    /**
     * 按当前登录用户过滤，只返回该用户有权限访问的站点，并带出 is_default 标志
     */
    public List<GbSite> selectGbSiteListForCurrentUser(GbSite gbSite);

    /**
     * 为新用户自动创建个人默认站点并建立关联
     *
     * @param userId   用户ID
     * @param userName 用户名（用于站点命名）
     */
    public void createPersonalSiteForUser(Long userId, String userName);
    /**
     * 根据站点ID解析该站点对应的用户（任取一位），并返回该用户的个人默认站点ID（如果存在）
     * @param siteId 站点ID
     * @return 个人默认站点ID或null
     */
    public Long resolvePersonalSiteIdForSite(Long siteId);
    /**
     * 新增网站
     * 
     * @param gbSite 网站
     * @return 结果
     */
    public int insertGbSite(GbSite gbSite);

    /**
     * 修改网站
     * 
     * @param gbSite 网站
     * @return 结果
     */
    public int updateGbSite(GbSite gbSite);

    /**
     * 批量删除网站
     * 
     * @param ids 需要删除的网站主键集合
     * @return 结果
     */
    public int deleteGbSiteByIds(Long[] ids);

    /**
     * 删除网站信息
     * 
     * @param id 网站主键
     * @return 结果
     */
    public int deleteGbSiteById(Long id);

    /**
     * 校验网站域名是否唯一
     * 
     * @param gbSite 网站信息
     * @return 结果
     */
    public boolean checkDomainUnique(GbSite gbSite);
}
