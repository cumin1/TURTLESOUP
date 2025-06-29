<template>
  <div class="home-container">
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-icon">
          <el-icon class="turtle-icon"><Turtle /></el-icon>
        </div>
        <h1 class="hero-title">
          欢迎来到 <span class="highlight">海龟汤</span>
        </h1>
        <p class="hero-subtitle">
          挑战你的横向思维能力，体验最有趣的推理游戏
        </p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/soup')" class="start-btn">
            开始推理
          </el-button>
          <el-button size="large" @click="handleGuestLogin" class="guest-btn">
            游客体验
          </el-button>
        </div>
      </div>
    </div>

    <div class="features-section">
      <h2 class="section-title">游戏特色</h2>
      <div class="features-grid">
        <div class="feature-card card-container">
          <div class="feature-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <h3>AI智能问答</h3>
          <p>与AI进行实时对话，获得智能提示</p>
        </div>
        <div class="feature-card card-container">
          <div class="feature-icon">
            <el-icon><Collection /></el-icon>
          </div>
          <h3>丰富题库</h3>
          <p>68个精选海龟汤题目，难度各异</p>
        </div>
        <div class="feature-card card-container">
          <div class="feature-icon">
            <el-icon><User /></el-icon>
          </div>
          <h3>游客模式</h3>
          <p>无需注册即可体验游戏乐趣</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Turtle, ChatDotRound, Collection, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const handleGuestLogin = () => {
  userStore.guestLogin()
  ElMessage.success('游客登录成功！')
  router.push('/soup')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.home-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="50" cy="50" r="1" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
  pointer-events: none;
}

.hero-section {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 20px 80px;
  min-height: 70vh;
  text-align: center;
  position: relative;
}

.hero-icon {
  margin-bottom: 30px;
}

.turtle-icon {
  font-size: 80px;
  color: #fff;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.3));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.hero-title {
  font-size: 56px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  letter-spacing: 2px;
}

.highlight {
  color: #ffd700;
  text-shadow: 0 0 10px rgba(255,215,0,0.5);
}

.hero-subtitle {
  font-size: 22px;
  color: rgba(255,255,255,0.9);
  margin-bottom: 50px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.start-btn {
  background: linear-gradient(45deg, #ff6b6b, #ee5a24);
  border: none;
  padding: 15px 30px;
  font-size: 18px;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(255,107,107,0.4);
  transition: all 0.3s ease;
}

.start-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255,107,107,0.6);
}

.guest-btn {
  background: rgba(255,255,255,0.2);
  border: 2px solid rgba(255,255,255,0.3);
  color: #fff;
  padding: 15px 30px;
  font-size: 18px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.guest-btn:hover {
  background: rgba(255,255,255,0.3);
  border-color: rgba(255,255,255,0.5);
  transform: translateY(-2px);
}

.features-section {
  padding: 80px 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.section-title {
  text-align: center;
  font-size: 42px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 60px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  padding: 40px 30px;
  text-align: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  transition: left 0.5s;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.2);
}

.feature-icon {
  margin-bottom: 20px;
}

.feature-icon .el-icon {
  font-size: 48px;
  color: #667eea;
}

.feature-card h3 {
  font-size: 24px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 15px;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.6;
  font-size: 16px;
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 42px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .section-title {
    font-size: 32px;
  }
}
</style>
