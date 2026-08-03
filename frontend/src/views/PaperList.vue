<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPapers } from '@/api/paper'

const router = useRouter()
const papers = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const keyword = ref('')

async function fetchPapers() {
  loading.value = true
  try {
    const res: any = await getPapers({ page: page.value, size: 12, keyword: keyword.value })
    papers.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  router.push(`/papers/${id}`)
}

onMounted(() => fetchPapers())
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">论文管理</h2>
      <p class="page-subtitle">管理已上传的论文</p>
    </div>

    <div class="toolbar">
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索论文..." prefix-icon="Search" clearable @keyup.enter="fetchPapers" @clear="fetchPapers" />
      </div>
      <el-button type="primary" @click="router.push('/papers/upload')">
        <el-icon><Upload /></el-icon> 上传论文
      </el-button>
    </div>

    <div v-loading="loading">
      <el-table :data="papers" style="width: 100%" @row-click="(row: any) => goDetail(row.id)">
        <el-table-column prop="title" label="论文标题" min-width="300">
          <template #default="{ row }">
            <span class="table-title">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="authors" label="作者" width="200" />
        <el-table-column prop="keywords" label="关键词" min-width="200">
          <template #default="{ row }">
            <el-tag v-if="row.keywords" v-for="tag in (row.keywords || '').split(',').slice(0, 3)" :key="tag" size="small" style="margin-right: 4px;">
              {{ tag }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="浏览量" width="100">
          <template #default="{ row }">
            <span style="display: flex; align-items: center; gap: 4px;">
              <el-icon><View /></el-icon> {{ row.viewCount || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="160">
          <template #default="{ row }">
            {{ new Date(row.createTime).toLocaleDateString() }}
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="!loading && papers.length === 0" class="empty-state">
      <el-icon :size="64"><Document /></el-icon>
      <p>还没有论文</p>
    </div>
  </div>
</template>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-box {
  flex: 1;
  max-width: 360px;
}

.table-title {
  color: #667eea;
  cursor: pointer;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #5a5a7e;
}
</style>
