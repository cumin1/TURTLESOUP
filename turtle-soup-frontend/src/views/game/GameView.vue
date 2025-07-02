<template>
  <div class="mystic-game-bg">
    <div class="mystic-game-container">
      <!-- 顶部题目信息（不再固定） -->
      <div class="mystic-header-card" style="position:static;">
        <div class="mystic-header-content">
          <img src="/detective.svg" class="mystic-detective-icon" alt="detective" />
          <div class="mystic-title-block">
            <h1 class="mystic-title">🔮 {{ soup?.title || '谜题标题' }}</h1>
            <div class="mystic-meta">
              <el-tag :type="getDifficultyType(soup?.difficulty)" class="mystic-diff-tag">
                {{ getDifficultyText(soup?.difficulty) }}
              </el-tag>
              <span class="mystic-question-count">已提问: {{ questionCount }} 次</span>
            </div>
          </div>
        </div>
        <div class="mystic-bg-block">
          <span class="mystic-bg-label">🦄 题目背景</span>
          <p class="mystic-bg-text">{{ soup?.description || '题目背景描述...' }}</p>
        </div>
      </div>
      <!-- 聊天与操作区 -->
      <div class="mystic-main-area">
        <div class="mystic-chat-card">
          <div class="mystic-chat-section">
            <div class="mystic-chat-messages" ref="chatContainer">
              <div
                v-for="(message, index) in chatMessages"
                :key="index"
                :class="['mystic-message', message.type]"
              >
                <div class="mystic-message-content">
                  <span v-if="message.type==='ai'" class="mystic-ai-icon">🤖</span>
                  <span v-if="message.type==='user'" class="mystic-user-icon">🧑‍💻</span>
                  {{ message.content }}
                </div>
                <div class="mystic-message-time">
                  {{ message.time }}
                </div>
              </div>
            </div>
            <div class="mystic-chat-input-wrap">
              <el-input
                v-model="currentQuestion"
                placeholder="输入你的问题..."
                @keyup.enter="sendQuestion"
                :disabled="aiLoading || isGameCompleted"
                class="mystic-chat-input"
                prefix-icon="el-icon-magic-stick"
              />
              <el-button
                type="primary"
                @click="sendQuestion"
                :loading="aiLoading"
                :disabled="!currentQuestion.trim() || isGameCompleted"
                class="mystic-send-btn"
              >
                <span>发送 ✨</span>
              </el-button>
            </div>
          </div>
          <div class="mystic-actions">
            <el-button @click="showAnswer = !showAnswer" class="mystic-action-btn">
              {{ showAnswer ? '隐藏答案' : '查看答案' }}
            </el-button>
            <el-button @click="resetGame" class="mystic-action-btn">重新开始</el-button>
            <el-button @click="handleBackToDetail" class="mystic-action-btn">返回详情页</el-button>
            <el-button v-if="sessionId && gameStatus === '进行中' && !isGameCompleted" type="danger" @click="handleStopGame" class="mystic-action-btn">结束游戏</el-button>
            <el-button v-if="isGameCompleted" type="success" disabled class="mystic-action-btn">游戏已通关</el-button>
          </div>
        </div>
        <transition name="fade">
          <div v-if="showAnswer" class="mystic-answer-card">
            <h2>🌟 答案</h2>
            <p>{{ soup?.answer }}</p>
          </div>
        </transition>
      </div>
      <div v-if="loading" class="mystic-loading">
        <el-skeleton :rows="5" animated />
      </div>
      <div v-else-if="!soup" class="mystic-error">
        <el-empty description="题目不存在" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSoupDetail } from '@/api/soupApi'
import { askAi, winGame, stopGame, getGameStatus } from '@/api/soupApi'
import request from '@/api/index'

const route = useRoute()
const router = useRouter()

const soup = ref(null)
const loading = ref(true)
const showAnswer = ref(false)
const currentQuestion = ref('')
const aiLoading = ref(false)
const questionCount = ref(0)
const chatMessages = ref([])
const chatContainer = ref(null)
const sessionId = ref(route.params.id || route.query.sessionId)
const soupId = ref(route.query.soupId)
const gameStatus = ref('进行中')
const isGameCompleted = ref(false)

const getDifficultyType = (difficulty) => {
  const types = { '简单': 'success', '中等': 'warning', '困难': 'danger' }
  return types[difficulty] || 'info'
}

const getDifficultyText = (difficulty) => {
  const texts = { '简单': 'Easy', '中等': 'Medium', '困难': 'Hard' }
  return texts[difficulty] || 'Unknown'
}

const loadSoupDetail = async () => {
  if (!soupId.value) {
    ElMessage.error('无效的题目ID')
    return
  }
  try {
    const response = await getSoupDetail(soupId.value)
    soup.value = response.data
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const sendQuestion = async () => {
  if (!currentQuestion.value.trim() || aiLoading.value) return

  const question = currentQuestion.value.trim()
  const time = new Date().toLocaleTimeString()
  
  // 添加用户问题
  chatMessages.value.push({
    type: 'user',
    content: question,
    time
  })
  
  questionCount.value++
  currentQuestion.value = ''
  aiLoading.value = true

  try {
    const response = await askAi({
      sessionId: sessionId.value,
      soupId: soupId.value,
      question: question
    })
    
    const aiResponse = response.data
    
    // 添加AI回答
    chatMessages.value.push({
      type: 'ai',
      content: aiResponse,
      time: new Date().toLocaleTimeString()
    })
    
    // 判断是否成功
    if (aiResponse.startsWith('SUCCESS:')) {
      // 调用通关接口
      await winGame(sessionId.value)
      ElMessage.success('恭喜你推理成功！游戏通关！')
      // 设置游戏完成状态
      isGameCompleted.value = true
      gameStatus.value = '已通关'
      // 禁用输入框
      aiLoading.value = false
      return
    }
    
  } catch (error) {
    ElMessage.error('AI回答失败')
  } finally {
    aiLoading.value = false
    await nextTick()
    scrollToBottom()
  }
}

const scrollToBottom = () => {
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const resetGame = () => {
  chatMessages.value = []
  questionCount.value = 0
  showAnswer.value = false
}

const handleBackToDetail = () => {
  if (soupId.value && sessionId.value) {
    router.push({ path: `/soup/${soupId.value}`, query: { sessionId: sessionId.value } })
  } else if (soupId.value) {
    router.push(`/soup/${soupId.value}`)
  }
}

const handleStopGame = async () => {
  if (!sessionId.value) {
    ElMessage.warning('无有效游戏会话')
    return
  }
  try {
    await stopGame(sessionId.value)
    ElMessage.success('已结束游戏')
    gameStatus.value = '已结束'
    if (soupId.value) {
      router.push({ path: `/soup/${soupId.value}` })
    }
  } catch (e) {
    ElMessage.error('结束游戏失败')
  }
}

onMounted(async () => {
  if (sessionId.value) {
    // 加载历史对话
    try {
      const res = await request({
        url: `/game/sessionDetail/${sessionId.value}`,
        method: 'get'
      })
      if (Array.isArray(res.data)) {
        chatMessages.value = res.data.map(item => ({
          type: item.type === 'AI' ? 'ai' : 'user',
          content: item.question || item.aiAnswer,
          time: item.createdAt ? new Date(item.createdAt).toLocaleTimeString() : ''
        })).sort((a, b) => new Date(a.time) - new Date(b.time))
      }
    } catch (e) {
      // 忽略加载历史对话失败
    }
  }
  loadSoupDetail()
})
</script>

<style scoped>
/* 背景和整体布局 */
.mystic-game-bg {
  min-height: 100vh;
  background: linear-gradient(135deg, #232946 0%, #3e2f5b 60%, #a7c7e7 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 0 0 40px 0;
}
.mystic-game-container {
  width: 100%;
  max-width: 700px;
  margin: 40px auto 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 顶部题目信息卡片 */
.mystic-header-card {
  background: linear-gradient(120deg, #2d3250 60%, #6e6bc4 100%);
  border-radius: 24px;
  box-shadow: 0 8px 32px rgba(44,62,80,0.18);
  padding: 32px 32px 18px 32px;
  color: #fff;
  margin-bottom: 0;
  border: 2px solid #a7c7e7;
  overflow: visible;
}
.mystic-header-content {
  display: flex;
  align-items: center;
  gap: 24px;
}
.mystic-detective-icon {
  width: 60px;
  height: 60px;
  filter: drop-shadow(0 0 12px #a7c7e7);
  margin-right: 8px;
}
.mystic-title-block {
  flex: 1;
}
.mystic-title {
  font-size: 2.2rem;
  font-weight: 700;
  letter-spacing: 2px;
  margin-bottom: 10px;
  text-shadow: 0 2px 16px #a7c7e7, 0 0 2px #fff;
  font-family: 'Comic Sans MS', 'PingFang SC', 'Arial', sans-serif;
}
.mystic-meta {
  display: flex;
  align-items: center;
  gap: 18px;
}
.mystic-diff-tag {
  background: rgba(255,255,255,0.12);
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  color: #fff;
  border: none;
}
.mystic-question-count {
  font-size: 0.98rem;
  color: #e0eafc;
  background: rgba(255,255,255,0.08);
  border-radius: 8px;
  padding: 2px 10px;
}
.mystic-bg-block {
  margin-top: 18px;
  background: rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 18px 20px 12px 20px;
  box-shadow: 0 2px 12px rgba(167,199,231,0.08);
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.mystic-bg-label {
  font-size: 1.1rem;
  color: #ffd700;
  font-weight: 600;
  margin-bottom: 4px;
  letter-spacing: 1px;
}
.mystic-bg-text {
  font-size: 1.08rem;
  color: #f7f8fa;
  font-family: 'Comic Sans MS', 'PingFang SC', 'Arial', sans-serif;
  margin: 0;
  line-height: 1.7;
}

/* 聊天与操作区 */
.mystic-main-area {
  margin-top: 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.mystic-chat-card {
  background: linear-gradient(120deg, #f7f8fa 60%, #e0eafc 100%);
  border-radius: 22px;
  box-shadow: 0 4px 24px rgba(44,62,80,0.10);
  padding: 28px 18px 18px 18px;
  min-height: 420px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: relative;
}
.mystic-chat-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.mystic-chat-messages {
  height: 320px;
  overflow-y: auto;
  border-radius: 16px;
  padding: 18px 10px 10px 10px;
  background: linear-gradient(135deg, #e0eafc 0%, #f7f8fa 100%);
  box-shadow: 0 2px 8px rgba(167,199,231,0.08);
  margin-bottom: 10px;
  border: 1.5px solid #a7c7e7;
}
.mystic-message {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}
.mystic-message.user {
  align-items: flex-end;
}
.mystic-message-content {
  display: inline-block;
  max-width: 80%;
  padding: 12px 18px;
  border-radius: 18px 18px 8px 18px;
  word-wrap: break-word;
  font-size: 1.08rem;
  font-family: 'Comic Sans MS', 'PingFang SC', 'Arial', sans-serif;
  box-shadow: 0 2px 8px rgba(44,62,80,0.06);
  background: linear-gradient(135deg, #fffbe6 0%, #ffd5dc 100%);
  color: #232946;
  position: relative;
  margin-bottom: 2px;
  transition: background 0.3s;
}
.mystic-message.user .mystic-message-content {
  background: linear-gradient(135deg, #b6e3f4 0%, #d1d4f9 100%);
  color: #232946;
  border-radius: 18px 18px 18px 8px;
}
.mystic-message.ai .mystic-message-content {
  background: linear-gradient(135deg, #e0eafc 0%, #c0aede 100%);
  color: #3e2f5b;
  border-radius: 18px 8px 18px 18px;
}
.mystic-ai-icon, .mystic-user-icon {
  margin-right: 8px;
  font-size: 1.2em;
  vertical-align: middle;
}
.mystic-message-time {
  font-size: 0.88rem;
  color: #a7c7e7;
  margin-top: 2px;
  text-align: right;
}
.mystic-chat-input-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  background: rgba(255,255,255,0.7);
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(44,62,80,0.06);
  padding: 8px 12px;
}
.mystic-chat-input {
  flex: 1;
  border-radius: 12px;
  background: #fff;
  font-size: 1.08rem;
  border: none;
  box-shadow: none;
}
.mystic-send-btn {
  background: linear-gradient(90deg, #6e6bc4 0%, #ffd5dc 100%);
  color: #232946;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(167,199,231,0.12);
  transition: background 0.3s;
}
.mystic-send-btn:hover {
  background: linear-gradient(90deg, #ffd5dc 0%, #6e6bc4 100%);
}

.mystic-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 10px;
}
.mystic-action-btn {
  background: linear-gradient(90deg, #232946 0%, #6e6bc4 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(44,62,80,0.10);
  transition: background 0.3s;
}
.mystic-action-btn:hover {
  background: linear-gradient(90deg, #6e6bc4 0%, #232946 100%);
}

.mystic-answer-card {
  background: linear-gradient(120deg, #fffbe6 60%, #ffd5dc 100%);
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(255,223,191,0.12);
  padding: 24px 18px;
  margin-top: 10px;
  color: #232946;
  font-size: 1.1rem;
  font-family: 'Comic Sans MS', 'PingFang SC', 'Arial', sans-serif;
}
.mystic-loading, .mystic-error {
  padding: 40px;
  text-align: center;
}

/* 动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

@media (max-width: 700px) {
  .mystic-game-container {
    max-width: 98vw;
    margin: 16px auto 0 auto;
    padding: 0 2vw;
  }
  .mystic-header-card {
    padding: 18px 8px 10px 8px;
  }
  .mystic-title {
    font-size: 1.3rem;
  }
  .mystic-bg-block {
    padding: 10px 8px 8px 8px;
  }
  .mystic-chat-card {
    padding: 12px 4px 8px 4px;
    min-height: 320px;
  }
  .mystic-chat-messages {
    height: 180px;
    padding: 8px 2px 2px 2px;
  }
}
</style> 