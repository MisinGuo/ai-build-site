import fs from "fs";
import path from "path";
import crypto from "crypto";

type Range = { start?: number; end?: number };

// 缓存已处理的外部文件映射：原路径 -> 新路径
const externalFileCache = new Map<string, string>();

/**
 * 解析行号范围，支持格式：
 * - #L10 (单行)
 * - #L10-L20 (行范围)
 */
function parseLineRange(fragment?: string): Range | null {
  if (!fragment) return null;
  const m1 = fragment.match(/^L(\d+)$/);
  if (m1) return { start: Number(m1[1]), end: Number(m1[1]) };
  const m2 = fragment.match(/^L(\d+)-L(\d+)$/);
  if (m2) return { start: Number(m2[1]), end: Number(m2[2]) };
  return null;
}

/**
 * HTML 转义
 */
function escapeHtml(s: string) {
  return s
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

/**
 * 生成文件的哈希后缀（用于避免文件名冲突）
 */
function generateFileHash(filePath: string): string {
  const content = fs.readFileSync(filePath, 'utf-8');
  return crypto.createHash('md5').update(content).digest('hex').substring(0, 8);
}

/**
 * 查找 docs 根目录（包含 .vitepress 的目录）
 */
function findDocsRoot(startPath: string): string | null {
  let currentDir = startPath;
  while (currentDir !== path.dirname(currentDir)) {
    if (fs.existsSync(path.join(currentDir, '.vitepress'))) {
      return currentDir;
    }
    currentDir = path.dirname(currentDir);
  }
  return null;
}

/**
 * 处理外部 .md 文件：复制到 external 目录并返回新路径
 */
function handleExternalMdFile(absolutePath: string, docsRoot: string): string {
  // 检查缓存
  if (externalFileCache.has(absolutePath)) {
    return externalFileCache.get(absolutePath)!;
  }
  
  const externalDir = path.join(docsRoot, 'external');
  
  // 创建 external 目录
  if (!fs.existsSync(externalDir)) {
    fs.mkdirSync(externalDir, { recursive: true });
  }
  
  // 生成唯一的文件名：原文件名 + 哈希后缀
  const originalName = path.basename(absolutePath, '.md');
  const hash = generateFileHash(absolutePath);
  const newFileName = `${originalName}-${hash}.md`;
  const destPath = path.join(externalDir, newFileName);
  
  // 读取源文件内容
  const content = fs.readFileSync(absolutePath, 'utf-8');
  const relativeSrc = path.relative(path.dirname(docsRoot), absolutePath).replace(/\\/g, '/');
  
  // 添加位置指引头部
  const header = `---
externalFile: true
originalPath: ${relativeSrc}
---

::: tip 📍 外部文档位置
本文档来自项目外部：\`${relativeSrc}\`
:::

`;
  
  // 写入目标文件（添加头部）
  fs.writeFileSync(destPath, header + content, 'utf-8');
  
  // 生成 VitePress 路径（相对于 docs 根目录，去掉 .md 扩展名）
  const vitepressPath = `/external/${originalName}-${hash}`;
  
  // 缓存映射
  externalFileCache.set(absolutePath, vitepressPath);
  
  console.log(`  ✓ 外部文件已复制: ${relativeSrc} -> ${vitepressPath}`);
  
  return vitepressPath;
}

/**
 * 检测文件类型并返回语言标识
 */
function detectLanguage(filePath: string): string {
  const ext = path.extname(filePath).toLowerCase();
  const langMap: Record<string, string> = {
    '.vue': 'vue',
    '.js': 'javascript',
    '.mjs': 'javascript',
    '.cjs': 'javascript',
    '.ts': 'typescript',
    '.mts': 'typescript',
    '.cts': 'typescript',
    '.jsx': 'jsx',
    '.tsx': 'tsx',
    '.java': 'java',
    '.xml': 'xml',
    '.sql': 'sql',
    '.json': 'json',
    '.yaml': 'yaml',
    '.yml': 'yaml',
    '.md': 'markdown',
    '.css': 'css',
    '.scss': 'scss',
    '.sass': 'sass',
    '.less': 'less',
    '.html': 'html',
    '.htm': 'html',
    '.py': 'python',
    '.sh': 'bash',
    '.bash': 'bash',
    '.bat': 'bat',
    '.cmd': 'bat',
    '.properties': 'properties',
    '.conf': 'ini',
    '.ini': 'ini',
    '.toml': 'toml',
    '.dockerfile': 'dockerfile',
    '.gitignore': 'txt',
    '.env': 'properties',
    '': 'txt'
  };
  return langMap[ext] || 'text';
}

/**
 * 渲染为 Markdown 代码块（让 VitePress 正常处理）
 */
function renderAsCodeBlock(lines: string[], sourceFile: string) {
  const lang = detectLanguage(sourceFile);
  const codeContent = lines.join('\n');
  
  // 生成标准的 Markdown 代码块，VitePress 会正常渲染它
  return `\n::: details 📄 源文件: ${sourceFile}\n\`\`\`${lang}\n${codeContent}\n\`\`\`\n:::\n`;
}

/**
 * markdown-it 插件：解析 <!--@xinclude: xxx --> 语法和自动处理外部链接
 */
export function xincludePlugin(md: any) {
  // 使用 before_parse 钩子处理源文本
  md.core.ruler.before('normalize', 'xinclude', (state: any) => {
    const env = state.env || {};
    const mdFilePath: string | undefined = env.path;
    const mdFileDir = mdFilePath ? path.dirname(mdFilePath) : process.cwd();
    
    const srcText = state.src as string;
    
    let out = "";
    let lastIndex = 0;
    const allMatches: Array<{ index: number; length: number; srcPath: string; frag?: string }> = [];
    
    // 1. 收集 <!--@xinclude: ... --> 指令
    const xincludeRe = /<!--\s*@xinclude:\s*([^\s]+?)\s*-->/g;
    let match;
    while ((match = xincludeRe.exec(srcText)) !== null) {
      const [srcPath, frag] = match[1].split("#");
      allMatches.push({
        index: match.index,
        length: match[0].length,
        srcPath,
        frag
      });
    }
    
    // 2. 智能处理 .md 文件链接
    const docsRoot = findDocsRoot(mdFileDir);
    if (!docsRoot) {
      console.warn(`⚠️  无法找到 docs 根目录: ${mdFileDir}`);
    }
    
    const fixedSrc = srcText.replace(/\[([^\]]+)\]\(([^)]+\.md(?:#[^)]*)?)\)/g, (match, text, href) => {
      // 跳过 HTTP 链接、绝对路径
      if (/^https?:\/\//i.test(href) || href.startsWith('/')) return match;
      
      const [pathPart, frag] = href.split('#');
      const fragmentPart = frag ? `#${frag}` : '';
      
      // 跳过模板链接（包含占位符的链接，如 XXX, YYYY-MM-DD 等）
      if (/XXX|YYYY-MM|YYYY-MM-DD/.test(pathPart)) {
        return match;
      }
      
      try {
        // 解析绝对路径
        const abs = path.resolve(mdFileDir, pathPart);
        const normalizedAbs = path.normalize(abs);
        const normalizedDocsRoot = docsRoot ? path.normalize(docsRoot) : '';
        
        // 检查文件是否存在
        if (!fs.existsSync(normalizedAbs)) {
          const relPath = path.relative(docsRoot || process.cwd(), mdFilePath || '');
          // 始终输出警告信息（构建时也显示）
          console.warn(`⚠️  [链接错误] ${href} -> 文件不存在 (在: ${relPath})`);
          
          // 生成错误提示块，在页面中显示，避免死链接导致构建失败
          const errorBlock = `\n::: danger 🔗 链接错误\n文档链接指向不存在的文件：\`${href}\`\n\n**可能的原因：**\n- 文件已被移动或删除\n- 文件路径不正确\n- 这是一个模板占位符链接\n\n*当前文档：${relPath}*\n:::\n`;
          return errorBlock;
        }
        
        // 文件存在，检查是否在 docs 目录内
        const isInsideDocs = normalizedAbs.startsWith(normalizedDocsRoot);
        
        if (isInsideDocs) {
          // 文件在 docs 目录内，VitePress 会自动处理
          return match;
        } else {
          // 文件在 docs 目录外，需要复制到 external 目录
          if (!docsRoot) {
            console.error(`❌ 无法处理外部文件链接 (找不到 docs 根目录): ${href}`);
            return match;
          }
          
          const newPath = handleExternalMdFile(normalizedAbs, docsRoot);
          return `[${text}](${newPath}${fragmentPart})`;
        }
      } catch (error) {
        console.error(`❌ 处理链接时出错: ${href}`, error);
        return match;
      }
    });
    
    // 3. 收集普通 Markdown 链接 [text](path)，自动处理非 .md 文件避免 404
    const linkRe = /\[([^\]]+)\]\(([^)]+)\)/g;
    while ((match = linkRe.exec(fixedSrc)) !== null) {
      const href = match[2];
      
      // 跳过 HTTP 链接、锚点、绝对路径
      if (/^https?:\/\//i.test(href) || href.startsWith('#') || href.startsWith('/')) continue;
      
      const [pathPart, frag] = href.split('#');
      const ext = path.extname(pathPart).toLowerCase();
      
      // .md 文件保持正常链接跳转（已在上面处理）
      if (ext === '.md') continue;
      
      // 没有扩展名的跳过（可能是目录）
      if (!ext) continue;
      
      try {
        // 处理相对路径（包括 ./, ../, 和直接的相对路径如 .vitepress/）
        const abs = path.resolve(mdFileDir, pathPart);
        
        // 文件不存在则跳过
        if (!fs.existsSync(abs)) continue;
        
        // 自动内嵌显示（避免 404）
        allMatches.push({
          index: match.index,
          length: match[0].length,
          srcPath: pathPart,
          frag
        });
      } catch (error) {
        continue;
      }
    }
    
    // 按位置排序，避免重复处理
    allMatches.sort((a, b) => a.index - b.index);
    
    // 如果没有 xinclude 匹配，但有 .md 链接修复，返回修复后的内容
    if (allMatches.length === 0) {
      if (fixedSrc !== srcText) {
        state.src = fixedSrc;
        return true;
      }
      return false;
    }
    
    // 4. 统一处理所有 xinclude 匹配
    for (const m of allMatches) {
      out += fixedSrc.slice(lastIndex, m.index);
      lastIndex = m.index + m.length;
      
      const range = parseLineRange(m.frag);
      
      try {
        let text: string;
        const isHttp = /^https?:\/\//i.test(m.srcPath);
        
        if (isHttp) {
          out += `<div class="xinclude-error">❌ 暂不支持远程文件: ${escapeHtml(m.srcPath)}<br>请使用本地文件或在构建配置中预下载</div>`;
          continue;
        }
        
        // 尝试多个可能的路径
        let abs: string;
        let fileFound = false;
        
        if (path.isAbsolute(m.srcPath)) {
          abs = m.srcPath;
          fileFound = fs.existsSync(abs);
        } else {
          // 1. 首先尝试相对于当前markdown文件的路径
          abs = path.resolve(mdFileDir, m.srcPath);
          fileFound = fs.existsSync(abs);
          
          // 2. 如果找不到，尝试相对于docs根目录（向上查找直到找到.vitepress目录）
          if (!fileFound) {
            let currentDir = mdFileDir;
            while (currentDir !== path.dirname(currentDir)) {
              if (fs.existsSync(path.join(currentDir, '.vitepress'))) {
                const docsRootPath = path.resolve(currentDir, m.srcPath);
                if (fs.existsSync(docsRootPath)) {
                  abs = docsRootPath;
                  fileFound = true;
                  break;
                }
              }
              currentDir = path.dirname(currentDir);
            }
          }
        }
        
        if (!fileFound) {
          throw { code: 'ENOENT', message: '文件不存在' };
        }
        
        text = fs.readFileSync(abs, 'utf-8');
        
        let lines = text.replace(/\r\n/g, "\n").split("\n");
        
        if (range?.start) {
          const start = Math.max(1, range.start);
          const end = range.end ? Math.max(start, range.end) : start;
          lines = lines.slice(start - 1, end);
          out += renderAsCodeBlock(lines, `${m.srcPath} (L${start}${end !== start ? `-L${end}` : ''})`);
        } else {
          out += renderAsCodeBlock(lines, m.srcPath);
        }
      } catch (error: any) {
        const errorMsg = error?.code === 'ENOENT' 
          ? `文件不存在` 
          : error?.message || String(error);
        
        out += `<div class="xinclude-error">❌ 无法加载文件: ${escapeHtml(m.srcPath)}<br>错误: ${escapeHtml(errorMsg)}</div>`;
      }
    }
    
    out += fixedSrc.slice(lastIndex);
    
    // 更新源文本
    state.src = out;
    return true;
  });
}

/**
 * 清理 external 目录（用于构建后清理临时文件）
 */
export function cleanupExternalFiles(docsDir: string) {
  const externalDir = path.join(docsDir, 'external');
  
  if (fs.existsSync(externalDir)) {
    try {
      // 只删除带哈希后缀的文件（由插件生成的）
      const files = fs.readdirSync(externalDir);
      let cleanedCount = 0;
      
      for (const file of files) {
        // 匹配格式: 文件名-8位哈希.md
        if (/-[a-f0-9]{8}\.md$/.test(file)) {
          fs.unlinkSync(path.join(externalDir, file));
          cleanedCount++;
        }
      }
      
      console.log(`🧹 已清理 ${cleanedCount} 个临时外部文件`);
      
      // 如果目录为空，删除目录
      const remainingFiles = fs.readdirSync(externalDir);
      if (remainingFiles.length === 0) {
        fs.rmdirSync(externalDir);
      }
    } catch (error) {
      console.error('清理外部文件时出错:', error);
    }
  }
  
  // 清空缓存
  externalFileCache.clear();
}
