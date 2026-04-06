package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.GbPlatformCfAccount;
import java.util.List;
import java.util.Map;

/**
 * 平台Cloudflare账号 Service 接口
 */
public interface IGbPlatformCfAccountService {

    List<GbPlatformCfAccount> selectList(GbPlatformCfAccount query);

    GbPlatformCfAccount selectById(Long id);

    /** 查询启用账号（供向导下拉，token已脱敏） */
    List<GbPlatformCfAccount> selectEnabledList();

    int insert(GbPlatformCfAccount account);

    int update(GbPlatformCfAccount account);

    int deleteByIds(Long[] ids);

    /** 设置默认账号 */
    int setDefault(Long id);

    /** 验证账号凭据是否可用 */
    Map<String, Object> verify(Long id);
}
