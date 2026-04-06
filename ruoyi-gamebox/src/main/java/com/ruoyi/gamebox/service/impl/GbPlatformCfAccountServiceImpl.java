package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbPlatformCfAccount;
import com.ruoyi.gamebox.mapper.GbPlatformCfAccountMapper;
import com.ruoyi.gamebox.service.IGbPlatformCfAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 平台Cloudflare账号 Service 实现
 */
@Service
public class GbPlatformCfAccountServiceImpl implements IGbPlatformCfAccountService {

    @Autowired
    private GbPlatformCfAccountMapper mapper;

    @Override
    public List<GbPlatformCfAccount> selectList(GbPlatformCfAccount query) {
        return mapper.selectList(query);
    }

    @Override
    public GbPlatformCfAccount selectById(Long id) {
        GbPlatformCfAccount account = mapper.selectById(id);
        if (account != null) {
            account.setApiToken(maskToken(account.getApiToken()));
        }
        return account;
    }

    @Override
    public List<GbPlatformCfAccount> selectEnabledList() {
        return mapper.selectEnabledList();
    }

    @Override
    public int insert(GbPlatformCfAccount account) {
        account.setCreateBy(SecurityUtils.getUsername());
        account.setCreateTime(DateUtils.getNowDate());
        if (Integer.valueOf(1).equals(account.getIsDefault())) {
            mapper.clearDefault();
        }
        return mapper.insert(account);
    }

    @Override
    public int update(GbPlatformCfAccount account) {
        account.setUpdateBy(SecurityUtils.getUsername());
        account.setUpdateTime(DateUtils.getNowDate());
        if (Integer.valueOf(1).equals(account.getIsDefault())) {
            mapper.clearDefault();
        }
        return mapper.update(account);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    @Transactional
    public int setDefault(Long id) {
        mapper.clearDefault();
        GbPlatformCfAccount update = new GbPlatformCfAccount();
        update.setId(id);
        update.setIsDefault(1);
        update.setUpdateBy(SecurityUtils.getUsername());
        update.setUpdateTime(DateUtils.getNowDate());
        return mapper.update(update);
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 8) return "****";
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }

    @Override
    public Map<String, Object> verify(Long id) {
        Map<String, Object> result = new HashMap<>();
        GbPlatformCfAccount account = mapper.selectById(id);
        if (account == null) {
            result.put("success", false);
            result.put("message", "账号不存在");
            return result;
        }
        String token = account.getApiToken();
        String accountId = account.getAccountId();
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "未配置 API Token");
            return result;
        }
        try {
            // 验证 Token 本身
            HttpURLConnection conn = (HttpURLConnection)
                    new URL("https://api.cloudflare.com/client/v4/user/tokens/verify").openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            String body = readBody(code < 400 ? conn.getInputStream() : conn.getErrorStream());
            if (code != 200) {
                result.put("success", false);
                result.put("message", "API Token 无效（HTTP " + code + "）");
                return result;
            }
            // 验证对该账户的访问权限
            if (accountId != null && !accountId.isEmpty()) {
                HttpURLConnection conn2 = (HttpURLConnection)
                        new URL("https://api.cloudflare.com/client/v4/accounts/" + accountId).openConnection();
                conn2.setRequestProperty("Authorization", "Bearer " + token);
                conn2.setConnectTimeout(10000);
                conn2.setReadTimeout(10000);
                int code2 = conn2.getResponseCode();
                String body2 = readBody(code2 < 400 ? conn2.getInputStream() : conn2.getErrorStream());
                if (code2 == 200) {
                    String cfName = extractJsonField(body2, "name");
                    result.put("success", true);
                    result.put("message", "验证通过" + (cfName != null ? "，CF账户名：" + cfName : ""));
                } else {
                    result.put("success", false);
                    result.put("message", "Token 有效，但无权访问该 Account ID（HTTP " + code2 + "）");
                }
            } else {
                result.put("success", true);
                result.put("message", "API Token 验证通过（未配置 Account ID）");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "连接异常：" + e.getMessage());
        }
        return result;
    }

    private String readBody(InputStream is) {
        if (is == null) return "";
        try (Scanner sc = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
            return sc.hasNext() ? sc.next() : "";
        }
    }

    private String extractJsonField(String json, String field) {
        if (json == null) return null;
        String key = "\"" + field + "\":\"";
        int idx = json.indexOf(key);
        if (idx < 0) return null;
        int start = idx + key.length();
        int end = json.indexOf('"', start);
        return end > start ? json.substring(start, end) : null;
    }
}
