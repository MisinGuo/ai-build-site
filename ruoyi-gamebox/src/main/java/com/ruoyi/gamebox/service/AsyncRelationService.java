package com.ruoyi.gamebox.service;

/**
 * 异步关联处理服务
 * 专门处理耗时的异步关联操作
 * 
 * @author ruoyi
 * @date 2025-12-29
 */
public interface AsyncRelationService {
    
    /**
     * 异步添加盒子中的游戏到网站
     * 
     * @param siteId 网站ID
     * @param boxId 盒子ID
     * @param username 操作用户
     */
    void asyncAddBoxGamesToSite(Long siteId, Long boxId, String username);
}
