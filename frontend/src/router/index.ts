import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/gallery',
    children: [
      {
        path: 'gallery',
        name: 'Gallery',
        component: () => import('@/views/Gallery.vue'),
        meta: { title: '论文画廊' }
      },
      {
        path: 'projects',
        name: 'ProjectList',
        component: () => import('@/views/ProjectList.vue'),
        meta: { title: '建模项目' }
      },
      {
        path: 'projects/create',
        name: 'ProjectCreate',
        component: () => import('@/views/ProjectCreate.vue'),
        meta: { title: '新建项目' }
      },
      {
        path: 'projects/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/ProjectDetail.vue'),
        meta: { title: '项目详情' }
      },
      {
        path: 'papers',
        name: 'PaperList',
        component: () => import('@/views/PaperList.vue'),
        meta: { title: '论文管理' }
      },
      {
        path: 'papers/upload',
        name: 'PaperUpload',
        component: () => import('@/views/PaperUpload.vue'),
        meta: { title: '上传论文' }
      },
      {
        path: 'papers/:id',
        name: 'PaperDetail',
        component: () => import('@/views/PaperDetail.vue'),
        meta: { title: '论文详情' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach((to) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - Math Showcase`
  }
})

export default router
