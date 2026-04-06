<template>
  <div class="app-container home">
    <!-- Hero Banner -->
    <div class="hero-banner">
      <div class="hero-content">
        <div class="hero-logo">
          <el-icon><MagicStick /></el-icon>
        </div>
        <h1 class="hero-title">AI 建站运营平台</h1>
        <p class="hero-subtitle">矩阵建站 · AI 运营 · 智能内容 · 全球分发</p>
        <div class="hero-tags">
          <el-tag effect="plain" round>Spring Boot 2.5</el-tag>
          <el-tag effect="plain" round type="success">Vue 3</el-tag>
          <el-tag effect="plain" round type="warning">Next.js 14</el-tag>
          <el-tag effect="plain" round type="danger">Cloudflare Workers</el-tag>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card" @click="goTo('/aisite/matrix')">
          <div class="stat-content">
            <div class="stat-icon matrix-icon">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">矩阵站组</div>
              <div class="stat-value">{{ stats.matrixGroups }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card" @click="goTo('/aisite/sites')">
          <div class="stat-content">
            <div class="stat-icon site-icon">
              <el-icon><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">推广站点</div>
              <div class="stat-value">{{ stats.sites }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card" @click="goTo('/aisite/content-items')">
          <div class="stat-content">
            <div class="stat-icon content-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">AI 内容条数</div>
              <div class="stat-value">{{ stats.contentItems }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="stat-card" @click="goTo('/aisite/operations')">
          <div class="stat-content">
            <div class="stat-icon task-icon">
              <el-icon><Operation /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">运营任务</div>
              <div class="stat-value">{{ stats.operationTasks }}</div>
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
                AI 建站运营平台
              </span>
            </div>
          </template>
          <div class="intro-content">
            <p class="intro-desc">
              基于若依框架构建的全行业 AI 建站运营平台，支持矩阵站点创建、AI 内容自动生产、工作流编排与全球边缘分发，一站式管理从建站到增长的全链路。
            </p>
            <div class="feature-grid">
              <div class="feature-item">
                <el-icon class="feature-icon" color="#409EFF"><Grid /></el-icon>
                <div class="feature-text">
                  <h4>矩阵站点管理</h4>
                  <p>统一管理多行业、多语言矩阵站组，批量部署</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#67C23A"><MagicStick /></el-icon>
                <div class="feature-text">
                  <h4>AI 内容生产</h4>
                  <p>原子工具 + 工作流编排，自动生成高质量内容</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#E6A23C"><SetUp /></el-icon>
                <div class="feature-text">
                  <h4>工作流编排</h4>
                  <p>可视化配置内容生产流程，AI 工具自由组合</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#F56C6C"><Connection /></el-icon>
                <div class="feature-text">
                  <h4>边缘全球分发</h4>
                  <p>Next.js + Cloudflare Workers，SSR/ISR SEO 友好</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#909399"><TrendCharts /></el-icon>
                <div class="feature-text">
                  <h4>SEO 数据看板</h4>
                  <p>关键词排名、流量趋势、历史快照一览</p>
                </div>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#13C2C2"><Translate /></el-icon>
                <div class="feature-text">
                  <h4>多语言支持</h4>
                  <p>内容多语言翻译与本地化，覆盖全球市场</p>
                </div>
              </div>
            </div>
            <div class="version-info">
              <el-tag type="primary">当前版本: v1.0.0</el-tag>
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
                <div class="quick-link-item" @click="goTo('/aisite/matrix')">
                  <el-icon class="link-icon" color="#409EFF"><Grid /></el-icon>
                  <span>矩阵站管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/sites')">
                  <el-icon class="link-icon" color="#67C23A"><Monitor /></el-icon>
                  <span>站点管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/content-items')">
                  <el-icon class="link-icon" color="#E6A23C"><Document /></el-icon>
                  <span>内容管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/operations')">
                  <el-icon class="link-icon" color="#F56C6C"><Operation /></el-icon>
                  <span>运营任务</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/workflows')">
                  <el-icon class="link-icon" color="#909399"><SetUp /></el-icon>
                  <span>工作流</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/categories')">
                  <el-icon class="link-icon" color="#409EFF"><List /></el-icon>
                  <span>分类管理</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/storage-configs')">
                  <el-icon class="link-icon" color="#67C23A"><FolderOpened /></el-icon>
                  <span>存储配置</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="quick-link-item" @click="goTo('/aisite/atomic-tools')">
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
              title="欢迎使用 AI 建站运营平台"
              type="success"
              :closable="false"
              style="margin-bottom: 15px"
            >
              <template #default>
                开始构建您的矩阵站点吧！
              </template>
            </el-alert>
            <div class="tip-item">
              <el-icon color="#409EFF"><CircleCheck /></el-icon>
              <span>在「矩阵站管理」中创建站组并关联站点</span>
            </div>
            <div class="tip-item">
              <el-icon color="#67C23A"><CircleCheck /></el-icon>
              <span>配置原子工具和工作流，自动化内容生产</span>
            </div>
            <div class="tip-item">
              <el-icon color="#E6A23C"><CircleCheck /></el-icon>
              <span>在「AI 建站」模块一键生成站点配置</span>
            </div>
            <div class="tip-item">
              <el-icon color="#F56C6C"><CircleCheck /></el-icon>
              <span>SEO 看板实时监控关键词与流量趋势</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const stats = ref({
  matrixGroups: '-',
  sites: '-',
  contentItems: '-',
  operationTasks: '-'
})

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped lang="scss">
.home {
  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;

  // Hero Banner
  .hero-banner {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
    border-radius: 12px;
    padding: 48px 40px;
    margin-bottom: 20px;
    text-align: center;

    .hero-logo {
      font-size: 56px;
      color: #e94560;
      margin-bottom: 16px;
      .el-icon { font-size: 56px; }
    }

    .hero-title {
      font-size: 32px;
      font-weight: 700;
      color: #fff;
      margin: 0 0 12px 0;
      letter-spacing: 2px;
    }

    .hero-subtitle {
      font-size: 16px;
      color: #a8b2c8;
      margin: 0 0 20px 0;
      letter-spacing: 1px;
    }

    .hero-tags {
      display: flex;
      justify-content: center;
      gap: 10px;
      flex-wrap: wrap;

      .el-tag {
        border-color: rgba(255, 255, 255, 0.3);
        color: #fff;
        background: rgba(255, 255, 255, 0.08);
      }
    }
  }

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

          &.matrix-icon {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
          }

          &.site-icon {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            color: white;
          }

          &.content-icon {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
          }

          &.task-icon {
            background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
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

