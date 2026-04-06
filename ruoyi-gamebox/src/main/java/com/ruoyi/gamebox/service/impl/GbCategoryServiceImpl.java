package com.ruoyi.gamebox.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.gamebox.mapper.GbCategoryMapper;
import com.ruoyi.gamebox.mapper.GbCategoryTypeMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbCategory;
import com.ruoyi.gamebox.domain.GbCategoryType;
import com.ruoyi.gamebox.service.IGbCategoryService;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;

/**
 * 分类管理Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbCategoryServiceImpl implements IGbCategoryService 
{
    @Autowired
    private GbCategoryMapper gbCategoryMapper;

    @Autowired
    private GbCategoryTypeMapper gbCategoryTypeMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    /** 将当前用户的个人默认站点 ID 注入到查询参数中，替代旧版 siteId=0 的全局默认逻辑 */
    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {
            // 非 HTTP 请求上下文（如定时任务）时跳过
        }
    }

    /**
     * 查询分类
     * 
     * @param id 分类主键
     * @return 分类
     */
    @Override
    public GbCategory selectGbCategoryById(Long id)
    {
        return gbCategoryMapper.selectGbCategoryById(id);
    }

    /**
     * 查询分类列表
     * 
     * @param gbCategory 分类
     * @return 分类
     */
    @Override
    public List<GbCategory> selectGbCategoryList(GbCategory gbCategory)
    {
        relatedModeSiteValidator.validate(gbCategory.getQueryMode(), gbCategory.getSiteId());
        injectPersonalSiteId(gbCategory);
        return gbCategoryMapper.selectGbCategoryList(gbCategory);
    }

    /**
     * 查询可见的分类列表（用于编辑器选择）
     * 
     * @param gbCategory 分类查询条件
     * @return 可见的分类集合
     */
    @Override
    public List<GbCategory> selectVisibleGbCategoryList(GbCategory gbCategory)
    {
        relatedModeSiteValidator.validate(gbCategory.getQueryMode(), gbCategory.getSiteId());
        injectPersonalSiteId(gbCategory);
        return gbCategoryMapper.selectVisibleGbCategoryList(gbCategory);
    }

    /**
     * 新增分类
     * 注意：全局分类（site_id=0）作为默认配置数据，新创建的分类会自动继承所有全局分类的关联配置
     * 创建者网站不需要创建关联记录，因为通过 site_id 字段已经天然关联
     * 
     * @param gbCategory 分类
     * @return 结果
     */
    @Override
    @Transactional
    public int insertGbCategory(GbCategory gbCategory)
    {
        gbCategory.setCreateTime(DateUtils.getNowDate());
        int result = gbCategoryMapper.insertGbCategory(gbCategory);
        
        // 不再为创建者网站创建关联记录
        // 因为在关联模式查询中，通过 c.site_id = #{siteId} 已经自动包含创建者的分类
        // 关联表只用于存储"跨站共享"的关系
        
        return result;
    }

    /**
     * 修改分类
     * 
     * @param gbCategory 分类
     * @return 结果
     */
    @Override
    public int updateGbCategory(GbCategory gbCategory)
    {
        gbCategory.setUpdateTime(DateUtils.getNowDate());
        
        // 获取修改前的分类信息
        GbCategory oldCategory = gbCategoryMapper.selectGbCategoryById(gbCategory.getId());
        if (oldCategory == null) {
            return 0;
        }
        
        // 检查是否修改了所属网站
        Long oldSiteId = oldCategory.getSiteId();
        Long newSiteId = gbCategory.getSiteId();
        boolean siteIdChanged = (oldSiteId == null && newSiteId != null) || 
                                (oldSiteId != null && !oldSiteId.equals(newSiteId));
        
        // 先更新当前分类
        int result = gbCategoryMapper.updateGbCategory(gbCategory);
        
        // 如果所属网站发生变化，需要同步更新所有子分类
        if (result > 0 && siteIdChanged) {
            updateChildrenSiteId(gbCategory.getId(), newSiteId);
        }
        
        return result;
    }
    
    /**
     * 递归更新所有子分类的所属网站
     * 
     * @param parentId 父分类ID
     * @param newSiteId 新的网站ID
     */
    private void updateChildrenSiteId(Long parentId, Long newSiteId) {
        // 查询所有直接子分类
        GbCategory queryParam = new GbCategory();
        queryParam.setParentId(parentId);
        List<GbCategory> children = gbCategoryMapper.selectGbCategoryList(queryParam);
        
        // 更新每个子分类的网站ID
        for (GbCategory child : children) {
            child.setSiteId(newSiteId);
            child.setUpdateTime(DateUtils.getNowDate());
            gbCategoryMapper.updateGbCategory(child);
            
            // 递归更新孙分类
            updateChildrenSiteId(child.getId(), newSiteId);
        }
    }

    /**
     * 批量删除分类
     * 
     * @param ids 需要删除的分类主键
     * @return 结果
     */
    @Override
    public int deleteGbCategoryByIds(Long[] ids)
    {
        return gbCategoryMapper.deleteGbCategoryByIds(ids);
    }

    /**
     * 删除分类信息
     * 
     * @param id 分类主键
     * @return 结果
     */
    @Override
    public int deleteGbCategoryById(Long id)
    {
        return gbCategoryMapper.deleteGbCategoryById(id);
    }

    /**
     * 获取分类类型选项列表（从数据库读取）
     * 
     * @return 分类类型选项列表
     */
    @Override
    public List<Map<String, Object>> getCategoryTypeOptions()
    {
        List<Map<String, Object>> options = new ArrayList<>();
        
        // 从数据库查询启用的分类类型
        GbCategoryType queryParam = new GbCategoryType();
        queryParam.setStatus("0"); // 只查询正常状态的
        List<GbCategoryType> categoryTypes = gbCategoryTypeMapper.selectGbCategoryTypeList(queryParam);
        
        // 转换为前端需要的格式
        for (GbCategoryType type : categoryTypes) {
            Map<String, Object> option = new HashMap<>();
            option.put("value", type.getValue());
            option.put("label", type.getLabel());
            option.put("tagType", type.getTagType());
            option.put("description", type.getDescription());
            options.add(option);
        }
        
        return options;
    }

    /**
     * 通过盒子ID查询关联的所有分类
     * 
     * @param boxId 盒子ID
     * @return 分类集合
     */
    @Override
    public List<GbCategory> selectCategoriesByBoxId(Long boxId)
    {
        return gbCategoryMapper.selectCategoriesByBoxId(boxId);
    }

    /**
     * 通过游戏ID查询关联的所有分类
     * 
     * @param gameId 游戏ID
     * @return 分类集合
     */
    @Override
    public List<GbCategory> selectCategoriesByGameId(Long gameId)
    {
        return gbCategoryMapper.selectCategoriesByGameId(gameId);
    }

    /**
     * 通过文章ID查询关联的所有分类
     * 
     * @param articleId 文章ID
     * @return 分类集合
     */
    @Override
    public List<GbCategory> selectCategoriesByArticleId(Long articleId)
    {
        return gbCategoryMapper.selectCategoriesByArticleId(articleId);
    }

    /**
     * 查询文章板块列表
     * 
     * @param siteId 网站ID，null表示查询所有板块
     * @return 板块列表
     */
    @Override
    public List<GbCategory> selectArticleSections(Long siteId)
    {
        Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
        return gbCategoryMapper.selectArticleSections(siteId, personalSiteId);
    }

    /**
     * 通过板块ID查询分类列表
     * 
     * @param sectionId 板块ID
     * @return 分类集合
     */
    @Override
    public List<GbCategory> selectCategoriesBySection(Long sectionId)
    {
        return gbCategoryMapper.selectCategoriesBySection(sectionId);
    }
}
