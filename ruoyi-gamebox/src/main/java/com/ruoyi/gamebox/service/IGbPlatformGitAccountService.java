package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbPlatformGitAccount;
import java.util.List;
import java.util.Map;

/**
 * 平台Git账号 Service 接口
 */
public interface IGbPlatformGitAccountService {

    List<GbPlatformGitAccount> selectList(GbPlatformGitAccount query);

    GbPlatformGitAccount selectById(Long id);

    /** 查询启用账号（供向导下拉，token已脱敏） */
    List<GbPlatformGitAccount> selectEnabledList();

    int insert(GbPlatformGitAccount account);

    int update(GbPlatformGitAccount account);

    int deleteByIds(Long[] ids);

    /** 设置默认账号（清除其他默认后设置当前） */
    int setDefault(Long id);

    /** 验证账号 Token 是否可用 */
    Map<String, Object> verify(Long id);
}
