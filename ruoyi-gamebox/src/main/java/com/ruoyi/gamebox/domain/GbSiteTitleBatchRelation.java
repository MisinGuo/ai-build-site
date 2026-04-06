package com.ruoyi.gamebox.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 网站-标题池批次关联对象 gb_site_title_batch_relations
 * 
 * @author ruoyi
 * @date 2026-01-02
 */
public class GbSiteTitleBatchRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 网站ID */
    private Long siteId;

    /** 标题池批次ID */
    private Long titleBatchId;

    /** 关联类型（shared:跨站共享，exclude:排除） */
    private String relationType;

    /** 是否可见（1:可见，0:隐藏） */
    private String isVisible;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getTitleBatchId() {
        return titleBatchId;
    }

    public void setTitleBatchId(Long titleBatchId) {
        this.titleBatchId = titleBatchId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(String isVisible) {
        this.isVisible = isVisible;
    }
}
