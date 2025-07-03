<template>
  <div class="page-container">
    <div class="register-container">
      <div class="register-card card-container">
        <div class="register-header">
          <h1 class="register-title">注册账号</h1>
          <p class="register-subtitle">加入海龟汤，开启思维挑战之旅</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              class="custom-input"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              class="custom-input"
              show-password
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱（可选）"
              size="large"
              class="custom-input"
            />
          </el-form-item>

          <el-form-item v-if="registerForm.email">
            <el-row :gutter="10" style="width:100%">
              <el-col :span="16">
                <el-input
                  v-model="registerForm.code"
                  placeholder="请输入验证码"
                  size="large"
                  class="custom-input"
                />
              </el-col>
              <el-col :span="8">
                <el-button
                  :disabled="mailBtnDisabled || !registerForm.email"
                  size="large"
                  @click="handleSendMail"
                  style="width:100%"
                >
                  {{ mailBtnText }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-button custom-button"
              :loading="loading"
              @click="handleRegister"
            >
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p class="login-link">
            已有账号？
            <router-link to="/login" class="link-text">立即登录</router-link>
          </p>
          <p class="home-link">
            <router-link to="/" class="link-text">返回首页</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { sendMail } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  code: ''
})

const mailBtnText = ref('发送验证码')
const mailBtnDisabled = ref(false)
let mailTimer = null

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleSendMail = async () => {
  if (!registerForm.email) {
    ElMessage.warning('请输入邮箱')
    return
  }
  // 简单校验邮箱格式
  const emailReg = /^[^@\s]+@[^@\s]+\.[^@\s]+$/
  if (!emailReg.test(registerForm.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }
  mailBtnDisabled.value = true
  mailBtnText.value = '发送中...'
  try {
    await sendMail(registerForm.email)
    ElMessage.success('验证码已发送，请查收邮箱')
    let count = 60
    mailBtnText.value = `${count}s后重发`
    mailTimer = setInterval(() => {
      count--
      mailBtnText.value = `${count}s后重发`
      if (count <= 0) {
        clearInterval(mailTimer)
        mailBtnText.value = '发送验证码'
        mailBtnDisabled.value = false
      }
    }, 1000)
  } catch (e) {
    ElMessage.error('验证码发送失败')
    mailBtnText.value = '发送验证码'
    mailBtnDisabled.value = false
  }
}

const handleRegister = async () => {
  try {
    await registerFormRef.value.validate()
    loading.value = true
    const { confirmPassword, ...registerData } = registerForm
    await userStore.register(registerData)
    ElMessage.success('注册成功！请登录')
    router.push('/login')
  } catch (error) {
    ElMessage.error(error.message || '注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-title {
  font-size: 32px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 10px;
}

.register-subtitle {
  color: #7f8c8d;
  font-size: 14px;
}

.register-form {
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
}

.login-link,
.home-link {
  margin: 10px 0;
  font-size: 14px;
  color: #7f8c8d;
}

.link-text {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.link-text:hover {
  text-decoration: underline;
}
</style> 