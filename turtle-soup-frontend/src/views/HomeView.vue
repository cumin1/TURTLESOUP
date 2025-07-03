<template>
  <div class="home-container">
    <canvas class="bubble-bg"></canvas>
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
        </div>
      </div>
    </div>

    <!-- 新增：玩法引导区 -->
    <div class="guide-section card-container">
      <h2 class="section-title">如何玩海龟汤？</h2>
      <div class="guide-steps">
        <div class="guide-step">
          <img src="/detective.svg" class="guide-icon" alt="detective" />
          <div>
            <h3>1. 选择谜题</h3>
            <p>浏览题库，挑选你感兴趣的海龟汤谜题。</p>
          </div>
        </div>
        <div class="guide-step">
          <img src="/magnifier.svg" class="guide-icon" alt="magnifier" />
          <div>
            <h3>2. 与AI互动</h3>
            <p>向AI助手提问，获取线索，逐步接近真相。</p>
          </div>
        </div>
        <div class="guide-step">
          <img src="/footprint.svg" class="guide-icon" alt="footprint" />
          <div>
            <h3>3. 推理真相</h3>
            <p>大胆假设，小心求证，揭开谜题背后的故事！</p>
          </div>
        </div>
        <div class="guide-step">
          <img src="/detective.svg" class="guide-icon" alt="history" />
          <div>
            <h3>4. 查看历史</h3>
            <p>随时回顾你的推理历程，挑战更高难度！</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增：趣味彩蛋区 -->
    <div class="fun-section card-container">
      <h2 class="section-title">趣味彩蛋</h2>
      <div class="fun-egg">
        <el-icon class="fun-egg-icon"><Turtle /></el-icon>
        <span>{{ randomFunFact }}</span>
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
import { onMounted, onBeforeUnmount, ref } from 'vue'

const router = useRouter()
const userStore = useUserStore()

const handleGuestLogin = () => {
  userStore.guestLogin()
  ElMessage.success('游客登录成功！')
  router.push('/soup')
}

// 动态气泡和可爱表情特效
const emojis = ['🐢', '🐰', '🐱', '🐟', '🐧', '🦄', '🌈', '⭐', '🍉', '🍓', '🍩', '🍬']
let animationId = null
let bubbles = []
let canvas, ctx

function randomEmoji() {
  return emojis[Math.floor(Math.random() * emojis.length)]
}

function createBubble(width, height) {
  return {
    x: Math.random() * width,
    y: height + 30 + Math.random() * 100,
    r: 18 + Math.random() * 22,
    speed: 0.5 + Math.random() * 1.2,
    drift: (Math.random() - 0.5) * 0.6,
    opacity: 0.5 + Math.random() * 0.5,
    emoji: Math.random() < 0.5 ? randomEmoji() : null
  }
}

function drawBubbles() {
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  for (const b of bubbles) {
    ctx.save()
    ctx.globalAlpha = b.opacity
    if (b.emoji) {
      ctx.font = `${b.r * 1.2}px serif`
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillText(b.emoji, b.x, b.y)
    } else {
      ctx.beginPath()
      ctx.arc(b.x, b.y, b.r, 0, Math.PI * 2)
      ctx.fillStyle = 'rgba(255,255,255,0.18)'
      ctx.shadowColor = '#fff'
      ctx.shadowBlur = 8
      ctx.fill()
    }
    ctx.restore()
  }
}

function animate() {
  for (const b of bubbles) {
    b.y -= b.speed
    b.x += b.drift
    if (b.y < -40) {
      // 重生
      Object.assign(b, createBubble(canvas.width, canvas.height))
      b.y = canvas.height + 30
    }
  }
  drawBubbles()
  animationId = requestAnimationFrame(animate)
}

function resizeCanvas() {
  canvas.width = window.innerWidth
  canvas.height = window.innerHeight
}

onMounted(() => {
  canvas = document.querySelector('.bubble-bg')
  if (!canvas) return
  ctx = canvas.getContext('2d')
  resizeCanvas()
  // 生成气泡
  bubbles = Array.from({ length: 22 }, () => createBubble(canvas.width, canvas.height))
  animate()
  window.addEventListener('resize', resizeCanvas)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(animationId)
  window.removeEventListener('resize', resizeCanvas)
})

const funFacts = [
  '你知道吗？海龟汤的名字其实和海龟无关，而是源自一道"汤"谜题！',
  '推理的关键：多问"为什么"，大胆假设，小心求证。',
  '有些谜题看似离奇，其实真相往往很简单！',
  'AI助手不会骗你，但也不会直接告诉你答案哦~',
  '每一次推理，都是对思维的锻炼！',
  '你能猜到本页面有多少只🐢吗？',
  '谜题太难？试试和朋友一起推理吧！',
  '海龟汤适合聚会、破冰、提升逻辑思维能力！',
  '有趣的谜题能让你忘记时间，沉浸推理世界！',
  '据说，最强的推理高手能三问之内猜出真相！',
]
const randomFunFact = funFacts[Math.floor(Math.random() * funFacts.length)]
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.bubble-bg {
  position: absolute;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 0;
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

.guide-section {
  margin: 48px auto 0 auto;
  max-width: 900px;
  background: linear-gradient(120deg, #f7f8fa 60%, #e0eafc 100%);
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(167,199,231,0.08);
  padding: 32px 24px;
}
.guide-steps {
  display: flex;
  flex-wrap: wrap;
  gap: 32px 24px;
  justify-content: space-between;
  margin-top: 24px;
}
.guide-step {
  display: flex;
  align-items: center;
  gap: 18px;
  flex: 1 1 220px;
  min-width: 220px;
}
.guide-icon {
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 2px 8px rgba(44,62,80,0.06);
  padding: 8px;
}
.fun-section {
  margin: 40px auto 0 auto;
  max-width: 700px;
  background: linear-gradient(120deg, #e0eafc 60%, #f7f8fa 100%);
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(167,199,231,0.08);
  padding: 28px 20px;
  text-align: center;
}
.fun-egg {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-size: 1.15rem;
  color: #6e6bc4;
  margin-top: 12px;
}
.fun-egg-icon {
  font-size: 2.2rem;
  color: #ffd700;
}
</style>
