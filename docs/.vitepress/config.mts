import { defineConfig } from 'vitepress'
import { mermaidPlugin } from './plugins/mermaid'
import { xincludePlugin, cleanupExternalFiles } from './plugins/xinclude'
import { autoConvertLinksPlugin } from './plugins/auto-convert-links'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

export default defineConfig({
  vue: {
    template: {
      compilerOptions: {
        isCustomElement: tag => tag === 'mermaid'
      }
    },
  },
  title: 'AI 建站运营平台',
  description: 'AI 建站运营平台技术文档（矩阵建站 + AI运营）',
  lang: 'zh-CN',
  base: '/',

  async buildEnd() {
    const docsDir = path.resolve(__dirname, '..')
    cleanupExternalFiles(docsDir)
  },

  ignoreDeadLinks: true,

  themeConfig: {
    nav: [
      { text: '首页', link: '/' },
      {
        text: '规划方案',
        items: [
          { text: 'AI建站运营平台技术方案', link: '/AI建站运营平台技术方案.md' },
          { text: '全行业建站工具升级方案', link: '/全行业建站工具升级方案.md' },
          { text: '综合站完善规划', link: '/综合站完善规划.md' },
        ]
      },
      {
        text: '工具与工作流',
        items: [
          { text: '工作流使用指南', link: '/工作流使用指南.md' },
          { text: '工作流工具编排系统使用指南', link: '/工作流工具编排系统使用指南.md' },
          { text: '内容工具工作流方案V2', link: '/内容工具工作流方案V2.md' },
          { text: '原子工具实现方案', link: '/原子工具实现方案.md' },
        ]
      },
      {
        text: '功能参考',
        items: [
          { text: '多语言支持解决方案', link: '/多语言支持解决方案.md' },
          { text: '多语言API支持说明', link: '/多语言API支持说明.md' },
          { text: '子站SEO内容战略指南', link: '/子站SEO内容战略指南.md' },
          { text: 'Cloudflare首页缓存方案对比', link: '/Cloudflare首页缓存方案对比.md' },
        ]
      },
    ],

    sidebar: [
      {
        text: '📌 规划方案',
        collapsed: false,
        items: [
          { text: 'AI建站运营平台技术方案', link: '/AI建站运营平台技术方案.md' },
          { text: '全行业建站工具升级方案', link: '/全行业建站工具升级方案.md' },
          { text: '综合站完善规划', link: '/综合站完善规划.md' },
        ]
      },
      {
        text: '🔧 工具与工作流',
        collapsed: false,
        items: [
          { text: '工作流使用指南', link: '/工作流使用指南.md' },
          { text: '工作流工具编排系统使用指南', link: '/工作流工具编排系统使用指南.md' },
          { text: '内容工具工作流方案V2', link: '/内容工具工作流方案V2.md' },
          { text: '原子工具实现方案', link: '/原子工具实现方案.md' },
        ]
      },
      {
        text: '🌍 功能参考',
        collapsed: false,
        items: [
          { text: '多语言支持解决方案', link: '/多语言支持解决方案.md' },
          { text: '多语言API支持说明', link: '/多语言API支持说明.md' },
          { text: '子站SEO内容战略指南', link: '/子站SEO内容战略指南.md' },
          { text: 'Cloudflare首页缓存方案对比', link: '/Cloudflare首页缓存方案对比.md' },
        ]
      },
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com' }
    ],

    footer: {
      message: 'AI 建站运营平台',
      copyright: 'Copyright © 2025-2026'
    },

    search: {
      provider: 'local'
    },

    outline: {
      level: [2, 3],
      label: '目录'
    },

    docFooter: {
      prev: '上一页',
      next: '下一页'
    },

    lastUpdated: {
      text: '最后更新于'
    }
  },

  markdown: {
    lineNumbers: true,
    config: (md) => {
      md.use(autoConvertLinksPlugin)
      md.use(xincludePlugin)
      md.use(mermaidPlugin)
    }
  },

  vite: {
    server: {
      fs: {
        strict: false,
        allow: ['..']
      }
    },
    build: {
      target: 'esnext'
    }
  }
})
