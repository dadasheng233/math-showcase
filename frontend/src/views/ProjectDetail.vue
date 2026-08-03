<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProjectDetail, updateProject, deleteProject, togglePublish } from '@/api/project'
import { getApiUrl } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const project = ref<any>(null)
const loading = ref(false)
const isEditing = ref(false)

const editForm = reactive({
  title: '',
  description: '',
  tags: ''
})

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

function getFileTypeLabel(type: string) {
  const map: Record<string, string> = {
    pdf: 'PDF 文档', doc: 'Word 文档', python: 'Python 代码', matlab: 'MATLAB 代码',
    jupyter: 'Jupyter 笔记', java: 'Java 代码', c: 'C/C++ 代码', image: '图片',
    data: '数据文件', archive: '压缩包'
  }
  return map[type] || '其他文件'
}

function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function downloadFile(file: any) {
  if (!file.filePath) {
    ElMessage.warning('文件路径不存在')
    return
  }
  const url = getApiUrl('/files/uploads/' + file.filePath)
  const a = document.createElement('a')
  a.href = url
  a.download = file.fileName
  a.target = '_blank'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

async function doTogglePublish() {
  if (!project.value) return
  try {
    await togglePublish(project.value.id)
    const newStatus = project.value.status === 'DEPLOYED' ? 'ACTIVE' : 'DEPLOYED'
    project.value.status = newStatus
    ElMessage.success(newStatus === 'DEPLOYED' ? '项目已部署上线' : '项目已下线')
  } catch (err: any) {
    ElMessage.error(err.message || '操作失败')
  }
}

function startEdit() {
  if (!project.value) return
  editForm.title = project.value.title || ''
  editForm.description = project.value.description || ''
  editForm.tags = project.value.tags || ''
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function saveEdit() {
  if (!editForm.title.trim()) {
    ElMessage.warning('请输入项目标题')
    return
  }
  loading.value = true
  try {
    await updateProject(project.value.id, {
      title: editForm.title.trim(),
      description: editForm.description.trim(),
      tags: editForm.tags.trim()
    })
    await fetchDetail()
    isEditing.value = false
    ElMessage.success('修改已保存')
  } catch (err: any) {
    ElMessage.error(err.message || '保存失败')
  } finally {
    loading.value = false
  }
}

async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定要删除这个项目吗？所有相关文件也将被删除，此操作不可恢复。', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteProject(project.value.id)
    ElMessage.success('已删除')
    router.push('/projects')
  } catch (err: any) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '删除失败')
    }
  }
}

onMounted(() => fetchDetail())
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
      <div v-if="project && !isEditing" class="top-actions">
        <el-button @click="startEdit">
          <el-icon><Edit /></el-icon> 编辑
        </el-button>
        <el-button type="danger" @click="handleDelete">
          <el-icon><Delete /></el-icon> 删除
        </el-button>
      </div>
    </div>

    <!-- Edit Mode -->
    <div v-if="isEditing" class="edit-form card">
      <h3 class="section-title">编辑项目信息</h3>
      <el-form label-position="top" size="large">
        <el-form-item label="项目标题" required>
          <el-input v-model="editForm.title" placeholder="项目标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="editForm.description" type="textarea" :rows="5" placeholder="描述项目背景、方法和结论..." maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="editForm.tags" placeholder="逗号分隔，例如：国赛,优化模型,Python" />
        </el-form-item>
        <div class="form-actions">
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" :loading="loading" @click="saveEdit">保存修改</el-button>
        </div>
      </el-form>
    </div>

    <div v-if="project && !isEditing" class="detail-content">
      <div class="detail-header">
        <div>
          <h2 class="page-title">{{ project.title }}</h2>
          <p class="page-subtitle">{{ project.description || '暂无描述' }}</p>
        </div>
        <div class="header-actions">
          <el-tag v-if="project.tags" v-for="tag in project.tags.split(',')" :key="tag" style="margin-left: 8px;">
            {{ tag }}
          </el-tag>
          <el-divider direction="vertical" />
          <el-button
            :type="project.status === 'DEPLOYED' ? 'success' : 'default'"
            size="small"
            @click="doTogglePublish"
          >
            <el-icon><Monitor /></el-icon>
            {{ project.status === 'DEPLOYED' ? '已部署' : '部署上线' }}
          </el-button>
        </div>
      </div>

      <div class="card" style="margin-top: 24px;">
        <h3 style="color: #fff; margin-bottom: 20px;">项目文件 ({{ (project.files || []).length }})</h3>
        <div v-if="project.files && project.files.length > 0" class="file-list">
          <div v-for="file in project.files" :key="file.id" class="file-item">
            <el-icon :size="28" :color="'#667eea'">
              <component :is="getFileIcon(file.fileType)" />
            </el-icon>
            <div class="file-info">
              <span class="file-name">{{ file.fileName }}</span>
              <span class="file-meta">
                <el-tag size="small" type="info">{{ getFileTypeLabel(file.fileType) }}</el-tag>
                {{ formatSize(file.fileSize) }} · {{ new Date(file.createTime).toLocaleDateString() }}
              </span>
            </div>
            <el-button class="download-btn" size="small" circle @click.stop="downloadFile(file)">
              <el-icon><Download /></el-icon>
            </el-button>
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.top-actions {
  display: flex;
  gap: 10px;
}

.edit-form {
  max-width: 700px;
  margin-top: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
  margin: 0 0 16px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 12px;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  transition: all 0.3s ease;
}

.file-item:hover {
  background: rgba(102, 126, 234, 0.06);
  border-color: rgba(102, 126, 234, 0.2);
}

.file-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-name {
  color: #ccd6f6;
  font-size: 14px;
  font-weight: 500;
}

.file-meta {
  color: #5a5a7e;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.download-btn {
  opacity: 0;
  transform: translateX(8px);
  transition: all 0.3s ease;
}

.file-item:hover .download-btn {
  opacity: 1;
  transform: translateX(0);
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #5a5a7e;
}
</style>
