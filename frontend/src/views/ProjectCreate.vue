<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { createProject, uploadProjectFiles } from '@/api/project'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const uploadLoading = ref(false)
const createdId = ref<number | null>(null)
const step = ref(1)

const form = reactive({
  title: '',
  description: '',
  tags: ''
})

const uploadRef = ref()
const fileList = ref<any[]>([])

async function handleCreate() {
  if (!form.title) {
    ElMessage.warning('请输入项目标题')
    return
  }
  loading.value = true
  try {
    const res: any = await createProject(form)
    createdId.value = res.data.id
    ElMessage.success('项目创建成功，请上传文件')
    step.value = 2
  } catch (err: any) {
    ElMessage.error(err.message || '创建失败')
  } finally {
    loading.value = false
  }
}

async function handleUpload() {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  uploadLoading.value = true
  try {
    const formData = new FormData()
    fileList.value.forEach((f: any) => {
      formData.append('files', f.raw)
    })
    await uploadProjectFiles(createdId.value!, formData)
    ElMessage.success('文件上传成功')
    router.push(`/projects/${createdId.value}`)
  } catch (err: any) {
    ElMessage.error(err.message || '上传失败')
  } finally {
    uploadLoading.value = false
  }
}

function handleSkip() {
  router.push(`/projects/${createdId.value}`)
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">新建建模项目</h2>
      <p class="page-subtitle">创建项目并上传建模相关文件</p>
    </div>

    <div class="steps">
      <div class="step" :class="{ active: step === 1, done: step > 1 }">
        <div class="step-dot">1</div>
        <span>创建项目</span>
      </div>
      <div class="step-line" :class="{ active: step > 1 }"></div>
      <div class="step" :class="{ active: step === 2 }">
        <div class="step-dot">2</div>
        <span>上传文件</span>
      </div>
    </div>

    <div v-if="step === 1" class="card">
      <el-form label-position="top" size="large">
        <el-form-item label="项目标题" required>
          <el-input v-model="form.title" placeholder="例如：2024国赛B题-无人机协同" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="项目描述">
          <el-input v-model="form.description" type="textarea" :rows="5" placeholder="描述这次建模的背景、方法和结论..." maxlength="2000" show-word-limit />
        </el-form-item>

        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="用逗号分隔，例如：国赛,优化模型,Python" />
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="handleCreate">下一步 - 上传文件</el-button>
      </el-form>
    </div>

    <div v-if="step === 2" class="card">
      <h3 style="color: #fff; margin-bottom: 24px;">上传建模文件</h3>
      <p style="color: #8892b0; margin-bottom: 16px;">上传代码、数据、图表、结果等文件（支持批量上传）</p>

      <el-upload
        ref="uploadRef"
        v-model:file-list="fileList"
        drag
        multiple
        :auto-upload="false"
        :limit="20"
      >
        <el-icon :size="48" class="upload-icon"><UploadFilled /></el-icon>
        <div class="upload-text">
          <p>将文件拖拽到此处，或<em>点击选择</em></p>
          <p class="upload-hint">支持代码、数据、图片、PDF 等各种格式</p>
        </div>
      </el-upload>

      <div class="upload-actions">
        <el-button @click="handleSkip">跳过，稍后上传</el-button>
        <el-button type="primary" :loading="uploadLoading" @click="handleUpload">上传并完成</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.steps {
  display: flex;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 20px;
}

.step {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #5a5a7e;
  font-size: 14px;
}

.step.active {
  color: #667eea;
}

.step.done {
  color: #52c41a;
}

.step-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 2px solid currentColor;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}

.step.active .step-dot {
  background: #667eea;
  border-color: #667eea;
  color: #fff;
}

.step.done .step-dot {
  background: #52c41a;
  border-color: #52c41a;
  color: #fff;
}

.step-line {
  flex: 1;
  height: 2px;
  background: rgba(255, 255, 255, 0.06);
  margin: 0 16px;
}

.step-line.active {
  background: #52c41a;
}

.upload-icon {
  color: #667eea;
}

.upload-text p {
  color: #8892b0;
  margin: 8px 0 0;
}

.upload-text em {
  color: #667eea;
  font-style: normal;
}

.upload-hint {
  font-size: 12px;
  color: #5a5a7e !important;
}

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
