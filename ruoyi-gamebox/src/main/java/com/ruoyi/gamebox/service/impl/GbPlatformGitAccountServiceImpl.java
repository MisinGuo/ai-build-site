package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.gamebox.domain.GbPlatformGitAccount;
import com.ruoyi.gamebox.mapper.GbPlatformGitAccountMapper;
import com.ruoyi.gamebox.service.IGbPlatformGitAccountService;
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
 * 平台Git账号 Service 实现
 */
@Service
public class GbPlatformGitAccountServiceImpl implements IGbPlatformGitAccountService {

    @Autowired
    private GbPlatformGitAccountMapper mapper;

    @Override
    public List<GbPlatformGitAccount> selectList(GbPlatformGitAccount query) {
        return mapper.selectList(query);
    }

    @Override
    public GbPlatformGitAccount selectById(Long id) {
        GbPlatformGitAccount account = mapper.selectById(id);
        if (account != null) {
            // 脱敏后返回
            account.setToken(maskToken(account.getToken()));
        }
        return account;
    }

    @Override
    public List<GbPlatformGitAccount> selectEnabledList() {
        return mapper.selectEnabledList();
    }

    @Override
    public int insert(GbPlatformGitAccount account) {
        account.setCreateBy(SecurityUtils.getUsername());
        account.setCreateTime(DateUtils.getNowDate());
        if (Integer.valueOf(1).equals(account.getIsDefault())) {
            mapper.clearDefault();
        }
        return mapper.insert(account);
    }

    @Override
    public int update(GbPlatformGitAccount account) {
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
        GbPlatformGitAccount update = new GbPlatformGitAccount();
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
        GbPlatformGitAccount account = mapper.selectById(id);
        if (account == null) {
            result.put("success", false);
            result.put("message", "账号不存在");
            return result;
        }
        String token = account.getToken();
        String provider = account.getProvider();
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "未配置 Token");
            return result;
        }
        try {
            String apiUrl;
            String authHeader;
            if ("gitee".equalsIgnoreCase(provider)) {
                apiUrl = "https://gitee.com/api/v5/user?access_token=" + token;
                authHeader = null;
            } else if ("gitlab".equalsIgnoreCase(provider)) {
                apiUrl = "https://gitlab.com/api/v4/user";
                authHeader = "Bearer " + token;
            } else {
                // 默认 github
                apiUrl = "https://api.github.com/user";
                authHeader = "token " + token;
            }
            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            if (authHeader != null) conn.setRequestProperty("Authorization", authHeader);
            conn.setRequestProperty("User-Agent", "GameBox-Admin");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            int code = conn.getResponseCode();
            String body = readBody(code < 400 ? conn.getInputStream() : conn.getErrorStream());
            if (code == 200) {
                String login = extractJsonField(body, "login");
                if (login == null) login = extractJsonField(body, "username");
                result.put("success", true);
                result.put("message", "验证通过" + (login != null ? "，用户名：" + login : ""));
            } else {
                result.put("success", false);
                result.put("message", "Token 无效（HTTP " + code + "）");
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
