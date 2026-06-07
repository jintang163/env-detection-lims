<template>
  <el-container class="app-container">
    <el-header class="app-header">
      <div class="logo">
        <el-icon><Document /></el-icon>
        <span class="title">LIMS 环境检测实验室信息管理系统</span>
      </div>
      <div class="user-info">
        <el-dropdown>
          <span class="user-name">
            <el-icon><User /></el-icon>
            管理员
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-container>
      <el-aside width="220px" class="app-aside">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/detection/data">
            <el-icon><DataLine /></el-icon>
            <span>检测数据录入</span>
          </el-menu-item>
          <el-menu-item index="/detection/original">
            <el-icon><Files /></el-icon>
            <span>原始记录管理</span>
          </el-menu-item>
          <el-menu-item index="/detection/review">
            <el-icon><EditPen /></el-icon>
            <span>数据审核</span>
          </el-menu-item>
          <el-menu-item index="/detection/trace">
            <el-icon><Connection /></el-icon>
            <span>数据溯源</span>
          </el-menu-item>
          <el-menu-item index="/detection/oos">
            <el-icon><Warning /></el-icon>
            <span>超标预警(OOS)</span>
          </el-menu-item>
          <el-sub-menu index="quality">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>质量与质控管理</span>
            </template>
            <el-menu-item index="/detection/quality/rule">
              <el-icon><Setting /></el-icon>
              <span>质控规则配置</span>
            </el-menu-item>
            <el-menu-item index="/detection/quality/sample">
              <el-icon><Box /></el-icon>
              <span>质控样品管理</span>
            </el-menu-item>
            <el-menu-item index="/detection/quality/plan">
              <el-icon><Calendar /></el-icon>
              <span>质控计划</span>
            </el-menu-item>
            <el-menu-item index="/detection/quality/chart">
              <el-icon><TrendCharts /></el-icon>
              <span>质控图分析</span>
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="advancedQuality">
            <template #title>
              <el-icon><Medal /></el-icon>
              <span>高级质量管理</span>
            </template>
            <el-menu-item index="/detection/proficiency">
              <el-icon><Trophy /></el-icon>
              <span>能力验证与比对</span>
            </el-menu-item>
            <el-menu-item index="/detection/standardCurve">
              <el-icon><Histogram /></el-icon>
              <span>标准曲线管理</span>
            </el-menu-item>
            <el-menu-item index="/detection/stability">
              <el-icon><TrendCharts /></el-icon>
              <span>稳定性考察</span>
            </el-menu-item>
            <el-menu-item index="/detection/capa">
              <el-icon><CircleCheck /></el-icon>
              <span>CAPA管理</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style lang="scss">
.app-container {
  height: 100vh;
}

.app-header {
  background: linear-gradient(90deg, #001529, #002140);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;

  .logo {
    display: flex;
    align-items: center;
    gap: 12px;

    .el-icon {
      font-size: 28px;
      color: #409EFF;
    }

    .title {
      font-size: 20px;
      font-weight: 600;
      letter-spacing: 1px;
    }
  }

  .user-info {
    .user-name {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .el-icon {
        font-size: 18px;
      }
    }
  }
}

.app-aside {
  background: #001529;

  :deep(.el-menu) {
    border-right: none;

    .el-menu-item {
      height: 56px;
      line-height: 56px;

      &:hover {
        background: rgba(64, 158, 255, 0.2) !important;
      }
    }
  }
}

.app-main {
  background: #f0f2f5;
  padding: 20px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
