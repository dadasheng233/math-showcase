<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProjects } from '@/api/project'

const router = useRouter()
const projects = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const keyword = ref('')

async function fetchProjects() {
  loading.value = true
  try {
    const res: any = await getProjects({ page: page.value, size: 12, keyword: keyword.value })
    projects.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  router.push(`/projects/${id}`)
}

onMounted(() => fetchProjects())
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">建模项目</h2>
      <p class="page-subtitle">管理你的数学建模项目文件</p>
    </div>

    <div class="toolbar">
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索项目..." prefix-icon="Search" clearable @keyup.enter="fetchProjects" @clear="fetchProjects" />
      </div>
      <el-button type="primary" @click="router.push('/projects/create')">
        <el-icon><Plus /></el-icon> 新建项目
      </el-button>
    </div>

    <div v-loading="loading" class="project-grid">
      <div v-for="project in projects" :key="project.id" class="project-card" @click="goDetail(project.id)">
        <div class="project-icon">
          <el-icon :size="32"><FolderOpened /></el-icon>
        </div>
        <div class="project-info">
          <h3>{{ project.title }}</h3>
          <p v-if="project.description">{{ project.description.slice(0, 80) }}...</p>
          <div class="project-meta">
            <span>{{ project.fileCount || 0 }} 个文件</span>
            <span>{{ new Date(project.createTime).toLocaleDateString() }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && projects.length === 0" class="empty-state">
      <el-icon :size="64"><FolderOpened /></el-icon>
      <p>还没有建模项目，快去创建吧</p>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.search-box {
  flex: 1;
  max-width: 360px;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 1000px) {
  .project-grid { grid-template-columns: 1fr; }
}

.project-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 14px;
  padding: 24px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.project-card:hover {
  border-color: rgba(102, 126, 234, 0.3);
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-2px);
}

.project-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2), rgba(118, 75, 162, 0.2));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  flex-shrink: 0;
}

.project-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px;
}

.project-info p {
  color: #8892b0;
  font-size: 13px;
  margin: 0 0 12px;
}

.project-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #5a5a7e;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #5a5a7e;
}

.empty-state p { margin-top: 16px; }
</style>
