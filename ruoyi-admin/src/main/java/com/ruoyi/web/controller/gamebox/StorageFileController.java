package com.ruoyi.web.controller.gamebox;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.gamebox.domain.dto.StorageFileInfo;
import com.ruoyi.gamebox.service.IStorageFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储文件管理Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/gamebox/storage/files")
public class StorageFileController extends BaseController
{
    @Autowired
    private IStorageFileService storageFileService;

    /**
     * 列出文件
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:list')")
    @GetMapping("/list/{configId}")
    public AjaxResult listFiles(@PathVariable Long configId, @RequestParam(required = false) String path)
    {
        List<StorageFileInfo> files = storageFileService.listFiles(configId, path);
        return success(files);
    }

    /**
     * 上传文件
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/upload/{configId}")
    public AjaxResult uploadFile(
            @PathVariable Long configId,
            @RequestParam String path,
            @RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty())
        {
            return error("上传文件不能为空");
        }
        
        StorageFileInfo fileInfo = storageFileService.uploadFile(configId, path, file);
        return success(fileInfo);
    }

    /**
     * 删除文件
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:remove')")
    @Log(title = "存储文件管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configId}")
    public AjaxResult deleteFile(@PathVariable Long configId, @RequestParam String path)
    {
        boolean success = storageFileService.deleteFile(configId, path);
        return success ? success() : error("删除文件失败");
    }

    /**
     * 读取文本文件内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping("/read/{configId}")
    public AjaxResult readTextFile(@PathVariable Long configId, @RequestParam String path)
    {
        String content = storageFileService.readTextFile(configId, path);
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("content", content);
        return success(result);
    }

    /**
     * 更新文本文件内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储文件管理", businessType = BusinessType.UPDATE)
    @PutMapping("/update/{configId}")
    public AjaxResult updateTextFile(
            @PathVariable Long configId,
            @RequestBody Map<String, String> params)
    {
        String path = params.get("path");
        String content = params.get("content");
        
        if (path == null || content == null)
        {
            return error("路径和内容不能为空");
        }
        
        boolean success = storageFileService.updateTextFile(configId, path, content);
        return success ? success() : error("更新文件失败");
    }

    /**
     * 创建文件夹
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储文件管理", businessType = BusinessType.INSERT)
    @PostMapping("/folder/{configId}")
    public AjaxResult createFolder(
            @PathVariable Long configId,
            @RequestBody Map<String, String> params)
    {
        String parentPath = params.get("parentPath");
        String folderName = params.get("folderName");
        
        if (folderName == null || folderName.trim().isEmpty())
        {
            return error("文件夹名称不能为空");
        }
        
        boolean success = storageFileService.createFolder(configId, parentPath, folderName);
        return success ? success() : error("创建文件夹失败");
    }

    /**
     * 获取文件下载URL
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping("/download-url/{configId}")
    public AjaxResult getDownloadUrl(@PathVariable Long configId, @RequestParam String path)
    {
        String url = storageFileService.getDownloadUrl(configId, path);
        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("downloadUrl", url);
        return success(result);
    }

    /**
     * 下载文件(通过后端代理)
     * @deprecated 建议使用 getPresignedDownloadUrl 获取预签名URL,让客户端直接下载
     */
    @Deprecated
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping("/download/{configId}")
    public void downloadFile(
            @PathVariable Long configId,
            @RequestParam String path,
            HttpServletResponse response) throws IOException
    {
        // 获取文件流
        InputStream inputStream = storageFileService.downloadFile(configId, path);
        
        // 从路径中提取文件名
        String fileName = path;
        if (path.contains("/"))
        {
            fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        
        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", 
            "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应流
        try (OutputStream outputStream = response.getOutputStream())
        {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }

    /**
     * 获取文件预签名下载URL(推荐方式)
     * 客户端可使用返回的URL直接从存储服务下载,无需通过服务器代理
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping("/presigned-url/{configId}")
    public AjaxResult getPresignedDownloadUrl(
            @PathVariable Long configId,
            @RequestParam String path,
            @RequestParam(required = false, defaultValue = "60") Integer expirationMinutes)
    {
        String presignedUrl = storageFileService.getPresignedDownloadUrl(configId, path, expirationMinutes);
        return AjaxResult.success().put("url", presignedUrl);
    }

    /**
     * 重命名文件或文件夹
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:edit')")
    @Log(title = "存储文件管理", businessType = BusinessType.UPDATE)
    @PutMapping("/rename/{configId}")
    public AjaxResult renameFile(
            @PathVariable Long configId,
            @RequestParam String oldPath,
            @RequestParam String newName)
    {
        if (newName == null || newName.trim().isEmpty())
        {
            return error("新名称不能为空");
        }
        
        // 验证文件名（不能包含特殊字符）
        if (newName.matches(".*[<>:\"|?*\\\\/].*"))
        {
            return error("文件名不能包含特殊字符: < > : \" | ? * \\ /");
        }
        
        boolean success = storageFileService.renameFile(configId, oldPath, newName);
        return success ? success() : error("重命名失败");
    }

    /**
     * 统计目录内容
     */
    @PreAuthorize("@ss.hasPermi('gamebox:storage:query')")
    @GetMapping("/count/{configId}")
    public AjaxResult countDirectoryContents(@PathVariable Long configId, @RequestParam String path)
    {
        Map<String, Integer> counts = storageFileService.countDirectoryContents(configId, path);
        return success(counts);
    }
}
