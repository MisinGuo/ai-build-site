<template>
  <div class="app-container home">
    <!-- 系统概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon game-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">游戏总数</div>
              <div class="stat-value">{{ statistics.totalGames }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon box-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">游戏盒子</div>
              <div class="stat-value">{{ statistics.totalBoxes }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon article-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">文章总数</div>
              <div class="stat-value">{{ statistics.totalArticles }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon master-article-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">主文章</div>
              <div class="stat-value">{{ statistics.totalMasterArticles }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon site-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">推广站点</div>
              <div class="stat-value">{{ statistics.totalSites }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 系统介绍 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template v-slot:header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><InfoFilled /></el-icon>
                游戏推广 CMS 系统
              </span>
            </div>
          </template>
          <div class="intro-content">
            <p class="intro-desc">
              基于若依框架开发的游戏推广内容管理系统，专注于游戏内容的智能化生产与多站点分发。
            </p>
            <div class="feature-grid">
              <div class="feature-item">
                <el-icon class="feature-icon" color="#409EFF"><EditPen /></el-icon>
                <div class="feature-text">
                  <h4>AI 内容生成</h4>
                  <p>集成 AI 自动生成游戏攻略、评测等优质内容</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#67C23A"><Link /></el-icon>
                <div class="feature-text">
                  <h4>智能关联管理</h4>
                  <p>游戏盒子、游戏、文章的多对多关系管理</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#E6A23C"><Grid /></el-icon>
                <div class="feature-text">
                  <h4>多站点管理</h4>
                  <p>统一后台管理多个推广站点内容</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#F56C6C"><Connection /></el-icon>
                <div class="feature-text">
                  <h4>边缘部署</h4>
                  <p>Next.js 站点部署在 Cloudflare，SEO 友好</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#909399"><Tools /></el-icon>
                <div class="feature-text">
                  <h4>工作流编排</h4>
                  <p>可视化工具编排，自动化内容生产流程</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#13C2C2"><MagicStick /></el-icon>
                <div class="feature-text">
                  <h4>多语言翻译</h4>
                  <p>支持内容多语言翻译和本地化管理</p>
                </div>
              </div>
            </div>
            <div class="version-info">
              <el-tag type="primary">当前版本: v{{ version }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷入口 -->
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template v-slot:header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Operation /></el-icon>
                快捷入口
              </span>
            </div>
          </template>
          <div class="quick-links">
            <el-row :gutter="15">
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('game-mgmt/game')">
                  <el-icon class="link-icon" color="#409EFF"><Trophy /></el-icon>
                  <span>游戏列表</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('game-mgmt/box')">
                  <el-icon class="link-icon" color="#67C23A"><Box /></el-icon>
                  <span>游戏盒子</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('content/article')">
                  <el-icon class="link-icon" color="#E6A23C"><Document /></el-icon>
                  <span>文章管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('config/site')">
                  <el-icon class="link-icon" color="#F56C6C"><Monitor /></el-icon>
                  <span>站点管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('content/workflow')">
                  <el-icon class="link-icon" color="#909399"><SetUp /></el-icon>
                  <span>工作流配置</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('config/category')">
                  <el-icon class="link-icon" color="#409EFF"><List /></el-icon>
                  <span>分类管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('config/storage')">
                  <el-icon class="link-icon" color="#67C23A"><FolderOpened /></el-icon>
                  <span>存储配置</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('ai-config/atomic-tool')">
                  <el-icon class="link-icon" color="#E6A23C"><Tools /></el-icon>
                  <span>原子工具</span>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 技术栈和系统信息 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover">
          <template v-slot:header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Cpu /></el-icon>
                技术栈
              </span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8">
              <div class="tech-stack">
                <h4 class="tech-title">后端技术</h4>
                <ul class="tech-list">
                  <li><el-tag size="small">Spring Boot 2.7</el-tag></li>
                  <li><el-tag size="small">Spring Security</el-tag></li>
                  <li><el-tag size="small">MyBatis</el-tag></li>
                  <li><el-tag size="small">MySQL 8.0</el-tag></li>
                  <li><el-tag size="small">Redis 6.x</el-tag></li>
                  <li><el-tag size="small">JWT</el-tag></li>
                </ul>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <div class="tech-stack">
                <h4 class="tech-title">管理后台</h4>
                <ul class="tech-list">
                  <li><el-tag size="small" type="success">Vue 3</el-tag></li>
                  <li><el-tag size="small" type="success">Element Plus</el-tag></li>
                  <li><el-tag size="small" type="success">Vite</el-tag></li>
                  <li><el-tag size="small" type="success">Pinia</el-tag></li>
                  <li><el-tag size="small" type="success">Axios</el-tag></li>
                  <li><el-tag size="small" type="success">Vue Router</el-tag></li>
                </ul>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="8">
              <div class="tech-stack">
                <h4 class="tech-title">推广站点</h4>
                <ul class="tech-list">
                  <li><el-tag size="small" type="warning">Next.js 14</el-tag></li>
                  <li><el-tag size="small" type="warning">React 18</el-tag></li>
                  <li><el-tag size="small" type="warning">Tailwind CSS</el-tag></li>
                  <li><el-tag size="small" type="warning">Cloudflare Workers</el-tag></li>
                  <li><el-tag size="small" type="warning">SSR/ISR</el-tag></li>
                  <li><el-tag size="small" type="warning">GitHub Actions</el-tag></li>
                </ul>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover">
          <template v-slot:header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Bell /></el-icon>
                系统提示
              </span>
            </div>
          </template>
          <div class="tips-content">
            <el-alert
              title="欢迎使用游戏推广 CMS 系统"
              type="success"
              :closable="false"
              style="margin-bottom: 15px"
            >
              <template #default>
                开始管理您的游戏推广内容吧！
              </template>
            </el-alert>
            <div class="tip-item">
              <el-icon color="#409EFF"><CircleCheck /></el-icon>
              <span>支持批量导入游戏和内容数据</span>
            </div>
            <div class="tip-item">
              <el-icon color="#67C23A"><CircleCheck /></el-icon>
              <span>使用 AI 工具快速生成文章内容</span>
            </div>
            <div class="tip-item">
              <el-icon color="#E6A23C"><CircleCheck /></el-icon>
              <span>通过工作流编排自动化内容生产</span>
            </div>
            <div class="tip-item">
              <el-icon color="#F56C6C"><CircleCheck /></el-icon>
              <span>多站点内容统一管理和分发</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getDashboardStatistics } from '@/api/gamebox/dashboard'

const router = useRouter()
const route = useRoute()
const version = ref('1.0.0')

// 统计数据
const statistics = ref({
  totalGames: '-',
  totalBoxes: '-',
  totalArticles: '-',
  totalMasterArticles: '-',
  totalSites: '-'
})

// 获取统计数据
const loadStatistics = async () => {
  try {
    const res = await getDashboardStatistics()
    if (res.code === 200 && res.data) {
      statistics.value.totalGames = res.data.totalGames ?? '-'
      statistics.value.totalBoxes = res.data.totalBoxes ?? '-'
      statistics.value.totalArticles = res.data.totalArticles ?? '-'
      statistics.value.totalMasterArticles = res.data.totalMasterArticles ?? '-'
      statistics.value.totalSites = res.data.totalSites ?? '-'
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 跳转页面 - 使用菜单路由的相对路径
const goTo = (menuPath) => {
  router.push('/gamebox/' + menuPath)
}

// 监听路由变化，当返回首页时重新加载数据
watch(() => route.path, (newPath) => {
  if (newPath === '/index' || newPath === '/') {
    loadStatistics()
  }
})

// 组件挂载时加载数据
onMounted(() => {
  loadStatistics()
})
</script>

<style scoped lang="scss">
.home {
  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;

  // 统计卡片样式
  .overview-cards {
    .stat-card {
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-5px);
      }

      .stat-content {
        display: flex;
        align-items: center;
        padding: 10px;

        .stat-icon {
          width: 60px;
          height: 60px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
          font-size: 28px;
          margin-right: 20px;

          &.game-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }

          &.box-icon {
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
          }

          &.article-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
          }

          &.master-article-icon {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            color: white;
          }

          &.site-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: white;
          }
        }

        .stat-info {
          flex: 1;

          .stat-title {
            font-size: 14px;
            color: #999;
            margin-bottom: 8px;
          }

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #333;
          }
        }
      }
    }
  }

  // 卡片标题样式
  .card-header {
    .card-title {
      display: flex;
      align-items: center;
      font-size: 16px;
      font-weight: 600;

      .el-icon {
        margin-right: 8px;
      }
    }
  }

  // 系统介绍样式
  .intro-content {
    .intro-desc {
      font-size: 14px;
      color: #666;
      line-height: 1.8;
      margin-bottom: 20px;
    }

    .feature-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 15px;
      margin-bottom: 20px;

      .feature-item {
        display: flex;
        align-items: flex-start;
        padding: 12px;
        background: #f5f7fa;
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          background: #eef1f6;
          transform: translateX(5px);
        }

        .feature-icon {
          font-size: 24px;
          margin-right: 12px;
          margin-top: 4px;
        }

        .feature-text {
          flex: 1;

          h4 {
            margin: 0 0 4px 0;
            font-size: 14px;
            font-weight: 600;
            color: #333;
          }

          p {
            margin: 0;
            font-size: 12px;
            color: #666;
            line-height: 1.5;
          }
        }
      }
    }

    .version-info {
      padding-top: 15px;
      border-top: 1px solid #eee;
    }
  }

  // 快捷入口样式
  .quick-links {
    .quick-link-item {
      display: flex;
      align-items: center;
      justify-content: center;
      flex-direction: column;
      padding: 25px 15px;
      background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      margin-bottom: 15px;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .link-icon {
        font-size: 32px;
        margin-bottom: 10px;
      }

      span {
        font-size: 14px;
        font-weight: 500;
        color: #333;
      }
    }
  }

  // 技术栈样式
  .tech-stack {
    .tech-title {
      margin-top: 0;
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: 600;
      color: #333;
      padding-bottom: 10px;
      border-bottom: 2px solid #409EFF;
    }

    .tech-list {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        margin-bottom: 10px;

        .el-tag {
          width: 100%;
          text-align: center;
          padding: 8px;
        }
      }
    }
  }

  // 系统提示样式
  .tips-content {
    .tip-item {
      display: flex;
      align-items: center;
      padding: 10px;
      margin-bottom: 10px;
      background: #f5f7fa;
      border-radius: 6px;

      .el-icon {
        margin-right: 10px;
        font-size: 18px;
      }

      span {
        font-size: 13px;
        color: #666;
      }
    }
  }
}

// 响应式调整
@media (max-width: 768px) {
  .home {
    .intro-content .feature-grid {
      grid-template-columns: 1fr;
    }

    .stat-content .stat-icon {
      width: 50px !important;
      height: 50px !important;
      font-size: 24px !important;
      margin-right: 15px !important;
    }

    .stat-value {
      font-size: 24px !important;
    }
  }
}
</style>

