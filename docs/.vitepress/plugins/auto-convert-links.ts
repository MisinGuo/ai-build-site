import path from 'path';

// 需要转换为 xinclude 的文件扩展名（源代码文件）
const CODE_EXTENSIONS = [
  '.java', '.xml', '.js', '.ts', '.vue', '.jsx', '.tsx',
  '.py', '.go', '.rs', '.cpp', '.c', '.h', '.hpp',
  '.sql', '.json', '.yaml', '.yml', '.properties', '.conf',
  '.css', '.scss', '.sass', '.less', '.mts', '.cts'
];

/**
 * 判断路径是否指向源代码文件
 */
function isCodeFile(filePath: string): boolean {
  const ext = path.extname(filePath).toLowerCase();
  return CODE_EXTENSIONS.includes(ext);
}

/**
 * 判断链接是否需要转换（相对路径的源代码文件）
 */
function isConvertibleLink(linkPath: string): boolean {
  // 跳过 HTTP/HTTPS 链接
  if (linkPath.startsWith('http://') || linkPath.startsWith('https://')) {
    return false;
  }
  
  // 跳过锚点链接
  if (linkPath.startsWith('#')) {
    return false;
  }
  
  // 跳过绝对路径
  if (path.isAbsolute(linkPath)) {
    return false;
  }
  
  // 处理所有相对路径
  return true;
}

/**
 * markdown-it 插件：自动将源代码链接转换为 xinclude 注释
 * 在 normalize 之前处理，需要在 xinclude 插件之前执行
 */
export function autoConvertLinksPlugin(md: any) {
  md.core.ruler.before('normalize', 'auto_convert_links', (state: any) => {
    const srcText = state.src as string;
    
    // 匹配 Markdown 链接：[text](path)
    const linkPattern = /\[([^\]]+)\]\(([^)]+)\)/g;
    
    let result = srcText;
    let match;
    const replacements: Array<{ original: string; converted: string }> = [];
    
    while ((match = linkPattern.exec(srcText)) !== null) {
      const [fullMatch, linkText, linkPath] = match;
      
      // 分离路径和行号
      const [pathPart, lineRange] = linkPath.split('#');
      
      // 只转换相对路径的源代码文件链接
      if (!isConvertibleLink(pathPart) || !isCodeFile(pathPart)) {
        continue;
      }
      
      // 生成 xinclude 注释
      const lineRangePart = lineRange ? `#${lineRange}` : '';
      const xinclude = `<!--@xinclude: ${pathPart}${lineRangePart} -->`;
      
      // 保留原始链接文本作为标题
      const converted = `**${linkText}**：\n${xinclude}`;
      
      replacements.push({
        original: fullMatch,
        converted
      });
    }
    
    // 应用所有替换
    for (const { original, converted } of replacements) {
      result = result.replace(original, converted);
    }
    
    // 只有在有变化时才更新
    if (result !== srcText) {
      state.src = result;
    }
    
    return false;
  });
}
