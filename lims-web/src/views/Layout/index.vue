<template>
  <a-layout class="layout-container" :class="{ 'sider-collapsed': appStore.collapsed }">
    <a-layout-sider
      v-model:collapsed="appStore.collapsed"
      collapsible
      :trigger="null"
      class="sider"
    >
      <div class="logo">
        <img src="/vite.svg" alt="logo" class="logo-img" />
        <span v-show="!appStore.collapsed" class="logo-text">LIMS系统</span>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="inline"
        theme="dark"
        @click="handleMenuClick"
      >
        <a-menu-item v-for="menu in menus" :key="menu.path">
          <component :is="menu.icon" />
          <span>{{ menu.title }}</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">
          <span class="trigger" @click="appStore.toggleCollapsed">
            <component :is="appStore.collapsed ? MenuUnfoldOutlined : MenuFoldOutlined" />
          </span>
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item>首页</a-breadcrumb-item>
            <a-breadcrumb-item>{{ currentTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar size="small" style="background-color: #1890ff">
                {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
              </a-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || '用户' }}</span>
              <DownOutlined />
            </div>
            <template #overlay>
              <a-menu @click="handleUserMenu">
                <a-menu-item key="profile">
                  <UserOutlined /> 个人中心
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout">
                  <LogoutOutlined /> 退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>

      <a-layout-footer class="footer">
        环境检测LIMS系统 ©{{ new Date().getFullYear() }} Created by LIMS Team
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  HomeOutlined,
  UserOutlined,
  FileTextOutlined,
  FormOutlined,
  DollarOutlined,
  QrcodeOutlined,
  DatabaseOutlined,
  SwapOutlined,
  SaveOutlined,
  DeleteOutlined,
  DownOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const selectedKeys = ref<string[]>([route.path])

const menus = [
  { path: '/dashboard', title: '首页', icon: HomeOutlined },
  { path: '/customer', title: '客户管理', icon: UserOutlined },
  { path: '/contract', title: '合同管理', icon: FileTextOutlined },
  { path: '/entrust', title: '委托单管理', icon: FormOutlined },
  { path: '/quotation', title: '报价单管理', icon: DollarOutlined },
  { path: '/sample-register', title: '样品登记', icon: FormOutlined },
  { path: '/sample-label', title: '样品标识', icon: QrcodeOutlined },
  { path: '/sample-storage', title: '样品存储', icon: DatabaseOutlined },
  { path: '/sample-transfer', title: '样品流转跟踪', icon: SwapOutlined },
  { path: '/sample-retain', title: '留样管理', icon: SaveOutlined },
  { path: '/sample-disposal', title: '样品处置', icon: DeleteOutlined }
]

const currentTitle = computed(() => {
  const menu = menus.find(m => m.path === route.path)
  return menu?.title || '首页'
})

const handleMenuClick = ({ key }: { key: string }) => {
  selectedKeys.value = [key]
  router.push(key)
}

const handleUserMenu = ({ key }: { key: string }) => {
  if (key === 'logout') {
    Modal.confirm({
      title: '确认退出',
      content: '您确定要退出登录吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        await userStore.logout()
        message.success('退出成功')
        router.push('/login')
      }
    })
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.sider {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  height: 64px;
  padding: 0 16px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  overflow: hidden;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 12px;
}

.logo-text {
  white-space: nowrap;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: sticky;
  top: 0;
  z-index: 99;
  margin-left: 200px;
  transition: margin-left 0.2s;
}

.sider-collapsed .header {
  margin-left: 80px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.breadcrumb {
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 8px;
}

.username {
  font-size: 14px;
}

.content {
  margin: 24px;
  margin-left: 224px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 184px);
  transition: margin-left 0.2s;
}

.sider-collapsed .content {
  margin-left: 104px;
}

.footer {
  text-align: center;
  margin-left: 200px;
  transition: margin-left 0.2s;
}

.sider-collapsed .footer {
  margin-left: 80px;
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
