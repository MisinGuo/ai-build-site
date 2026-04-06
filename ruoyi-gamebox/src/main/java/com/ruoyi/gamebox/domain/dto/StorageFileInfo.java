package com.ruoyi.gamebox.domain.dto;

import java.util.Date;

/**
 * 存储文件信息DTO
 * 
 * @author ruoyi
 */
public class StorageFileInfo
{
    /** 文件路径 */
    private String path;

    /** 文件名称 */
    private String name;

    /** 文件类型：file-文件, directory-目录 */
    private String type;

    /** 文件大小（字节） */
    private Long size;

    /** 文件MIME类型 */
    private String mimeType;

    /** 最后修改时间 */
    private Date lastModified;

    /** 是否可编辑（文本文件） */
    private Boolean editable;

    /** 下载URL */
    private String downloadUrl;

    public StorageFileInfo()
    {
    }

    public StorageFileInfo(String path, String name, String type)
    {
        this.path = path;
        this.name = name;
        this.type = type;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long getSize()
    {
        return size;
    }

    public void setSize(Long size)
    {
        this.size = size;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public Boolean getEditable()
    {
        return editable;
    }

    public void setEditable(Boolean editable)
    {
        this.editable = editable;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl)
    {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString()
    {
        return "StorageFileInfo{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", mimeType='" + mimeType + '\'' +
                ", lastModified=" + lastModified +
                ", editable=" + editable +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
