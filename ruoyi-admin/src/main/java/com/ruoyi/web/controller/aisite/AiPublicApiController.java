package com.ruoyi.web.controller.aisite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.aisite.domain.AiContentItem;
import com.ruoyi.aisite.domain.AiContentType;
import com.ruoyi.aisite.domain.AiSite;
import com.ruoyi.aisite.service.IAiContentItemService;
import com.ruoyi.aisite.service.IAiContentTypeService;
import com.ruoyi.aisite.service.IAiSiteService;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 公开内容 API — 前端站点消费，无需鉴权
 * 映射路径已在 SecurityConfig 中配置 /api/public/** permitAll
 */
@RestController
@RequestMapping("/api/public")
public class AiPublicApiController
{
    @Autowired
    private IAiContentItemService contentItemService;

    @Autowired
    private IAiContentTypeService contentTypeService;

    @Autowired
    private IAiSiteService siteService;

    /**
     * 根据请求 Host 获取站点信息
     * GET /api/public/site-info
     */
    @GetMapping("/site-info")
    public AjaxResult getSiteInfo(HttpServletRequest request)
    {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isEmpty())
        {
            host = request.getServerName();
        }
        // 去除端口号
        if (host.contains(":"))
        {
            host = host.substring(0, host.indexOf(":"));
        }
        AiSite site = siteService.selectAiSiteByDomain(host);
        if (site == null)
        {
            return AjaxResult.error("站点不存在");
        }
        return AjaxResult.success(site);
    }

    /**
     * 根据站点 id 获取站点信息
     * GET /api/public/site-info/{siteId}
     */
    @GetMapping("/site-info/{siteId}")
    public AjaxResult getSiteInfoById(@PathVariable Long siteId)
    {
        AiSite site = siteService.selectAiSiteById(siteId);
        if (site == null)
        {
            return AjaxResult.error("站点不存在");
        }
        return AjaxResult.success(site);
    }

    /**
     * 内容列表（含类型 schema）
     * GET /api/public/content-items?siteId=&typeCode=&page=1&size=20
     */
    @GetMapping("/content-items")
    public AjaxResult listContentItems(
            @RequestParam Long siteId,
            @RequestParam(required = false) String typeCode,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size)
    {
        // 限制单页最大数量，防止滥用
        if (size > 100) { size = 100; }

        AiContentItem query = new AiContentItem();
        query.setSiteId(siteId);
        query.setTypeCode(typeCode);
        query.setPublishStatus("published");

        PageHelper.startPage(page, size);
        List<AiContentItem> items = contentItemService.selectAiContentItemList(query);
        PageInfo<AiContentItem> pageInfo = new PageInfo<>(items);

        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("pages", pageInfo.getPages());

        // 附带内容类型 schema
        if (typeCode != null && !typeCode.isEmpty())
        {
            AiContentType contentType = contentTypeService.selectAiContentTypeByCode(siteId, typeCode);
            result.put("contentType", contentType);
        }

        return AjaxResult.success(result);
    }

    /**
     * 内容详情（含类型 schema，并累加浏览次数）
     * GET /api/public/content-items/{slug}?siteId=&typeCode=
     */
    @GetMapping("/content-items/{slug}")
    public AjaxResult getContentItem(
            @PathVariable String slug,
            @RequestParam Long siteId,
            @RequestParam(required = false) String typeCode)
    {
        AiContentItem item;
        if (typeCode != null && !typeCode.isEmpty())
        {
            item = contentItemService.selectAiContentItemBySlug(siteId, typeCode, slug);
        }
        else
        {
            item = null;
            // 遍历 slug 匹配（不推荐，仅兜底）
        }

        if (item == null)
        {
            return AjaxResult.error(404, "内容不存在");
        }

        // 累加浏览次数（异步不阻塞响应）
        contentItemService.incrementViewCount(item.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("item", item);

        if (typeCode != null && !typeCode.isEmpty())
        {
            AiContentType contentType = contentTypeService.selectAiContentTypeByCode(siteId, typeCode);
            result.put("contentType", contentType);
        }

        return AjaxResult.success(result);
    }
}
