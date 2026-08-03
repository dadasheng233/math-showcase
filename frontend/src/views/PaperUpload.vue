<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { createPaper, uploadPaperFile, uploadPaperCover, uploadPaperAttachments } from '@/api/paper'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const uploadLoading = ref(false)
const createdId = ref<number | null>(null)
const step = ref(1)

const form = reactive({
  title: '',
  authors: '',
  abstractText: '',
  keywords: ''
})

const coverFileList = ref<any[]>([])
const uploadRef = ref()
const fileList = ref<any[]>([])
const attachmentFileList = ref<any[]>([])

async function handleCreate() {
  if (!form.title) {
    ElMessage.warning('请输入论文标题')
    return
  }
  loading.value = true
  try {
    const res: any = await createPaper({ ...form, coverImage: '' })
    createdId.value = res.data.id

    // 如果选了封面，先上传封面
    if (coverFileList.value.length > 0) {
      const coverFile = coverFileList.value[0].raw
      if (coverFile) {
        const coverFd = new FormData()
        coverFd.append('file', coverFile)
        await uploadPaperCover(createdId.value!, coverFd)
      }
    }

    ElMessage.success('论文信息创建成功')
    step.value = 2
  } catch (err: any) {
    ElMessage.error(err.message || '创建失败')
  } finally {
    loading.value = false
  }
}

async function handleUpload() {
  let file: File | null = null
  if (uploadRef.value?.uploadFiles?.length > 0) {
    file = uploadRef.value.uploadFiles[0].raw
  }
  if (!file && fileList.value.length > 0) {
    file = fileList.value[0].raw
  }

  if (!file) {
    ElMessage.warning('请选择论文文件')
    return
  }

  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    await uploadPaperFile(createdId.value!, formData)

    // 上传附件
    if (attachmentFileList.value.length > 0) {
      const attFd = new FormData()
      attachmentFileList.value.forEach((f: any) => {
        if (f.raw) attFd.append('files', f.raw)
      })
      await uploadPaperAttachments(createdId.value!, attFd)
    }

    ElMessage.success('论文上传成功')
    router.push(`/papers/${createdId.value}`)
  } catch (err: any) {
    const msg = err?.response?.data?.message || err?.message || '上传失败'
    ElMessage.error(msg)
  } finally {
    uploadLoading.value = false
  }
}

function handleSkip() {
  router.push(`/papers/${createdId.value}`)
}
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">上传论文</h2>
      <p class="page-subtitle">发布你的数学建模论文</p>
    </div>

    <div class="steps">
      <div class="step" :class="{ active: step === 1, done: step > 1 }">
        <div class="step-dot">1</div>
        <span>填写信息</span>
      </div>
      <div class="step-line" :class="{ active: step > 1 }"></div>
      <div class="step" :class="{ active: step === 2 }">
        <div class="step-dot">2</div>
        <span>上传论文</span>
      </div>
    </div>

    <div v-if="step === 1" class="card">
      <el-form label-position="top" size="large" style="max-width: 700px;">
        <el-form-item label="论文标题" required>
          <el-input v-model="form.title" placeholder="输入论文完整标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="作者">
          <el-input v-model="form.authors" placeholder="多个作者用逗号分隔" />
        </el-form-item>

        <el-form-item label="摘要">
          <el-input v-model="form.abstractText" type="textarea" :rows="5" placeholder="输入论文摘要..." maxlength="2000" show-word-limit />
        </el-form-item>

        <el-form-item label="关键词">
          <el-input v-model="form.keywords" placeholder="用逗号分隔，例如：多因素分析,NIPT,胎儿异常" />
        </el-form-item>

        <el-form-item label="封面图片（选填）">
          <el-upload
            v-model:file-list="coverFileList"
            list-type="picture-card"
            :auto-upload="false"
            :limit="1"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="handleCreate">下一步 - 上传论文</el-button>
      </el-form>
    </div>

    <div v-if="step === 2" class="card">
      <h3 style="color: #fff; margin-bottom: 24px;">上传论文文件</h3>

      <el-upload
        ref="uploadRef"
        v-model:file-list="fileList"
        drag
        :auto-upload="false"
        :limit="1"
        accept=".pdf,.doc,.docx"
      >
        <el-icon :size="48" class="upload-icon"><UploadFilled /></el-icon>
        <div class="upload-text">
          <p>将论文文件拖拽到此处，或<em>点击选择</em></p>
          <p class="upload-hint">支持 PDF、Word 格式</p>
        </div>
      </el-upload>

      <div style="margin-top: 24px;">
        <h4 style="color: #8892b0; margin-bottom: 12px;">附件（选填）</h4>
        <el-upload
          v-model:file-list="attachmentFileList"
          :auto-upload="false"
          multiple
          drag
          accept=".py,.java,.c,.cpp,.h,.js,.ts,.txt,.zip,.rar,.7z,.md,.json,.xml,.yml,.yaml,.xlsx,.xls,.csv,.png,.jpg,.jpeg"
        >
          <el-icon :size="32" class="upload-icon"><FolderOpened /></el-icon>
          <div class="upload-text">
            <p>拖拽代码、数据文件等到此处，或<em>点击选择</em></p>
            <p class="upload-hint">支持 Python、Java、C、JS、TS、TXT、ZIP、Excel 等</p>
          </div>
        </el-upload>
      </div>

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

.step.active { color: #667eea; }
.step.done { color: #52c41a; }

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

.step-line.active { background: #52c41a; }

.upload-icon { color: #667eea; }

.upload-text p { color: #8892b0; margin: 8px 0 0; }
.upload-text em { color: #667eea; font-style: normal; }
.upload-hint { font-size: 12px; color: #5a5a7e !important; }

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}
</style>
