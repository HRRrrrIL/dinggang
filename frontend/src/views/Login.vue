<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">顶钢 CRM</h2>
      <p class="subtitle">客户关系与合同管理系统</p>

      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="账号" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>

      <p class="tip">默认账号: admin / admin123</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: 'admin',
  password: 'admin123'
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/login', form.value)
    localStorage.setItem('token', res.data.tokenValue)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (e) {
    // 错误已被拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}
.title {
  text-align: center;
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}
.subtitle {
  text-align: center;
  color: #999;
  font-size: 14px;
  margin-bottom: 30px;
}
.tip {
  text-align: center;
  color: #ccc;
  font-size: 12px;
  margin-top: 20px;
}
</style>
