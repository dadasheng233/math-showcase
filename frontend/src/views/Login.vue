<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const isRegister = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      if (isRegister.value) {
        await userStore.register({ username: form.username, password: form.password, nickname: form.nickname })
      } else {
        await userStore.login({ username: form.username, password: form.password })
      }
      ElMessage.success(isRegister.value ? '注册成功' : '登录成功')
      router.push('/gallery')
    } catch (err: any) {
      ElMessage.error(err.message || '操作失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">Σ</div>
        <h1>Math Showcase</h1>
        <p>数学建模成果展示平台</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>

        <el-form-item v-if="isRegister" label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称（选填）" prefix-icon="UserFilled" />
        </el-form-item>

        <el-button type="primary" :loading="loading" @click="handleSubmit" class="submit-btn">
          {{ isRegister ? '注册' : '登录' }}
        </el-button>

        <p class="switch-text">
          {{ isRegister ? '已有账号？' : '没有账号？' }}
          <a href="#" @click.prevent="isRegister = !isRegister">
            {{ isRegister ? '去登录' : '去注册' }}
          </a>
        </p>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0a0a1a;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.3;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: #667eea;
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: #764ba2;
  bottom: -50px;
  left: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.shape-3 {
  width: 200px;
  height: 200px;
  background: #f093fb;
  top: 50%;
  left: 50%;
  animation: float 12s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(30px, -20px) scale(1.1); }
}

.login-card {
  width: 420px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: 48px 40px;
  backdrop-filter: blur(20px);
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-logo {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin: 0 auto 16px;
}

.login-header h1 {
  font-size: 24px;
  color: #fff;
  margin: 0 0 8px;
}

.login-header p {
  color: #8892b0;
  font-size: 14px;
  margin: 0;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea, #764ba2) !important;
  border: none !important;
  font-size: 16px;
  margin-top: 8px;
}

.switch-text {
  text-align: center;
  color: #8892b0;
  font-size: 14px;
  margin-top: 20px;
}

.switch-text a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}
</style>
