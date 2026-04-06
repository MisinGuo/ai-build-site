package com.ruoyi.gamebox.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gamebox.domain.GbSite;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.service.IGbSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 网站Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbSiteServiceImpl implements IGbSiteService
{
    @Autowired
    private GbSiteMapper gbSiteMapper;

    /**
     * 查询网站
     * 
     * @param id 网站主键
     * @return 网站
     */
    @Override
    public GbSite selectGbSiteById(Long id)
    {
        return gbSiteMapper.selectGbSiteById(id);
    }

    /**
     * 查询网站列表
     * 
     * @param gbSite 网站
     * @return 网站
     */
    @Override
    public List<GbSite> selectGbSiteList(GbSite gbSite)
    {
        return gbSiteMapper.selectGbSiteList(gbSite);
    }

    /**
     * 新增网站
     * 
     * @param gbSite 网站
     * @return 结果
     */
    @Override
    public int insertGbSite(GbSite gbSite)
    {
        gbSite.setCreateTime(DateUtils.getNowDate());
        int result = gbSiteMapper.insertGbSite(gbSite);
        
        // 获取当前用户ID并创建用户-网站关联
        Long currentUserId = gbSite.getUserId() != null ? gbSite.getUserId() : SecurityUtils.getUserId();
        if (currentUserId != null && result > 0) {
            gbSiteMapper.insertUserSiteRelation(currentUserId, gbSite.getId(), 0);
        }
        
        return result;
    }

    /**
     * 修改网站
     * 
     * @param gbSite 网站
     * @return 结果
     */
    @Override
    public int updateGbSite(GbSite gbSite)
    {
        gbSite.setUpdateTime(DateUtils.getNowDate());
        return gbSiteMapper.updateGbSite(gbSite);
    }

    /**
     * 批量删除网站
     * 
     * @param ids 需要删除的网站主键
     * @return 结果
     */
    @Override
    public int deleteGbSiteByIds(Long[] ids)
    {
        return gbSiteMapper.deleteGbSiteByIds(ids);
    }

    /**
     * 删除网站信息
     * 
     * @param id 网站主键
     * @return 结果
     */
    @Override
    public int deleteGbSiteById(Long id)
    {
        return gbSiteMapper.deleteGbSiteById(id);
    }

    /**
     * 校验网站域名是否唯一
     * 
     * @param gbSite 网站信息
     * @return 结果
     */
    @Override
    public boolean checkDomainUnique(GbSite gbSite)
    {
        Long id = StringUtils.isNull(gbSite.getId()) ? -1L : gbSite.getId();
        GbSite info = gbSiteMapper.checkDomainUnique(gbSite.getDomain());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue())
        {
            return false;
        }
        return true;
    }

    /**
     * 按当前登录用户过滤，只返回该用户有权限访问的站点
     */
    @Override
    public List<GbSite> selectGbSiteListForCurrentUser(GbSite gbSite)
    {
        Long userId = SecurityUtils.getUserId();
        return gbSiteMapper.selectGbSiteListByUserId(gbSite, userId);
    }

    /**
     * 为新用户自动创建个人默认站点并建立关联
     */
    @Override
    public void createPersonalSiteForUser(Long userId, String userName)
    {
        GbSite personal = new GbSite();
        personal.setName(userName + "的个人默认站点");
        personal.setCode("_personal_" + userId);
        personal.setDomain("_personal_" + userId);
        personal.setSiteType("personal");
        personal.setIsPersonal(1);
        personal.setSortOrder(-1);
        personal.setStatus("1");
        personal.setDelFlag("0");
        personal.setDefaultLocale("zh-CN");
        personal.setCreateBy(userName);
        personal.setCreateTime(DateUtils.getNowDate());
        gbSiteMapper.insertGbSite(personal);
        // 将个人站点设为该用户的默认站点
        gbSiteMapper.insertUserSiteRelation(userId, personal.getId(), 1);
    }

    /**
     * 根据站点ID解析该站点对应的用户（任取一位），并返回该用户的个人默认站点ID（如果存在）
     */
    @Override
    public Long resolvePersonalSiteIdForSite(Long siteId)
    {
        if (siteId == null) {
            return null;
        }
        try {
            Long userId = gbSiteMapper.selectAnyUserIdBySiteId(siteId);
            if (userId == null) {
                return null;
            }
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(userId);
            return personalSiteId;
        } catch (Exception e) {
            return null;
        }
    }
}
