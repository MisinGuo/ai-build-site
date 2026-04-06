package com.ruoyi.gamebox.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.gamebox.domain.GbStorageConfig;
import com.ruoyi.gamebox.domain.dto.StorageFileInfo;
import com.ruoyi.gamebox.service.IGbStorageConfigService;
import com.ruoyi.gamebox.service.IStorageFileService;
import com.ruoyi.gamebox.support.GbProxyConfigSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.time.Duration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.HttpMethod;

import org.kohsuke.github.GHContent;

import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Base64;
import org.kohsuke.github.GHContentUpdateResponse;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.*;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aliyun.oss.model.ListObjectsRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存储文件Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class StorageFileServiceImpl implements IStorageFileService
{
    private static final Logger logger = LoggerFactory.getLogger(StorageFileServiceImpl.class);
    
    @Autowired
    private IGbStorageConfigService gbStorageConfigService;

    @Autowired
    private GbProxyConfigSupport proxyConfigSupport;

    // 可编辑的文本文件扩展名
    private static final String[] EDITABLE_EXTENSIONS = {
        ".txt", ".md", ".markdown", ".json", ".xml", ".yaml", ".yml", 
        ".html", ".htm", ".css", ".js", ".ts", ".java", ".py", ".sh", 
        ".bat", ".sql", ".properties", ".conf", ".config", ".ini"
    };

    @Override
    public List<StorageFileInfo> listFiles(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return listGithubFiles(config, path);
            case "s3":
                return listS3Files(config, path);
            case "aliyun_oss":
                return listOssFiles(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public StorageFileInfo uploadFile(Long configId, String path, MultipartFile file)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return uploadGithubFile(config, path, file);
            case "s3":
                return uploadS3File(config, path, file);
            case "aliyun_oss":
                return uploadOssFile(config, path, file);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public boolean deleteFile(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return deleteGithubFile(config, path);
            case "s3":
                return deleteS3File(config, path);
            case "aliyun_oss":
                return deleteOssFile(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public String readTextFile(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return readGithubTextFile(config, path);
            case "s3":
                return readS3TextFile(config, path);
            case "aliyun_oss":
                return readOssTextFile(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public boolean updateTextFile(Long configId, String path, String content)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return updateGithubTextFile(config, path, content);
            case "s3":
                return updateS3TextFile(config, path, content);
            case "aliyun_oss":
                return updateOssTextFile(config, path, content);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public String getDownloadUrl(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return getGithubDownloadUrl(config, path);
            case "s3":
                return getS3DownloadUrl(config, path);
            case "aliyun_oss":
                return getOssDownloadUrl(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public String getPresignedDownloadUrl(Long configId, String path, Integer expirationMinutes)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        // 默认有效期60分钟
        int expiration = (expirationMinutes != null && expirationMinutes > 0) ? expirationMinutes : 60;

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                // GitHub使用直接URL(已经是公开的)
                return getGithubDirectDownloadUrl(config, path);
            case "s3":
                return getS3PresignedUrl(config, path, expiration);
            case "aliyun_oss":
                return getOssPresignedUrl(config, path, expiration);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    @Deprecated
    public InputStream downloadFile(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return downloadGithubFile(config, path);
            case "s3":
                return downloadS3File(config, path);
            case "aliyun_oss":
                return downloadOssFile(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    @Override
    public boolean checkFileExists(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return checkGithubFileExists(config, path);
            case "s3":
                return checkS3FileExists(config, path);
            case "aliyun_oss":
                return checkOssFileExists(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    // ==================== GitHub 实现 ====================

    /**
     * 创建GitHub客户端
     */
    private GitHub createGitHubClient(GbStorageConfig config)
    {
        try
        {
            GitHubBuilder builder = new GitHubBuilder().withOAuthToken(config.getGithubToken());
            Proxy proxy = proxyConfigSupport.buildJavaProxy("storage_github");
            if (proxy != null) {
                builder.withProxy(proxy);
            }
            return builder.build();
        }
        catch (IOException e)
        {
            throw new ServiceException("创建GitHub客户端失败: " + e.getMessage());
        }
    }

    /**
     * 获取GitHub仓库对象
     */
    private GHRepository getGitHubRepository(GbStorageConfig config)
    {
        try
        {
            GitHub github = createGitHubClient(config);
            return github.getRepository(config.getGithubOwner() + "/" + config.getGithubRepo());
        }
        catch (IOException e)
        {
            throw new ServiceException("获取GitHub仓库失败: " + e.getMessage());
        }
    }

    private List<StorageFileInfo> listGithubFiles(GbStorageConfig config, String path)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            List<StorageFileInfo> files = new ArrayList<>();
            
            // GitHub API: 根目录使用空字符串，不能使用 "/"
            String dirPath = (path == null || path.isEmpty() || path.equals("/")) ? "" : path;
            
            List<GHContent> contents = repo.getDirectoryContent(dirPath, branch);
            for (GHContent content : contents)
            {
                // 过滤.gitkeep占位文件，不展示给用户
                if (content.isFile() && ".gitkeep".equals(content.getName()))
                {
                    continue;
                }
                files.add(convertToStorageFileInfo(content));
            }
            
            return files;
        }
        catch (IOException e)
        {
            throw new ServiceException("获取GitHub文件列表失败: " + e.getMessage());
        }
    }

    /**
     * 将GHContent转换为StorageFileInfo
     */
    private StorageFileInfo convertToStorageFileInfo(GHContent content) throws IOException
    {
        StorageFileInfo fileInfo = new StorageFileInfo();
        fileInfo.setName(content.getName());
        fileInfo.setPath(content.getPath());
        fileInfo.setType(content.isDirectory() ? "directory" : "file");
        
        if (content.isFile())
        {
            fileInfo.setSize(content.getSize());
            fileInfo.setDownloadUrl(content.getDownloadUrl());
            fileInfo.setEditable(isEditableFile(content.getName()));
        }
        
        return fileInfo;
    }

    private StorageFileInfo uploadGithubFile(GbStorageConfig config, String path, MultipartFile file)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            byte[] fileBytes = file.getBytes();
            
            // 构建完整的文件路径
            String filePath;
            if (path == null || path.isEmpty())
            {
                // 根目录：直接使用文件名
                filePath = file.getOriginalFilename();
            }
            else
            {
                // 子目录：路径 + 文件名
                filePath = path.endsWith("/") ? path + file.getOriginalFilename() : path + "/" + file.getOriginalFilename();
            }
            
            // 使用GitHub SDK创建文件
            GHContentUpdateResponse response = repo.createContent()
                .content(fileBytes)
                .message("Upload file: " + file.getOriginalFilename())
                .path(filePath)
                .branch(branch)
                .commit();
            
            StorageFileInfo fileInfo = new StorageFileInfo();
            fileInfo.setPath(filePath);
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setType("file");
            fileInfo.setSize((long) fileBytes.length);
            fileInfo.setEditable(isEditableFile(file.getOriginalFilename()));
            
            return fileInfo;
        }
        catch (IOException e)
        {
            throw new ServiceException("上传文件到GitHub失败: " + e.getMessage());
        }
    }

    private boolean deleteGithubFile(GbStorageConfig config, String path)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            // 尝试获取文件内容
            try
            {
                GHContent content = repo.getFileContent(path, branch);
                
                // 如果是文件，直接删除
                if (content.isFile())
                {
                    content.delete("Delete file: " + path, branch);
                    return true;
                }
            }
            catch (IOException e)
            {
                // 文件不存在，可能是文件夹，尝试递归删除
            }
            
            // 尝试作为文件夹删除（递归删除所有内容）
            try
            {
                List<GHContent> contents = repo.getDirectoryContent(path, branch);
                
                // 递归删除所有文件和子文件夹
                for (GHContent content : contents)
                {
                    if (content.isFile())
                    {
                        content.delete("Delete file: " + content.getPath(), branch);
                    }
                    else if (content.isDirectory())
                    {
                        // 递归删除子文件夹
                        deleteGithubFile(config, content.getPath());
                    }
                }
                
                return true;
            }
            catch (IOException e2)
            {
                throw new ServiceException("删除GitHub文件/文件夹失败: " + e2.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("删除GitHub文件失败: " + e.getMessage());
        }
    }

    private String readGithubTextFile(GbStorageConfig config, String path)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            // 获取文件内容
            GHContent content = repo.getFileContent(path, branch);
            
            // 读取文件内容（SDK自动处理Base64解码）
            return content.getContent();
        }
        catch (IOException e)
        {
            throw new ServiceException("读取GitHub文件失败: " + e.getMessage());
        }
    }

    private boolean updateGithubTextFile(GbStorageConfig config, String path, String content)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            try
            {
                // 尝试获取文件内容（包含SHA）
                GHContent fileContent = repo.getFileContent(path, branch);
                
                // 文件存在，执行更新
                fileContent.update(content, "Update file: " + path, branch);
            }
            catch (IOException e)
            {
                // 文件不存在（404），创建新文件
                if (e.getMessage() != null && e.getMessage().contains("404"))
                {
                    repo.createContent()
                        .content(content)
                        .path(path)
                        .branch(branch)
                        .message("Create file: " + path)
                        .commit();
                }
                else
                {
                    // 其他错误，继续抛出
                    throw e;
                }
            }
            
            return true;
        }
        catch (IOException e)
        {
            throw new ServiceException("更新GitHub文件失败: " + e.getMessage());
        }
    }

    private boolean checkGithubFileExists(GbStorageConfig config, String path)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            try
            {
                repo.getFileContent(path, branch);
                return true;
            }
            catch (IOException e)
            {
                if (e.getMessage() != null && e.getMessage().contains("404"))
                {
                    return false;
                }
                throw e;
            }
        }
        catch (IOException e)
        {
            throw new ServiceException("检查GitHub文件失败: " + e.getMessage());
        }
    }

    private String getGithubDownloadUrl(GbStorageConfig config, String path)
    {
        return String.format("https://raw.githubusercontent.com/%s/%s/%s/%s",
                config.getGithubOwner(), config.getGithubRepo(), config.getGithubBranch(), path);
    }

    private InputStream downloadGithubFile(GbStorageConfig config, String path)
    {
        try
        {
            GitHub github = createGitHubClient(config);
            String repoFullName = config.getGithubOwner() + "/" + config.getGithubRepo();
            GHRepository repo = github.getRepository(repoFullName);
            String branch = config.getGithubBranch();
            
            // 获取文件内容对象
            GHContent content = repo.getFileContent(path, branch);
            
            // 检查文件大小
            long fileSize = content.getSize();
            
            // GitHub API对于大文件（>1MB）不返回内容，需要通过download URL获取
            if (fileSize > 1024 * 1024 || content.getContent() == null || content.getContent().isEmpty())
            {
                // 大文件：通过GitHub raw URL下载
                String downloadUrl = String.format("https://raw.githubusercontent.com/%s/%s/%s/%s",
                        config.getGithubOwner(), config.getGithubRepo(), branch, path);
                
                URL url = new URL(downloadUrl);
                Proxy proxy = proxyConfigSupport.buildJavaProxy("storage_github");
                HttpURLConnection connection = (HttpURLConnection) (proxy != null ? url.openConnection(proxy) : url.openConnection());
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(120000);
                
                // 添加认证（对于私有仓库）
                if (config.getGithubToken() != null && !config.getGithubToken().isEmpty())
                {
                    connection.setRequestProperty("Authorization", "token " + config.getGithubToken());
                }
                
                // 读取内容到内存
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[8192];
                int bytesRead;
                
                while ((bytesRead = inputStream.read(data)) != -1)
                {
                    buffer.write(data, 0, bytesRead);
                }
                
                inputStream.close();
                connection.disconnect();
                
                return new ByteArrayInputStream(buffer.toByteArray());
            }
            else
            {
                // 小文件：判断是文本还是二进制
                String fileName = content.getName().toLowerCase();
                boolean isTextFile = isEditableFile(fileName);
                
                String contentStr = content.getContent();
                byte[] bytes;
                
                if (isTextFile)
                {
                    // 文本文件：getContent()返回的是已解码的UTF-8字符串
                    bytes = contentStr.getBytes(StandardCharsets.UTF_8);
                }
                else
                {
                    // 二进制文件：getContent()返回base64编码的字符串
                    // 移除所有换行符和空白字符
                    contentStr = contentStr.replaceAll("\\s+", "");
                    bytes = Base64.getDecoder().decode(contentStr);
                }
                
                return new ByteArrayInputStream(bytes);
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("下载GitHub文件失败: " + e.getMessage());
        }
    }

    // ==================== S3 实现 ====================

    private S3Client createS3Client(GbStorageConfig config)
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
        
        String region = config.getR2AccountId();
        if (StringUtils.isEmpty(region))
        {
            if (endpoint.contains("r2.cloudflarestorage.com"))
            {
                region = "auto";
            }
            else if (endpoint.contains("tebi.io"))
            {
                region = "global";
            }
            else
            {
                region = "us-east-1";
            }
        }
        
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
            config.getR2AccessKey(), 
            config.getR2SecretKey()
        );
        
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .endpointOverride(URI.create(endpoint))
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofSeconds(30))         // 单次调用总超时 30 秒
                .apiCallAttemptTimeout(Duration.ofSeconds(20))  // 单次尝试超时 20 秒
                .build())
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .chunkedEncodingEnabled(true)
                .build())
            .build();
    }

    private List<StorageFileInfo> listS3Files(GbStorageConfig config, String path)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            
            String prefix = StringUtils.isEmpty(path) ? "" : (path.endsWith("/") ? path : path + "/");
            
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(config.getR2Bucket())
                .prefix(prefix)
                .delimiter("/")
                .build();
            
            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            
            List<StorageFileInfo> files = new ArrayList<>();
            
            // 添加子目录
            for (CommonPrefix commonPrefix : listResponse.commonPrefixes())
            {
                String dirPath = commonPrefix.prefix();
                String dirName = dirPath.substring(prefix.length());
                if (dirName.endsWith("/"))
                {
                    dirName = dirName.substring(0, dirName.length() - 1);
                }
                
                StorageFileInfo fileInfo = new StorageFileInfo();
                fileInfo.setName(dirName);
                fileInfo.setPath(dirPath);
                fileInfo.setType("directory");
                files.add(fileInfo);
            }
            
            // 添加文件
            for (S3Object s3Object : listResponse.contents())
            {
                String filePath = s3Object.key();
                if (!filePath.equals(prefix)) // 排除目录自身
                {
                    String fileName = filePath.substring(prefix.length());
                    
                    StorageFileInfo fileInfo = new StorageFileInfo();
                    fileInfo.setName(fileName);
                    fileInfo.setPath(filePath);
                    fileInfo.setType("file");
                    fileInfo.setSize(s3Object.size());
                    fileInfo.setLastModified(Date.from(s3Object.lastModified()));
                    fileInfo.setEditable(isEditableFile(fileName));
                    files.add(fileInfo);
                }
            }
            
            return files;
        }
        catch (S3Exception e)
        {
            throw new ServiceException("获取S3文件列表失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private StorageFileInfo uploadS3File(GbStorageConfig config, String path, MultipartFile file)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            
            // 构建完整的文件路径
            String filePath;
            if (path == null || path.isEmpty())
            {
                // 根目录：直接使用文件名
                filePath = file.getOriginalFilename();
            }
            else
            {
                // 子目录：路径 + 文件名
                filePath = path.endsWith("/") ? path + file.getOriginalFilename() : path + "/" + file.getOriginalFilename();
            }
            
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(filePath)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();
            
            // 使用InputStream而不是字节数组，对大文件更友好
            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            StorageFileInfo fileInfo = new StorageFileInfo();
            fileInfo.setPath(filePath);
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setType("file");
            fileInfo.setSize(file.getSize());
            fileInfo.setMimeType(file.getContentType());
            
            return fileInfo;
        }
        catch (Exception e)
        {
            logger.error("上传文件到S3失败: ", e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty())
            {
                errorMsg = e.getClass().getSimpleName() + " - " + (e.getCause() != null ? e.getCause().getMessage() : "未知错误");
            }
            throw new ServiceException("上传文件到S3失败: " + errorMsg);
        }
        finally
        {
            if (s3Client != null)
            {
                try { s3Client.close(); } catch (Exception ignored) {}
            }
        }
    }

    private boolean deleteS3File(GbStorageConfig config, String path)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            String bucket = config.getR2Bucket();
            
            // 判断是文件还是目录
            if (path.endsWith("/"))
            {
                // 目录：需要递归删除所有子对象
                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .build();
                
                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
                
                // 删除所有子对象
                for (S3Object s3Object : listResponse.contents())
                {
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Object.key())
                        .build();
                    
                    s3Client.deleteObject(deleteRequest);
                }
                
                // 删除目录本身（如果存在占位符）
                DeleteObjectRequest deleteDirRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();
                
                s3Client.deleteObject(deleteDirRequest);
            }
            else
            {
                // 文件：直接删除
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();
                
                s3Client.deleteObject(deleteRequest);
            }
            
            return true;
        }
        catch (S3Exception e)
        {
            throw new ServiceException("删除S3文件失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private boolean checkS3FileExists(GbStorageConfig config, String path)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            String bucket = config.getR2Bucket();
            
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .build();
            
            s3Client.headObject(headRequest);
            return true;
        }
        catch (NoSuchKeyException e)
        {
            return false;
        }
        catch (Exception e)
        {
            throw new ServiceException("检查S3文件失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private String readS3TextFile(GbStorageConfig config, String path)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            
            GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(path)
                .build();
            
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getRequest);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8));
            String content = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            
            return content;
        }
        catch (Exception e)
        {
            throw new ServiceException("读取S3文件失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private boolean updateS3TextFile(GbStorageConfig config, String path, String content)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(path)
                .contentType("text/plain; charset=utf-8")
                .build();
            
            s3Client.putObject(putRequest, RequestBody.fromBytes(contentBytes));
            return true;
        }
        catch (S3Exception e)
        {
            throw new ServiceException("更新S3文件失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private String getS3DownloadUrl(GbStorageConfig config, String path)
    {
        // 生成预签名URL（暂时返回简单URL，后续可实现预签名）
        String endpoint = config.getR2PublicUrl();
        if (!endpoint.startsWith("http"))
        {
            endpoint = "https://" + endpoint;
        }
        return endpoint + "/" + config.getR2Bucket() + "/" + path;
    }

    private InputStream downloadS3File(GbStorageConfig config, String path)
    {
        S3Client s3Client = null;
        ResponseInputStream<GetObjectResponse> response = null;
        try
        {
            s3Client = createS3Client(config);
            
            GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(path)
                .build();
            
            response = s3Client.getObject(getRequest);
            
            // 读取完整的文件内容到字节数组
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = response.read(data)) != -1)
            {
                buffer.write(data, 0, bytesRead);
            }
            
            // 返回包含完整内容的ByteArrayInputStream
            return new ByteArrayInputStream(buffer.toByteArray());
        }
        catch (Exception e)
        {
            throw new ServiceException("下载S3文件失败: " + e.getMessage());
        }
        finally
        {
            if (response != null)
            {
                try { response.close(); } catch (Exception ignored) {}
            }
            if (s3Client != null)
            {
                try { s3Client.close(); } catch (Exception ignored) {}
            }
        }
    }

    // ==================== 阿里云 OSS 实现 ====================

    private OSS createOssClient(GbStorageConfig config)
    {
        return new OSSClientBuilder().build(
            config.getOssEndpoint(), 
            config.getOssAccessKey(), 
            config.getOssSecretKey()
        );
    }

    private List<StorageFileInfo> listOssFiles(GbStorageConfig config, String path)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            String prefix = StringUtils.isEmpty(path) ? "" : (path.endsWith("/") ? path : path + "/");
            
            com.aliyun.oss.model.ListObjectsRequest listRequest = 
                new com.aliyun.oss.model.ListObjectsRequest(config.getOssBucket());
            listRequest.setPrefix(prefix);
            listRequest.setDelimiter("/");
            
            ObjectListing listing = ossClient.listObjects(listRequest);
            
            List<StorageFileInfo> files = new ArrayList<>();
            
            // 添加子目录
            for (String commonPrefix : listing.getCommonPrefixes())
            {
                String dirName = commonPrefix.substring(prefix.length());
                if (dirName.endsWith("/"))
                {
                    dirName = dirName.substring(0, dirName.length() - 1);
                }
                
                StorageFileInfo fileInfo = new StorageFileInfo();
                fileInfo.setName(dirName);
                fileInfo.setPath(commonPrefix);
                fileInfo.setType("directory");
                files.add(fileInfo);
            }
            
            // 添加文件
            for (OSSObjectSummary summary : listing.getObjectSummaries())
            {
                String filePath = summary.getKey();
                if (!filePath.equals(prefix)) // 排除目录自身
                {
                    String fileName = filePath.substring(prefix.length());
                    
                    StorageFileInfo fileInfo = new StorageFileInfo();
                    fileInfo.setName(fileName);
                    fileInfo.setPath(filePath);
                    fileInfo.setType("file");
                    fileInfo.setSize(summary.getSize());
                    fileInfo.setLastModified(summary.getLastModified());
                    fileInfo.setEditable(isEditableFile(fileName));
                    files.add(fileInfo);
                }
            }
            
            return files;
        }
        catch (com.aliyun.oss.OSSException e)
        {
            throw new ServiceException("获取OSS文件列表失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private StorageFileInfo uploadOssFile(GbStorageConfig config, String path, MultipartFile file)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            // 构建完整的文件路径
            String filePath;
            if (path == null || path.isEmpty())
            {
                // 根目录：直接使用文件名
                filePath = file.getOriginalFilename();
            }
            else
            {
                // 子目录：路径 + 文件名
                filePath = path.endsWith("/") ? path + file.getOriginalFilename() : path + "/" + file.getOriginalFilename();
            }
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            ossClient.putObject(config.getOssBucket(), filePath, file.getInputStream(), metadata);
            
            StorageFileInfo fileInfo = new StorageFileInfo();
            fileInfo.setPath(filePath);
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setType("file");
            fileInfo.setSize(file.getSize());
            fileInfo.setMimeType(file.getContentType());
            
            return fileInfo;
        }
        catch (Exception e)
        {
            throw new ServiceException("上传文件到OSS失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private boolean deleteOssFile(GbStorageConfig config, String path)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            String bucket = config.getOssBucket();
            
            // 判断是文件还是目录
            if (path.endsWith("/"))
            {
                // 目录：需要递归删除所有子对象
                ListObjectsRequest listRequest = new ListObjectsRequest(bucket);
                listRequest.setPrefix(path);
                
                ObjectListing listing = ossClient.listObjects(listRequest);
                
                // 删除所有子对象
                for (OSSObjectSummary summary : listing.getObjectSummaries())
                {
                    ossClient.deleteObject(bucket, summary.getKey());
                }
                
                // 删除目录本身（如果存在占位符）
                ossClient.deleteObject(bucket, path);
            }
            else
            {
                // 文件：直接删除
                ossClient.deleteObject(bucket, path);
            }
            
            return true;
        }
        catch (com.aliyun.oss.OSSException e)
        {
            throw new ServiceException("删除OSS文件失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private boolean checkOssFileExists(GbStorageConfig config, String path)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            String bucket = config.getOssBucket();
            
            return ossClient.doesObjectExist(bucket, path);
        }
        catch (com.aliyun.oss.OSSException e)
        {
            throw new ServiceException("检查OSS文件失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private String readOssTextFile(GbStorageConfig config, String path)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            OSSObject ossObject = ossClient.getObject(config.getOssBucket(), path);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(ossObject.getObjectContent(), StandardCharsets.UTF_8));
            String content = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            
            return content;
        }
        catch (Exception e)
        {
            throw new ServiceException("读取OSS文件失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private boolean updateOssTextFile(GbStorageConfig config, String path, String content)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain; charset=utf-8");
            metadata.setContentLength(contentBytes.length);
            
            ossClient.putObject(config.getOssBucket(), path, inputStream, metadata);
            return true;
        }
        catch (com.aliyun.oss.OSSException e)
        {
            throw new ServiceException("更新OSS文件失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private String getOssDownloadUrl(GbStorageConfig config, String path)
    {
        // 生成OSS公共读URL
        String endpoint = config.getOssEndpoint();
        if (!endpoint.startsWith("http"))
        {
            endpoint = "https://" + endpoint;
        }
        
        // 阿里云OSS URL格式：https://bucket.endpoint/path
        String bucketUrl = endpoint.replace("://", "://" + config.getOssBucket() + ".");
        return bucketUrl + "/" + path;
    }

    private InputStream downloadOssFile(GbStorageConfig config, String path)
    {
        OSS ossClient = null;
        InputStream objectContent = null;
        try
        {
            ossClient = createOssClient(config);
            
            // 获取OSS对象
            OSSObject ossObject = ossClient.getObject(config.getOssBucket(), path);
            objectContent = ossObject.getObjectContent();
            
            // 读取完整的文件内容到字节数组
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int bytesRead;
            while ((bytesRead = objectContent.read(data)) != -1)
            {
                buffer.write(data, 0, bytesRead);
            }
            
            // 返回包含完整内容的ByteArrayInputStream
            return new ByteArrayInputStream(buffer.toByteArray());
        }
        catch (Exception e)
        {
            throw new ServiceException("下载OSS文件失败: " + e.getMessage());
        }
        finally
        {
            if (objectContent != null)
            {
                try { objectContent.close(); } catch (IOException ignored) {}
            }
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    // ==================== 工具方法 ====================

    @Override
    public boolean createFolder(Long configId, String parentPath, String folderName)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return createGithubFolder(config, parentPath, folderName);
            case "s3":
                return createS3Folder(config, parentPath, folderName);
            case "aliyun_oss":
                return createOssFolder(config, parentPath, folderName);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    private boolean createGithubFolder(GbStorageConfig config, String parentPath, String folderName)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            // GitHub没有真正的文件夹概念，需要创建一个.gitkeep占位文件
            String folderPath = StringUtils.isEmpty(parentPath) 
                ? folderName 
                : (parentPath.endsWith("/") ? parentPath + folderName : parentPath + "/" + folderName);
            
            String gitkeepPath = folderPath + "/.gitkeep";
            
            // 创建.gitkeep文件
            repo.createContent()
                .content("")
                .message("Create folder: " + folderName)
                .path(gitkeepPath)
                .branch(branch)
                .commit();
            
            return true;
        }
        catch (IOException e)
        {
            throw new ServiceException("创建GitHub文件夹失败: " + e.getMessage());
        }
    }

    private boolean createS3Folder(GbStorageConfig config, String parentPath, String folderName)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            
            // S3使用对象键模拟文件夹，以/结尾表示文件夹
            String folderPath = StringUtils.isEmpty(parentPath) 
                ? folderName + "/" 
                : (parentPath.endsWith("/") ? parentPath + folderName + "/" : parentPath + "/" + folderName + "/");
            
            // 创建一个空对象来表示文件夹
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(folderPath)
                .build();
            
            s3Client.putObject(putRequest, RequestBody.fromBytes(new byte[0]));
            
            return true;
        }
        catch (Exception e)
        {
            throw new ServiceException("创建S3文件夹失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private boolean createOssFolder(GbStorageConfig config, String parentPath, String folderName)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            // OSS使用对象键模拟文件夹，以/结尾表示文件夹
            String folderPath = StringUtils.isEmpty(parentPath) 
                ? folderName + "/" 
                : (parentPath.endsWith("/") ? parentPath + folderName + "/" : parentPath + "/" + folderName + "/");
            
            // 创建一个空对象来表示文件夹
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);
            
            ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
            ossClient.putObject(config.getOssBucket(), folderPath, inputStream, metadata);
            
            return true;
        }
        catch (Exception e)
        {
            throw new ServiceException("创建OSS文件夹失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 判断文件是否可编辑（文本文件）
     */
    private boolean isEditableFile(String fileName)
    {
        if (StringUtils.isEmpty(fileName))
        {
            return false;
        }
        
        String lowerName = fileName.toLowerCase();
        for (String ext : EDITABLE_EXTENSIONS)
        {
            if (lowerName.endsWith(ext))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean renameFile(Long configId, String oldPath, String newName)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return renameGithubFile(config, oldPath, newName);
            case "s3":
                return renameS3File(config, oldPath, newName);
            case "aliyun_oss":
                return renameOssFile(config, oldPath, newName);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    private boolean renameGithubFile(GbStorageConfig config, String oldPath, String newName)
    {
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            // 计算新路径
            String newPath;
            int lastSlashIndex = oldPath.lastIndexOf('/');
            if (lastSlashIndex >= 0)
            {
                // 包含路径，保留父路径
                newPath = oldPath.substring(0, lastSlashIndex + 1) + newName;
            }
            else
            {
                // 根目录文件
                newPath = newName;
            }
            
            // 检查是文件还是目录
            try
            {
                GHContent content = repo.getFileContent(oldPath, branch);
                
                // 是文件：读取内容，创建新文件，删除旧文件
                if (content.isFile())
                {
                    InputStream is = content.read();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                    }
                    is.close();
                    byte[] fileContent = baos.toByteArray();

                    // 创建新文件
                    repo.createContent()
                        .content(fileContent)
                        .message("Rename file: " + oldPath + " -> " + newPath)
                        .path(newPath)
                        .branch(branch)
                        .commit();

                    // 重新获取旧文件的content对象（包含最新的SHA）
                    GHContent oldContent = repo.getFileContent(oldPath, branch);
                    // 删除旧文件
                    oldContent.delete("Rename file: delete old file " + oldPath, branch);

                    return true;
                }
            }
            catch (IOException e)
            {
                // 文件不存在，可能是目录
            }
            
            // 尝试作为目录处理 - 需要递归复制所有内容
            try
            {
                renameGithubDirectory(repo, branch, oldPath, newPath);
                // 删除旧目录（递归删除所有文件）
                deleteGithubFile(config, oldPath);
                return true;
            }
            catch (IOException e)
            {
                throw new ServiceException("重命名GitHub目录失败: " + e.getMessage());
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("重命名GitHub文件/目录失败: " + e.getMessage());
        }
    }

    /**
     * 递归重命名GitHub目录
     */
    private void renameGithubDirectory(GHRepository repo, String branch, String oldPath, String newPath) throws IOException
    {
        List<GHContent> contents = repo.getDirectoryContent(oldPath, branch);
        
        for (GHContent content : contents)
        {
            // 计算相对路径
            String relativePath = content.getPath().substring(oldPath.length());
            if (relativePath.startsWith("/"))
            {
                relativePath = relativePath.substring(1);
            }
            
            String targetPath = newPath + "/" + relativePath;
            
            if (content.isFile())
            {
                // 读取文件内容
                InputStream is = content.read();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1)
                {
                    baos.write(buffer, 0, len);
                }
                is.close();
                byte[] fileContent = baos.toByteArray();
                
                // 在新路径创建文件
                repo.createContent()
                    .content(fileContent)
                    .message("Rename folder: move " + content.getPath() + " -> " + targetPath)
                    .path(targetPath)
                    .branch(branch)
                    .commit();
            }
            else if (content.isDirectory())
            {
                // 递归处理子目录
                renameGithubDirectory(repo, branch, content.getPath(), targetPath);
            }
        }
    }

    private boolean renameS3File(GbStorageConfig config, String oldPath, String newName)
    {
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            String bucket = config.getR2Bucket();
            
            // 计算新路径
            String newPath;
            // 对于目录，先去掉末尾的/，然后计算父路径
            String pathForCalculation = oldPath.endsWith("/") ? oldPath.substring(0, oldPath.length() - 1) : oldPath;
            int lastSlashIndex = pathForCalculation.lastIndexOf('/');
            if (lastSlashIndex >= 0)
            {
                newPath = pathForCalculation.substring(0, lastSlashIndex + 1) + newName;
            }
            else
            {
                newPath = newName;
            }
            
            // 判断是文件还是目录
            if (!oldPath.endsWith("/"))
            {
                // 文件：检查存在性后重命名
                HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(oldPath)
                    .build();
                
                s3Client.headObject(headRequest);
                
                // 复制文件
                String encodedKey = oldPath.replace(" ", "%20");
                String copySource = bucket + "/" + encodedKey;
                
                CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .copySource(copySource)
                    .destinationBucket(bucket)
                    .destinationKey(newPath)
                    .build();
                
                s3Client.copyObject(copyRequest);
                
                // 删除旧文件
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(oldPath)
                    .build();
                
                s3Client.deleteObject(deleteRequest);
                
                return true;
            }
            else
            {
                // 目录：递归重命名所有子对象
                logger.info("开始重命名S3目录: {} -> {}", oldPath, newPath);
                
                // 确保新路径以/结尾
                if (!newPath.endsWith("/"))
                {
                    newPath = newPath + "/";
                }
                
                // 列出所有子对象
                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(oldPath)
                    .build();
                
                ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
                List<S3Object> objects = listResponse.contents();
                
                if (objects.isEmpty())
                {
                    logger.warn("S3目录为空: {}", oldPath);
                    return true;
                }
                
                logger.info("找到 {} 个对象需要重命名", objects.size());
                
                int processed = 0;
                for (S3Object s3Object : objects)
                {
                    String oldKey = s3Object.key();
                    String relativePath = oldKey.substring(oldPath.length());
                    String newKey = newPath + relativePath;
                    
                    logger.info("重命名对象 [{}/{}]: {} -> {}", ++processed, objects.size(), oldKey, newKey);
                    
                    // URL编码特殊字符
                    String encodedOldKey = oldKey.replace(" ", "%20")
                                                 .replace("+", "%2B")
                                                 .replace("=", "%3D");
                    String copySource = bucket + "/" + encodedOldKey;
                    
                    // 复制对象
                    CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                        .copySource(copySource)
                        .destinationBucket(bucket)
                        .destinationKey(newKey)
                        .build();
                    
                    s3Client.copyObject(copyRequest);
                    
                    // 删除旧对象
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(oldKey)
                        .build();
                    
                    s3Client.deleteObject(deleteRequest);
                }
                
                logger.info("S3目录重命名完成: {} -> {}", oldPath, newPath);
                return true;
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("重命名S3文件/目录失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private boolean renameOssFile(GbStorageConfig config, String oldPath, String newName)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            String bucket = config.getOssBucket();
            
            // 计算新路径
            String newPath;
            // 对于目录，先去掉末尾的/，然后计算父路径
            String pathForCalculation = oldPath.endsWith("/") ? oldPath.substring(0, oldPath.length() - 1) : oldPath;
            int lastSlashIndex = pathForCalculation.lastIndexOf('/');
            if (lastSlashIndex >= 0)
            {
                newPath = pathForCalculation.substring(0, lastSlashIndex + 1) + newName;
            }
            else
            {
                newPath = newName;
            }
            
            // 检查是文件还是目录
            if (!oldPath.endsWith("/"))
            {
                // 文件：复制到新路径，然后删除旧文件
                ossClient.copyObject(bucket, oldPath, bucket, newPath);
                ossClient.deleteObject(bucket, oldPath);
            }
            else
            {
                // 目录：需要重命名所有子对象
                logger.info("开始重命名OSS目录: {} -> {}", oldPath, newPath);
                
                // 确保新路径以/结尾
                if (!newPath.endsWith("/"))
                {
                    newPath = newPath + "/";
                }
                
                ObjectListing listing = ossClient.listObjects(bucket, oldPath);
                List<OSSObjectSummary> objects = listing.getObjectSummaries();
                
                if (objects.isEmpty())
                {
                    logger.warn("OSS目录为空: {}", oldPath);
                    return true;
                }
                
                logger.info("找到 {} 个对象需要重命名", objects.size());
                
                int processed = 0;
                for (OSSObjectSummary summary : objects)
                {
                    String oldKey = summary.getKey();
                    String relativePath = oldKey.substring(oldPath.length());
                    String newKey = newPath + relativePath;
                    
                    logger.info("重命名对象 [{}/{}]: {} -> {}", ++processed, objects.size(), oldKey, newKey);
                    
                    // 复制对象
                    ossClient.copyObject(bucket, oldKey, bucket, newKey);
                    
                    // 删除旧对象
                    ossClient.deleteObject(bucket, oldKey);
                }
                
                logger.info("OSS目录重命名完成: {} -> {}", oldPath, newPath);
            }
            
            return true;
        }
        catch (Exception e)
        {
            throw new ServiceException("重命名OSS文件/目录失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    @Override
    public Map<String, Integer> countDirectoryContents(Long configId, String path)
    {
        GbStorageConfig config = gbStorageConfigService.selectGbStorageConfigById(configId);
        if (config == null)
        {
            throw new ServiceException("存储配置不存在");
        }

        String storageType = config.getStorageType();
        
        switch (storageType)
        {
            case "github":
                return countGithubDirectory(config, path);
            case "s3":
                return countS3Directory(config, path);
            case "aliyun_oss":
                return countOssDirectory(config, path);
            default:
                throw new ServiceException("不支持的存储类型: " + storageType);
        }
    }

    private Map<String, Integer> countGithubDirectory(GbStorageConfig config, String path)
    {
        Map<String, Integer> result = new HashMap<>();
        result.put("fileCount", 0);
        result.put("dirCount", 0);
        
        try
        {
            GHRepository repo = getGitHubRepository(config);
            String branch = config.getGithubBranch();
            
            countGithubDirectoryRecursive(repo, branch, path, result);
            
            return result;
        }
        catch (IOException e)
        {
            throw new ServiceException("统计GitHub目录失败: " + e.getMessage());
        }
    }

    private void countGithubDirectoryRecursive(GHRepository repo, String branch, String path, Map<String, Integer> result) throws IOException
    {
        List<GHContent> contents = repo.getDirectoryContent(path, branch);
        
        for (GHContent content : contents)
        {
            if (content.isFile() && !".gitkeep".equals(content.getName()))
            {
                result.put("fileCount", result.get("fileCount") + 1);
            }
            else if (content.isDirectory())
            {
                result.put("dirCount", result.get("dirCount") + 1);
                countGithubDirectoryRecursive(repo, branch, content.getPath(), result);
            }
        }
    }

    private Map<String, Integer> countS3Directory(GbStorageConfig config, String path)
    {
        Map<String, Integer> result = new HashMap<>();
        result.put("fileCount", 0);
        result.put("dirCount", 0);
        
        S3Client s3Client = null;
        try
        {
            s3Client = createS3Client(config);
            String bucket = config.getR2Bucket();
            
            // 确保路径以/结尾
            String prefix = path.endsWith("/") ? path : path + "/";
            
            countS3DirectoryRecursive(s3Client, bucket, prefix, result);
            
            return result;
        }
        catch (Exception e)
        {
            throw new ServiceException("统计S3目录失败: " + e.getMessage());
        }
        finally
        {
            if (s3Client != null)
            {
                s3Client.close();
            }
        }
    }

    private void countS3DirectoryRecursive(S3Client s3Client, String bucket, String prefix, Map<String, Integer> result)
    {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
            .bucket(bucket)
            .prefix(prefix)
            .delimiter("/")
            .build();
        
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        
        // 统计子目录
        for (CommonPrefix commonPrefix : listResponse.commonPrefixes())
        {
            result.put("dirCount", result.get("dirCount") + 1);
            // 递归统计子目录
            countS3DirectoryRecursive(s3Client, bucket, commonPrefix.prefix(), result);
        }
        
        // 统计文件
        for (S3Object s3Object : listResponse.contents())
        {
            String key = s3Object.key();
            if (!key.equals(prefix)) // 排除目录占位符本身
            {
                result.put("fileCount", result.get("fileCount") + 1);
            }
        }
    }

    private Map<String, Integer> countOssDirectory(GbStorageConfig config, String path)
    {
        Map<String, Integer> result = new HashMap<>();
        result.put("fileCount", 0);
        result.put("dirCount", 0);
        
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            String bucket = config.getOssBucket();
            
            // 确保路径以/结尾
            String prefix = path.endsWith("/") ? path : path + "/";
            
            countOssDirectoryRecursive(ossClient, bucket, prefix, result);
            
            return result;
        }
        catch (Exception e)
        {
            throw new ServiceException("统计OSS目录失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    private void countOssDirectoryRecursive(OSS ossClient, String bucket, String prefix, Map<String, Integer> result)
    {
        ListObjectsRequest listRequest = new ListObjectsRequest(bucket);
        listRequest.setPrefix(prefix);
        listRequest.setDelimiter("/");
        
        ObjectListing listing = ossClient.listObjects(listRequest);
        
        // 统计子目录
        for (String dirPath : listing.getCommonPrefixes())
        {
            result.put("dirCount", result.get("dirCount") + 1);
            // 递归统计子目录
            countOssDirectoryRecursive(ossClient, bucket, dirPath, result);
        }
        
        // 统计文件
        for (OSSObjectSummary summary : listing.getObjectSummaries())
        {
            String key = summary.getKey();
            if (!key.equals(prefix)) // 排除目录自身
            {
                result.put("fileCount", result.get("fileCount") + 1);
            }
        }
    }

    // ==================== 预签名URL生成方法 ====================

    /**
     * 生成S3预签名下载URL
     */
    private String getS3PresignedUrl(GbStorageConfig config, String path, int expirationMinutes)
    {
        S3Presigner presigner = null;
        try
        {
            // 创建凭证
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.getR2AccessKey(),
                config.getR2SecretKey()
            );

            // 处理endpoint，确保有协议头
            String endpoint = config.getR2PublicUrl();
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://"))
            {
                endpoint = "https://" + endpoint;
            }
            
            logger.info("生成S3预签名URL - Bucket: {}, Path: {}, Endpoint: {}", 
                config.getR2Bucket(), path, endpoint);

            // 创建S3 Presigner（配置path-style访问，避免bucket名称变成子域名）
            presigner = S3Presigner.builder()
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build())
                .build();

            // 创建GetObject请求（设置response-content-disposition强制下载）
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(config.getR2Bucket())
                .key(path)
                .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                .build();

            // 生成预签名请求
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

            // 获取预签名URL
            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }
        catch (Exception e)
        {
            logger.error("生成S3预签名URL失败: {}", e.getMessage(), e);
            throw new ServiceException("生成下载链接失败: " + e.getMessage());
        }
        finally
        {
            if (presigner != null)
            {
                presigner.close();
            }
        }
    }

    /**
     * 生成OSS预签名下载URL
     */
    private String getOssPresignedUrl(GbStorageConfig config, String path, int expirationMinutes)
    {
        OSS ossClient = null;
        try
        {
            ossClient = createOssClient(config);
            
            // 设置URL过期时间
            Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);
            
            // 提取文件名
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            
            // 创建请求，设置响应头强制下载
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(config.getOssBucket(), path);
            request.setExpiration(expiration);
            request.setMethod(HttpMethod.GET);
            request.addQueryParameter("response-content-disposition", "attachment; filename=\"" + fileName + "\"");
            
            // 生成预签名URL
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        }
        catch (Exception e)
        {
            logger.error("生成OSS预签名URL失败: {}", e.getMessage(), e);
            throw new ServiceException("生成下载链接失败: " + e.getMessage());
        }
        finally
        {
            if (ossClient != null)
            {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 获取GitHub文件直接下载URL
     */
    private String getGithubDirectDownloadUrl(GbStorageConfig config, String path)
    {
        // GitHub使用raw.githubusercontent.com直接访问
        String branch = config.getGithubBranch();
        if (StringUtils.isEmpty(branch))
        {
            branch = "main";
        }
        return String.format("https://raw.githubusercontent.com/%s/%s/%s/%s",
            config.getGithubOwner(),
            config.getGithubRepo(),
            branch,
            path
        );
    }
}
