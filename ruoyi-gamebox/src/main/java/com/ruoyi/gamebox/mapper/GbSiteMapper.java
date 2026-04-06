package com.ruoyi.gamebox.mapper;

import java.util.List;
import com.ruoyi.gamebox.domain.GbSite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 网站Mapper接口
 * 
 * @author ruoyi
 */
@Mapper
public interface GbSiteMapper
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
     * 删除网站
     * 
     * @param id 网站主键
     * @return 结果
     */
    public int deleteGbSiteById(Long id);

    /**
     * 批量删除网站
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteGbSiteByIds(Long[] ids);

    /**
     * 校验网站域名是否唯一
     * 
     * @param domain 网站域名
     * @return 结果
     */
    public GbSite checkDomainUnique(String domain);

    /**
     * 查询指定用户有权限访问的站点列表（含 is_default 标志）
     */
    public List<GbSite> selectGbSiteListByUserId(@Param("site") GbSite site, @Param("userId") Long userId);

    /**
     * 新增用户-站点关联记录
     */
    public int insertUserSiteRelation(@Param("userId") Long userId, @Param("siteId") Long siteId, @Param("isDefault") int isDefault);

    /**
     * 查询某用户的个人默认站点 ID（is_default=1）
     */
    public Long selectPersonalSiteIdByUserId(@Param("userId") Long userId);

    /**
     * 查询与指定站点有关联的任意用户ID（用于从用户找到其个人默认站点）
     */
    public Long selectAnyUserIdBySiteId(@Param("siteId") Long siteId);
}
