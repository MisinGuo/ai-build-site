package com.ruoyi.gamebox.domain.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类验证结果DTO
 * 
 * @author ruoyi
 */
public class CategoryValidationResult
{
    /** 是否验证通过 */
    private boolean valid;
    
    /** 错误信息列表 */
    private List<String> errors;
    
    /** 盒子分类是否有效 */
    private boolean boxCategoryValid;
    
    /** 盒子分类名称 */
    private String boxCategoryName;
    
    /** 无效的游戏数量 */
    private int invalidGameCount;
    
    /** 无效游戏的分类名称列表（去重） */
    private List<String> invalidGameCategoryNames;
    
    public CategoryValidationResult()
    {
        this.valid = true;
        this.errors = new ArrayList<>();
        this.boxCategoryValid = true;
        this.invalidGameCount = 0;
        this.invalidGameCategoryNames = new ArrayList<>();
    }
    
    public void addError(String error)
    {
        this.errors.add(error);
        this.valid = false;
    }
    
    public boolean isValid()
    {
        return valid;
    }
    
    public void setValid(boolean valid)
    {
        this.valid = valid;
    }
    
    public List<String> getErrors()
    {
        return errors;
    }
    
    public void setErrors(List<String> errors)
    {
        this.errors = errors;
    }
    
    public boolean isBoxCategoryValid()
    {
        return boxCategoryValid;
    }
    
    public void setBoxCategoryValid(boolean boxCategoryValid)
    {
        this.boxCategoryValid = boxCategoryValid;
    }
    
    public String getBoxCategoryName()
    {
        return boxCategoryName;
    }
    
    public void setBoxCategoryName(String boxCategoryName)
    {
        this.boxCategoryName = boxCategoryName;
    }
    
    public int getInvalidGameCount()
    {
        return invalidGameCount;
    }
    
    public void setInvalidGameCount(int invalidGameCount)
    {
        this.invalidGameCount = invalidGameCount;
    }
    
    public List<String> getInvalidGameCategoryNames()
    {
        return invalidGameCategoryNames;
    }
    
    public void setInvalidGameCategoryNames(List<String> invalidGameCategoryNames)
    {
        this.invalidGameCategoryNames = invalidGameCategoryNames;
    }
}
