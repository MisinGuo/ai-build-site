package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 矩阵站组实体 ai_matrix_groups
 */
public class AiMatrixGroup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 矩阵组名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 模板站点 ID */
    private Long templateSiteId;
    /** 行业（game/house/course/general...） */
    private String industry;
    /** 关键词列表 JSONB */
    private String keywordList;
    /** 域名模式（如 {keyword}.example.com） */
    private String domainPattern;
    /** 配置覆盖 JSONB */
    private String configOverrides;
    /** 总数量 */
    private Integer totalCount;
    /** 已建数量 */
    private Integer builtCount;
    /** 在线数量 */
    private Integer liveCount;
    /** 状态: draft/building/live/paused */
    private String status;
    /** 创建者 ID */
    private Long creatorId;
    /** 部门 ID */
    private Long deptId;
    private String delFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTemplateSiteId() { return templateSiteId; }
    public void setTemplateSiteId(Long templateSiteId) { this.templateSiteId = templateSiteId; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getKeywordList() { return keywordList; }
    public void setKeywordList(String keywordList) { this.keywordList = keywordList; }
    public String getDomainPattern() { return domainPattern; }
    public void setDomainPattern(String domainPattern) { this.domainPattern = domainPattern; }
    public String getConfigOverrides() { return configOverrides; }
    public void setConfigOverrides(String configOverrides) { this.configOverrides = configOverrides; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getBuiltCount() { return builtCount; }
    public void setBuiltCount(Integer builtCount) { this.builtCount = builtCount; }
    public Integer getLiveCount() { return liveCount; }
    public void setLiveCount(Integer liveCount) { this.liveCount = liveCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
