<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProjectDetail } from '@/api/project'

const route = useRoute()
const router = useRouter()
const project = ref<any>(null)
const loading = ref(false)

async function fetchDetail() {
  loading.value = true
  try {
    const res: any = await getProjectDetail(Number(route.params.id))
    project.value = res.data
  } finally {
    loading.value = false
  }
}

function getFileIcon(type: string) {
  const map: Record<string, string> = {
    pdf: 'Document', doc: 'Document', python: 'DataAnalysis', matlab: 'Monitor',
    jupyter: 'Notebook', java: 'Coffee', c: 'Cpu', image: 'Picture',
    data: 'DataBoard', archive: 'FolderOpened'
  }
  return map[type] || 'FolderOpened'
}

function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

onMounted(() => fetchDetail())
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <div v-if="project" class="detail-content">
      <div class="detail-header">
        <div>
          <h2 class="page-title">{{ project.title }}</h2>
          <p class="page-subtitle">{{ project.description || '暂无描述' }}</p>
        </div>
        <el-tag v-if="project.tags" v-for="tag in project.tags.split(',')" :key="tag" style="margin-left: 8px;">
          {{ tag }}
        </el-tag>
      </div>

      <div class="card" style="margin-top: 24px;">
        <h3 style="color: #fff; margin-bottom: 20px;">项目文件 ({{ (project.files || []).length }})</h3>
        <div v-if="project.files && project.files.length > 0" class="file-list">
          <div v-for="file in project.files" :key="file.id" class="file-item">
            <el-icon :size="24" :color="'#667eea'">
              <component :is="getFileIcon(file.fileType)" />
            </el-icon>
            <div class="file-info">
              <span class="file-name">{{ file.fileName }}</span>
              <span class="file-meta">{{ formatSize(file.fileSize) }} · {{ new Date(file.createTime).toLocaleDateString() }}</span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-icon :size="48"><Folder /></el-icon>
          <p>暂无文件</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-content {
  max-width: 900px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
  transition: background 0.2s;
}

.file-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-name {
  color: #ccd6f6;
  font-size: 14px;
}

.file-meta {
  color: #5a5a7e;
  font-size: 12px;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #5a5a7e;
}
</style>
