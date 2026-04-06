package com.ruoyi.web.controller.gamebox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.GbProxyConfig;
import com.ruoyi.gamebox.service.IGbProxyConfigService;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;

/**
 * 系统功能代理配置 Controller
 * 功能条目为预置数据，仅允许修改代理参数，不允许新增/删除。
 */
@RestController
@RequestMapping("/gamebox/proxy")
public class GbProxyConfigController extends BaseController
{
    @Autowired
    private IGbProxyConfigService gbProxyConfigService;

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;

    /**
     * 查询功能代理配置列表（全量，按分组排序）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:proxy:list')")
    @GetMapping("/list")
    public AjaxResult list(GbProxyConfig gbProxyConfig)
    {
        List<GbProxyConfig> list = gbProxyConfigService.selectGbProxyConfigList(gbProxyConfig);
        return success(list);
    }

    /**
     * 获取单条代理配置详情
     */
    @PreAuthorize("@ss.hasPermi('gamebox:proxy:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gbProxyConfigService.selectGbProxyConfigById(id));
    }

    /**
     * 修改代理配置（仅允许修改代理参数）
     */
    @PreAuthorize("@ss.hasPermi('gamebox:proxy:edit')")
    @Log(title = "代理配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GbProxyConfig gbProxyConfig)
    {
        gbProxyConfig.setUpdateBy(getUsername());
        int rows = gbProxyConfigService.updateGbProxyConfig(gbProxyConfig);
        if (rows > 0) {
            proxyConfigSupport.invalidateCache(gbProxyConfig.getFeatureKey());
        }
        return toAjax(rows);
    }

    /**
     * 批量修改代理配置
     */
    @PreAuthorize("@ss.hasPermi('gamebox:proxy:edit')")
    @Log(title = "代理配置批量更新", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public AjaxResult batchEdit(@RequestBody List<GbProxyConfig> configs)
    {
        String username = getUsername();
        for (GbProxyConfig config : configs)
        {
            config.setUpdateBy(username);
        }
        int count = gbProxyConfigService.batchUpdateGbProxyConfig(configs);
        // 批量清除所有涉及功能的缓存
        configs.forEach(c -> {
            if (c.getFeatureKey() != null) {
                proxyConfigSupport.invalidateCache(c.getFeatureKey());
            }
        });
        return success("已更新 " + count + " 条代理配置");
    }

    /**
     * 测试指定功能的代理连通性
     * 鼓励在开发环境用于验证代理配置是否实生效
     * 日志中会输出 [PROXY TEST] / [PROXY ON] / [PROXY OFF] 标识
     */
    @PreAuthorize("@ss.hasPermi('gamebox:proxy:query')")
    @GetMapping("/{id}/test-connection")
    public AjaxResult testConnection(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String testUrl)
    {
        GbProxyConfig config = gbProxyConfigService.selectGbProxyConfigById(id);
        if (config == null) {
            return error("配置不存在");
        }
        GbProxyConfigSupport.ProxyTestResult result = proxyConfigSupport.testConnectivity(config.getFeatureKey(), testUrl);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("featureKey", config.getFeatureKey());
        data.put("featureName", config.getFeatureName());
        data.put("proxyActive", result.proxyActive);
        data.put("proxyDesc", result.proxyDesc);
        data.put("testUrl", result.testUrl);
        data.put("success", result.success);
        data.put("httpStatus", result.httpStatus);
        data.put("elapsedMs", result.elapsedMs);
        data.put("errorMessage", result.errorMessage);
        return success(data);
    }
}
