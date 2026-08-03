<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { getPapers } from '@/api/paper'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

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
    if (page.value === 1) {
      papers.value = res.data.records
    } else {
      papers.value.push(...res.data.records)
    }
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  fetchPapers()
}

function goDetail(id: number) {
  router.push(`/papers/${id}`)
}

function goProject(id: number) {
  router.push(`/projects/${id}`)
}

onMounted(async () => {
  await fetchPapers()
  await nextTick()
  animateCards()
})

function animateCards() {
  gsap.from('.paper-card', {
    scrollTrigger: {
      trigger: '.gallery-grid',
      start: 'top 80%'
    },
    y: 60,
    opacity: 0,
    duration: 0.8,
    stagger: 0.1,
    ease: 'power3.out'
  })
}
</script>

<template>
  <div class="gallery-page">
    <div class="hero-section">
      <div class="hero-bg">
        <div class="hero-particle" v-for="i in 20" :key="i"
          :style="{
            left: Math.random() * 100 + '%',
            top: Math.random() * 100 + '%',
            animationDelay: Math.random() * 3 + 's',
            animationDuration: (3 + Math.random() * 4) + 's'
          }">
        </div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">数学建模成果展示</h1>
        <p class="hero-desc">探索优秀论文与建模项目，发现数学之美</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" round @click="router.push('/papers/upload')">
            <el-icon><Upload /></el-icon> 上传论文
          </el-button>
          <el-button size="large" round class="hero-btn-outline" @click="router.push('/projects/create')">
            <el-icon><Plus /></el-icon> 新建项目
          </el-button>
        </div>
      </div>
    </div>

    <div class="gallery-section">
      <div class="section-header">
        <h2 class="section-title">论文画廊</h2>
        <div class="search-box">
          <el-input
            v-model="keyword"
            placeholder="搜索论文..."
            prefix-icon="Search"
            clearable
            @keyup.enter="page = 1; fetchPapers()"
            @clear="page = 1; fetchPapers()"
          />
        </div>
      </div>

      <div v-loading="loading" class="gallery-grid">
        <div
          v-for="(paper, index) in papers"
          :key="paper.id"
          class="paper-card"
          :style="{ animationDelay: index * 0.05 + 's' }"
          @click="goDetail(paper.id)"
        >
          <div class="card-cover">
            <img v-if="paper.coverImage" :src="paper.coverImage" :alt="paper.title" />
            <div v-else class="card-cover-placeholder">
              <el-icon :size="48"><Document /></el-icon>
            </div>
            <div class="card-overlay">
              <span class="view-btn">查看详情</span>
            </div>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ paper.title }}</h3>
            <p class="card-authors" v-if="paper.authors">{{ paper.authors }}</p>
            <p class="card-abstract" v-if="paper.abstractText">{{ paper.abstractText.slice(0, 100) }}...</p>
            <div class="card-tags" v-if="paper.keywords">
              <el-tag
                v-for="tag in paper.keywords.split(',')"
                :key="tag"
                size="small"
                class="tag-item"
              >{{ tag }}</el-tag>
            </div>
            <div class="card-meta">
              <span><el-icon><View /></el-icon> {{ paper.viewCount || 0 }}</span>
              <span>{{ new Date(paper.createTime).toLocaleDateString() }}</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="papers.length < total" class="load-more">
        <el-button :loading="loading" @click="loadMore" round>加载更多</el-button>
      </div>

      <div v-if="!loading && papers.length === 0" class="empty-state">
        <el-icon :size="64"><Document /></el-icon>
        <p>还没有论文，快去上传吧</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.gallery-page {
  padding-bottom: 60px;
}

.hero-section {
  position: relative;
  padding: 80px 40px;
  text-align: center;
  overflow: hidden;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: #667eea;
  border-radius: 50%;
  animation: particleFloat linear infinite;
  opacity: 0.6;
}

@keyframes particleFloat {
  0% { transform: translateY(0) scale(1); opacity: 0; }
  20% { opacity: 0.6; }
  80% { opacity: 0.6; }
  100% { transform: translateY(-100px) scale(0); opacity: 0; }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 16px;
}

.hero-desc {
  font-size: 18px;
  color: #8892b0;
  margin: 0 0 32px;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.hero-btn-outline {
  background: transparent !important;
  border: 1px solid rgba(102, 126, 234, 0.4) !important;
  color: #8892b0 !important;
}

.hero-btn-outline:hover {
  border-color: #667eea !important;
  color: #667eea !important;
}

.gallery-section {
  padding: 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

.search-box {
  width: 280px;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

@media (max-width: 1400px) {
  .gallery-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 900px) {
  .gallery-grid { grid-template-columns: 1fr; }
}

.paper-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.paper-card:hover {
  transform: translateY(-4px);
  border-color: rgba(102, 126, 234, 0.3);
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.15);
}

.card-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #1a1a3e, #1a1a2e);
}

.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #3a3a5e;
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.paper-card:hover .card-overlay {
  opacity: 1;
}

.view-btn {
  padding: 8px 20px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 20px;
  color: #fff;
  font-size: 13px;
}

.card-body {
  padding: 16px 20px 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-authors {
  font-size: 13px;
  color: #667eea;
  margin: 0 0 8px;
}

.card-abstract {
  font-size: 13px;
  color: #8892b0;
  margin: 0 0 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tag-item {
  background: rgba(102, 126, 234, 0.1) !important;
  border-color: rgba(102, 126, 234, 0.2) !important;
  color: #667eea !important;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #5a5a7e;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.load-more {
  text-align: center;
  margin-top: 40px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #5a5a7e;
}

.empty-state p {
  margin-top: 16px;
  font-size: 16px;
}
</style>
