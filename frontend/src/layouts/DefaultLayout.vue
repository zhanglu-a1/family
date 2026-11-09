<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Clock,
  House,
  Notebook,
  Picture,
  Setting,
  SwitchButton,
  User
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => (route.meta.title as string | undefined) ?? '首页')
const displayName = computed(() => userStore.user?.nickname || userStore.user?.username || '家人')
const avatarChar = computed(() => displayName.value.slice(0, 1))

onMounted(() => {
  if (userStore.accessToken && !userStore.user) {
    userStore.loadProfile().catch(() => undefined)
  }
})

async function handleLogout() {
  userStore.clear()
  ElMessage.success('已退出登录')
  await router.push('/login')
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="216px" class="sidebar">
      <div class="brand">
        <div class="brand-mark fm-serif">家</div>
        <div>
          <div class="brand-title fm-serif">家庭回忆录</div>
          <div class="brand-sub">把故事留下来</div>
        </div>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
        background-color="#3f332b"
        text-color="#ded1c2"
        active-text-color="#f4c793"
      >
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/timeline">
          <el-icon><Clock /></el-icon>
          <span>时间轴</span>
        </el-menu-item>
        <el-menu-item index="/memoirs">
          <el-icon><Notebook /></el-icon>
          <span>回忆录</span>
        </el-menu-item>
        <el-menu-item index="/media">
          <el-icon><Picture /></el-icon>
          <span>相册</span>
        </el-menu-item>
        <el-menu-item index="/persons">
          <el-icon><User /></el-icon>
          <span>人物档案</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>空间设置</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-foot fm-muted">M0 · 基础骨架</div>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title fm-serif">{{ pageTitle }}</div>
        <el-dropdown trigger="click">
          <div class="user-chip">
            <el-avatar :size="30" class="avatar">{{ avatarChar }}</el-avatar>
            <span class="user-name">{{ displayName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="SwitchButton" @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

.sidebar {
  display: flex;
  flex-direction: column;
  background: var(--fm-sidebar);
  padding: 22px 0;
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px 22px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: #f4c793;
  color: #3f332b;
  font-size: 22px;
  font-weight: 700;
}

.brand-title {
  color: #f6efe6;
  font-size: 16px;
  letter-spacing: 1px;
}

.brand-sub {
  margin-top: 2px;
  color: #a99786;
  font-size: 12px;
}

.menu {
  border-right: none;
  flex: 1;
}

.sidebar-foot {
  padding: 12px 20px 0;
  font-size: 12px;
  color: #8b7a69;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  background: var(--fm-panel);
  border-bottom: 1px solid var(--fm-line);
}

.header-title {
  font-size: 18px;
  letter-spacing: 2px;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 999px;
}

.user-chip:hover {
  background: var(--fm-accent-soft);
}

.avatar {
  background: var(--fm-accent);
  color: #fff;
}

.user-name {
  font-size: 14px;
  color: var(--fm-ink);
}

.main {
  background: var(--fm-bg);
  padding: 24px;
}

@media (max-width: 768px) {
  .sidebar {
    display: none;
  }

  .main {
    padding: 16px;
  }
}
</style>
