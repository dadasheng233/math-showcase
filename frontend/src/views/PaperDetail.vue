<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPaperDetail } from '@/api/paper'
import gsap from 'gsap'

const route = useRoute()
const router = useRouter()
const paper = ref<any>(null)
const loading = ref(false)
const showPdf = ref(false)

const apiBase = import.meta.env.VITE_API_BASE_URL || '/api'

const pdfUrl = computed(() => {
  if (!paper.value?.filePath) return ''
  return `${apiBase}/files/uploads/${paper.value.filePath}`
})

async function fetchDetail() {
  loading.value = true
  try {
    const res: any = await getPaperDetail(Number(route.params.id))
    paper.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchDetail()
  await nextTick()
  gsap.from('.detail-section', {
    y: 40,
    opacity: 0,
    duration: 0.6,
    stagger: 0.15,
    ease: 'power3.out'
  })
})
</script>

<template>
  <div v-loading="loading" class="page-container">
    <div class="page-header">
      <el-button text @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回
      </el-button>
    </div>

    <div v-if="paper" class="paper-detail">
      <div class="detail-section paper-hero">
        <div class="hero-gradient"></div>
        <h1 class="paper-title">{{ paper.title }}</h1>
        <p class="paper-authors" v-if="paper.authors">{{ paper.authors }}</p>
        <div class="paper-meta">
          <span><el-icon><View /></el-icon> {{ paper.viewCount || 0 }} 次浏览</span>
          <span>{{ new Date(paper.createTime).toLocaleDateString() }}</span>
        </div>
      </div>

      <div class="detail-section card" v-if="paper.keywords">
        <div class="tag-cloud">
          <el-tag
            v-for="tag in (paper.keywords || '').split(',')"
            :key="tag"
            size="large"
            class="keyword-tag"
          >{{ tag }}</el-tag>
        </div>
      </div>

      <div class="detail-section card" v-if="paper.abstractText">
        <h3 class="section-title">摘要</h3>
        <p class="abstract-text">{{ paper.abstractText }}</p>
      </div>

      <div class="detail-section card" v-if="paper.fileName">
        <h3 class="section-title">论文文件</h3>
        <div class="file-actions">
          <div class="file-info">
            <el-icon :size="24"><Document /></el-icon>
            <span>{{ paper.fileName }}</span>
            <el-tag size="small">PDF</el-tag>
          </div>
          <div class="file-btns">
            <el-button type="primary" @click="showPdf = !showPdf">
              {{ showPdf ? '收起预览' : '在线预览' }}
            </el-button>
            <el-button @click="() => window.open(pdfUrl, '_blank')">
              <el-icon><Download /></el-icon> 下载
            </el-button>
          </div>
        </div>

        <div v-if="showPdf" class="pdf-viewer">
          <iframe
            :src="pdfUrl"
            class="pdf-frame"
            frameborder="0"
          ></iframe>
        </div>
      </div>

      <div class="detail-section card" v-if="paper.coverImage">
        <h3 class="section-title">封面预览</h3>
        <img :src="paper.coverImage" class="cover-preview" style="max-width: 100%; border-radius: 12px;" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.paper-detail {
  max-width: 900px;
}

.paper-hero {
  position: relative;
  padding: 48px 40px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.06);
  overflow: hidden;
}

.hero-gradient {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.2), transparent);
  pointer-events: none;
}

.paper-title {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea, #764ba2, #f093fb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 16px;
  position: relative;
}

.paper-authors {
  font-size: 16px;
  color: #667eea;
  margin: 0 0 12px;
  position: relative;
}

.paper-meta {
  display: flex;
  gap: 20px;
  color: #5a5a7e;
  font-size: 13px;
  position: relative;
}

.paper-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #667eea;
  margin: 0 0 16px;
}

.abstract-text {
  color: #8892b0;
  line-height: 1.8;
  font-size: 15px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword-tag {
  background: rgba(102, 126, 234, 0.1) !important;
  border-color: rgba(102, 126, 234, 0.2) !important;
  color: #667eea !important;
}

.file-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding: 20px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #ccd6f6;
}

.file-btns {
  display: flex;
  gap: 10px;
}

.pdf-viewer {
  margin-top: 16px;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.pdf-frame {
  width: 100%;
  height: 700px;
}
</style>
