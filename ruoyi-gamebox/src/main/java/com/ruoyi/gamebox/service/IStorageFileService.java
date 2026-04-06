package com.ruoyi.gamebox.service;

import com.ruoyi.gamebox.domain.dto.StorageFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 存储文件Service接口
 * 
 * @author ruoyi
 */
public interface IStorageFileService
{
    /**
     * 列出指定路径下的文件列表
     * 
     * @param configId 存储配置ID
     * @param path 路径（为空则列出根目录）
     * @return 文件列表
     */
    List<StorageFileInfo> listFiles(Long configId, String path);

    /**
     * 上传文件
     * 
     * @param configId 存储配置ID
     * @param path 目标路径
     * @param file 文件
     * @return 上传后的文件信息
     */
    StorageFileInfo uploadFile(Long configId, String path, MultipartFile file);

    /**
     * 删除文件
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @return 是否成功
     */
    boolean deleteFile(Long configId, String path);

    /**
     * 检查文件是否存在
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @return 是否存在
     */
    boolean checkFileExists(Long configId, String path);

    /**
     * 读取文本文件内容
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @return 文件内容
     */
    String readTextFile(Long configId, String path);

    /**
     * 更新文本文件内容
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @param content 新内容
     * @return 是否成功
     */
    boolean updateTextFile(Long configId, String path, String content);

    /**
     * 获取文件下载URL
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @return 下载URL
     */
    String getDownloadUrl(Long configId, String path);

    /**
     * 下载文件（返回输入流）
     * @deprecated 建议使用 getPresignedDownloadUrl 获取预签名URL,让客户端直接下载
     * @param configId 存储配置ID
     * @param path 文件路径
     * @return 文件输入流
     */
    @Deprecated
    InputStream downloadFile(Long configId, String path);

    /**
     * 获取文件的预签名下载URL(推荐使用)
     * 客户端可以使用此URL直接从存储服务下载文件,无需通过服务器代理
     * 
     * @param configId 存储配置ID
     * @param path 文件路径
     * @param expirationMinutes URL有效期(分钟),默认60分钟
     * @return 预签名下载URL
     */
    String getPresignedDownloadUrl(Long configId, String path, Integer expirationMinutes);

    /**
     * 创建文件夹
     * 
     * @param configId 存储配置ID
     * @param parentPath 父路径
     * @param folderName 文件夹名称
     * @return 是否成功
     */
    boolean createFolder(Long configId, String parentPath, String folderName);

    /**
     * 重命名文件或文件夹
     * 
     * @param configId 存储配置ID
     * @param oldPath 旧路径
     * @param newName 新名称（不包含路径）
     * @return 是否成功
     */
    boolean renameFile(Long configId, String oldPath, String newName);

    /**
     * 统计目录内容（递归统计所有文件和子目录数量）
     * 
     * @param configId 存储配置ID
     * @param path 目录路径
     * @return 包含fileCount和dirCount的Map
     */
    java.util.Map<String, Integer> countDirectoryContents(Long configId, String path);
}
