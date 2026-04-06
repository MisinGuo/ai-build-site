package com.ruoyi.aisite.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

/**
 * SEO 指标快照实体 ai_seo_metrics_snapshots
 */
public class AiSeoMetrics extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 站点 ID */
    private Long siteId;
    /** 快照日期（每日一条） */
    private Date snapshotDate;
    /** 已收录页面数 */
    private Integer indexedPages;
    /** 平均排名位置 */
    private BigDecimal avgPosition;
    /** 点击量 */
    private Integer clicks;
    /** 展示量 */
    private Integer impressions;
    /** 点击率 */
    private BigDecimal ctr;
    /** 附加指标 JSONB */
    private String metricsJson;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Date getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(Date snapshotDate) { this.snapshotDate = snapshotDate; }
    public Integer getIndexedPages() { return indexedPages; }
    public void setIndexedPages(Integer indexedPages) { this.indexedPages = indexedPages; }
    public BigDecimal getAvgPosition() { return avgPosition; }
    public void setAvgPosition(BigDecimal avgPosition) { this.avgPosition = avgPosition; }
    public Integer getClicks() { return clicks; }
    public void setClicks(Integer clicks) { this.clicks = clicks; }
    public Integer getImpressions() { return impressions; }
    public void setImpressions(Integer impressions) { this.impressions = impressions; }
    public BigDecimal getCtr() { return ctr; }
    public void setCtr(BigDecimal ctr) { this.ctr = ctr; }
    public String getMetricsJson() { return metricsJson; }
    public void setMetricsJson(String metricsJson) { this.metricsJson = metricsJson; }
}
