package com.ruoyi.gamebox.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.gamebox.mapper.GbStorageConfigMapper;
import com.ruoyi.gamebox.mapper.GbSiteMapper;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.service.IGbStorageConfigService;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import com.ruoyi.gamebox.support.RelatedModeSiteValidator;

// AWS SDK v2
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

// 阿里云 OSS SDK
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

// GitHub Java API
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHBranch;

import java.net.Proxy;

/**
 * 存储配置Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class GbStorageConfigServiceImpl implements IGbStorageConfigService 
{
    @Autowired
    private GbStorageConfigMapper gbStorageConfigMapper;

    @Autowired
    private GbSiteMapper gbSiteMapper;

    @Autowired
    private RelatedModeSiteValidator relatedModeSiteValidator;

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;

    private void injectPersonalSiteId(BaseEntity entity) {
        try {
            Long personalSiteId = gbSiteMapper.selectPersonalSiteIdByUserId(SecurityUtils.getUserId());
            if (personalSiteId != null) {
                entity.getParams().put("personalSiteId", personalSiteId);
            }
        } catch (Exception ignored) {}
    }

    /**
     * 查询存储配置
     * 
     * @param id 配置主键
     * @return 存储配置
     */
    @Override
    public GbStorageConfig selectGbStorageConfigById(Long id)
    {
        return gbStorageConfigMapper.selectGbStorageConfigById(id);
    }

    /**
     * 查询存储配置列表
     * 
     * @param gbStorageConfig 存储配置
     * @return 存储配置
     */
    @Override
    public List<GbStorageConfig> selectGbStorageConfigList(GbStorageConfig gbStorageConfig)
    {
        relatedModeSiteValidator.validate(gbStorageConfig.getQueryMode(), gbStorageConfig.getSiteId());
        injectPersonalSiteId(gbStorageConfig);
        return gbStorageConfigMapper.selectGbStorageConfigList(gbStorageConfig);
    }

    /**
     * 新增存储配置
     * 
     * @param gbStorageConfig 存储配置
     * @return 结果
     */
    @Override
    public int insertGbStorageConfig(GbStorageConfig gbStorageConfig)
    {
        gbStorageConfig.setCreateTime(DateUtils.getNowDate());
        return gbStorageConfigMapper.insertGbStorageConfig(gbStorageConfig);
    }

    /**
     * 修改存储配置
     * 
     * @param gbStorageConfig 存储配置
     * @return 结果
     */
    @Override
    public int updateGbStorageConfig(GbStorageConfig gbStorageConfig)
    {
        gbStorageConfig.setUpdateTime(DateUtils.getNowDate());
        return gbStorageConfigMapper.updateGbStorageConfig(gbStorageConfig);
    }

    /**
     * 批量删除存储配置
     * 
     * @param ids 需要删除的配置主键
     * @return 结果
     */
    @Override
    public int deleteGbStorageConfigByIds(Long[] ids)
    {
        return gbStorageConfigMapper.deleteGbStorageConfigByIds(ids);
    }

    /**
     * 删除存储配置信息
     * 
     * @param id 配置主键
     * @return 结果
     */
    @Override
    public int deleteGbStorageConfigById(Long id)
    {
        return gbStorageConfigMapper.deleteGbStorageConfigById(id);
    }

    /**
     * 验证存储配置是否正确
     * 
     * @param gbStorageConfig 存储配置
     * @return 验证结果消息
     */
    @Override
    public String validateConfig(GbStorageConfig gbStorageConfig)
    {
        if (gbStorageConfig == null || StringUtils.isEmpty(gbStorageConfig.getStorageType()))
        {
            return "存储类型不能为空";
        }

        String storageType = gbStorageConfig.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return validateGithubConfig(gbStorageConfig);
            case "aliyun_oss":
                return validateAliyunOssConfig(gbStorageConfig);
            case "tencent_cos":
                return validateTencentCosConfig(gbStorageConfig);
            case "qiniu":
                return validateQiniuConfig(gbStorageConfig);
            case "s3":
                return validateS3Config(gbStorageConfig);
            case "local":
                return validateLocalConfig(gbStorageConfig);
            default:
                return "不支持的存储类型: " + storageType;
        }
    }

    /**
     * 验证 GitHub 配置
     */
    private String validateGithubConfig(GbStorageConfig config)
    {
        // 检查必填字段
        if (StringUtils.isEmpty(config.getGithubOwner()))
        {
            return "GitHub 仓库所有者不能为空";
        }
        if (StringUtils.isEmpty(config.getGithubRepo()))
        {
            return "GitHub 仓库名称不能为空";
        }
        if (StringUtils.isEmpty(config.getGithubBranch()))
        {
            return "GitHub 分支名称不能为空";
        }
        if (StringUtils.isEmpty(config.getGithubToken()))
        {
            return "GitHub Token 不能为空";
        }

        // 使用 GitHub SDK 验证仓库访问
        try
        {
            // 创建 GitHub 客户端（支持代理）
            GitHubBuilder builder = new GitHubBuilder().withOAuthToken(config.getGithubToken());
            Proxy proxy = proxyConfigSupport.buildJavaProxy("storage_config_verify");
            if (proxy != null) {
                builder.withProxy(proxy);
            }
            GitHub github = builder.build();
            
            // 获取仓库（验证仓库是否存在和 Token 是否有效）
            String repoFullName = config.getGithubOwner() + "/" + config.getGithubRepo();
            GHRepository repository = github.getRepository(repoFullName);
            
            // 验证分支是否存在
            GHBranch branch = repository.getBranch(config.getGithubBranch());
            
            if (branch != null)
            {
                return "success";
            }
            else
            {
                return "GitHub 分支 '" + config.getGithubBranch() + "' 不存在";
            }
        }
        catch (org.kohsuke.github.GHFileNotFoundException e)
        {
            // 仓库或分支不存在
            if (e.getMessage().contains("Branch"))
            {
                return "GitHub 分支 '" + config.getGithubBranch() + "' 不存在";
            }
            else
            {
                return "GitHub 仓库 '" + config.getGithubOwner() + "/" + config.getGithubRepo() + "' 不存在";
            }
        }
        catch (org.kohsuke.github.HttpException e)
        {
            // HTTP 错误（如 401, 403）
            int responseCode = e.getResponseCode();
            if (responseCode == 401)
            {
                return "GitHub Token 无效或已过期";
            }
            else if (responseCode == 403)
            {
                return "GitHub Token 权限不足";
            }
            else if (responseCode == 404)
            {
                return "GitHub 仓库或分支不存在";
            }
            else
            {
                return "验证失败，HTTP 状态码: " + responseCode;
            }
        }
        catch (IOException e)
        {
            // 网络或其他 IO 错误
            String message = e.getMessage();
            if (message != null && message.contains("Bad credentials"))
            {
                return "GitHub Token 无效";
            }
            else if (message != null && message.contains("Not Found"))
            {
                return "GitHub 仓库不存在或无权访问";
            }
            else
            {
                return "连接 GitHub API 失败: " + message;
            }
        }
    }

    /**
     * 验证阿里云 OSS 配置
     */
    private String validateAliyunOssConfig(GbStorageConfig config)
    {
        if (StringUtils.isEmpty(config.getOssEndpoint()))
        {
            return "阿里云 OSS Endpoint 不能为空";
        }
        if (StringUtils.isEmpty(config.getOssBucket()))
        {
            return "阿里云 OSS Bucket 名称不能为空";
        }
        if (StringUtils.isEmpty(config.getOssAccessKey()))
        {
            return "阿里云 OSS Access Key 不能为空";
        }
        if (StringUtils.isEmpty(config.getOssSecretKey()))
        {
            return "阿里云 OSS Secret Key 不能为空";
        }
        
        // 使用阿里云 OSS SDK 验证连接
        OSS ossClient = null;
        try
        {
            // 创建 OSS 客户端
            ossClient = new OSSClientBuilder().build(
                config.getOssEndpoint(), 
                config.getOssAccessKey(), 
                config.getOssSecretKey()
            );
            
            // 先检查 Bucket 是否存在
            boolean exists = ossClient.doesBucketExist(config.getOssBucket());
            
            if (!exists)
            {
                return "OSS Bucket '" + config.getOssBucket() + "' 不存在";
            }
            
            // 通过获取 Bucket 信息来验证凭证是否正确
            // 这个操作需要实际的权限，可以验证 Access Key 和 Secret Key 是否有效
            com.aliyun.oss.model.BucketInfo bucketInfo = ossClient.getBucketInfo(config.getOssBucket());
            
            // 如果能成功获取到 Bucket 信息，说明凭证有效且有访问权限
            if (bucketInfo != null && bucketInfo.getBucket() != null)
            {
                return "success";
            }
            else
            {
                return "验证失败：无法获取 Bucket 信息";
            }
        }
        catch (com.aliyun.oss.OSSException e)
        {
            // OSS 服务端异常
            String errorCode = e.getErrorCode();
            if ("InvalidAccessKeyId".equals(errorCode))
            {
                return "验证失败：Access Key 不正确";
            }
            else if ("SignatureDoesNotMatch".equals(errorCode))
            {
                return "验证失败：Secret Key 不正确";
            }
            else if ("AccessDenied".equals(errorCode))
            {
                return "验证失败：该账户没有访问此 Bucket 的权限";
            }
            else if ("NoSuchBucket".equals(errorCode))
            {
                return "验证失败：Bucket '" + config.getOssBucket() + "' 不存在";
            }
            else
            {
                return "验证失败：" + e.getErrorMessage();
            }
        }
        catch (com.aliyun.oss.ClientException e)
        {
            // 客户端异常（网络问题等）
            String message = e.getMessage();
            if (message != null && message.contains("SocketTimeoutException"))
            {
                return "验证失败：连接超时，请检查 Endpoint 配置和网络连接";
            }
            else if (message != null && message.contains("UnknownHostException"))
            {
                return "验证失败：无法解析 Endpoint 域名，请检查 Endpoint 配置";
            }
            else
            {
                return "验证失败：无法连接到 OSS 服务 - " + message;
            }
        }
        catch (Exception e)
        {
            return "验证失败：" + e.getMessage();
        }
        finally
        {
            if (ossClient != null)
            {
                try
                {
                    ossClient.shutdown();
                }
                catch (Exception e)
                {
                    // 忽略关闭异常
                }
            }
        }
    }

    /**
     * 验证腾讯云 COS 配置
     */
    private String validateTencentCosConfig(GbStorageConfig config)
    {
        if (StringUtils.isEmpty(config.getCosRegion()))
        {
            return "腾讯云 COS Region 不能为空";
        }
        if (StringUtils.isEmpty(config.getCosBucket()))
        {
            return "腾讯云 COS Bucket 名称不能为空";
        }
        if (StringUtils.isEmpty(config.getCosSecretId()))
        {
            return "腾讯云 COS Secret ID 不能为空";
        }
        if (StringUtils.isEmpty(config.getCosSecretKey()))
        {
            return "腾讯云 COS Secret Key 不能为空";
        }
        
        // TODO: 实际验证 COS 连接
        return "success";
    }

    /**
     * 验证七牛云配置
     */
    private String validateQiniuConfig(GbStorageConfig config)
    {
        // TODO: 七牛云配置字段待添加到数据库表中
        // 暂时返回成功
        return "success";
    }

    /**
     * 验证 AWS S3 配置（使用 R2 字段兼容）
     */
    private String validateS3Config(GbStorageConfig config)
    {
        if (StringUtils.isEmpty(config.getR2Bucket()))
        {
            return "S3 Bucket 名称不能为空";
        }
        if (StringUtils.isEmpty(config.getR2AccessKey()))
        {
            return "S3 Access Key 不能为空";
        }
        if (StringUtils.isEmpty(config.getR2SecretKey()))
        {
            return "S3 Secret Key 不能为空";
        }
        
        if (StringUtils.isEmpty(config.getR2PublicUrl()))
        {
            return "端点URL不能为空（如 https://s3.tebi.io）";
        }
        
        S3Client s3Client = null;
        try
        {
            String endpoint = config.getR2PublicUrl();
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://"))
            {
                endpoint = "https://" + endpoint;
            }
            if (endpoint.endsWith("/"))
            {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }
            
            // 获取或推断 region
            String region = config.getR2AccountId();
            if (StringUtils.isEmpty(region))
            {
                // 根据端点判断
                if (endpoint.contains("r2.cloudflarestorage.com"))
                {
                    region = "auto";
                }
                else if (endpoint.contains("tebi.io"))
                {
                    region = "global";
                }
                else if (endpoint.contains("oraclecloud.com"))
                {
                    // Oracle Cloud：从端点URL中提取region
                    // 例如：https://xxx.compat.objectstorage.ap-sydney-1.oraclecloud.com
                    String[] parts = endpoint.split("\\.");
                    for (int i = 0; i < parts.length - 2; i++)
                    {
                        if ("objectstorage".equals(parts[i]) && i + 1 < parts.length)
                        {
                            region = parts[i + 1];
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(region))
                    {
                        region = "us-ashburn-1"; // Oracle Cloud 默认 region
                    }
                }
                else
                {
                    region = "us-east-1";
                }
            }
            
            // 创建凭证
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.getR2AccessKey(), 
                config.getR2SecretKey()
            );
            
            // 构建 S3 客户端
            // MinIO、Cloudflare R2、Oracle Cloud 等 S3 兼容服务都需要路径风格访问
            s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(
                    software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build()
                )
                .build();
            
            // 尝试访问 bucket（使用 HeadBucket 操作）
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                .bucket(config.getR2Bucket())
                .build();
            
            s3Client.headBucket(headBucketRequest);
            
            return "success";
        }
        catch (S3Exception e)
        {
            // S3 服务异常
            int statusCode = e.statusCode();
            String errorCode = e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "";
            
            if (statusCode == 404)
            {
                return "验证失败：Bucket '" + config.getR2Bucket() + "' 不存在";
            }
            else if (statusCode == 403)
            {
                if ("InvalidAccessKeyId".equals(errorCode) || "InvalidAccessKey".equals(errorCode))
                {
                    return "验证失败：Access Key 不正确";
                }
                else if ("SignatureDoesNotMatch".equals(errorCode))
                {
                    return "验证失败：Secret Key 不正确";
                }
                else if ("AccessDenied".equals(errorCode))
                {
                    return "验证失败：该账户没有访问此 Bucket 的权限";
                }
                else
                {
                    return "验证失败：访问被拒绝 - " + e.getMessage();
                }
            }
            else if (statusCode == 301 || statusCode == 307)
            {
                return "验证失败：需要重定向，可能端点URL或Region配置不正确";
            }
            else
            {
                return "验证失败：" + e.getMessage();
            }
        }
        catch (software.amazon.awssdk.core.exception.SdkClientException e)
        {
            // SDK 客户端异常（网络问题等）
            String errorMsg = e.getMessage();
            if (errorMsg == null)
            {
                errorMsg = e.toString();
            }
            
            // 检查是否是域名解析错误
            if (errorMsg.contains("UnknownHostException") || e.getCause() instanceof java.net.UnknownHostException)
            {
                String endpoint = config.getR2PublicUrl();
                return "验证失败：无法解析域名。请检查：\n" +
                       "1. 域名是否正确（当前：" + endpoint + "）\n" +
                       "2. 如果是本地服务，请使用 localhost 或 127.0.0.1\n" +
                       "3. 如果使用自定义域名，请确保 DNS 或 hosts 文件已正确配置";
            }
            else if (errorMsg.contains("Unable to execute HTTP request") || errorMsg.contains("Connection refused"))
            {
                return "验证失败：无法连接到 S3 服务，请检查端点URL是否正确，以及服务是否正在运行";
            }
            else if (errorMsg.contains("Connection timeout") || errorMsg.contains("Read timeout"))
            {
                return "验证失败：连接超时，请检查网络连接和端点URL";
            }
            else
            {
                return "验证失败：" + errorMsg;
            }
        }
        catch (Exception e)
        {
            String errorMsg = e.getMessage();
            if (errorMsg == null)
            {
                errorMsg = e.toString();
            }
            
            // 检查是否是域名解析错误
            if (errorMsg.contains("UnknownHostException") || e.getCause() instanceof java.net.UnknownHostException)
            {
                String endpoint = config.getR2PublicUrl();
                return "验证失败：无法解析域名。请检查：\n" +
                       "1. 域名是否正确（当前：" + endpoint + "）\n" +
                       "2. 如果是本地服务，请使用 localhost 或 127.0.0.1\n" +
                       "3. 如果使用自定义域名，请确保 DNS 或 hosts 文件已正确配置";
            }
            
            return "验证失败：" + errorMsg;
        }
        finally
        {
            if (s3Client != null)
            {
                try
                {
                    s3Client.close();
                }
                catch (Exception e)
                {
                    // 忽略关闭异常
                }
            }
        }
    }
    /**
     * 验证本地存储配置
     */
    private String validateLocalConfig(GbStorageConfig config)
    {
        // 本地存储基本不需要验证
        return "success";
    }
}
