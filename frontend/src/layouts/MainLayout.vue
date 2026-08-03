<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { computed } from 'vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo" @click="router.push('/gallery')">
        <span class="logo-icon">Σ</span>
        <span class="logo-text">Math Showcase</span>
      </div>

      <nav class="nav">
        <router-link to="/gallery" class="nav-item" :class="{ active: activeMenu === '/gallery' }">
          <el-icon><PictureFilled /></el-icon>
          <span>论文画廊</span>
        </router-link>

        <router-link to="/projects" class="nav-item" :class="{ active: activeMenu.startsWith('/projects') }">
          <el-icon><FolderOpened /></el-icon>
          <span>建模项目</span>
        </router-link>

        <router-link to="/papers" class="nav-item" :class="{ active: activeMenu.startsWith('/papers') && activeMenu !== '/gallery' }">
          <el-icon><Document /></el-icon>
          <span>论文管理</span>
        </router-link>

        <router-link to="/papers/upload" class="nav-item" :class="{ active: activeMenu === '/papers/upload' }">
          <el-icon><Upload /></el-icon>
          <span>上传论文</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="32" icon="UserFilled" />
          <span class="username">{{ userStore.nickname || userStore.username }}</span>
        </div>
        <el-button text class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>

    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 240px;
  background: rgba(15, 15, 30, 0.95);
  border-right: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
  backdrop-filter: blur(20px);
}

.logo {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 10px;
  color: #8892b0;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
}

.nav-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #ccd6f6;
}

.nav-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
  color: #667eea;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-size: 13px;
  color: #ccd6f6;
}

.logout-btn {
  color: #ff6b6b !important;
}

.main-content {
  flex: 1;
  margin-left: 240px;
  min-height: 100vh;
}
</style>
