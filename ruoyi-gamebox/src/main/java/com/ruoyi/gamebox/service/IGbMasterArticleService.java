package com.ruoyi.gamebox.service;

import java.util.List;
import com.ruoyi.gamebox.domain.GbMasterArticle;

/**
 * 主文章内容Service接口
 * 
 * @author ruoyi
 * @date 2026-01-12
 */
public interface IGbMasterArticleService 
{
    /**
     * 查询主文章内容
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    public GbMasterArticle selectGbMasterArticleById(Long id);

    /**
     * 查询主文章内容列表
     * 
     * @param gbMasterArticle 主文章内容
     * @return 主文章内容集合
     */
    public List<GbMasterArticle> selectGbMasterArticleList(GbMasterArticle gbMasterArticle);

    /**
     * 新增主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    public int insertGbMasterArticle(GbMasterArticle gbMasterArticle);

    /**
     * 修改主文章内容
     * 
     * @param gbMasterArticle 主文章内容
     * @return 结果
     */
    public int updateGbMasterArticle(GbMasterArticle gbMasterArticle);

    /**
     * 批量删除主文章内容
     * 
     * @param ids 需要删除的主文章内容主键集合
     * @return 结果
     */
    public int deleteGbMasterArticleByIds(Long[] ids);

    /**
     * 删除主文章内容信息
     * 
     * @param id 主文章内容主键
     * @return 结果
     */
    public int deleteGbMasterArticleById(Long id);

    // 注释：content_key字段已从表中移除
    /**
     * 根据内容标识查询主文章
     * 
     * @param contentKey 内容标识
     * @return 主文章内容
     */
    // public GbMasterArticle selectGbMasterArticleByContentKey(String contentKey);

    /**
     * 查询主文章详情（包含所有关联信息）
     * 
     * @param id 主文章内容主键
     * @return 主文章内容
     */
    public GbMasterArticle selectGbMasterArticleWithAssociations(Long id);

    // 注释：content_key字段已从表中移除
    /**
     * 检查内容标识是否已存在
     * 
     * @param contentKey 内容标识
     * @param excludeId 排除的ID(用于更新时)
     * @return 是否存在
     */
    // public boolean checkContentKeyExists(String contentKey, Long excludeId);

    /**
     * 获取主文章统计信息
     * 
     * @param id 主文章ID
     * @return 统计信息（包含语言版本数量等）
     */
    public GbMasterArticle getMasterArticleStatistics(Long id);

    /**
     * 为主文章创建多语言版本
     * 
     * @param masterArticleId 主文章ID
     * @param languageCodes 语言代码列表
     * @return 创建成功的数量
     */
    public int createLanguageVersions(Long masterArticleId, List<String> languageCodes);

    /**
     * 同步主文章的关联关系到语言版本
     * 
     * @param masterArticleId 主文章ID
     * @return 同步结果
     */
    public boolean syncAssociationsToLanguageVersions(Long masterArticleId);

    // 注释：主文章表已删除 status 字段，批量更新状态功能已废弃
    /**
     * 批量更新主文章状态（已废弃）
     * 
     * @param ids 主文章ID列表
     * @param status 状态
     * @param operatorBy 操作人
     * @return 更新数量
     */
    // public int batchUpdateStatus(Long[] ids, String status, String operatorBy);

    /**
     * 根据类别统计主文章数量
     * 
     * @param categoryId 类别ID
     * @return 数量
     */
    public int countByCategoryId(Long categoryId);

    /**
     * 保存主文章为草稿
     * 
     * @param publishDTO 发布数据传输对象
     * @return 保存结果
     */
    public com.ruoyi.common.core.domain.AjaxResult saveMasterArticleDraft(com.ruoyi.gamebox.domain.dto.GbMasterArticlePublishDTO publishDTO);

    /**
     * 更新主文章草稿
     * 
     * @param publishDTO 发布数据传输对象
     * @return 更新结果
     */
    public com.ruoyi.common.core.domain.AjaxResult updateMasterArticleDraft(com.ruoyi.gamebox.domain.dto.GbMasterArticlePublishDTO publishDTO);



    /**
     * 获取主文章编辑数据（包含默认语言版本内容）
     * 
     * @param masterArticleId 主文章ID
     * @return 编辑数据
     */
    public com.ruoyi.common.core.domain.AjaxResult getMasterArticleEditData(Long masterArticleId);

    // ==================== 排除管理 ====================

    /**
     * 排除默认主文章（对某个网站不可见）
     * 
     * @param siteId 网站ID
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int excludeDefaultMasterArticle(Long siteId, Long masterArticleId);

    /**
     * 恢复默认主文章（取消排除）
     * 
     * @param siteId 网站ID
     * @param masterArticleId 主文章ID
     * @return 结果
     */
    public int restoreDefaultMasterArticle(Long siteId, Long masterArticleId);

    /**
     * 获取主文章被哪些网站排除
     * 
     * @param masterArticleId 主文章ID
     * @return 被排除的网站ID列表
     */
    public List<Long> getExcludedSitesByMasterArticle(Long masterArticleId);

    /**
     * 批量管理主文章的排除网站
     * 
     * @param masterArticleId 主文章ID
     * @param excludedSiteIds 要排除的网站ID列表
     * @return 操作数量（新增+删除）
     */
    public int manageDefaultMasterArticleExclusions(Long masterArticleId, List<Long> excludedSiteIds);
}