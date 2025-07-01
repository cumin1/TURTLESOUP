<template>
  <div class="game-container">
    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="soup" class="game-content">
      <div class="game-header card-container detective-bg">
        <img src="/detective.svg" class="detective-icon" alt="detective" />
        <div>
          <h1 class="game-title">{{ soup.title }}</h1>
          <div class="game-meta">
            <el-tag :type="getDifficultyType(soup.difficulty)">
              {{ getDifficultyText(soup.difficulty) }}
            </el-tag>
            <span class="question-count">已提问: {{ questionCount }} 次</span>
          </div>
        </div>
      </div>
      <div class="game-background card-container">
        <h2>题目背景</h2>
        <p class="background-text">{{ soup.description }}</p>
      </div>

      <div class="game-body card-container">
        <div class="question-section">
          <h2>题目描述</h2>
          <p class="question-text">{{ soup.question }}</p>
        </div>

        <div class="chat-section">
          <h2>AI问答</h2>
          <div class="chat-messages" ref="chatContainer">
            <div
              v-for="(message, index) in chatMessages"
              :key="index"
              :class="['message', message.type]"
            >
              <div class="message-content">
                {{ message.content }}
              </div>
              <div class="message-time">
                {{ message.time }}
              </div>
            </div>
          </div>

          <div class="chat-input">
            <el-input
              v-model="currentQuestion"
              placeholder="输入你的问题..."
              @keyup.enter="sendQuestion"
              :disabled="aiLoading || isGameCompleted"
            />
            <el-button
              type="primary"
              @click="sendQuestion"
              :loading="aiLoading"
              :disabled="!currentQuestion.trim() || isGameCompleted"
            >
              发送
            </el-button>
          </div>
        </div>

        <div class="game-actions">
          <el-button @click="showAnswer = !showAnswer">
            {{ showAnswer ? '隐藏答案' : '查看答案' }}
          </el-button>
          <el-button @click="resetGame">重新开始</el-button>
          <el-button @click="handleBackToDetail">返回详情页</el-button>
          <el-button v-if="sessionId && gameStatus === '进行中' && !isGameCompleted" type="danger" @click="handleStopGame">结束游戏</el-button>
          <el-button v-if="isGameCompleted" type="success" disabled>游戏已通关</el-button>
        </div>
      </div>

      <div v-if="showAnswer" class="answer-section card-container">
        <h2>答案</h2>
        <p>{{ soup.answer }}</p>
      </div>
    </div>

    <div v-else class="error">
      <el-empty description="题目不存在" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSoupDetail } from '@/api/soupApi'
import { askAi, winGame, stopGame, getGameStatus } from '@/api/soupApi'

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

onMounted(() => {
  loadSoupDetail()
})
</script>

<style scoped>
.game-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.game-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.game-header {
  padding: 30px;
}

.game-header.detective-bg {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #e0eafc 0%, #cfdef3 100%);
  border-bottom: 2px solid #ffd700;
}

.game-title {
  font-size: 28px;
  margin-bottom: 20px;
}

.game-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.question-count {
  color: #7f8c8d;
}

.game-body {
  padding: 30px;
}

.question-section {
  margin-bottom: 30px;
}

.question-section h2 {
  margin-bottom: 15px;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
}

.chat-section {
  margin-bottom: 30px;
}

.chat-section h2 {
  margin-bottom: 15px;
}

.chat-messages {
  height: 400px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  background: #fafafa;
}

.message {
  margin-bottom: 15px;
}

.message.user {
  text-align: right;
}

.message.ai {
  text-align: left;
}

.message-content {
  display: inline-block;
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  word-wrap: break-word;
}

.message.user .message-content {
  background: #409eff;
  color: white;
}

.message.ai .message-content {
  background: white;
  border: 1px solid #e4e7ed;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input .el-input {
  flex: 1;
}

.game-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.answer-section {
  padding: 30px;
}

.answer-section h2 {
  margin-bottom: 15px;
}

.loading,
.error {
  padding: 40px;
  text-align: center;
}

.detective-icon {
  width: 50px;
  height: 50px;
  margin-right: 20px;
}

.background-text {
  font-size: 16px;
  color: #2d3e50;
  font-family: 'Comic Sans MS', 'PingFang SC', 'Arial', sans-serif;
  margin: 0;
}

@media (max-width: 600px) {
  .game-header.detective-bg {
    flex-direction: column;
    align-items: flex-start;
    padding: 10px !important;
  }
  .background-text {
    font-size: 14px;
  }
}
</style> 