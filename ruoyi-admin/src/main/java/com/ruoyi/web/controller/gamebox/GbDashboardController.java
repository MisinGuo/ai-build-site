package com.ruoyi.web.controller.gamebox;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.gamebox.mapper.GbDashboardMapper;

/**
 * 首页仪表盘 Controller
 */
@RestController
@RequestMapping("/gamebox/dashboard")
public class GbDashboardController extends BaseController {

    @Autowired
    private GbDashboardMapper gbDashboardMapper;

    /**
     * 获取首页统计数据（仅返回当前用户有权限的非个人站点数据）
     */
    @GetMapping("/statistics")
    public AjaxResult statistics() {
        Long userId = getUserId();
        Map<String, Long> data = new HashMap<>();
        data.put("totalGames", gbDashboardMapper.countGames(userId));
        data.put("totalBoxes", gbDashboardMapper.countGameBoxes(userId));
        data.put("totalArticles", gbDashboardMapper.countArticles(userId));
        data.put("totalMasterArticles", gbDashboardMapper.countMasterArticles(userId));
        data.put("totalSites", gbDashboardMapper.countSites(userId));
        return AjaxResult.success(data);
    }
}
